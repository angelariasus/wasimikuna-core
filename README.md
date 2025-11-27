# Sistema de Gestión de Alimentos Wasimikuna

## Descripción General

**Wasimikuna** es un sistema integral desarrollado para el **Programa Nacional de Alimentación Escolar Comunitaria Wasi Mikuna**, un programa del **Ministerio de Desarrollo e Inclusión Social (Midis)** del Perú. Está orientado a administrar y supervisar la gestión alimentaria escolar en instituciones educativas públicas, permitiendo la gestión de proveedores, productos alimentarios, menús nutricionales, órdenes de compra, inventarios, envíos y el seguimiento completo de la cadena alimentaria desde la producción hasta el consumo escolar.

---

## Características Principales

- **Gestión de Instituciones Educativas:** Registro y administración de centros educativos beneficiarios, códigos modulares, direcciones y datos de contacto.
- **Gestión de Afiliados/Proveedores:** Administración completa de proveedores alimentarios, agricultores locales y distribuidores autorizados.
- **Gestión de Productos Alimentarios:** Catálogo de productos con información nutricional, fechas de vencimiento, lotes y trazabilidad completa.
- **Planificación de Menús:** Creación y gestión de menús nutricionales balanceados con recetas, ingredientes y valores nutricionales.
- **Órdenes de Compra:** Generación automática y manual de órdenes de compra con seguimiento de estados y aprobaciones.
- **Control de Inventarios:** Gestión de stock, recepciones, kardex y reportes de movimientos de productos.
- **Gestión de Envíos:** Seguimiento logístico completo desde almacén hasta instituciones educativas.
- **Incidencias Sanitarias:** Registro y seguimiento de eventos relacionados con seguridad alimentaria.
- **Auditoría y Trazabilidad:** Sistema completo de auditoría con registro de todas las operaciones y cambios.
- **Comités de Gestión:** Administración de comités de gestión alimentaria por institución educativa.
- **Reportes Avanzados:** Generación de reportes de stock, kardex, consumo y estadísticas nutricionales.

---

## Arquitectura del Sistema

El sistema está desarrollado en **Java** utilizando **Spring Boot 4.0.0** y sigue una arquitectura multicapa moderna:

- **Controller:** Controladores REST con documentación OpenAPI/Swagger para manejo de solicitudes HTTP.
- **DTO (Data Transfer Object):** Objetos de transferencia optimizados para comunicación entre capas.
- **Model/Entity:** Entidades JPA que representan el modelo de datos de Oracle Database.
- **Repository:** Repositorios JPA con consultas JPQL personalizadas para acceso a datos.
- **Service:** Capa de servicios con lógica de negocio y transacciones.
- **Exception:** Manejo centralizado y personalizado de errores y excepciones del sistema.
- **Configuration:** Configuraciones de CORS, Swagger/OpenAPI y manejo global de excepciones.

La base de datos está implementada en **Oracle Database** con scripts SQL optimizados en la carpeta `database/`.

---

## Estructura de Carpetas

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/system/wasimikuna/
│   │   │   ├── controller/           # Controladores REST API
│   │   │   │   ├── AfiliadoController.java
│   │   │   │   ├── InstitucionEducativaController.java
│   │   │   │   ├── ProductoController.java
│   │   │   │   ├── PlatoController.java
│   │   │   │   ├── OrdenCompraController.java
│   │   │   │   ├── EnvioController.java
│   │   │   │   ├── IncidenciaSanitariaController.java
│   │   │   │   ├── UsuarioSistemaController.java
│   │   │   │   ├── AuditoriaSistemaController.java
│   │   │   │   └── ...
│   │   │   ├── dto/                  # Objetos de transferencia de datos
│   │   │   │   ├── AfiliadoDTO.java
│   │   │   │   ├── InstitucionEducativaDTO.java
│   │   │   │   ├── ProductoDTO.java
│   │   │   │   ├── EnvioDTO.java
│   │   │   │   └── ...
│   │   │   ├── model/                # Entidades JPA
│   │   │   │   ├── Afiliado.java
│   │   │   │   ├── InstitucionEducativa.java
│   │   │   │   ├── Producto.java
│   │   │   │   ├── UsuarioSistema.java
│   │   │   │   └── ...
│   │   │   ├── repository/           # Repositorios de datos
│   │   │   │   ├── AfiliadoRepository.java
│   │   │   │   ├── ProductoRepository.java
│   │   │   │   ├── ReporteRepository.java
│   │   │   │   └── ...
│   │   │   ├── service/              # Servicios de negocio
│   │   │   │   ├── AfiliadoService.java
│   │   │   │   ├── ProductoService.java
│   │   │   │   ├── UsuarioSistemaService.java
│   │   │   │   └── ...
│   │   │   ├── exception/            # Manejo de excepciones
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── ...
│   │   │   ├── config/               # Configuraciones
│   │   │   │   ├── CorsConfig.java
│   │   │   │   ├── OpenApiConfig.java
│   │   │   │   └── ...
│   │   │   └── WasimikunaApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       └── templates/
│   └── test/
│       └── java/com/system/wasimikuna/
│           └── WasimikunaApplicationTests.java
├── target/                           # Archivos compilados
├── pom.xml                          # Configuración Maven
└── README.md

