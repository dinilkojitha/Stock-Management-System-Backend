plugins {
    java
    id("org.springframework.boot") version "4.1.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "Web-Based Stock Management System for Multi-Branch Hotel Operations"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

// Preserve the POM's explicit versions and Spring Boot dependency management.
extra["lombok.version"] = "1.18.30"
val lombokVersion = "1.18.30"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-restclient")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    runtimeOnly("com.mysql:mysql-connector-j")

    // Existing shared-module dependencies, unchanged from Maven.
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")
    implementation("com.openhtmltopdf:openhtmltopdf-pdfbox:1.0.10")

    // Maven provided scope and annotation processing, excluded from the boot JAR.
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("com.h2database:h2")
    // Gradle's JUnit Platform execution needs an explicit runtime launcher.
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 17
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    // Preserve Surefire's isolated database settings; never test against MySQL.
    systemProperty("spring.datasource.url", "jdbc:h2:mem:stockmaster;MODE=MySQL;DB_CLOSE_DELAY=-1")
    systemProperty("spring.datasource.username", "sa")
    systemProperty("spring.datasource.password", "")
    systemProperty("spring.datasource.driver-class-name", "org.h2.Driver")
    systemProperty("spring.jpa.hibernate.ddl-auto", "none")
}

tasks.wrapper {
    gradleVersion = "9.6.0"
    distributionType = Wrapper.DistributionType.BIN
    distributionSha256Sum = "bbaeb2fef8710818cf0e261201dab964c572f92b942812df0c3620d62a529a01"
}
