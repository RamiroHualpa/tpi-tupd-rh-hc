package Models;

import java.time.LocalDate;
import java.util.Objects;

public class Paciente extends Base{
    private String nombre;
    private String apellido;
    private String dni;
    private LocalDate fechaNacimiento;
    private HistoriaClinica historiaClinica;

    public Paciente(int id, boolean eliminado, String nombre, String apellido, String dni, LocalDate fechaNacimiento, HistoriaClinica historiaClinica) {
        super(id, eliminado);
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
        this.historiaClinica = historiaClinica;
    }

    public Paciente() {
        super();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public HistoriaClinica getHistoriaClinica() {
        return historiaClinica;
    }

    public void setHistoriaClinica(HistoriaClinica historiaClinica) {
        this.historiaClinica = historiaClinica;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Paciente)) return false;
        Paciente other = (Paciente) obj;

        // Si ambos tienen ID asignado, se comparan por ID
        if (getId() != 0 && other.getId() != 0) {
            return getId() == other.getId();
        }

        // Si no tienen ID, se comparan por DNI (campo único)
        return Objects.equals(dni, other.dni);
    }

    @Override
    public int hashCode() {
        // Si hay ID, se usa para el hash; si no, el DNI
        return getId() != 0 ? Objects.hash(getId()) : Objects.hash(dni);
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Paciente [");
        sb.append("ID: ").append(getId());
        sb.append(", Nombre: ").append(nombre);
        sb.append(", Apellido: ").append(apellido);
        sb.append(", DNI: ").append(dni);
        sb.append(", Fecha de Nacimiento: ").append(fechaNacimiento);
        sb.append(", Eliminado: ").append(isEliminado());
        if (historiaClinica != null) {
            sb.append(", Historia Clínica: { Nro: ").append(historiaClinica.getNroHistoria());
            sb.append(", Grupo Sanguíneo: ").append(historiaClinica.getGrupoSanguineo());
            sb.append(" }");
        } else {
            sb.append(", Historia Clínica: No asignada");
        }
        sb.append("]");
        return sb.toString();
    }

}
