
plugins {
    val kotlinVersion = "2.0.21"
    kotlin("plugin.jpa") version kotlinVersion
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-jpa")

    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("com.h2database:h2")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}