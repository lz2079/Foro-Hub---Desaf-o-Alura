# Foro Hub - Desafío Alura

## Descripción

API REST para un foro donde los usuarios pueden crear tópicos con dudas o sugerencias, y otros usuarios pueden responder e interactuar. Esta API permite la gestión completa de tópicos, incluyendo su creación, listado, actualización y eliminación, con autenticación basada en JWT.

## Tecnologías Utilizadas

*   **Java 17**
*   **Spring Boot 3.5.4**
*   **Maven**
*   **Spring Data JPA**
*   **Spring Security**
*   **JWT (JSON Web Tokens)**
*   **MySQL 8.0+**
*   **Flyway Migration**
*   **Lombok**
*   **Validation (Jakarta Bean Validation)**

## Arquitectura

La API sigue una arquitectura limpia basada en capas:

*   **Controller:** Capa de presentación, expone los endpoints REST.
*   **Service:** Capa de lógica de negocio.
*   **Repository:** Capa de acceso a datos, interactúa con la base de datos.
*   **Domain/Entity:** Modelo de dominio, representa las entidades del negocio.
*   **DTO (Data Transfer Object):** Objetos para transferir datos entre capas.
*   **Infra/Security:** Configuración de seguridad y filtros.

## Endpoints de la API

### Autenticación

*   `POST /login`: Autenticar usuario y obtener token JWT.
    *   **Body (JSON):**
        ```json
        {
          "email": "usuario@example.com",
          "contrasena": "clave_secreta"
        }
        ```
    *   **Respuesta (200 OK):**
        ```json
        {
          "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        }
        ```

### Gestión de Usuarios

*   `POST /usuarios`: Registrar un nuevo usuario.
    *   **Body (JSON):**
        ```json
        {
          "nombre": "Nombre del Usuario",
          "email": "usuario@example.com",
          "contrasena": "clave_secreta"
        }
        ```
    *   **Respuesta (201 Created):**
        ```json
        {
          "id": 1,
          "nombre": "Nombre del Usuario",
          "email": "usuario@example.com"
        }
        ```

### Gestión de Tópicos

*   `GET /topicos`: Listar todos los tópicos (paginado).
    *   **Parámetros de consulta (Query Params) opcionales:**
        *   `page`: Número de página (por defecto 0).
        *   `size`: Tamaño de página (por defecto 10).
        *   `sort`: Campo por el que ordenar (por defecto `fechaCreacion`).
    *   **Respuesta (200 OK):**
        ```json
        {
          "content": [
            {
              "id": 1,
              "titulo": "Título del Tópico",
              "mensaje": "Mensaje del Tópico",
              "fechaCreacion": "2023-10-27T10:00:00",
              "status": "ACTIVO",
              "autor": "Autor del Tópico",
              "curso": "Curso Relacionado"
            }
          ],
          "pageable": {
            "pageNumber": 0,
            "pageSize": 10,
            // ... otros metadatos de paginación
          },
          "totalElements": 1,
          "totalPages": 1,
          // ... otros campos
        }
        ```

*   `POST /topicos`: Crear un nuevo tópico (requiere autenticación).
    *   **Headers:**
        *   `Authorization: Bearer <tu_token_jwt>`
    *   **Body (JSON):**
        ```json
        {
          "titulo": "Título del Tópico",
          "mensaje": "Mensaje detallado del tópico",
          "autor": "Nombre del Autor",
          "curso": "Nombre del Curso"
        }
        ```
    *   **Respuesta (201 Created):**
        ```json
        {
          "id": 1,
          "titulo": "Título del Tópico",
          "mensaje": "Mensaje detallado del tópico",
          "fechaCreacion": "2023-10-27T10:00:00",
          "status": "ACTIVO",
          "autor": "Nombre del Autor",
          "curso": "Nombre del Curso"
        }
        ```

