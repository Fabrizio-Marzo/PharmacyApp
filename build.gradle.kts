plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "8.1.0" // Aggiungi il plugin Shadow
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("App")  // Assicurati che questo sia il nome completo della tua classe principale (compreso il pacchetto se necessario)
}

val javaFXModules = listOf(
    "base",
    "controls",
    "fxml",
    "swing",
    "graphics"
)

val supportedPlatforms = listOf("linux", "mac", "win") // Piattaforme supportate

dependencies {
    val javaFxVersion = "21"  // Usa la versione corretta di JavaFX
    for (platform in supportedPlatforms) {
        for (module in javaFXModules) {
            implementation("org.openjfx:javafx-$module:$javaFxVersion:$platform")
        }
    }

    // https://mvnrepository.com/artifact/org.webjars/font-awesome
    implementation("org.webjars:font-awesome:6.5.2")
    // https://mvnrepository.com/artifact/de.jensd/fontawesomefx
    implementation("de.jensd:fontawesomefx:8.2")
    // https://mvnrepository.com/artifact/com.mysql/mysql-connector-j
    implementation("com.mysql:mysql-connector-j:8.1.0")
}

tasks.named<JavaExec>("run") {
    jvmArgs = listOf(
        "--module-path", classpath.asPath, // Aggiungi il percorso dei moduli di JavaFX
        "--add-modules", "javafx.controls,javafx.fxml" // Aggiungi i moduli necessari
    )
}