database/
├── cleaner.sql                      # Scripts de limpieza
├── schema.sql                       # Definición de esquema
├── seed.sql                        # Datos de prueba
└── tablespaces.sql                 # Configuración de tablespaces
```

---

## Casos de Uso Destacados

1. **Registro de Institución Educativa:**  
   Permite registrar nuevos centros educativos con código modular, datos de contacto y asignación de usuarios responsables.

2. **Gestión de Proveedores Alimentarios:**  
   Administración completa de afiliados proveedores, incluyendo agricultores locales y distribuidores autorizados con documentación legal.

3. **Planificación de Menús Nutricionales:**  
   Creación de menús balanceados con recetas, ingredientes, valores nutricionales y programación semanal/mensual.

4. **Generación de Órdenes de Compra:**  
   Creación automática de órdenes basadas en programación de menús y niveles de inventario, con flujo de aprobaciones.

5. **Control de Recepciones e Inventario:**  
   Registro detallado de productos recibidos, control de calidad, fechas de vencimiento y actualización automática de stock.

6. **Seguimiento Logístico de Envíos:**  
   Gestión completa del despacho desde almacén hasta institución educativa con seguimiento en tiempo real.

7. **Gestión de Incidencias Sanitarias:**  
   Registro y seguimiento de eventos relacionados con seguridad alimentaria, intoxicaciones o productos defectuosos.

8. **Auditoría y Trazabilidad:**  
   Sistema completo de auditoría que registra todas las operaciones para garantizar transparencia y cumplimiento normativo.

9. **Reportes de Gestión:**  
   Generación de reportes de stock, kardex, consumo nutricional y estadísticas operativas para toma de decisiones.

---

## Tecnologías Utilizadas

### Backend
- **Java 21** - Lenguaje de programación principal
- **Spring Boot 4.0.0** - Framework de aplicación con auto-configuración
- **Spring Data JPA** - Persistencia y acceso a datos
- **Spring Web** - Desarrollo de APIs REST
- **Hibernate 7.1.8** - ORM para manejo de base de datos
- **Oracle Database** - Sistema de gestión de base de datos empresarial
- **HikariCP** - Pool de conexiones de alto rendimiento
- **Lombok** - Reducción de código boilerplate
- **Maven** - Gestión de dependencias y construcción

### Documentación
- **Swagger/OpenAPI 3** - Documentación interactiva de APIs
- **SpringDoc OpenAPI** - Integración automática con Spring Boot

### Base de Datos
- **Oracle Database 21.3** - Base de datos principal
- **XEPDB1** - Pluggable Database para desarrollo
- **Tablespaces personalizados** - Optimización de almacenamiento

---

## Requisitos Técnicos

### Software Requerido
- **Java 21 LTS** o superior
- **Maven 3.8+** para gestión de dependencias
- **Oracle Database 21.3** o superior
- **Oracle SQL Developer** o herramienta similar para gestión de BD

---

## Instalación y Ejecución

### 1. Preparación del Entorno

Clona el repositorio del proyecto:
```bash
git clone https://github.com/angelariasus/wasimikuna-core.git
cd wasimikuna-core
```

### 2. Configuración de Base de Datos

Ejecuta los scripts SQL en el siguiente orden:
```bash
# 1. Crear tablespaces (como usuario privilegiado)
sqlplus sys/password@localhost:1521/XE as sysdba @database/tablespaces.sql