*   `GET /topicos/{id}`: Obtener detalles de un tópico específico.
    *   **Respuesta (200 OK):**
        ```json
        {
          "id": 1,
          "titulo": "Título del Tópico",
          "mensaje": "Mensaje detallado del tópico",
          "fechaCreacion": "2023-10-27T10:00:00",
          "status": "ACTIVO",
          "autor": "Nombre del Autor",
          "curso": "Nombre del Curso"
        }
        ```

*   `PUT /topicos/{id}`: Actualizar un tópico existente (requiere autenticación).
    *   **Headers:**
        *   `Authorization: Bearer <tu_token_jwt>`
    *   **Body (JSON - campos opcionales):**
        ```json
        {
          "titulo": "Título Actualizado del Tópico",
          "mensaje": "Mensaje actualizado del tópico"
          // "autor" y "curso" no se incluyen, por lo tanto, no se actualizarán.
        }
        ```
    *   **Respuesta (200 OK):**
        ```json
        {
          "id": 1,
          "titulo": "Título Actualizado del Tópico",
          "mensaje": "Mensaje actualizado del tópico",
          "fechaCreacion": "2023-10-27T10:00:00",
          "status": "ACTIVO",
          "autor": "Nombre del Autor Original",
          "curso": "Nombre del Curso Original"
        }
        ```

*   `DELETE /topicos/{id}`: Eliminar un tópico existente (requiere autenticación).
    *   **Headers:**
        *   `Authorization: Bearer <tu_token_jwt>`
    *   **Respuesta (204 No Content):**
        *   Sin cuerpo.

## Configuración del Proyecto

### Requisitos Previos

*   **Java 17+**
*   **Maven 3.6+**
*   **MySQL 8.0+**

### Pasos para Ejecutar el Proyecto

1.  **Clonar el repositorio:**
    ```bash
    git clone <url_del_repositorio>
    cd desafio-foro
    ```

2.  **Configurar la base de datos:**
    *   Crear una base de datos en MySQL:
        ```sql
        CREATE DATABASE foro_hub;
        ```
    *   (Opcional) Crear un usuario específico para la aplicación:
        ```sql
        CREATE USER 'foro_user'@'localhost' IDENTIFIED BY 'foro_password';
        GRANT ALL PRIVILEGES ON foro_hub.* TO 'foro_user'@'localhost';
        FLUSH PRIVILEGES;
        ```

3.  **Configurar `application.properties`:**
    *   Abrir `src/main/resources/application.properties`.
    *   Ajustar las propiedades de conexión a la base de datos:
        ```properties
        spring.datasource.url=jdbc:mysql://localhost:3306/foro_hub
        spring.datasource.username=root # o 'foro_user' si creaste uno
        spring.datasource.password=root # o 'foro_password' si creaste uno
        ```
    *   (Opcional) Ajustar otras propiedades como `jwt.secret` y `jwt.expiration` para producción.

4.  **Ejecutar la aplicación:**
    *   Desde la línea de comandos:
        ```bash
        ./mvnw spring-boot:run
        ```
    *   Desde IntelliJ IDEA:
        *   Abrir `DesafioForoApplication.java`.
        *   Hacer clic derecho -> `Run 'DesafioForoApplication'`.

5.  **La API estará disponible en:** `http://localhost:8080`

## Pruebas de la API

Se recomienda usar herramientas como **Insomnia** o **Postman** para probar los endpoints de la API.

### Ejemplos de Pruebas

#### 1. Registrar Usuario - Éxito
*   **Método:** `POST`
*   **URL:** `http://localhost:8080/usuarios`
*   **Body (JSON):**
    ```json
    {
      "nombre": "Ana García",
      "email": "ana.garcia@example.com",
      "contrasena": "claveSecreta456"
    }
    ```
*   **Respuesta Esperada:** `201 Created`

