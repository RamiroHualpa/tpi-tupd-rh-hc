# 🏥 Historias de Usuario - Sistema de Gestión de Pacientes y Historias Clínicas

Especificaciones funcionales completas del sistema CRUD de **pacientes** y sus **historias clínicas**.

## Tabla de Contenidos

-   [Épica 1: Gestión de Pacientes](#%C3%A9pica-1-gesti%C3%B3n-de-pacientes)

-   [Épica 2: Gestión de Historias Clínicas](#%C3%A9pica-2-gesti%C3%B3n-de-historias-cl%C3%ADnicas)

-   [Épica 3: Operaciones Asociadas](#%C3%A9pica-3-operaciones-asociadas)

-   [Reglas de Negocio](#reglas-de-negocio)

-   [Modelo de Datos](#modelo-de-datos)


## Épica 1: Gestión de Pacientes

### HU-001: Crear Paciente

**Como** usuario del sistema  
**Quiero** crear un registro de paciente con sus datos personales  
**Para** almacenarlo en la base de datos junto con su historia clínica (si aplica)

#### Criterios de Aceptación

`Escenario: Crear paciente sin historia clínica   Dado que el usuario selecciona "Crear paciente"   Cuando ingresa nombre "Juan", apellido "Pérez", DNI "12345678"   Y responde "n" a agregar historia clínica   Entonces el sistema crea el paciente con ID autogenerado   Y muestra "Paciente creado exitosamente con ID: X"  Escenario: Crear paciente con historia clínica   Dado que el usuario selecciona "Crear paciente"   Cuando ingresa nombre "María", apellido "González", DNI "87654321"   Y responde "s" a agregar historia clínica   Y completa número de historia "HC-001" y grupo sanguíneo "A+"   Entonces el sistema crea primero la historia clínica   Y luego crea el paciente asociado   Y muestra "Paciente creado exitosamente con historia clínica vinculada"  Escenario: Intento de crear paciente con DNI duplicado   Dado que existe un paciente con DNI "12345678"   Cuando el usuario intenta crear otro con el mismo DNI   Entonces el sistema muestra "Ya existe un paciente con el DNI: 12345678"   Y no crea el registro`

#### Reglas de Negocio Aplicables

-   **RN-001**: Nombre, apellido y DNI son obligatorios

-   **RN-002**: El DNI debe ser único

-   **RN-003**: El campo historia clínica es opcional

-   **RN-004**: IDs se generan automáticamente

-   **RN-005**: Validaciones de negocio antes de insertar


#### Implementación Técnica

-   **Clase**: `MenuHandler.crearPaciente()`

-   **Servicio**: `PacienteServiceImpl.insertar()`

-   **DAO**: `PacienteDAO.insertar()`

-   **Validaciones**: `validatePaciente()` y `validateDniUnique()`

-   **Flujo**:

    1.  Captura datos y los normaliza con `.trim()`

    2.  Crea el objeto `Paciente`

    3.  Si se agrega historia clínica, la inserta primero (transacción)

    4.  Inserta paciente con FK a `historia_clinica.paciente_id`

    5.  Muestra confirmación


### HU-002: Listar Todos los Pacientes

**Como** usuario del sistema  
**Quiero** ver todos los pacientes registrados  
**Para** consultar su información y su historia clínica asociada

#### Criterios de Aceptación

`Escenario: Listar pacientes con historia clínica   Dado que existen pacientes en el sistema   Cuando el usuario selecciona "Listar pacientes"   Entonces se muestran nombre, apellido, DNI y número de historia si existe  Escenario: Listar pacientes sin historia clínica   Dado que algunos pacientes no tienen historia clínica asociada   Cuando se listan todos   Entonces se muestra "Sin historia clínica" en su fila  Escenario: No hay pacientes activos   Dado que no existen pacientes con eliminado = FALSE   Cuando el usuario intenta listar   Entonces el sistema muestra "No se encontraron pacientes."`

#### Reglas de Negocio Aplicables

-   **RN-006**: Solo se listan pacientes con `eliminado = FALSE`

-   **RN-007**: Se usa `LEFT JOIN` con historia\_clinica

-   **RN-008**: Si no hay HC, se muestra mensaje correspondiente


#### Implementación Técnica

-   **Clase**: `MenuHandler.listarPacientes()`

-   **Servicio**: `PacienteServiceImpl.getAll()`

-   **DAO**: `PacienteDAO.getAll()` con `SELECT_ALL_SQL`


### HU-003: Buscar Paciente por DNI

**Como** usuario del sistema  
**Quiero** buscar un paciente por su número de DNI  
**Para** encontrar su registro fácilmente

#### Criterios de Aceptación

`Escenario: Buscar paciente existente   Dado que existe un paciente con DNI "12345678"   Cuando el usuario busca ese DNI   Entonces el sistema muestra los datos completos del paciente  Escenario: Buscar paciente inexistente   Dado que no existe ningún paciente con DNI "99999999"   Cuando el usuario busca ese DNI   Entonces el sistema muestra "No se encontró paciente con DNI: 99999999"`

#### Reglas de Negocio Aplicables

-   **RN-009**: La búsqueda requiere un DNI válido

-   **RN-010**: Se eliminan espacios en blanco

-   **RN-011**: Se usa búsqueda exacta (no LIKE)

-   **RN-012**: No se permiten búsquedas vacías


#### Implementación Técnica

-   **Clase**: `MenuHandler.buscarPacientePorDni()`

-   **Servicio**: `PacienteServiceImpl.buscarPorDni()`

-   **DAO**: `PacienteDAO.buscarPorDni()`


### HU-004: Actualizar Paciente

**Como** usuario del sistema  
**Quiero** modificar los datos personales de un paciente  
**Para** mantener la información actualizada

#### Criterios de Aceptación

`Escenario: Actualizar apellido y teléfono   Dado que existe paciente ID 1   Cuando el usuario ingresa nuevo apellido y teléfono   Entonces el sistema actualiza esos campos   Y mantiene el resto sin cambios  Escenario: Actualizar DNI duplicado   Dado que existen dos pacientes   Cuando intento cambiar el DNI de uno a un valor ya existente   Entonces se muestra "Ya existe un paciente con ese DNI"   Y no se actualiza el registro`

#### Reglas de Negocio Aplicables

-   **RN-013**: Se valida DNI único excepto para el mismo paciente

-   **RN-014**: Campos vacíos mantienen valores originales

-   **RN-015**: ID > 0 es obligatorio para actualizar


#### Implementación Técnica

-   **Clase**: `MenuHandler.actualizarPaciente()`

-   **Servicio**: `PacienteServiceImpl.actualizar()`

-   **Validaciones**: `validateDniUnique(dni, id)`


### HU-005: Eliminar Paciente

**Como** usuario del sistema  
**Quiero** eliminar un paciente del sistema  
**Para** mantener solo los registros activos

#### Criterios de Aceptación

`Escenario: Eliminar paciente existente   Dado que existe paciente con ID 1   Cuando el usuario elimina el paciente   Entonces el sistema marca eliminado = TRUE   Y muestra "Paciente eliminado exitosamente."  Escenario: Paciente inexistente   Dado que el ID ingresado no corresponde a ningún paciente   Entonces se muestra "No se encontró paciente con ID: X"`

#### Reglas de Negocio Aplicables

-   **RN-016**: Eliminación es lógica

-   **RN-017**: Se valida ID antes de eliminar

-   **RN-018**: Historias clínicas asociadas no se eliminan automáticamente


#### Implementación Técnica

-   **Clase**: `MenuHandler.eliminarPaciente()`

-   **Servicio**: `PacienteServiceImpl.eliminar()`

-   **DAO**: `PacienteDAO.eliminar()`


## Épica 2: Gestión de Historias Clínicas

### HU-006: Crear Historia Clínica Independiente

**Como** usuario del sistema  
**Quiero** registrar una historia clínica sin asociarla todavía a un paciente  
**Para** asignarla más adelante

#### Criterios de Aceptación

`Escenario: Crear historia clínica válida   Dado que el usuario ingresa número "HC-123" y grupo sanguíneo "O+"   Cuando confirma creación   Entonces el sistema guarda la historia   Y muestra "Historia clínica creada con ID: X"  Escenario: Crear historia con número duplicado   Dado que existe una historia con nro_historia = "HC-123"   Cuando intento crear otra con el mismo número   Entonces el sistema muestra "Ya existe una historia con ese número"`

#### Reglas de Negocio Aplicables

-   **RN-019**: El número de historia clínica debe ser único

-   **RN-020**: Grupo sanguíneo y número son obligatorios

-   **RN-021**: Puede no tener paciente asignado


#### Implementación Técnica

-   **Clase**: `MenuHandler.crearHistoriaClinica()`

-   **Servicio**: `HistoriaClinicaServiceImpl.insertar()`

-   **DAO**: `HistoriaClinicaDAO.insertar()`


### HU-007: Listar Historias Clínicas

**Como** usuario del sistema  
**Quiero** ver todas las historias clínicas registradas  
**Para** revisar su estado y asociaciones

#### Criterios de Aceptación

`Escenario: Listar historias con pacientes asociados   Dado que existen historias en el sistema   Cuando el usuario selecciona "Listar historias clínicas"   Entonces el sistema muestra número, grupo sanguíneo y nombre del paciente  Escenario: Historia clínica sin paciente   Dado que existe historia sin paciente asociado   Entonces se muestra "Paciente: [sin asignar]"`

#### Reglas de Negocio Aplicables

-   **RN-022**: Solo listar registros con `eliminado = FALSE`

-   **RN-023**: Mostrar asociación mediante LEFT JOIN

-   **RN-024**: Campo paciente\_id puede ser NULL


#### Implementación Técnica

-   **Clase**: `MenuHandler.listarHistoriasClinicas()`

-   **Servicio**: `HistoriaClinicaServiceImpl.getAll()`

-   **DAO**: `HistoriaClinicaDAO.getAll()`


### HU-008: Actualizar Historia Clínica

**Como** usuario del sistema  
**Quiero** modificar los datos de una historia clínica existente  
**Para** mantener información médica actualizada

#### Criterios de Aceptación

`Escenario: Actualizar antecedentes y medicación   Dado que existe historia con ID 1   Cuando el usuario actualiza los campos   Entonces el sistema guarda los cambios correctamente`

#### Reglas de Negocio Aplicables

-   **RN-025**: ID válido requerido para actualizar

-   **RN-026**: Se valida unicidad del número antes de guardar

-   **RN-027**: Campos vacíos mantienen valores actuales


#### Implementación Técnica

-   **Clase**: `MenuHandler.actualizarHistoriaClinica()`

-   **Servicio**: `HistoriaClinicaServiceImpl.actualizar()`

-   **Validación**: `validateNroHistoriaUnique()`


### HU-009: Eliminar Historia Clínica

**Como** usuario del sistema  
**Quiero** eliminar una historia clínica  
**Para** mantener solo registros activos

#### Criterios de Aceptación

`Escenario: Eliminar historia existente   Dado que existe historia con ID 3   Cuando el usuario confirma eliminación   Entonces el sistema marca eliminado = TRUE   Y muestra "Historia clínica eliminada exitosamente"`

#### Reglas de Negocio Aplicables

-   **RN-028**: Eliminación lógica

-   **RN-029**: No se permite eliminar historias asociadas sin validar FK


#### Implementación Técnica

-   **Clase**: `MenuHandler.eliminarHistoriaClinica()`

-   **Servicio**: `HistoriaClinicaServiceImpl.eliminar()`


## Épica 3: Operaciones Asociadas

### HU-010: Asignar Historia Clínica a Paciente

**Como** usuario del sistema  
**Quiero** vincular una historia clínica existente a un paciente  
**Para** completar su información médica

#### Criterios de Aceptación

`Escenario: Asignar historia a paciente correctamente   Dado que existe paciente ID 2 y historia clínica ID 5 sin paciente asignado   Cuando el usuario vincula ambos   Entonces la historia actualiza su paciente_id = 2   Y se muestra "Historia clínica asignada correctamente"`

#### Reglas de Negocio Aplicables

-   **RN-030**: Se valida existencia de ambos registros

-   **RN-031**: Historia no debe estar ya asociada a otro paciente


#### Implementación Técnica

-   **Servicio**: `PacienteServiceImpl.insertar()`

-   **DAO**: `HistoriaClinicaDAO.actualizar()`


## Modelo de Datos

### Diagrama Entidad-Relación


```
┌────────────────────────────┐
│        paciente             │
├────────────────────────────┤
│ id (PK)                    │
│ nombre                     │
│ apellido                   │
│ dni (UNIQUE)               │
│ fecha_nacimiento           │
│ telefono                   │
│ eliminado                  │
└───────────┬────────────────┘
│ 1
│
▼
┌────────────────────────────┐
│     historia_clinica       │
├────────────────────────────┤
│ id (PK)                    │
│ nro_historia (UNIQUE)      │
│ grupo_sanguineo            │
│ antecedentes               │
│ medicacion_actual          │
│ observaciones              │
│ paciente_id (FK)           │
│ fecha_creacion             │
│ eliminado                  │
└────────────────────────────┘
```