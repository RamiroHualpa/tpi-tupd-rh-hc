package Main;

import Models.GrupoSanguineo;
import Models.HistoriaClinica;
import Models.Paciente;
import Service.PacienteServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class MenuHandler {
    private final Scanner scanner;
    private final PacienteServiceImpl pacienteService;

    public MenuHandler(Scanner scanner, PacienteServiceImpl pacienteService) {
        if (scanner == null) throw new IllegalArgumentException("Scanner no puede ser null");
        if (pacienteService == null) throw new IllegalArgumentException("PacienteService no puede ser null");
        this.scanner = scanner;
        this.pacienteService = pacienteService;
    }

    // =====================================
    //  CRUD Pacientes
    // =====================================

    public void crearPaciente() {
        try {
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine().trim();
            System.out.print("Apellido: ");
            String apellido = scanner.nextLine().trim();
            System.out.print("DNI: ");
            String dni = scanner.nextLine().trim();
            System.out.print("Fecha de nacimiento (YYYY-MM-DD): ");
            LocalDate fecha = LocalDate.parse(scanner.nextLine().trim());

            HistoriaClinica hc = null;
            System.out.print("¿Desea registrar una historia clínica para este paciente? (s/n): ");
            if (scanner.nextLine().equalsIgnoreCase("s")) {
                hc = crearHistoriaClinicaInteractiva();
            }

            Paciente p = new Paciente();
            p.setNombre(nombre);
            p.setApellido(apellido);
            p.setDni(dni);
            p.setFechaNacimiento(fecha);
            p.setHistoriaClinica(hc);

            pacienteService.insertar(p);
            System.out.println("Paciente registrado con ID: " + p.getId());
        } catch (Exception e) {
            System.err.println("Error al registrar paciente: " + e.getMessage());
        }
    }

    public void listarPacientes() {
        try {
            List<Paciente> pacientes = pacienteService.getAll();
            if (pacientes.isEmpty()) {
                System.out.println("No hay pacientes registrados.");
                return;
            }

            for (Paciente p : pacientes) {
                System.out.println("ID: " + p.getId() + " | " + p.getNombre() + " " + p.getApellido()
                        + " | DNI: " + p.getDni()
                        + " | Nacimiento: " + p.getFechaNacimiento());
                if (p.getHistoriaClinica() != null) {
                    System.out.println("   HC Nº: " + p.getHistoriaClinica().getNroHistoria()
                            + " | Grupo: " + p.getHistoriaClinica().getGrupoSanguineo().getSimbolo());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al listar pacientes: " + e.getMessage());
        }
    }

    public void actualizarPaciente() {
        try {
            System.out.print("ID del paciente: ");
            int id = Integer.parseInt(scanner.nextLine());
            Paciente p = pacienteService.getById(id);
            if (p == null) {
                System.out.println("Paciente no encontrado.");
                return;
            }

            System.out.print("Nuevo nombre (" + p.getNombre() + "): ");
            String nombre = scanner.nextLine().trim();
            if (!nombre.isEmpty()) p.setNombre(nombre);

            System.out.print("Nuevo apellido (" + p.getApellido() + "): ");
            String apellido = scanner.nextLine().trim();
            if (!apellido.isEmpty()) p.setApellido(apellido);

            System.out.print("Nuevo DNI (" + p.getDni() + "): ");
            String dni = scanner.nextLine().trim();
            if (!dni.isEmpty()) p.setDni(dni);

            pacienteService.actualizar(p);
            System.out.println("Paciente actualizado correctamente.");
        } catch (Exception e) {
            System.err.println("Error al actualizar paciente: " + e.getMessage());
        }
    }

    public void eliminarPaciente() {
        try {
            System.out.print("ID del paciente a eliminar: ");
            int id = Integer.parseInt(scanner.nextLine());
            pacienteService.eliminar(id);
            System.out.println("Paciente eliminado (soft delete).");
        } catch (Exception e) {
            System.err.println("Error al eliminar paciente: " + e.getMessage());
        }
    }

    // =====================================
    //  CRUD Historia Clínica
    // =====================================

    public void crearHistoriaClinica() {
        try {
            HistoriaClinica hc = crearHistoriaClinicaInteractiva();
            pacienteService.getHistoriaClinicaService().insertar(hc);
            System.out.println("Historia clínica creada con ID: " + hc.getId());
        } catch (Exception e) {
            System.err.println("Error al crear historia clínica: " + e.getMessage());
        }
    }

    public void listarHistoriasClinicas() {
        try {
            List<HistoriaClinica> historias = pacienteService.getHistoriaClinicaService().getAll();
            if (historias.isEmpty()) {
                System.out.println("No hay historias clínicas registradas.");
                return;
            }
            for (HistoriaClinica hc : historias) {
                System.out.println("ID: " + hc.getId() + " | Nº: " + hc.getNroHistoria() +
                        " | Grupo: " + hc.getGrupoSanguineo().getSimbolo() +
                        " | PacienteID: " + hc.getPacienteId());
            }
        } catch (Exception e) {
            System.err.println("Error al listar historias clínicas: " + e.getMessage());
        }
    }

    public void actualizarHistoriaClinica() {
        try {
            System.out.print("ID de la historia clínica: ");
            int id = Integer.parseInt(scanner.nextLine());
            HistoriaClinica hc = pacienteService.getHistoriaClinicaService().getById(id);
            if (hc == null) {
                System.out.println("Historia no encontrada.");
                return;
            }

            System.out.print("Nuevo número de historia (" + hc.getNroHistoria() + "): ");
            String nro = scanner.nextLine().trim();
            if (!nro.isEmpty()) hc.setNroHistoria(nro);

            pacienteService.getHistoriaClinicaService().actualizar(hc);
            System.out.println("Historia clínica actualizada correctamente.");
        } catch (Exception e) {
            System.err.println("Error al actualizar historia clínica: " + e.getMessage());
        }
    }

    public void eliminarHistoriaClinica() {
        try {
            System.out.print("ID de la historia clínica a eliminar: ");
            int id = Integer.parseInt(scanner.nextLine());
            pacienteService.getHistoriaClinicaService().eliminar(id);
            System.out.println("Historia clínica eliminada (soft delete).");
        } catch (Exception e) {
            System.err.println("Error al eliminar historia clínica: " + e.getMessage());
        }
    }

    // =====================================
    //  Búsquedas
    // =====================================

    public void buscarPacientePorDni() {
        try {
            System.out.print("Ingrese DNI: ");
            String dni = scanner.nextLine().trim();
            Paciente p = pacienteService.buscarPorDni(dni);
            if (p == null) {
                System.out.println("No se encontró paciente con ese DNI.");
                return;
            }
            System.out.println(p.getNombre() + " " + p.getApellido() + " - HC: " +
                    (p.getHistoriaClinica() != null ? p.getHistoriaClinica().getNroHistoria() : "Sin historia"));
        } catch (Exception e) {
            System.err.println("Error al buscar paciente: " + e.getMessage());
        }
    }

    public void buscarHistoriaPorNumero() {
        try {
            System.out.print("Número de historia clínica: ");
            String nro = scanner.nextLine().trim();
            HistoriaClinica hc = pacienteService.getHistoriaClinicaService().buscarPorNroHistoria(nro);
            if (hc == null) {
                System.out.println("No se encontró historia clínica con ese número.");
                return;
            }
            System.out.println("Historia " + hc.getNroHistoria() + " | Grupo: " +
                    hc.getGrupoSanguineo().getSimbolo() + " | PacienteID: " + hc.getPacienteId());
        } catch (Exception e) {
            System.err.println("Error al buscar historia clínica: " + e.getMessage());
        }
    }

    // =====================================
    //  Métodos auxiliares
    // =====================================

    private HistoriaClinica crearHistoriaClinicaInteractiva() {
        System.out.print("Número de historia: ");
        String nro = scanner.nextLine().trim();

        System.out.println("Seleccione grupo sanguíneo:");
        for (GrupoSanguineo gs : GrupoSanguineo.values()) {
            System.out.println("- " + gs.name() + " (" + gs.getSimbolo() + ")");
        }
        System.out.print("Ingrese tipo: ");
        GrupoSanguineo grupo = GrupoSanguineo.valueOf(scanner.nextLine().trim().toUpperCase());

        System.out.print("Antecedentes: ");
        String ant = scanner.nextLine().trim();
        System.out.print("Medicación actual: ");
        String med = scanner.nextLine().trim();
        System.out.print("Observaciones: ");
        String obs = scanner.nextLine().trim();

        HistoriaClinica hc = new HistoriaClinica();
        hc.setNroHistoria(nro);
        hc.setGrupoSanguineo(grupo);
        hc.setAntecedentes(ant);
        hc.setMedaicacionActual(med);
        hc.setObservaciones(obs);
        return hc;
    }
}