#### 2. Registrar Usuario - Email Duplicado
*   **Método:** `POST`
*   **URL:** `http://localhost:8080/usuarios`
*   **Body (JSON):** *(Mismo email que el ejemplo anterior)*
*   **Respuesta Esperada:** `400 Bad Request`

#### 3. Login - Éxito
*   **Método:** `POST`
*   **URL:** `http://localhost:8080/login`
*   **Body (JSON):**
    ```json
    {
      "email": "ana.garcia@example.com",
      "contrasena": "claveSecreta456"
    }
    ```
*   **Respuesta Esperada:** `200 OK` con token JWT

#### 4. Login - Credenciales Inválidas
*   **Método:** `POST`
*   **URL:** `http://localhost:8080/login`
*   **Body (JSON):**
    ```json
    {
      "email": "usuario@incorrecto.com",
      "contrasena": "clave_erronea"
    }
    ```
*   **Respuesta Esperada:** `403 Forbidden`

#### 5. Crear Tópico - Sin Token
*   **Método:** `POST`
*   **URL:** `http://localhost:8080/topicos`
*   **Body (JSON):**
    ```json
    {
      "titulo": "Duda sobre JWT",
      "mensaje": "¿Cómo se implementa correctamente la validación de tokens?",
      "autor": "Juan Pérez",
      "curso": "Spring Security"
    }
    ```
*   **(Sin encabezado `Authorization`)**
*   **Respuesta Esperada:** `403 Forbidden`

#### 6. Crear Tópico - Con Token Válido
*   **Método:** `POST`
*   **URL:** `http://localhost:8080/topicos`
*   **Headers:**
    *   `Authorization: Bearer <tu_token_jwt>`
*   **Body (JSON):**
    ```json
    {
      "titulo": "Duda sobre JWT - Con Token",
      "mensaje": "¡Este tópico se crea usando un token JWT!",
      "autor": "Juan Pérez",
      "curso": "Spring Security"
    }
    ```
*   **Respuesta Esperada:** `201 Created`

#### 7. Crear Tópico - Datos Inválidos
*   **Método:** `POST`
*   **URL:** `http://localhost:8080/topicos`
*   **Headers:**
    *   `Authorization: Bearer <tu_token_jwt>`
*   **Body (JSON):**
    ```json
    {
      "titulo": "", // Campo obligatorio @NotBlank
      "mensaje": "", // Campo obligatorio @NotBlank
      "autor": "", // Campo obligatorio @NotBlank
      "curso": "" // Campo obligatorio @NotBlank
    }
    ```
*   **Respuesta Esperada:** `400 Bad Request`

#### 8. Crear Tópico - Duplicado
*   **Método:** `POST`
*   **URL:** `http://localhost:8080/topicos`
*   **Headers:**
    *   `Authorization: Bearer <tu_token_jwt>`
*   **Body (JSON):** *(Usar el mismo título y mensaje que en "Crear Tópico - Con Token")*
*   **Respuesta Esperada:** `400 Bad Request`

#### 9. Listar Tópicos - Sin Token
*   **Método:** `GET`
*   **URL:** `http://localhost:8080/topicos`
*   **(Sin encabezado `Authorization`)**
*   **Respuesta Esperada:** `403 Forbidden`

#### 10. Listar Tópicos - Con Token
*   **Método:** `GET`
*   **URL:** `http://localhost:8080/topicos`
*   **Headers:**
    *   `Authorization: Bearer <tu_token_jwt>`
*   **Respuesta Esperada:** `200 OK` con lista de tópicos

#### 11. Actualizar Tópico - Sin Token
*   **Método:** `PUT`
*   **URL:** `http://localhost:8080/topicos/1` *(Usar un ID existente)*
*   **(Sin encabezado `Authorization`)**
*   **Body (JSON):**
    ```json
    {
      "titulo": "Título Actualizado",
      "mensaje": "Mensaje actualizado"
    }
    ```
*   **Respuesta Esperada:** `403 Forbidden`

