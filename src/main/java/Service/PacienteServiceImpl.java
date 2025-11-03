package Service;

import Config.DatabaseConnection;
import Dao.PacienteDAO;
import Models.HistoriaClinica;
import Models.Paciente;

import java.sql.Connection;
import java.util.List;

/**
 * Servicio de negocio para la entidad Paciente.
 *
 * Responsabilidades:
 * - Validar datos del paciente (nombre, apellido, DNI).
 * - Garantizar unicidad del DNI.
 * - Coordinar operaciones transaccionales entre Paciente y HistoriaClinica.
 * - Proveer métodos de búsqueda especializados.
 *
 * Patrón: Service Layer con manejo de transacciones JDBC.
 */
public class PacienteServiceImpl implements GenericService<Paciente> {

    private final PacienteDAO pacienteDAO;
    private final HistoriaClinicaServiceImpl historiaClinicaService;

    public PacienteServiceImpl(PacienteDAO pacienteDAO, HistoriaClinicaServiceImpl historiaClinicaService) {
        if (pacienteDAO == null) {
            throw new IllegalArgumentException("PacienteDAO no puede ser null");
        }
        if (historiaClinicaService == null) {
            throw new IllegalArgumentException("HistoriaClinicaServiceImpl no puede ser null");
        }
        this.pacienteDAO = pacienteDAO;
        this.historiaClinicaService = historiaClinicaService;
    }

    /**
     * Inserta un nuevo paciente con su historia clínica de forma transaccional.
     */
    @Override
    public void insertar(Paciente paciente) throws Exception {
        validatePaciente(paciente);
        validateDniUnique(paciente.getDni(), null);

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Paso 1: Insertar Historia Clínica si existe
            if (paciente.getHistoriaClinica() != null) {
                HistoriaClinica hc = paciente.getHistoriaClinica();
                historiaClinicaService.insertTx(hc, conn);
                paciente.setHistoriaClinica(hc); // con ID generado
            }

            // Paso 2: Insertar Paciente
            pacienteDAO.insertTx(paciente, conn);

            // Paso 3: Relacionar HC con Paciente (si aplica)
            if (paciente.getHistoriaClinica() != null) {
                paciente.getHistoriaClinica().setPacienteId((long) paciente.getId());
                historiaClinicaService.actualizar(paciente.getHistoriaClinica());
            }

            conn.commit();
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw new Exception("Error transaccional al insertar paciente y su historia clínica: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    @Override
    public void actualizar(Paciente paciente) throws Exception {
        validatePaciente(paciente);
        if (paciente.getId() <= 0) {
            throw new IllegalArgumentException("El ID del paciente debe ser mayor a 0");
        }
        validateDniUnique(paciente.getDni(), paciente.getId());
        pacienteDAO.actualizar(paciente);
    }

    @Override
    public void eliminar(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        pacienteDAO.eliminar(id);
    }

    @Override
    public Paciente getById(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        return pacienteDAO.getById(id);
    }

    @Override
    public List<Paciente> getAll() throws Exception {
        return pacienteDAO.getAll();
    }

    /**
     * Busca paciente por DNI exacto.
     */
    public Paciente buscarPorDni(String dni) throws Exception {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede estar vacío");
        }
        return pacienteDAO.buscarPorDni(dni);
    }

    // ============================================================
    //  VALIDACIONES DE NEGOCIO
    // ============================================================

    private void validatePaciente(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser null");
        }
        if (paciente.getNombre() == null || paciente.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (paciente.getApellido() == null || paciente.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido no puede estar vacío");
        }
        if (paciente.getDni() == null || paciente.getDni().trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede estar vacío");
        }
    }

    private void validateDniUnique(String dni, Integer pacienteId) throws Exception {
        Paciente existente = pacienteDAO.buscarPorDni(dni);
        if (existente != null) {
            if (pacienteId == null || existente.getId() != pacienteId) {
                throw new IllegalArgumentException("Ya existe un paciente con el DNI: " + dni);
            }
        }
    }

    /**
     * Retorna el servicio de Historia Clínica asociado a este servicio de Paciente.
     * Permite acceder a las operaciones de historia clínica desde el menú principal.
     */
    public HistoriaClinicaServiceImpl getHistoriaClinicaService() {
        return historiaClinicaService;
    }
}
