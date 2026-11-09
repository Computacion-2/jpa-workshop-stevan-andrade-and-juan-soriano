# Finanzas Personales (Talleres 2, 3 y 4, Computación en Internet II)

Backend de una aplicación de finanzas personales hecho con **Spring Boot + Spring Data JPA (Hibernate) + H2**.
La idea es registrar ingresos y gastos de cada usuario y guardar resúmenes mensuales para comparar periodos.

- **Taller 2:** la lógica de negocio quedó en tres servicios: **Usuarios, Roles y Permisos**.
- **Taller 3:** se agregó un módulo web con **Spring MVC, Thymeleaf y Spring Security** (inicio de sesión con formulario,
  registro y gestión de usuarios, roles y permisos). Ver [Taller 3](#taller-3-módulo-web-con-spring-security).
- **Taller 4:** se agregó una **API REST** con **DTOs, MapStruct y autenticación con JWT**, que funciona en paralelo y
  aislada del módulo MVC, con pruebas en **Postman** y documentación en **Swagger**.
  Ver [Taller 4](#taller-4-api-rest-con-jwt).

## 🎥 Demostración del proyecto

Haz clic en la imagen para ver el video de demostración del Taller 3:

[![Video de demostración](https://img.youtube.com/vi/3fSzft0fw3o/0.jpg)](https://youtu.be/3fSzft0fw3o)

## 1. Tecnologías y versiones

| Herramienta | Versión |
|---|---|
| Java (JDK) | 21 |
| Spring Boot | 4.1.1 |
| Maven | 3.9.x (el wrapper del proyecto descarga la 3.9.16) |
| Empaquetado | WAR (`jpa-workshop-0.0.1-SNAPSHOT.war`) |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | H2 en memoria, con su consola web (`spring-boot-h2console`) |
| Seguridad | Spring Security, JWT con JJWT 0.12.6 (HS256) y contraseñas con BCrypt |
| DTOs | MapStruct 1.6.3 y Bean Validation (`spring-boot-starter-validation`) |
| Documentación de la API | springdoc-openapi 3.2.0 (Swagger UI) |
| Lombok | Versión que administra Spring Boot |
| Pruebas | JUnit (Jupiter) y Mockito, ambos en la versión que trae Spring Boot 4.1.1 |
| Pruebas de la API | Postman (colección y entorno en la carpeta `postman/`) |
| Servidor para desplegar | Tomcat 11 |

## 2. Estructura del proyecto

El repositorio tiene **dos niveles de carpeta**. El `pom.xml` está en la carpeta interna `jpa-workshop`:

```
jpa-workshop-stevan-andrade-and-juan-soriano/   <- raíz del repositorio (git)
├── README.md
├── postman/                                    <- colección y entorno de Postman
└── jpa-workshop/                               <- AQUÍ está el pom.xml, desde aquí se ejecuta todo
    ├── pom.xml
    ├── mvnw / mvnw.cmd
    └── src
        ├── main
        │   ├── java/com/example
        │   │   ├── jpa_workshop    JpaWorkshopApplication, ServletInitializer
        │   │   ├── config          SecurityConfig, OpenApiConfig, DatabaseInitializer
        │   │   ├── controller
        │   │   │   ├── mvc         controladores del módulo web (Thymeleaf)
        │   │   │   ├── rest        AuthController, UserController, RoleController, PermissionController
        │   │   │   └── ApiExceptionHandler.java
        │   │   ├── dto             DTOs de entrada (Request) y de salida (Response)
        │   │   ├── mapper          mappers de MapStruct (entidad <-> DTO)
        │   │   ├── exception       ResourceNotFoundException
        │   │   ├── security        JwtAuthenticationFilter, RestSecurityErrorHandler, AppUserDetails(Service)
        │   │   ├── model           10 entidades JPA y enums
        │   │   ├── repository      un repositorio por entidad
        │   │   └── service
        │   │       ├── interf      interfaces de los servicios (IUserService, IJwtService...)
        │   │       └── impl        UserService, RoleService, PermissionService, AuthService, JwtService
        │   └── resources
        │       ├── application.properties
        │       ├── schema.sql      crea las tablas
        │       ├── data.sql        datos iniciales
        │       └── templates       vistas de Thymeleaf
        └── test/java/com/example   pruebas unitarias e integración (sección 8)
```

## 3. Requisitos previos

1. Tener instalado **Java 21**. Se comprueba con:
   ```bash
   java -version
   ```
2. Tener **Maven** instalado (`mvn -version`) o usar el wrapper que viene en el proyecto (`./mvnw` en Linux/Mac, `mvnw.cmd` en Windows).
3. No hace falta instalar ninguna base de datos: H2 va embebida y se crea en memoria al arrancar.
4. Para probar la API: **Postman** (o Newman desde la terminal) y un navegador para Swagger.

## 4. Dónde ubicarse para ejecutar los comandos

**Todos los comandos de Maven se ejecutan en la carpeta que contiene el `pom.xml`**, es decir, la carpeta interna `jpa-workshop`:

```bash
cd jpa-workshop-stevan-andrade-and-juan-soriano/jpa-workshop
ls pom.xml        # en Windows: dir pom.xml  (debe aparecer el archivo)
```

Si Maven responde `there is no POM in this directory`, es porque se está en la carpeta equivocada (la raíz del repositorio).

## 5. Compilar

```bash
mvn clean compile
```

Para compilar y generar el archivo `.war` (sin correr las pruebas):

```bash
mvn clean package -DskipTests
```

El resultado queda en `target/jpa-workshop-0.0.1-SNAPSHOT.war`.

## 6. Ejecutar la aplicación

Desde la carpeta del `pom.xml`:

```bash
mvn spring-boot:run
```

También se puede ejecutar el `.war` generado:

```bash
java -jar target/jpa-workshop-0.0.1-SNAPSHOT.war
```

Al arrancar, Spring ejecuta `schema.sql` (crea las tablas) y luego `data.sql` (datos iniciales).
Como H2 trabaja en memoria, la base empieza limpia en cada arranque y no hay que borrar nada entre ejecuciones.

Con la configuración de `application.properties` la aplicación queda en:

| Qué | URL |
|---|---|
| Módulo web (MVC) | http://localhost:8082/jpa-workshop |
| API REST | http://localhost:8082/jpa-workshop/api |
| Swagger UI | http://localhost:8082/jpa-workshop/swagger-ui.html |
| Consola de H2 | http://localhost:8082/jpa-workshop/h2-console |

Para desplegar en un Tomcat externo, ver la [sección 12](#12-despliegue-en-tomcat).

## 7. Consola web de H2

Con la aplicación corriendo, primero iniciar sesión en el módulo web (ver Taller 3) y después abrir:

```
http://localhost:8082/jpa-workshop/h2-console
```

En la pantalla de login escribir:

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:mem:finanzas` |
| User Name | `sa` |
| Password | `sa` |

Estos datos son las credenciales de la **base de datos** (salen de `spring.datasource.*` en `application.properties`),
no de la tabla `app_user`. El JDBC URL debe coincidir con el del datasource: si se escribe otro nombre, H2 abre una base distinta y vacía.

Para comprobar que todo cargó bien, probar estas consultas en la consola:

```sql
SELECT * FROM app_role;
SELECT * FROM permission;
SELECT * FROM app_user;
```

Datos iniciales:

- Roles: `ADMIN` (todos los permisos) y `USER` (permisos de movimientos).
- Usuarios: `admin@finanzas.com` (ADMIN) y `ana@finanzas.com` (USER).
- Movimientos, presupuestos y resúmenes de agosto y septiembre de 2026 para comparar un mes con otro.

## Taller 3: módulo web con Spring Security

Con la aplicación corriendo (sección 6), abrir http://localhost:8082/jpa-workshop. Si no hay sesión, redirige al inicio
de sesión. Se entra con el **correo** y la contraseña.

### Cuentas de prueba

`data.sql` deja las contraseñas como `PENDING_HASH` y `DatabaseInitializer` las cifra con BCrypt al arrancar.
La contraseña de prueba es lo que va antes de la `@` seguido de `123`:

| Correo | Contraseña | Rol | Qué puede hacer en el módulo |
|---|---|---|---|
| `admin@finanzas.com` | `admin123` | ADMIN | Todo: usuarios, roles y permisos |
| `ana@finanzas.com` | `ana123` | USER | Solo ver su panel (su rol tiene permisos de movimientos) |

Las cuentas creadas desde "Regístrate" reciben el rol `USER` y su contraseña se guarda cifrada.
Estas mismas cuentas sirven para iniciar sesión en la API REST del Taller 4.

### Cómo se autoriza

Al iniciar sesión, Spring Security recibe el rol del usuario como `ROLE_<NOMBRE>` y cada permiso de su rol con su nombre.
Los controladores se protegen con `@PreAuthorize`:

| Permiso | Permite |
|---|---|
| `USER_READ` | Ver la lista de usuarios |
| `USER_WRITE` | Asociar un rol a un usuario y quitárselo |
| `ROLE_READ` | Ver los roles, sus permisos y cuántos usuarios tienen |
| `ROLE_WRITE` | Crear roles y asociarles permisos |
| `PERMISSION_READ` | Ver los permisos |
| `PERMISSION_WRITE` | Crear y eliminar permisos |

Eliminar un rol exige además `hasRole('ADMIN')`. Los roles `ADMIN` y `USER` no se pueden eliminar.

Reglas que vienen del Taller 2 y se respetan en las vistas:

- Un usuario **siempre** tiene rol. Por eso "Quitar rol" lo devuelve al rol básico `USER` en vez de dejarlo vacío.
- Un rol debe tener **al menos un permiso**: se eligen al crearlo y no se puede quitar el último.
- No se puede eliminar un rol con usuarios ni un permiso asignado a un rol.

Los permisos se cargan al iniciar sesión: si cambia el rol de un usuario o los permisos de su rol, los verá al volver a entrar.

Desde el Taller 4 los servicios devuelven DTOs, y las vistas leen de ellos el rol del usuario, los permisos del rol
y la cantidad de usuarios (`UserResponseDTO.role`, `RoleResponseDTO.permissions` y `RoleResponseDTO.userCount`).

### Rutas

| Ruta | Método | Qué hace | Requiere |
|---|---|---|---|
| `/mvc/auth/login` | GET / POST | Iniciar sesión | Público |
| `/mvc/auth/register` | GET / POST | Registrar usuario | Público |
| `/logout` | POST | Cerrar sesión | Sesión |
| `/` | GET | Panel con el rol y los permisos del usuario | Sesión |
| `/app/users` | GET | Listar usuarios | `USER_READ` |
| `/app/users/{id}/role` | GET / POST | Asociar un rol a un usuario | `USER_WRITE` |
| `/app/users/{id}/role/remove` | POST | Quitar el rol (vuelve a `USER`) | `USER_WRITE` |
| `/app/roles` | GET | Listar roles | `ROLE_READ` |
| `/app/roles/new` | GET / POST | Crear rol con sus permisos | `ROLE_WRITE` |
| `/app/roles/{id}/permissions` | GET / POST | Asociar permisos a un rol | `ROLE_WRITE` |
| `/app/roles/{id}/delete` | POST | Eliminar rol | `ADMIN` + `ROLE_WRITE` |
| `/app/permissions` | GET / POST | Listar y crear permisos | `PERMISSION_READ` / `PERMISSION_WRITE` |
| `/app/permissions/{id}/delete` | POST | Eliminar permiso | `PERMISSION_WRITE` |

Todos los formularios usan `th:action`, así que envían el token CSRF, y después de cada POST el controlador redirige.

### Recorrido de prueba

1. Entrar a "Regístrate" y crear la cuenta `Juan Pérez` / `juan@finanzas.com` / `juan123`.
2. Iniciar sesión con esa cuenta: el panel muestra el rol `USER` y el menú no tiene Usuarios, Roles ni Permisos.
   Abrir a mano `/jpa-workshop/app/roles` muestra la página 403.
3. Cerrar sesión y entrar como `admin@finanzas.com` / `admin123`.
4. **Crear rol:** en Roles, "Crear rol" con el nombre `SOPORTE` y los permisos `USER_READ` y `ROLE_READ`.
5. **Asociar permisos a un rol:** en la fila de `SOPORTE`, "Asociar permisos", marcar también `PERMISSION_READ` y guardar.
6. **Asociar rol a un usuario:** en Usuarios, fila de Juan, "Asociar rol", elegir `SOPORTE`.
7. Entrar como Juan: ahora ve Usuarios, Roles y Permisos, pero sin botones para crear o eliminar.
8. **Eliminar rol con usuarios:** como admin, eliminar `SOPORTE` no se deja porque Juan lo tiene.
9. **Quitar rol a un usuario:** en Usuarios, "Quitar rol" en la fila de Juan. Vuelve a `USER`.
10. **Eliminar rol:** ahora `SOPORTE` sí se puede eliminar.

## Taller 4: API REST con JWT

La API REST (`/api/**`) funciona en paralelo con el módulo MVC y tiene su **propia cadena de seguridad**.
En `SecurityConfig` hay dos `SecurityFilterChain`:

| Cadena | Rutas | Autenticación | Sesión |
|---|---|---|---|
| `apiSecurityFilterChain` (`@Order(1)`) | `/api/**` | Token JWT en `Authorization: Bearer <token>` | No (STATELESS) |
| `securityFilterChain` (`@Order(2)`) | Todo lo demás (MVC, Swagger, H2) | Formulario de login | Sí |

En la cadena de la API, CSRF está desactivado porque no hay sesión ni cookie: el token viaja en una cabecera que el navegador no envía solo.
El probador `index.html` del Taller 2 ya no sirve para la API porque no envía el token; para probarla se usan Swagger o Postman.

### Flujo

1. `POST /api/auth/login` con correo y contraseña. `AuthService` busca al usuario con `AppUserDetailsService`,
   compara la contraseña con BCrypt (`matches`) y le pide a `JwtService` el token.
2. El token va firmado con **HS256** y lleva: `sub` (correo), `userId`, `name`, `role`, `authorities`
   (el rol como `ROLE_X` y todos sus permisos), `iat` y `exp`. Nunca lleva la contraseña.
3. En cada petición a `/api/**`, `JwtAuthenticationFilter` lee la cabecera, valida la firma y la expiración,
   reconstruye el usuario desde el token (sin consultar la base de datos) y lo guarda en el `SecurityContextHolder`.
4. Las reglas de `apiSecurityFilterChain` deciden con esas authorities. Sin token o con token inválido responde **401**;
   con token válido pero sin el permiso, **403**. Ambas respuestas son JSON (`RestSecurityErrorHandler`).

Como los permisos van dentro del token, si se le cambia el rol a un usuario, el cambio se nota cuando vuelve a iniciar
sesión o cuando vence el token anterior.

### Configuración

En `application.properties`:

```properties
app.security.jwt.secret-key=${JWT_SECRET:<clave de desarrollo>}
app.security.jwt.expiration-time=${JWT_EXPIRATION:3600000}
```

- `JWT_SECRET`: clave en Base64 de mínimo 32 bytes (256 bits). En el servidor se define una propia,
  por ejemplo la que genera `openssl rand -base64 32`. El valor después de los dos puntos solo es para desarrollo local.
- `JWT_EXPIRATION`: vigencia del token en milisegundos. Por defecto, 1 hora.

### Endpoints de autenticación (públicos)

| Método | Ruta | Cuerpo | Respuesta |
|---|---|---|---|
| POST | `/api/auth/login` | `{ "email": "...", "password": "..." }` | 200 `{ "accessToken", "tokenType": "Bearer", "expiresIn" }` o 401 |
| POST | `/api/auth/register` | `{ "name", "email", "password", "confirmPassword" }` | 201 con el usuario (rol USER) o 400 |

### Endpoints protegidos

Cada entidad expone los cinco endpoints básicos: listar (`GET`), obtener por id (`GET /{id}`), crear (`POST`),
actualizar (`PUT /{id}`) y eliminar (`DELETE /{id}`). Roles tiene además
`POST` y `DELETE /api/roles/{roleId}/permissions/{permissionId}` para agregar o quitar un permiso.

| Ruta | GET | POST / PUT / DELETE |
|---|---|---|
| `/api/users/**` | `USER_READ` | `USER_WRITE` |
| `/api/roles/**` | `ROLE_READ` | `ROLE_WRITE` (eliminar un rol: solo `ADMIN`) |
| `/api/permissions/**` | `PERMISSION_READ` | `PERMISSION_WRITE` |

Con los datos iniciales, `admin@finanzas.com` (`admin123`) tiene todos los permisos y `ana@finanzas.com`
(`ana123`, rol USER) recibe 403 en estas rutas.

Los datos de entrada y salida son DTOs (`UserRequestDTO`, `UserResponseDTO`, etc.) y la conversión con las entidades
la hacen los mappers de MapStruct. Las respuestas nunca incluyen la contraseña, y al crear o actualizar un usuario
la contraseña se guarda cifrada con BCrypt.

### Errores de la API

Todos los errores responden JSON con `timestamp`, `status`, `error` y `message`:

| Código | Cuándo |
|---|---|
| 400 | Datos inválidos (`@Valid`, incluye la lista `errors` por campo), JSON mal formado o una regla de negocio no se cumple |
| 401 | Sin token, token alterado o vencido, o login con credenciales incorrectas |
| 403 | Token válido pero sin el rol o el permiso necesario |
| 404 | El usuario, rol o permiso no existe |
| 409 | La operación viola una restricción de la base de datos |

### Prueba rápida con curl

```bash
curl -X POST http://localhost:8082/jpa-workshop/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@finanzas.com","password":"admin123"}'

curl http://localhost:8082/jpa-workshop/api/users \
  -H "Authorization: Bearer <accessToken>"
```

## Taller 4: documentación con Swagger

Con la aplicación corriendo, abrir:

```
http://localhost:8082/jpa-workshop/swagger-ui.html
```

La documentación en formato OpenAPI (JSON) está en `/jpa-workshop/v3/api-docs`. Ambas rutas son públicas.

Swagger muestra los cuatro controladores REST (Autenticación, Usuarios, Roles y Permisos). Cada método tiene su descripción,
el permiso que necesita, los códigos de respuesta y ejemplos de los datos de entrada.

Para probar los endpoints protegidos desde Swagger:

1. Abrir **Autenticacion**, `POST /api/auth/login`, **Try it out** y **Execute** (el ejemplo ya trae las credenciales del administrador).
2. Copiar el valor de `accessToken` de la respuesta.
3. Clic en **Authorize** (arriba a la derecha), pegar el token y **Authorize**.
4. Desde ahí, cualquier endpoint se puede probar con **Try it out**. El token se conserva al recargar la página.

## Taller 4: pruebas con Postman

En la carpeta `postman/` están la colección y el entorno:

| Archivo | Contenido |
|---|---|
| `finanzas-api.postman_collection.json` | Colección **Finanzas Personales - API REST** |
| `finanzas-local.postman_environment.json` | Entorno **Finanzas - Local** (`base_url`, credenciales de prueba, tokens e ids que se van guardando) |

### Cómo ejecutarla

1. Arrancar la aplicación (sección 6).
2. En Postman: **Import** y seleccionar los dos archivos de `postman/`.
3. Elegir el entorno **Finanzas - Local** en la esquina superior derecha.
4. Clic en los tres puntos de la colección y **Run collection**, dejar el orden de las carpetas y pulsar **Run**.

Si la aplicación corre en otro puerto o en el Tomcat del servidor, solo se cambia `base_url` en el entorno.

Desde la terminal también se puede ejecutar con Newman (desde la raíz del repositorio):

```bash
npx newman run postman/finanzas-api.postman_collection.json -e postman/finanzas-local.postman_environment.json
```

### Qué prueba cada carpeta

| Carpeta | Peticiones |
|---|---|
| `01 - Auth` | Login de admin y de Ana (guarda `admin_token` y `user_token` y revisa el payload del token), registro con un correo único y login del usuario registrado |
| `02 - Users` | Crear, obtener por id, listar, actualizar, iniciar sesión con la contraseña nueva, eliminar y confirmar el 404 |
| `03 - Permissions` | Crear, obtener por id, listar, actualizar, eliminar y confirmar el 404 |
| `04 - Roles` | Crear, obtener por id, listar, agregar y quitar un permiso, actualizar, eliminar y confirmar el 404 |
| `05 - Errores` | Sin token (401), token alterado (401), Ana sin permiso (403), Ana eliminando un rol (403), login incorrecto (401), datos inválidos (400 con `errors`), JSON mal formado (400), contraseñas distintas en el registro (400) y usuario inexistente (404) |

### Automatización

- La colección usa **Bearer Token** con `{{jwt_token}}`, y todas las peticiones lo heredan.
- Un script **pre-request** de la colección inicia sesión como administrador si `jwt_token` está vacío, así cualquier petición se puede ejecutar sola.
- Los scripts **post-response** guardan en el entorno los tokens y los ids creados (`created_user_id`, `created_role_id`, `created_permission_id`) para las peticiones siguientes.
- Los correos y nombres se generan con `Date.now()`, y cada carpeta elimina lo que crea, así la colección se puede ejecutar varias veces seguidas.

## 8. Pruebas (JUnit + Mockito)

Las pruebas unitarias usan **JUnit** para las aserciones y **Mockito** para simular los repositorios y servicios
(`@Mock`, `@InjectMocks`, `when`, `verify`), así que **no necesitan base de datos ni levantar Spring**.

| Clase de prueba | Qué prueba |
|---|---|
| `PermissionServiceTest` | Consulta, inserción, actualización y eliminación de permisos, y el 404 cuando no existe |
| `RoleServiceTest` | CRUD de roles, agregar, quitar y reemplazar permisos, búsqueda por nombre y que actualizar un rol conserve sus permisos |
| `UserServiceTest` | CRUD de usuarios, contraseña cifrada al crear y actualizar, correos repetidos, cambiar y quitar el rol |
| `AuthServiceLoginTest` | Login correcto, contraseña incorrecta, correo inexistente y datos vacíos |
| `AuthServiceRegisterTest` | Registro con rol por defecto, sin doble cifrado, correo duplicado, contraseñas distintas y contraseña corta |
| `JwtServiceTest` | Token válido, roles y permisos en el token, firma alterada, otra clave, token vencido y texto que no es token |
| `ResponseMapperTest` | Que los mappers llenen el rol del usuario, los permisos del rol y la cantidad de usuarios |
| `RequestDtoValidationTest` | Las validaciones de los DTOs de entrada (usuario, rol, permiso, login y registro) |
| `AppUserDetailsServiceTest` | Carga del usuario con su rol y permisos para Spring Security |
| `DatabaseInitializerTest` | Cifrado de las contraseñas iniciales al arrancar |
| `JpaWorkshopApplicationTests` | Prueba de integración: arranca el contexto completo con H2 y valida entidades, repositorios y scripts SQL |

Además, en los servicios se prueban las reglas de negocio (ver sección 9).

### Comandos

Siempre desde la carpeta del `pom.xml` (`jpa-workshop/jpa-workshop`):

Ejecutar **todas** las pruebas:

```bash
mvn test
```

Ejecutar **una sola clase** de prueba:

```bash
mvn -Dtest=UserServiceTest test
mvn -Dtest=JwtServiceTest test
```

Ejecutar **varias clases**:

```bash
mvn -Dtest=PermissionServiceTest,RoleServiceTest,UserServiceTest test
```

Ejecutar **un método** de una clase:

```bash
mvn -Dtest=PermissionServiceTest#testCreate test
```

Compilar y probar todo junto:

```bash
mvn clean test
```

Si no se tiene Maven instalado, se reemplaza `mvn` por `./mvnw` (Linux/Mac) o `mvnw.cmd` (Windows).

### Cómo leer el resultado

- Si todo está bien, la consola termina con `BUILD SUCCESS` y una línea parecida a `Tests run: N, Failures: 0, Errors: 0`.
- Si algo falla aparece `BUILD FAILURE` y el nombre del test que falló.
- Los reportes detallados quedan en la carpeta `target/surefire-reports`.

## 9. Reglas de negocio (en los servicios)

Cada servicio usa **solo su propio repositorio**. Si necesita datos de otra entidad, se los pide al servicio dueño de esa entidad:

```
UserService  ->  RoleService  ->  PermissionService
```

- **Un usuario no puede quedar sin rol:** no se crea ni se actualiza sin un rol que exista.
- **Un rol no puede quedar sin permisos:** no se crea sin permisos y no se le puede quitar el último.
- No se elimina un rol que tenga usuarios.
- No se elimina un permiso que esté asignado a algún rol.
- Correo, nombre de rol y nombre de permiso son únicos.
- Las contraseñas se guardan cifradas con BCrypt al crear o actualizar un usuario. Si al actualizar no se envía contraseña, se conserva la actual.
- Cuando algo no existe, el servicio lanza `ResourceNotFoundException` (404 en la API). Cuando una regla no se cumple,
  lanza `IllegalArgumentException` con un mensaje claro (400 en la API).

## 10. Problemas frecuentes

| Problema | Causa y solución |
|---|---|
| `there is no POM in this directory` | Se está en la raíz del repositorio. Entrar a la carpeta interna `jpa-workshop`. |
| `mvn` no se reconoce como comando | Maven no está instalado o no está en el `PATH`. Usar `./mvnw` o `mvnw.cmd`. |
| Error de versión de Java al compilar | El proyecto necesita Java 21. Revisar con `java -version`. |
| `Port 8082 was already in use` | Otro programa usa el puerto. Cerrarlo o cambiar `server.port` en `application.properties`. |
| La consola de H2 abre vacía o no encuentra las tablas | El JDBC URL del login no es `jdbc:h2:mem:finanzas`. Corregirlo. |
| La API responde 401 | Falta la cabecera `Authorization: Bearer <token>`, falta la palabra `Bearer` o el token venció. Volver a hacer login. |
| La API responde 403 | El usuario no tiene el permiso de esa ruta (por ejemplo, Ana en `/api/users`). Usar el token del administrador. |
| Postman falla en todas las peticiones | La aplicación no está corriendo, no se eligió el entorno **Finanzas - Local** o `base_url` no coincide. |
| `No tests were executed` con `-Dtest=...` | El nombre de la clase está mal escrito o el comando no se ejecutó en la carpeta del `pom.xml`. |
| No existe `target/site/jacoco/index.html` | El reporte se genera al correr `mvn clean test` y solo si las pruebas terminan bien. Ejecutar el comando de nuevo en la carpeta del `pom.xml` y revisar que no haya tests fallando. |

## 11. Reporte de cobertura con JaCoCo

JaCoCo mide qué porcentaje del código se ejecuta cuando corren las pruebas y lo muestra en un reporte HTML.

### Cómo generar el reporte

Siempre desde la carpeta del `pom.xml` (`jpa-workshop/jpa-workshop`):

**Paso 1.** Ejecutar las pruebas. Esto también genera el reporte:

```bash
mvn clean test
```

**Paso 2.** Abrir el reporte en el navegador.

En Windows (PowerShell o CMD):

```bash
start .\target\site\jacoco\index.html
```

En Mac:

```bash
open target/site/jacoco/index.html
```

En Linux:

```bash
xdg-open target/site/jacoco/index.html
```

Si el comando no abre nada, se puede entrar a la carpeta `target/site/jacoco` y abrir el archivo `index.html` con doble clic.

Notas:
- `clean` borra la carpeta `target`, así que el reporte se vuelve a crear completo en cada ejecución.
- El reporte solo se genera si las pruebas terminan bien (`BUILD SUCCESS`).
- La carpeta `target` no se sube a Git; cada integrante genera su propio reporte.

### Cómo leer el reporte

La primera pantalla muestra una fila por paquete (`com.example.service.impl`, `com.example.security`, `com.example.mapper`, etc.) y una fila `Total`. Las columnas principales son:

| Columna | Qué significa |
|---|---|
| Missed Instructions / Cov. | Instrucciones del código que las pruebas no ejecutaron y el porcentaje que sí se ejecutó |
| Missed Branches / Cov. | Lo mismo para las ramas de los `if` (el camino verdadero y el falso). `n/a` significa que el paquete no tiene `if` |
| Missed / Lines | Líneas sin ejecutar y total de líneas |
| Missed / Methods | Métodos sin ejecutar y total de métodos |
| Missed / Classes | Clases sin ejecutar y total de clases |

Las barras de colores ayudan a leerlo rápido: verde es código cubierto por las pruebas y rojo es código que ninguna prueba ejecutó.

Las pruebas se enfocan en la lógica: servicios, JWT, mappers y validaciones. Los paquetes `model`, `controller` y
`jpa_workshop` tienen menos cobertura porque son entidades (sus `getters` y `setters` los genera Lombok), controladores
que solo delegan en los servicios (se prueban con Postman) y la clase que arranca la aplicación.

Para ver el detalle de un paquete o una clase, se hace clic en su nombre dentro del reporte. Las líneas en verde se ejecutaron, las amarillas se ejecutaron solo a medias (falta una rama de un `if`) y las rojas nunca se ejecutaron.

## 12. Despliegue en Tomcat

El proyecto se empaqueta como **WAR** (`ServletInitializer` permite desplegarlo en un Tomcat externo).
Spring Boot 4 usa Servlet 6.1, por lo que se necesita **Tomcat 11** y **Java 21**.

1. Generar el WAR desde la carpeta del `pom.xml`:
   ```bash
   mvn clean package
   ```
2. Renombrar `target/jpa-workshop-0.0.1-SNAPSHOT.war` a `jpa-workshop.war`. En un Tomcat externo la ruta base es el nombre
   del archivo, así la aplicación queda en `/jpa-workshop`, igual que en local.
3. Definir la clave del JWT en el servidor, en `bin/setenv.sh` (Linux) o `bin/setenv.bat` (Windows) de Tomcat:
   ```bash
   export JWT_SECRET=<clave generada con openssl rand -base64 32>
   ```
   ```bat
   set JWT_SECRET=<clave generada con openssl rand -base64 32>
   ```
4. Copiar `jpa-workshop.war` a la carpeta `webapps` de Tomcat y arrancarlo (`bin/startup.sh` o `bin/startup.bat`).
5. En Tomcat externo el puerto lo define Tomcat (normalmente `8080`), no `server.port`. Las URLs quedan así:

| Qué | URL |
|---|---|
| Módulo web | `http://<servidor>:8080/jpa-workshop` |
| Swagger UI | `http://<servidor>:8080/jpa-workshop/swagger-ui.html` |
| API REST | `http://<servidor>:8080/jpa-workshop/api` |

Para probar el despliegue con Postman, cambiar `base_url` del entorno a `http://<servidor>:8080/jpa-workshop`
y ejecutar la colección completa.

## 13. Autores

Stevan Andrade y Juan Soriano - Universidad Icesi, Computación en Internet II.
