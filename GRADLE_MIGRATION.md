# StockMaster backend — Gradle Kotlin DSL migration

Student: Dinsitha W. A. M. — IT25101443

## Resumed state

No partial Gradle migration files existed. The Maven POM was intact. Six existing
controller CORS changes from the integration review were preserved, not rewritten.

## Settings

- Spring Boot **4.1.0**, unchanged; Gradle Wrapper **9.6.0**.
- Java toolchain/release **17**; verification runtime **17.0.20.1**.
- Group/version: `com.example` / `0.0.1-SNAPSHOT`, unchanged.
- Project/archive: `stock-management-system-backend`, unchanged.
- Package: `com.example.stockmanagementsystembackend`, unchanged.
- Spring dependency management plugin **1.1.7** imports Boot's dependency BOM.
- Maven compiler configuration becomes JavaCompile settings with UTF-8,
  `-parameters`, release 17 and Lombok annotation processing.
- Spring Boot Maven packaging becomes `bootJar`. Lombok and DevTools remain
  excluded from the executable JAR.
- Surefire becomes `useJUnitPlatform()` with its original test-only properties:

```properties
spring.datasource.url=jdbc:h2:mem:stockmaster;MODE=MySQL;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=none
```

Tests use isolated H2, not shared MySQL. The actual application configuration is
`src/main/resources/application.yml`; it was neither changed nor renamed. Runtime
MySQL configuration, schema, mappings and API routes are unchanged.

## Complete dependency mapping

All 14 direct Maven dependencies are retained. Versions marked managed still use
Spring Boot 4.1.0's BOM, including the original Lombok version override.

| Maven dependency | Original version/scope | Gradle configuration |
| --- | --- | --- |
| org.springframework.boot:spring-boot-starter-web | managed / compile | implementation |
| org.springframework.boot:spring-boot-starter-restclient | managed / compile | implementation |
| org.springframework.boot:spring-boot-starter-data-jpa | managed / compile | implementation |
| org.springframework.boot:spring-boot-starter-validation | managed / compile | implementation |
| com.mysql:mysql-connector-j | managed / runtime | runtimeOnly |
| org.springframework.boot:spring-boot-starter-security | managed / compile | implementation |
| org.springframework.boot:spring-boot-starter-thymeleaf | managed / compile | implementation |
| org.springdoc:springdoc-openapi-starter-webmvc-ui | 2.8.5 / compile | implementation |
| com.openhtmltopdf:openhtmltopdf-pdfbox | 1.0.10 / compile | implementation |
| org.projectlombok:lombok | 1.18.30 / provided and processor | compileOnly, annotationProcessor, testCompileOnly, testAnnotationProcessor |
| org.springframework.boot:spring-boot-devtools | managed / optional runtime | developmentOnly |
| org.springframework.boot:spring-boot-starter-test | managed / test | testImplementation |
| org.springframework.boot:spring-boot-starter-security-test | managed / test | testImplementation |
| com.h2database:h2 | managed / test | testImplementation |

Build compatibility addition: BOM-managed
`testRuntimeOnly("org.junit.platform:junit-platform-launcher")` for Gradle's JUnit
Platform execution. No test source changes were required.

## Files

Created: `build.gradle.kts`, `settings.gradle.kts`, `gradlew`, `gradlew.bat`,
`gradle/wrapper/gradle-wrapper.jar`, `gradle/wrapper/gradle-wrapper.properties`,
and this report. The wrapper distribution is pinned to its official SHA-256.

Modified: `.gitignore`, adding only Gradle cache/output ignores (`/.gradle/` and
`/build/`). All existing ignore rules remain.

Removed: tracked `pom.xml`, after Gradle compilation, tests and clean build passed.
It can be recovered from Git history. `mvnw`, `mvnw.cmd` and `.mvn/` were absent.
Existing ignored `target/` artifacts were left alone.

Hash comparison confirms all 157 protected source, test, resource, backend frontend
and seed files remain unchanged. The separate React project was not edited.
No functionality, validation, controller, repository, entity, service or database
changes were made. No commit or push was performed.

## Verification and usage

From the backend directory, with JAVA_HOME pointing to Java 17:

```powershell
.\gradlew.bat clean compileJava
.\gradlew.bat test
.\gradlew.bat clean build
```

All three passed in order, both before and after removing the POM. Each test run
executed **59 tests, zero failures, errors or skips**. Bytecode major version 61
confirms Java 17. The executable JAR contains Spring Boot 4.1.0 and MySQL, but not
Lombok, DevTools or H2. The harmless class-data-sharing warning from mock-based
tests was also present under Maven.

Start with `.\gradlew.bat bootRun` or:

```powershell
java -jar build/libs/stock-management-system-backend-0.0.1-SNAPSHOT.jar
```

Use the non-`plain` JAR. Test reports are in `build/reports/tests/test/index.html`
and `build/test-results/test`. No remaining migration issue was found.

References:
[Spring Boot Gradle plugin](https://docs.spring.io/spring-boot/gradle-plugin/introduction.html),
[Gradle Java compatibility](https://docs.gradle.org/current/userguide/compatibility.html).
