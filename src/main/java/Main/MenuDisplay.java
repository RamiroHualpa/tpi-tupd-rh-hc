package Main;

/**
 * Clase utilitaria para mostrar el menú principal.
 */
public class MenuDisplay {
    public static void mostrarMenuPrincipal() {
        System.out.println("\n========= MENU PRINCIPAL =========");
        System.out.println("1. Registrar nuevo paciente");
        System.out.println("2. Listar pacientes");
        System.out.println("3. Actualizar datos de paciente");
        System.out.println("4. Eliminar paciente");
        System.out.println("----------------------------------");
        System.out.println("5. Registrar historia clínica");
        System.out.println("6. Listar historias clínicas");
        System.out.println("7. Actualizar historia clínica");
        System.out.println("8. Eliminar historia clínica");
        System.out.println("----------------------------------");
        System.out.println("9. Buscar paciente por DNI");
        System.out.println("10. Buscar historia clínica por número");
        System.out.println("0. Salir");
        System.out.print("Ingrese una opción: ");
    }
}
