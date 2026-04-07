plugins {
    id("java")
}

group = "ru.obabok"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://spongepowered.org")
}

dependencies {
//    testImplementation(platform("org.junit:junit-bom:5.10.0"))
//    testImplementation("org.junit.jupiter:junit-jupiter")
//    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    //если не скачивается
    //implementation("org.spongepowered:mixin:0.8.5")
    //то
    implementation(fileTree("libs") { include("mixin*.jar") })
    implementation("org.ow2.asm:asm:9.9.1")
    implementation("org.ow2.asm:asm-tree:9.9.1")
    implementation("org.ow2.asm:asm-commons:9.9.1")
    implementation("org.ow2.asm:asm-util:9.9.1")
    implementation("org.ow2.asm:asm-analysis:9.9.1")

    implementation("com.google.code.gson:gson:2.10.1")

    compileOnly(files("/home/alex/.local/share/PrismLauncher/libraries/com/mojang/minecraft/26.1.1/minecraft-26.1.1-client.jar"))
    compileOnly(files("/home/alex/.local/share/PrismLauncher/libraries/com/mojang/brigadier/1.3.10/brigadier-1.3.10.jar"))
    compileOnly(files("/home/alex/.local/share/PrismLauncher/libraries/org/jspecify/jspecify/1.0.0/jspecify-1.0.0.jar"))
    compileOnly(files("/home/alex/.local/share/PrismLauncher/libraries/it/unimi/dsi/fastutil/8.5.18/fastutil-8.5.18.jar"))
}

tasks.register<Copy>("copyDependencies") {
    from(configurations.runtimeClasspath)
    into("libs")
}

tasks.register<Copy>("updatePrism") {
    // Откуда берем свежий файл
    from(layout.buildDirectory.file("libs/SimpleLoader-1.0-SNAPSHOT.jar"))
    // Куда кладем (путь, который ты нашел)
    into(File("/home/alex/.local/share/PrismLauncher/libraries/ru/obabok/simpleloader/1/"))
    // Переименовываем, если Prism ждет имя "simpleloader-1.jar"
    rename { "simpleloader-1.jar" }
}

tasks.named("build") {
    finalizedBy("updatePrism")
}