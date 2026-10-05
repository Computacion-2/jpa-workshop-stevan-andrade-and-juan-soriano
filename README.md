# Finanzas Personales - Backend (Taller 2, Computación en Internet II)

Backend de una aplicación de finanzas personales hecho con **Spring Boot + Spring Data JPA (Hibernate) + H2**.
La idea es registrar ingresos y gastos de cada usuario y guardar resúmenes mensuales para comparar periodos.

En esta entrega la lógica de negocio está en tres servicios: **Usuarios, Roles y Permisos**.
El proyecto todavía **no tiene controladores ni seguridad (Spring Security)**, así que no hay endpoints HTTP:
la aplicación se prueba con las pruebas unitarias y con la consola web de H2.

## 1. Tecnologías y versiones

| Herramienta | Versión |
|---|---|
| Java (JDK) | 21 |
| Spring Boot | 4.1.1 |
| Maven | 3.9.x (el wrapper del proyecto descarga la 3.9.16) |
| Empaquetado | WAR (`jpa-workshop-0.0.1-SNAPSHOT.war`) |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | H2 en memoria, con su consola web (`spring-boot-h2console`) |
| Lombok | Versión que administra Spring Boot |
| Pruebas | JUnit (Jupiter) y Mockito, ambos en la versión que trae Spring Boot 4.1.1 |

## 2. Estructura del proyecto

El repositorio tiene **dos niveles de carpeta**. El `pom.xml` está en la carpeta interna `jpa-workshop`:

```
jpa-workshop-stevan-andrade-and-juan-soriano/   <- raíz del repositorio (git)
└── jpa-workshop/                               <- AQUÍ está el pom.xml, desde aquí se ejecuta todo
    ├── pom.xml
    ├── mvnw / mvnw.cmd
    └── src
        ├── main
        │   ├── java/com/example
        │   │   ├── jpa_workshop    JpaWorkshopApplication, ServletInitializer
        │   │   ├── model           10 entidades JPA y enums
        │   │   ├── repository      un repositorio por entidad
        │   │   └── service         UserService, RoleService, PermissionService
        │   └── resources
        │       ├── application.properties
        │       ├── schema.sql      crea las tablas
        │       └── data.sql        datos iniciales
        └── test/java/com/example
            ├── jpa_workshop        JpaWorkshopApplicationTests
            └── service             PermissionServiceTest, RoleServiceTest, UserServiceTest
```

## 3. Requisitos previos

1. Tener instalado **Java 21**. Se comprueba con:
   ```bash
   java -version
   ```
2. Tener **Maven** instalado (`mvn -version`) o usar el wrapper que viene en el proyecto (`./mvnw` en Linux/Mac, `mvnw.cmd` en Windows).
3. No hace falta instalar ninguna base de datos: H2 va embebida y se crea en memoria al arrancar.

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

- Puerto: `8082`
- Ruta base: `/jpa-workshop`

> Si se despliega el `.war` en un **Tomcat externo**, el puerto lo define Tomcat y la ruta base es el nombre del archivo
> (por ejemplo `/jpa-workshop-0.0.1-SNAPSHOT`). Spring Boot 4 usa Servlet 6.1, por lo que se recomienda un Tomcat 11.

## 7. Consola web de H2

Con la aplicación corriendo, abrir en el navegador:

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

## 8. Pruebas (JUnit + Mockito)

Hay pruebas unitarias para los tres servicios. Usan **JUnit** para las aserciones y **Mockito** para simular los
repositorios (`@Mock`, `@InjectMocks`, `when`, `verify`), así que **no necesitan base de datos ni levantar Spring**.

| Clase de prueba | Qué prueba |
|---|---|
| `PermissionServiceTest` | Consulta, inserción, actualización y eliminación de permisos |
| `RoleServiceTest` | Consulta, inserción, actualización y eliminación de roles, y agregar o quitar permisos de un rol |
| `UserServiceTest` | Consulta, inserción, actualización y eliminación de usuarios |
| `JpaWorkshopApplicationTests` | Prueba de integración: arranca el contexto completo con H2 y valida entidades, repositorios y scripts SQL |

