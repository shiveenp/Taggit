import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // spring
    id("org.springframework.boot") version "2.6.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    // kotlin
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
    kotlin("plugin.jpa") version "1.6.10"
    // versioning releases
    id("pl.allegro.tech.build.axion-release") version "1.13.6"
    // packaging frontend assets
    id("org.siouan.frontend-jdk11") version "6.0.0"
}

group = "io.shiveenp"
java.sourceCompatibility = JavaVersion.VERSION_17
project.version = scmVersion.version

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(project(":frontend"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // utils
    implementation("com.vladmihalcea:hibernate-types-55:2.14.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.flywaydb:flyway-core:8.5.7")
    implementation("io.github.microutils:kotlin-logging:2.1.21")
    implementation("io.jsonwebtoken:jjwt-api:0.11.2")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.2")
    implementation("org.junit.jupiter:junit-jupiter:5.8.2")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
    implementation(platform("org.testcontainers:testcontainers-bom:1.14.3")) //bom for testcontainers
    // see: https://github.com/netty/netty/issues/11693
    runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.75.Final:osx-aarch_64")

    // kotlin
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    runtimeOnly("org.postgresql:postgresql")

    // testing
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("io.mockk:mockk:1.12.3")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("io.github.serpro69:kotlin-faker:1.10.0")
    testImplementation("org.apache.commons:commons-math3:3.6.1")
    testImplementation("io.kotest:kotest-assertions-core:5.2.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.register<Copy>("processFrontendResources") {
    val frontendBuildDir = file("../frontend/_static")
    val frontendResourcesDir = file("${project.buildDir}/resources/main/static")

    group = "Frontend"
    description = "Process frontend resources"
    dependsOn(project(":frontend").tasks.named("assembleFrontend"))

    from(frontendBuildDir)
    into(frontendResourcesDir)
}

tasks.named("bootJar") {
    dependsOn(tasks.named("processFrontendResources"))
}