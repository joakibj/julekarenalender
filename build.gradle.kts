import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.github.julekarenalender"
version = "3.0.0"

plugins {
    kotlin("jvm") version "1.4.20"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    java
    application
}

sourceSets.main {
    java.srcDirs("src/main/java", "src/main/kotlin")
}

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://kotlin.bintray.com/kotlinx")
    maven(url = "https://dl.bintray.com/kodein-framework/Kodein-DB")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.dizitart:potassium-nitrite:3.4.2")
    implementation("org.jetbrains.kotlinx:kotlinx-cli-jvm:0.3")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.suppressWarnings = true

val compileJava: JavaCompile by tasks
compileJava.options.encoding = "UTF-8"

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shaddowJar") {
    manifest {
        attributes(mapOf("Main-Class" to "com.github.julekarenalender.JulekarenalenderKt"))
    }
}

application {
    mainClassName = "com.github.julekarenalender.JulekarenalenderKt"
}
