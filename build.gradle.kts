plugins {
    id("java")
    id("com.gradleup.shadow") version "9.4.2"
}

group = "io.gitlab.icestom"

repositories {
    mavenCentral()
    mavenLocal()
//    maven("https://repo.hypera.dev/snapshots/") maven died
}

val runIcestom: Configuration by configurations.creating

dependencies {
    val icestomVersion = "io.gitlab.icestom:IceStom:0.4.1"

    compileOnly(icestomVersion)
    runIcestom(icestomVersion)

    implementation("dev.lu15:luckperms-minestom:5.5-SNAPSHOT")
    implementation("org.spongepowered:configurate-hocon:3.7.3")
    implementation("com.h2database:h2:2.3.232")
}

val shadePrefix = "${project.group}.shaded"

tasks.shadowJar {
    archiveClassifier.set("")
    mergeServiceFiles()

    // LuckPerms and its Minestom port
    relocate("dev.lu15", "$shadePrefix.dev.lu15")
    relocate("me.lucko", "$shadePrefix.me.lucko")
    relocate("net.luckperms", "$shadePrefix.net.luckperms")
}

tasks.jar {
    archiveClassifier.set("plain")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.register<JavaExec>("runIcestom") {
    group = "icestom"
    description = "Runs an Icestom server with this plugin loaded from its compiled classes."

    dependsOn(tasks.named("classes")) // includes processResources

    mainClass.set("io.gitlab.icestom.icestom.IceStom")
    classpath = runIcestom +
            configurations.runtimeClasspath.get() +
            files(tasks.processResources.get().destinationDir)

    val runDir = layout.projectDirectory.dir("run").asFile
    workingDir = runDir
    doFirst { runDir.mkdirs() }

    val classesDir = sourceSets.main.get().output.classesDirs.first()
    systemProperty("io.gitlab.icestom.development", classesDir.absolutePath)
    standardInput = System.`in`
}