package Main;

import Dao.HistoriaClinicaDAO;
import Dao.PacienteDAO;
import Service.HistoriaClinicaServiceImpl;
import Service.PacienteServiceImpl;

import java.util.Scanner;

/**
 * Orquestador principal del menú de la aplicación.
 * Gestiona el ciclo de vida del menú y coordina todas las dependencias.
 *
 * Patrón: Application Controller + Dependency Injection manual.
 * Arquitectura: Main → Service → DAO → Models.
 */
public class AppMenu {
    private final Scanner scanner;
    private final MenuHandler menuHandler;
    private boolean running;

    public AppMenu() {
        this.scanner = new Scanner(System.in);
        PacienteServiceImpl pacienteService = createPacienteService();
        this.menuHandler = new MenuHandler(scanner, pacienteService);
        this.running = true;
    }

    public static void main(String[] args) {
        AppMenu app = new AppMenu();
        app.run();
    }

    public void run() {
        while (running) {
            try {
                MenuDisplay.mostrarMenuPrincipal();
                int opcion = Integer.parseInt(scanner.nextLine());
                processOption(opcion);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
            }
        }
        scanner.close();
    }

    private void processOption(int opcion) {
        switch (opcion) {
            case 1 -> menuHandler.crearPaciente();
            case 2 -> menuHandler.listarPacientes();
            case 3 -> menuHandler.actualizarPaciente();
            case 4 -> menuHandler.eliminarPaciente();
            case 5 -> menuHandler.crearHistoriaClinica();
            case 6 -> menuHandler.listarHistoriasClinicas();
            case 7 -> menuHandler.actualizarHistoriaClinica();
            case 8 -> menuHandler.eliminarHistoriaClinica();
            case 9 -> menuHandler.buscarPacientePorDni();
            case 10 -> menuHandler.buscarHistoriaPorNumero();
            case 0 -> {
                System.out.println("Saliendo...");
                running = false;
            }
            default -> System.out.println("Opción no válida.");
        }
    }

    /**
     * Inyección de dependencias manual:
     * HistoriaClinicaDAO → PacienteDAO → HistoriaClinicaService → PacienteService
     */
    private PacienteServiceImpl createPacienteService() {
        HistoriaClinicaDAO hcDAO = new HistoriaClinicaDAO();
        PacienteDAO pacienteDAO = new PacienteDAO(hcDAO);
        HistoriaClinicaServiceImpl hcService = new HistoriaClinicaServiceImpl(hcDAO);
        return new PacienteServiceImpl(pacienteDAO, hcService);
    }
}
