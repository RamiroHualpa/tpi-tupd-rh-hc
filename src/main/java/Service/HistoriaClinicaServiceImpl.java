package Service;

import Dao.HistoriaClinicaDAO;
import Models.HistoriaClinica;
import Models.GrupoSanguineo;

import java.sql.Connection;
import java.util.List;

import Config.DatabaseConnection;

/**
 * Servicio de negocio para la entidad HistoriaClinica.
 *
 * Responsabilidades:
 * - Validar datos de la historia clínica antes de persistir.
 * - Garantizar unicidad del número de historia (nroHistoria).
 * - Gestionar las operaciones CRUD delegando al DAO.
 * - Coordinar inserciones transaccionales cuando se llama desde PacienteServiceImpl.
 *
 * Patrón: Service Layer con validaciones de negocio y manejo de transacciones.
 */
public class HistoriaClinicaServiceImpl implements GenericService<HistoriaClinica> {

    private final HistoriaClinicaDAO historiaClinicaDAO;

    public HistoriaClinicaServiceImpl(HistoriaClinicaDAO historiaClinicaDAO) {
        if (historiaClinicaDAO == null) {
            throw new IllegalArgumentException("HistoriaClinicaDAO no puede ser null");
        }
        this.historiaClinicaDAO = historiaClinicaDAO;
    }

    @Override
    public void insertar(HistoriaClinica hc) throws Exception {
        validateHistoriaClinica(hc);
        validateNroHistoriaUnique(hc.getNroHistoria(), null);
        historiaClinicaDAO.insertar(hc);
    }

    @Override
    public void actualizar(HistoriaClinica hc) throws Exception {
        validateHistoriaClinica(hc);
        if (hc.getId() <= 0) {
            throw new IllegalArgumentException("El ID de la historia clínica debe ser mayor a 0 para actualizar");
        }
        validateNroHistoriaUnique(hc.getNroHistoria(), hc.getId());
        historiaClinicaDAO.actualizar(hc);
    }

    @Override
    public void eliminar(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        historiaClinicaDAO.eliminar(id);
    }

    @Override
    public HistoriaClinica getById(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        return historiaClinicaDAO.getById(id);
    }

    @Override
    public List<HistoriaClinica> getAll() throws Exception {
        return historiaClinicaDAO.getAll();
    }

    /**
     * Inserta una historia clínica en una transacción externa.
     *
     * Usado por PacienteServiceImpl.insertar() para coordinar el alta de ambas entidades.
     */
    public void insertTx(HistoriaClinica hc, Connection conn) throws Exception {
        validateHistoriaClinica(hc);
        validateNroHistoriaUnique(hc.getNroHistoria(), null);
        historiaClinicaDAO.insertTx(hc, conn);
    }

    /**
     * Busca una historia clínica por número.
     */
    public HistoriaClinica buscarPorNroHistoria(String nro) throws Exception {
        if (nro == null || nro.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de historia no puede estar vacío");
        }
        return historiaClinicaDAO.buscarPorNroHistoria(nro);
    }

    /**
     * Valida que la historia tenga datos coherentes.
     */
    private void validateHistoriaClinica(HistoriaClinica hc) {
        if (hc == null) {
            throw new IllegalArgumentException("La historia clínica no puede ser null");
        }
        if (hc.getNroHistoria() == null || hc.getNroHistoria().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de historia no puede estar vacío");
        }
        if (hc.getGrupoSanguineo() == null) {
            throw new IllegalArgumentException("El grupo sanguíneo es obligatorio");
        }
    }

    /**
     * Garantiza que el número de historia clínica sea único.
     */
    private void validateNroHistoriaUnique(String nro, Integer hcId) throws Exception {
        HistoriaClinica existente = historiaClinicaDAO.buscarPorNroHistoria(nro);
        if (existente != null) {
            if (hcId == null || existente.getId() != hcId) {
                throw new IllegalArgumentException("Ya existe una historia clínica con el número: " + nro);
            }
        }
    }
}