#### 12. Actualizar Tópico - Con Token
*   **Método:** `PUT`
*   **URL:** `http://localhost:8080/topicos/1` *(Usar un ID existente)*
*   **Headers:**
    *   `Authorization: Bearer <tu_token_jwt>`
*   **Body (JSON):**
    ```json
    {
      "titulo": "Título Actualizado con Token",
      "mensaje": "Mensaje actualizado con token"
    }
    ```
*   **Respuesta Esperada:** `200 OK`

#### 13. Eliminar Tópico - Sin Token
*   **Método:** `DELETE`
*   **URL:** `http://localhost:8080/topicos/1` *(Usar un ID existente)*
*   **(Sin encabezado `Authorization`)**
*   **Respuesta Esperada:** `403 Forbidden`

#### 14. Eliminar Tópico - Con Token
*   **Método:** `DELETE`
*   **URL:** `http://localhost:8080/topicos/1` *(Usar un ID existente)*
*   **Headers:**
    *   `Authorization: Bearer <tu_token_jwt>`
*   **Respuesta Esperada:** `204 No Content`

## Validaciones

La API implementa validaciones para garantizar la integridad de los datos:

*   **Campos obligatorios:** Todos los campos marcados con `@NotBlank` deben ser proporcionados.
*   **Email válido:** Los campos de email deben tener un formato válido (`@Email`).
*   **No duplicados:** No se permiten tópicos con el mismo título y mensaje, ni usuarios con el mismo email.

## DTOs (Data Transfer Objects)

### DatosRespuestaTopico
Representa la estructura de datos devuelta al crear, actualizar o consultar un tópico.

```json
{
  "id": 1,
  "titulo": "Título del Tópico",
  "mensaje": "Mensaje detallado del tópico",
  "fechaCreacion": "2023-10-27T10:00:00",
  "status": "ACTIVO",
  "autor": "Nombre del Autor",
  "curso": "Nombre del Curso"
}

### DatosListadoTopico
Representa la estructura de datos devuelta al listar tópicos (con paginación).
```json
{
  "content": [
    {
      "id": 1,
      "titulo": "Título del Tópico",
      "mensaje": "Mensaje del Tópico",
      "fechaCreacion": "2023-10-27T10:00:00",
      "status": "ACTIVO",
      "autor": "Autor del Tópico",
      "curso": "Curso Relacionado"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,

###DatosRegistroTopico
Representa la estructura de datos enviada para crear un nuevo tópico.
```json
{
  "titulo": "Título del Tópico",
  "mensaje": "Mensaje detallado del tópico",
  "autor": "Nombre del Autor",
  "curso": "Nombre del Curso"
}

###DatosActualizarTopico
Representa la estructura de datos enviada para actualizar un tópico existente (campos opcionales).
```json
{
  "titulo": "Título Actualizado del Tópico",
  "mensaje": "Mensaje actualizado del tópico"
  // "autor" y "curso" no se incluyen, por lo tanto, no se actualizarán.
}

###DatosRespuestaUsuario
Representa la estructura de datos devuelta al registrar un nuevo usuario.
```json
{
  "id": 1,
  "nombre": "Nombre del Usuario",
  "email": "usuario@example.com"
}

###DatosRegistroUsuario
Representa la estructura de datos enviada para registrar un nuevo usuario.
```json
{
  "nombre": "Nombre del Usuario",
  "email": "usuario@example.com",
  "contrasena": "clave_secreta"

###DatosAutenticacion
Representa la estructura de datos enviada para autenticar un usuario.
```json
{
  "email": "usuario@example.com",
  "contrasena": "clave_secreta"
}

###DatosRespuestaLogin
Representa la estructura de datos devuelta al autenticar un usuario exitosamente.
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

##Autor 

Luis Alberto Zepeda Toro 

##Licencia 

Este proyecto es un desafío educativo desarrollado como parte del curso de Alura Latam. No se asigna una licencia específica. 
