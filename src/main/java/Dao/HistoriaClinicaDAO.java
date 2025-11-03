package Dao;

import Config.DatabaseConnection;
import Models.GrupoSanguineo;
import Models.HistoriaClinica;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para HistoriaClinica - actualizado para mapear todos los atributos del modelo:
 * nro_historia, grupo_sanguineo, antecedentes, medicacion_actual, observaciones, paciente_id, fecha_creacion, eliminado
 */
public class HistoriaClinicaDAO implements GenericDAO<HistoriaClinica> {

    private static final String INSERT_SQL = """
        INSERT INTO historia_clinica
        (nro_historia, grupo_sanguineo, antecedentes, medicacion_actual, observaciones, paciente_id)
        VALUES (?, ?, ?, ?, ?, ?)
    """;

    private static final String UPDATE_SQL = """
        UPDATE historia_clinica
        SET nro_historia = ?, grupo_sanguineo = ?, antecedentes = ?, medicacion_actual = ?, observaciones = ?, paciente_id = ?
        WHERE id = ? AND eliminado = FALSE
    """;

    private static final String DELETE_SQL = "UPDATE historia_clinica SET eliminado = TRUE WHERE id = ?";

    private static final String SELECT_BY_ID_SQL = """
        SELECT id, nro_historia, grupo_sanguineo, antecedentes, medicacion_actual, observaciones, paciente_id, fecha_creacion, eliminado
        FROM historia_clinica
        WHERE id = ? AND eliminado = FALSE
    """;

    private static final String SELECT_ALL_SQL = """
        SELECT id, nro_historia, grupo_sanguineo, antecedentes, medicacion_actual, observaciones, paciente_id, fecha_creacion, eliminado
        FROM historia_clinica
        WHERE eliminado = FALSE
        ORDER BY id
    """;

    private static final String SEARCH_BY_NRO_SQL = """
        SELECT id, nro_historia, grupo_sanguineo, antecedentes, medicacion_actual, observaciones, paciente_id, fecha_creacion, eliminado
        FROM historia_clinica
        WHERE eliminado = FALSE AND nro_historia = ?
    """;

    private static final String SEARCH_BY_PACIENTE_SQL = """
        SELECT id, nro_historia, grupo_sanguineo, antecedentes, medicacion_actual, observaciones, paciente_id, fecha_creacion, eliminado
        FROM historia_clinica
        WHERE eliminado = FALSE AND paciente_id = ?
    """;

    @Override
    public void insertar(HistoriaClinica hc) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(stmt, hc);
            stmt.executeUpdate();
            setGeneratedId(stmt, hc);
        }
    }

    @Override
    public void insertTx(HistoriaClinica hc, Connection conn) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(stmt, hc);
            stmt.executeUpdate();
            setGeneratedId(stmt, hc);
        }
    }

    @Override
    public void actualizar(HistoriaClinica hc) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, hc.getNroHistoria());
            stmt.setString(2, hc.getGrupoSanguineo() != null ? hc.getGrupoSanguineo().getSimbolo() : null);
            stmt.setString(3, hc.getAntecedentes());
            stmt.setString(4, hc.getMedaicacionActual());
            stmt.setString(5, hc.getObservaciones());
            if (hc.getPacienteId() != null && hc.getPacienteId() > 0) {
                stmt.setLong(6, hc.getPacienteId());
            } else {
                stmt.setNull(6, Types.BIGINT);
            }
            stmt.setInt(7, hc.getId());

            int affected = stmt.executeUpdate();
            if (affected == 0) throw new SQLException("No se actualizó historia clínica (id=" + hc.getId() + ")");
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            if (affected == 0) throw new SQLException("No se encontró historia clínica con id=" + id);
        }
    }

    @Override
    public HistoriaClinica getById(int id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSet(rs);
            }
        }
        return null;
    }

    @Override
    public List<HistoriaClinica> getAll() throws Exception {
        List<HistoriaClinica> result = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SELECT_ALL_SQL)) {
            while (rs.next()) result.add(mapResultSet(rs));
        }
        return result;
    }

    public HistoriaClinica buscarPorNroHistoria(String nro) throws Exception {
        if (nro == null || nro.trim().isEmpty()) throw new IllegalArgumentException("nroHistoria vacío");
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_BY_NRO_SQL)) {
            stmt.setString(1, nro.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSet(rs);
            }
        }
        return null;
    }

    public HistoriaClinica buscarPorPacienteId(long pacienteId) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_BY_PACIENTE_SQL)) {
            stmt.setLong(1, pacienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSet(rs);
            }
        }
        return null;
    }

    /* helpers */
    private void setParameters(PreparedStatement stmt, HistoriaClinica hc) throws SQLException {
        stmt.setString(1, hc.getNroHistoria());
        stmt.setString(2, hc.getGrupoSanguineo() != null ? hc.getGrupoSanguineo().getSimbolo() : null);
        stmt.setString(3, hc.getAntecedentes());
        stmt.setString(4, hc.getMedaicacionActual());
        stmt.setString(5, hc.getObservaciones());
        if (hc.getPacienteId() != null && hc.getPacienteId() > 0) stmt.setLong(6, hc.getPacienteId());
        else stmt.setNull(6, Types.BIGINT);
    }

    private void setGeneratedId(PreparedStatement stmt, HistoriaClinica hc) throws SQLException {
        try (ResultSet gk = stmt.getGeneratedKeys()) {
            if (gk.next()) hc.setId(gk.getInt(1));
            else throw new SQLException("No se generó id para historia_clinica");
        }
    }

    private HistoriaClinica mapResultSet(ResultSet rs) throws SQLException {
        HistoriaClinica hc = new HistoriaClinica();
        hc.setId(rs.getInt("id"));
        hc.setNroHistoria(rs.getString("nro_historia"));
        hc.setAntecedentes(rs.getString("antecedentes"));
        hc.setMedaicacionActual(rs.getString("medicacion_actual"));
        hc.setObservaciones(rs.getString("observaciones"));
        long pid = rs.getLong("paciente_id");
        if (!rs.wasNull()) hc.setPacienteId(pid);
        String grupo = rs.getString("grupo_sanguineo");
        if (grupo != null) {
            for (GrupoSanguineo g : GrupoSanguineo.values()) {
                if (g.getSimbolo().equalsIgnoreCase(grupo)) { hc.setGrupoSanguineo(g); break; }
            }
        }
        return hc;
    }
}
