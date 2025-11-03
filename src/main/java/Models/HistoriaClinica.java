package Models;

import java.util.Objects;

public class HistoriaClinica extends Base{
    private String nroHistoria;
    private GrupoSanguineo grupoSanguineo;
    private String antecedentes;
    private String medaicacionActual;
    private String observaciones;

    // Campo auxiliar para que el DAO/Service maneje la FK del Paciente (en la tabla HC)
    // Se mantiene como Long para mapear al ID del Paciente en la BD (BIGINT),
    // aunque el ID propio de HC sea 'int' por herencia de Base.
    private Long pacienteId;

    public HistoriaClinica(int id, boolean eliminado, String nroHistoria, GrupoSanguineo grupoSanguineo, String antecedentes, String medaicacionActual, String observaciones, Long pacienteId) {
        super(id, eliminado);
        this.nroHistoria = nroHistoria;
        this.grupoSanguineo = grupoSanguineo;
        this.antecedentes = antecedentes;
        this.medaicacionActual = medaicacionActual;
        this.observaciones = observaciones;
        this.pacienteId = pacienteId;
    }

    public HistoriaClinica() {
        super();
    }

    public String getNroHistoria() {
        return nroHistoria;
    }

    public void setNroHistoria(String nroHistoria) {
        this.nroHistoria = nroHistoria;
    }

    public GrupoSanguineo getGrupoSanguineo() {
        return grupoSanguineo;
    }

    public void setGrupoSanguineo(GrupoSanguineo grupoSanguineo) {
        this.grupoSanguineo = grupoSanguineo;
    }

    public String getAntecedentes() {
        return antecedentes;
    }

    public void setAntecedentes(String antecedentes) {
        this.antecedentes = antecedentes;
    }

    public String getMedaicacionActual() {
        return medaicacionActual;
    }

    public void setMedaicacionActual(String medaicacionActual) {
        this.medaicacionActual = medaicacionActual;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HistoriaClinica)) return false;
        HistoriaClinica other = (HistoriaClinica) obj;

        if (getId() != 0 && other.getId() != 0) {
            return getId() == other.getId();
        }

        return Objects.equals(nroHistoria, other.nroHistoria);
    }

    @Override
    public int hashCode() {
        return getId() != 0 ? Objects.hash(getId()) : Objects.hash(nroHistoria);
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Historia Clínica [");
        sb.append("ID: ").append(getId());
        sb.append(", Nro: ").append(nroHistoria);
        sb.append(", Grupo Sanguíneo: ").append(grupoSanguineo);
        sb.append(", Antecedentes: ").append(antecedentes != null && !antecedentes.isBlank() ? antecedentes : "Ninguno");
        sb.append(", Medicación Actual: ").append(medaicacionActual != null && !medaicacionActual.isBlank() ? medaicacionActual : "Ninguna");
        sb.append(", Observaciones: ").append(observaciones != null && !observaciones.isBlank() ? observaciones : "Sin observaciones");
        sb.append(", Paciente ID: ").append(pacienteId != null ? pacienteId : "No asignado");
        sb.append(", Eliminado: ").append(isEliminado());
        sb.append("]");
        return sb.toString();
    }
}

