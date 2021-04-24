import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.4.32"
    kotlin("plugin.spring") version "1.4.32"
}

group = "pl.allegro.stypinski"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val springVersion = "2.3.10.RELEASE"
val junitVersion = "5.7.0"
val wiremockVersion = "2.27.2"

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web:$springVersion")
    implementation("org.springframework.boot:spring-boot-starter-hateoas:$springVersion")
    implementation("org.springframework.boot:spring-boot-starter-webflux:$springVersion")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:$springVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springVersion")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Junit
    implementation("org.junit.jupiter:junit-jupiter:$junitVersion")

    // WireMock
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.1")
    testImplementation("com.squareup.okhttp3:okhttp:4.9.1")
    testImplementation("com.github.tomakehurst:wiremock-jre8:2.27.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
