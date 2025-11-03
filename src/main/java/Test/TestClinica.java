package Test;

import Dao.HistoriaClinicaDAO;
import Dao.PacienteDAO;
import Models.HistoriaClinica;
import Models.Paciente;
import Models.GrupoSanguineo;
import Service.HistoriaClinicaServiceImpl;
import Service.PacienteServiceImpl;

import java.util.List;

public class TestClinica {

    public static void main(String[] args) {

        try {
            // =====================================================
            // Inicialización de DAOs y Servicios
            // =====================================================
            HistoriaClinicaDAO historiaDAO = new HistoriaClinicaDAO();
            PacienteDAO pacienteDAO = new PacienteDAO(historiaDAO);

            HistoriaClinicaServiceImpl historiaService = new HistoriaClinicaServiceImpl(historiaDAO);
            PacienteServiceImpl pacienteService = new PacienteServiceImpl(pacienteDAO, historiaService);

            System.out.println(" Conexión y servicios inicializados correctamente.");

            // =====================================================
            //  Listar todas las Historias Clínicas
            // =====================================================
            System.out.println("\n Historias Clínicas existentes:");
            List<HistoriaClinica> historias = historiaService.getAll();
            historias.forEach(System.out::println);

            // =====================================================
            //  Listar todos los Pacientes
            // =====================================================
            System.out.println("\n Pacientes registrados:");
            List<Paciente> pacientes = pacienteService.getAll();
            pacientes.forEach(System.out::println);

            // =====================================================
            //  Insertar un nuevo paciente con historia clínica
            // =====================================================
            HistoriaClinica nuevaHC = new HistoriaClinica();
            nuevaHC.setNroHistoria("HC-011");
            nuevaHC.setGrupoSanguineo(GrupoSanguineo.O_NEGATIVO);
            nuevaHC.setObservaciones("Chequeo inicial - sin antecedentes");

            Paciente nuevoPaciente = new Paciente();
            nuevoPaciente.setNombre("Federico");
            nuevoPaciente.setApellido("Méndez");
            nuevoPaciente.setDni("31111222");
            nuevoPaciente.setHistoriaClinica(nuevaHC);

            pacienteService.insertar(nuevoPaciente);
            System.out.println("\n Paciente insertado con éxito: " + nuevoPaciente);

            // =====================================================
            //  Buscar por DNI
            // =====================================================
            System.out.println("\n Buscando paciente por DNI 31111222...");
            Paciente encontrado = pacienteService.buscarPorDni("31111222");
            System.out.println("Resultado: " + encontrado);

            // =====================================================
            //  Buscar historia clínica por número
            // =====================================================
            System.out.println("\n Buscando historia clínica 'HC-011'...");
            HistoriaClinica hcEncontrada = historiaService.buscarPorNroHistoria("HC-011");
            System.out.println("Resultado: " + hcEncontrada);

            // =====================================================
            //  Actualizar observaciones
            // =====================================================
            System.out.println("\n Actualizando observaciones de la historia...");
            hcEncontrada.setObservaciones("Actualización post control médico");
            historiaService.actualizar(hcEncontrada);

            // =====================================================
            //  Eliminar (soft delete) paciente
            // =====================================================
            System.out.println("\n Eliminando paciente insertado...");
            pacienteService.eliminar(nuevoPaciente.getId());
            System.out.println("Paciente eliminado correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(" Error durante la prueba: " + e.getMessage());
        }
    }
}
