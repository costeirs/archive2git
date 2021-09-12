import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.5.30"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("com.github.ben-manes.versions") version "0.39.0"
    id("io.gitlab.arturbosch.detekt") version("1.18.1")
}

group = "com.costeira"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0-RC")
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.13.0.202109080827-r")
    implementation("commons-io:commons-io:2.11.0")

    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("org.slf4j:slf4j-simple:1.7.32")

    testImplementation(kotlin("test-junit5"))
    val jupiterVersion = "5.8.0"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$jupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jupiterVersion")
}

tasks.test {
    useJUnitPlatform()
}

java.sourceCompatibility = JavaVersion.VERSION_11
java.targetCompatibility = JavaVersion.VERSION_11

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlinx.cli.ExperimentalCli"
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlinx.serialization.ExperimentalSerializationApi"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.io.path.ExperimentalPathApi"
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "com.costeira.archive2git.MainKt"
        attributes["Implementation-Version"] = archiveVersion
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }) {
        exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/maven/", "/META-INF/*.kotlin_module", "about.html", "plugin.properties")
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}

// From https://github.com/pinterest/ktlint/blob/master/ktlint/build.gradle#L41
tasks.register("shellExecutable") {
    description = "Creates self-executable file, that runs generated jar"
    group = "Distribution"

    inputs.files(tasks.named("jar"))
    outputs.file(file("${buildDir}/bin/${project.name}"))

    doLast {
        val execFile = outputs.files.files.first()
        execFile.outputStream().use {
            it.write("#!/bin/sh\n\nexec java -Xmx512m -jar \"\$0\" \"\$@\"\n\n".toByteArray())
            it.write(inputs.files.singleFile.readBytes())
        }
        execFile.setExecutable(true, false)
    }
}
