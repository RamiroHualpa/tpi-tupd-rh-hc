package Dao;

import Config.DatabaseConnection;
import Models.HistoriaClinica;
import Models.Paciente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO implements GenericDAO<Paciente> {

    private static final String INSERT_SQL = """
        INSERT INTO paciente (nombre, apellido, dni, fecha_nacimiento, telefono)
        VALUES (?, ?, ?, ?, ?)
    """;

    private static final String UPDATE_SQL = """
        UPDATE paciente SET nombre = ?, apellido = ?, dni = ?, fecha_nacimiento = ?, telefono = ?
        WHERE id = ? AND eliminado = FALSE
    """;

    private static final String DELETE_SQL = "UPDATE paciente SET eliminado = TRUE WHERE id = ?";

    private static final String SELECT_BY_ID_SQL = """
        SELECT p.id, p.nombre, p.apellido, p.dni, p.fecha_nacimiento, p.telefono,
               h.id AS hc_id, h.nro_historia, h.grupo_sanguineo, h.antecedentes, h.medicacion_actual, h.observaciones
        FROM paciente p
        LEFT JOIN historia_clinica h ON h.paciente_id = p.id AND h.eliminado = FALSE
        WHERE p.id = ? AND p.eliminado = FALSE
    """;

    private static final String SELECT_ALL_SQL = """
        SELECT p.id, p.nombre, p.apellido, p.dni, p.fecha_nacimiento, p.telefono,
               h.id AS hc_id, h.nro_historia, h.grupo_sanguineo, h.antecedentes, h.medicacion_actual, h.observaciones
        FROM paciente p
        LEFT JOIN historia_clinica h ON h.paciente_id = p.id AND h.eliminado = FALSE
        WHERE p.eliminado = FALSE
        ORDER BY p.id
    """;

    private static final String SEARCH_BY_DNI_SQL = """
        SELECT p.id, p.nombre, p.apellido, p.dni, p.fecha_nacimiento, p.telefono,
               h.id AS hc_id, h.nro_historia, h.grupo_sanguineo, h.antecedentes, h.medicacion_actual, h.observaciones
        FROM paciente p
        LEFT JOIN historia_clinica h ON h.paciente_id = p.id AND h.eliminado = FALSE
        WHERE p.eliminado = FALSE AND p.dni = ?
    """;

    private final HistoriaClinicaDAO historiaClinicaDAO;

    // ============================
    // Constructor
    // ============================
    public PacienteDAO(HistoriaClinicaDAO historiaClinicaDAO) {
        if (historiaClinicaDAO == null)
            throw new IllegalArgumentException("HistoriaClinicaDAO no puede ser null");
        this.historiaClinicaDAO = historiaClinicaDAO;
    }
    @Override
    public void insertar(Paciente paciente) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, paciente.getNombre());
            stmt.setString(2, paciente.getApellido());
            stmt.setString(3, paciente.getDni());
            if (paciente.getFechaNacimiento() != null) stmt.setDate(4, Date.valueOf(paciente.getFechaNacimiento()));
            else stmt.setNull(4, Types.DATE);
            stmt.setString(5, null); // si no usas telefono en modelo, ajustar
            stmt.executeUpdate();
            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) paciente.setId(gk.getInt(1));
                else throw new SQLException("No se generó id para paciente");
            }
        }
    }

    @Override
    public void insertTx(Paciente paciente, Connection conn) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, paciente.getNombre());
            stmt.setString(2, paciente.getApellido());
            stmt.setString(3, paciente.getDni());
            if (paciente.getFechaNacimiento() != null) stmt.setDate(4, Date.valueOf(paciente.getFechaNacimiento()));
            else stmt.setNull(4, Types.DATE);
            stmt.setString(5, null);
            stmt.executeUpdate();
            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) paciente.setId(gk.getInt(1));
                else throw new SQLException("No se generó id para paciente (tx)");
            }
        }
    }

    @Override
    public void actualizar(Paciente paciente) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, paciente.getNombre());
            stmt.setString(2, paciente.getApellido());
            stmt.setString(3, paciente.getDni());
            if (paciente.getFechaNacimiento() != null) stmt.setDate(4, Date.valueOf(paciente.getFechaNacimiento()));
            else stmt.setNull(4, Types.DATE);
            stmt.setString(5, null); // telefono
            stmt.setInt(6, paciente.getId());

            int affected = stmt.executeUpdate();
            if (affected == 0) throw new SQLException("No se actualizó paciente id=" + paciente.getId());
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            if (affected == 0) throw new SQLException("No se encontró paciente id=" + id);
        }
    }

    @Override
    public Paciente getById(int id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToPaciente(rs);
            }
        }
        return null;
    }

    @Override
    public List<Paciente> getAll() throws Exception {
        List<Paciente> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SELECT_ALL_SQL)) {
            while (rs.next()) list.add(mapResultSetToPaciente(rs));
        }
        return list;
    }

    public Paciente buscarPorDni(String dni) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_BY_DNI_SQL)) {
            stmt.setString(1, dni.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToPaciente(rs);
            }
        }
        return null;
    }

    private Paciente mapResultSetToPaciente(ResultSet rs) throws SQLException {
        Paciente p = new Paciente();
        p.setId(rs.getInt("id"));
        p.setNombre(rs.getString("nombre"));
        p.setApellido(rs.getString("apellido"));
        p.setDni(rs.getString("dni"));
        Date d = rs.getDate("fecha_nacimiento");
        if (d != null) p.setFechaNacimiento(d.toLocalDate());

        int hcId = rs.getInt("hc_id");
        if (!rs.wasNull() && hcId > 0) {
            HistoriaClinica hc = new HistoriaClinica();
            hc.setId(hcId);
            hc.setNroHistoria(rs.getString("nro_historia"));
            hc.setAntecedentes(rs.getString("antecedentes"));
            hc.setMedaicacionActual(rs.getString("medicacion_actual"));
            hc.setObservaciones(rs.getString("observaciones"));
            String grupo = rs.getString("grupo_sanguineo");
            if (grupo != null) {
                for (Models.GrupoSanguineo g : Models.GrupoSanguineo.values()) {
                    if (g.getSimbolo().equalsIgnoreCase(grupo)) { hc.setGrupoSanguineo(g); break; }
                }
            }
            p.setHistoriaClinica(hc);
        }
        return p;
    }
}
