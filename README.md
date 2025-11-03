# Sistema de Gestión de Pacientes e Historias Clínicas

## Trabajo Práctico Integrador - Programación 2

### Descripción del Proyecto

Este Trabajo Práctico Integrador tiene como objetivo demostrar la aplicación práctica de los conceptos fundamentales de Programación Orientada a Objetos y Persistencia de Datos aprendidos durante el cursado de **Programación 2**.  
El proyecto consiste en desarrollar un sistema completo de gestión de **pacientes e historias clínicas**, permitiendo realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre estas entidades, implementando una arquitectura robusta y profesional.

### Objetivos Académicos

El desarrollo de este sistema permite aplicar y consolidar los siguientes conceptos clave:

**1. Arquitectura en Capas (Layered Architecture)**
- Implementación de separación de responsabilidades en 4 capas diferenciadas
- Capa de Presentación (Main/UI): Interacción con el usuario mediante consola
- Capa de Lógica de Negocio (Service): Validaciones y reglas de negocio
- Capa de Acceso a Datos (DAO): Operaciones de persistencia
- Capa de Modelo (Models): Representación de entidades del dominio

**2. Programación Orientada a Objetos**
- Aplicación de principios SOLID
- Uso de herencia mediante clase abstracta `Base`
- Implementación de interfaces genéricas (GenericDAO, GenericService)
- Encapsulamiento y sobrescritura de métodos (`equals`, `hashCode`, `toString`)

**3. Persistencia de Datos con JDBC**
- Conexión a base de datos MySQL mediante JDBC
- Implementación del patrón DAO
- Uso de PreparedStatements para prevenir SQL Injection
- Gestión de transacciones con commit y rollback
- Manejo de claves autogeneradas y relaciones 1:1 (Paciente ↔ Historia Clínica)

**4. Validación de Integridad de Datos**
- Validación de unicidad del número de historia clínica y DNI
- Validación de campos obligatorios
- Validación de integridad referencial (Foreign Keys)
- Implementación de eliminación lógica

### Funcionalidades Implementadas

El sistema permite gestionar dos entidades principales:

- **Pacientes**: nombre, apellido, DNI, fecha de nacimiento y teléfono
- **Historias Clínicas**: número de historia, grupo sanguíneo, antecedentes, medicación y observaciones

## Características Principales

- **Gestión de Pacientes**: Crear, listar, actualizar y eliminar pacientes con validación de DNI único
- **Gestión de Historias Clínicas**: Administrar historias clínicas asociadas a pacientes
- **Búsqueda Inteligente**: Filtrar pacientes por nombre o apellido
- **Eliminación Lógica (Soft Delete)**: Preserva integridad de datos
- **Validación Multi-capa**: En capa de servicio y base de datos
- **Transacciones**: Operaciones atómicas coordinadas entre entidades

## Requisitos del Sistema

| Componente | Versión Requerida |
|------------|-------------------|
| Java JDK | 17 o superior |
| MySQL | 8.0 o superior |
| Gradle | 8.12 (incluido wrapper) |

## Instalación

### 1. Configurar Base de Datos

Ejecutar el siguiente script SQL:

```sql
CREATE DATABASE IF NOT EXISTS dbtpi_pacientes;
USE dbtpi_pacientes;

CREATE TABLE paciente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    dni VARCHAR(20) NOT NULL UNIQUE,
    fecha_nacimiento DATE,
    telefono VARCHAR(30),
    eliminado BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE historia_clinica (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nro_historia VARCHAR(30) NOT NULL UNIQUE,
    grupo_sanguineo VARCHAR(5),
    antecedentes TEXT,
    medicacion_actual TEXT,
    observaciones TEXT,
    paciente_id INT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_historia_paciente FOREIGN KEY (paciente_id) REFERENCES paciente(id)
);
```

### 2. Compilar y Ejecutar

```bash
# Linux/macOS
./gradlew clean build

# Windows
gradlew.bat clean build
```

Luego ejecutar:
```bash
java -cp "build/classes/java/main:<ruta-mysql-jar>" Main.Main
```

## Uso del Sistema

### Menú Principal

```
========= MENU =========
1. Crear paciente
2. Listar pacientes
3. Buscar pacientes
4. Actualizar paciente
5. Eliminar paciente
6. Crear historia clínica
7. Listar historias clínicas
8. Actualizar historia clínica
9. Eliminar historia clínica
0. Salir
```

### Operaciones Principales

#### Crear Paciente
- Captura nombre, apellido, DNI, fecha de nacimiento y teléfono
- Valida campos obligatorios y duplicados

#### Crear Historia Clínica
- Permite asociarla a un paciente existente
- Valida número único y existencia del paciente

#### Listar Pacientes / Historias Clínicas
- Muestra todas las entidades no eliminadas
- JOIN entre paciente e historia_clinica

#### Actualizar o Eliminar
- Operaciones seguras con validación previa
- Eliminación lógica (marca eliminado = TRUE)

## Arquitectura del Proyecto

```
┌─────────────────────────────────────┐
│     Main / UI Layer                 │
│  AppMenu, MenuHandler               │
└───────────┬─────────────────────────┘
            │
┌───────────▼─────────────────────────┐
│     Service Layer                   │
│  PacienteServiceImpl,               │
│  HistoriaClinicaServiceImpl         │
└───────────┬─────────────────────────┘
            │
┌───────────▼─────────────────────────┐
│     DAO Layer                       │
│  PacienteDAO, HistoriaClinicaDAO    │
└───────────┬─────────────────────────┘
            │
┌───────────▼─────────────────────────┐
│     Models Layer                    │
│  Paciente, HistoriaClinica, Base    │
└─────────────────────────────────────┘
```

## Modelo de Datos

```
┌────────────────────────────┐
│        paciente            │
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

---

**Versión:** 1.0  
**Materia:** Programación 2  
**Tipo:** Trabajo Práctico Integrador  
**Tema:** Sistema de Gestión de Pacientes e Historias Clínicas  
**Java:** 17+ | **MySQL:** 8.x | **Gradle:** 8.12  