# 2. Crear esquema y tablas
sqlplus wasimikuna/password@localhost:1521/XEPDB1 @database/schema.sql

# 3. Cargar datos iniciales
sqlplus wasimikuna/password@localhost:1521/XEPDB1 @database/seed.sql
```

### 3. Configuración de la Aplicación

Modifica el archivo `backend/src/main/resources/application.properties`:
```properties
# Configuración de base de datos Oracle
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

# Configuración JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.OracleDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
hibernate.query.startup_check=false

# Configuración del servidor
server.port=8080
server.servlet.context-path=/
```

### 4. Compilación y Ejecución

Navega al directorio backend y ejecuta:
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 5. Acceso a la Aplicación

Una vez iniciada la aplicación, puedes acceder a:

- **API REST:** `http://localhost:8080/api/`
- **Documentación Swagger:** `http://localhost:8080/swagger-ui/index.html`
- **API Docs JSON:** `http://localhost:8080/v3/api-docs`

### 6. Verificación de Funcionamiento

Ejecuta una consulta de prueba:
```bash
curl -X GET "http://localhost:8080/api/usuarios" -H "accept: application/json"
```

---

## Endpoints Principales de la API

### Gestión de Usuarios
- `GET /api/usuarios` - Listar todos los usuarios
- `GET /api/usuarios/{id}` - Obtener usuario por ID
- `POST /api/usuarios` - Crear nuevo usuario
- `PUT /api/usuarios/{id}` - Actualizar usuario
- `PATCH /api/usuarios/{id}/toggle-estado` - Cambiar estado del usuario

### Gestión de Instituciones Educativas
- `GET /api/instituciones` - Listar instituciones educativas
- `POST /api/instituciones` - Registrar nueva institución
- `GET /api/instituciones/buscar` - Búsqueda por criterios

### Gestión de Afiliados/Proveedores
- `GET /api/afiliados` - Listar proveedores
- `POST /api/afiliados` - Registrar nuevo proveedor
- `GET /api/afiliados/buscar` - Búsqueda de proveedores

### Gestión de Productos
- `GET /api/productos` - Catálogo de productos
- `POST /api/productos` - Registrar nuevo producto
- `GET /api/productos/buscar` - Búsqueda en catálogo

### Gestión de Envíos
- `GET /api/envios` - Listar envíos
- `POST /api/envios` - Crear nuevo envío
- `PATCH /api/envios/{id}/entregar` - Marcar como entregado

### Auditoría
- `GET /api/auditoria` - Consultar log de auditoría
- `GET /api/auditoria/usuario/{userId}` - Auditoría por usuario

---

## Estructura de la Base de Datos

### Tablas Principales

1. **Seguridad y Accesos**
   - `Rol` - Roles del sistema
   - `Usuario_Sistema` - Usuarios con autenticación

2. **Actores del Sistema**
   - `Institucion_Educativa` - Centros educativos beneficiarios
   - `Afiliado` - Proveedores y agricultores
   - `Comite_Gestion` - Comités por institución

3. **Gestión Alimentaria**
   - `Producto` - Catálogo de productos alimentarios
   - `Plato` - Menús y recetas
   - `Receta_Producto` - Ingredientes por plato

4. **Operaciones**
   - `Orden_Compra` - Órdenes de compra
   - `Recepcion` - Recepciones de productos
   - `Envio` - Envíos a instituciones
   - `Programacion_Menu` - Programación de menús

5. **Control y Auditoría**
   - `Incidencia_Sanitaria` - Eventos de seguridad alimentaria
   - `Auditoria_Sistema` - Log completo de operaciones

---

## Licencia

Este proyecto está bajo la licencia MIT. Consulta el archivo [LICENSE.md](./LICENSE.md) para más detalles.
