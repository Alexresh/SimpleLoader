plugins {
    id("java")
}

group = "ru.obabok"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/repository/maven-releases")
}

val prismLibDir = when {
    System.getProperty("os.name").lowercase().contains("win") ->
        "C:/Users/Alexresh/AppData/Roaming/PrismLauncher/libraries"
    else ->
        "/home/alex/.local/share/PrismLauncher/libraries"
}
fun prismLib(path: String) = files("$prismLibDir/$path")


dependencies {
    //если не скачивается
    implementation("org.spongepowered:mixin:0.8.5")
    //то
    //implementation(fileTree("libs") { include("mixin*.jar") })
    implementation("org.ow2.asm:asm:9.9.1")
    implementation("org.ow2.asm:asm-tree:9.9.1")
    implementation("org.ow2.asm:asm-commons:9.9.1")
    implementation("org.ow2.asm:asm-util:9.9.1")
    implementation("org.ow2.asm:asm-analysis:9.9.1")
    implementation("com.google.code.gson:gson:2.10.1")

    compileOnly(prismLib("com/mojang/minecraft/26.1.1/minecraft-26.1.1-client.jar"))
    compileOnly(prismLib("com/mojang/brigadier/1.3.10/brigadier-1.3.10.jar"))
    compileOnly(prismLib("org/jspecify/jspecify/1.0.0/jspecify-1.0.0.jar"))
    compileOnly(prismLib("it/unimi/dsi/fastutil/8.5.18/fastutil-8.5.18.jar"))
}

tasks.register<Copy>("copyDependencies") {
    from(configurations.runtimeClasspath)
    from(configurations.compileClasspath) {
        include { it.file.absolutePath.contains("PrismLauncher") }
    }
    into("libs")
}

tasks.register<Copy>("updatePrism") {
    from(layout.buildDirectory.file("libs/SimpleLoader-1.0-SNAPSHOT.jar"))
    into("$prismLibDir/ru/obabok/simpleloader/1/")
    rename { "simpleloader-1.jar" }
}

tasks.named("build") {
    finalizedBy("updatePrism")
}

tasks.getByName<Jar>("jar") {
    manifest {
        attributes["Main-Class"] = "ru.obabok.InstallerEntrypoint"
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
}