Además, en los tres servicios se prueban las reglas de negocio (ver sección 9).

### Comandos

Siempre desde la carpeta del `pom.xml` (`jpa-workshop/jpa-workshop`):

Ejecutar **todas** las pruebas:

```bash
mvn test
```

Ejecutar **una sola clase** de prueba:

```bash
mvn -Dtest=PermissionServiceTest test
mvn -Dtest=RoleServiceTest test
mvn -Dtest=UserServiceTest test
```

Ejecutar **solo las tres de los servicios** (Mockito):

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
- Correo, nombre de rol y nombre de permiso son únicos (lo garantiza la base de datos con `UNIQUE` en `schema.sql`).
- Cuando algo no se cumple, el servicio lanza `IllegalArgumentException` con un mensaje claro.

## 10. Problemas frecuentes

| Problema | Causa y solución |
|---|---|
| `there is no POM in this directory` | Se está en la raíz del repositorio. Entrar a la carpeta interna `jpa-workshop`. |
| `mvn` no se reconoce como comando | Maven no está instalado o no está en el `PATH`. Usar `./mvnw` o `mvnw.cmd`. |
| Error de versión de Java al compilar | El proyecto necesita Java 21. Revisar con `java -version`. |
| `Port 8081 was already in use` | Otro programa usa el puerto. Cerrarlo o cambiar `server.port` en `application.properties`. |
| La consola de H2 abre vacía o no encuentra las tablas | El JDBC URL del login no es `jdbc:h2:mem:finanzas`. Corregirlo. |
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

La primera pantalla muestra una fila por paquete (`com.example.service`, `com.example.model`, `com.example.jpa_workshop`) y una fila `Total`. Las columnas principales son:

| Columna | Qué significa |
|---|---|
| Missed Instructions / Cov. | Instrucciones del código que las pruebas no ejecutaron y el porcentaje que sí se ejecutó |
| Missed Branches / Cov. | Lo mismo para las ramas de los `if` (el camino verdadero y el falso). `n/a` significa que el paquete no tiene `if` |
| Missed / Lines | Líneas sin ejecutar y total de líneas |
| Missed / Methods | Métodos sin ejecutar y total de métodos |
| Missed / Classes | Clases sin ejecutar y total de clases |

Las barras de colores ayudan a leerlo rápido: verde es código cubierto por las pruebas y rojo es código que ninguna prueba ejecutó.

### Resultado actual

| Paquete | Cobertura de instrucciones | Cobertura de ramas |
|---|---|---|
| `com.example.service` | 100 % | 100 % |
| `com.example.model` | 27 % | n/a |
| `com.example.jpa_workshop` | 15 % | n/a |
| **Total** | **87 %** | **100 %** (0 de 30 ramas sin cubrir) |

Los servicios (`UserService`, `RoleService`, `PermissionService`) quedan al 100 % porque las pruebas con JUnit y Mockito se enfocan en ellos: consulta, inserción, actualización, eliminación y reglas de negocio.
Los paquetes `model` y `jpa_workshop` tienen menos cobertura porque no tienen pruebas propias: son las entidades (sus `getters` y `setters` los genera Lombok) y la clase que arranca la aplicación.
Es un resultado esperado para este taller, ya que el objetivo es probar la lógica de los servicios.

Para ver el detalle de un paquete o una clase, se hace clic en su nombre dentro del reporte. Las líneas en verde se ejecutaron, las amarillas se ejecutaron solo a medias (falta una rama de un `if`) y las rojas nunca se ejecutaron.

## 12. Autores

Stevan Andrade y Juan Soriano - Universidad Icesi, Computación en Internet II.
