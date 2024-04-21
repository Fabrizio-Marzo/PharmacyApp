plugins {
     // Apply the java plugin to add support for Java
    java
}

repositories {
    mavenCentral()
}

val javaFXModules = listOf(
    "base",
    "controls",
    "fxml",
    "swing",
    "graphics"
)

val supportedPlatforms = listOf("linux", "mac", "win") // All required for OOP

dependencies {
    
    // JavaFX: comment out if you do not need them
    val javaFxVersion = 15
    for (platform in supportedPlatforms) {
        for (module in javaFXModules) {
            implementation("org.openjfx:javafx-$module:$javaFxVersion:$platform")
        }
    }

    // https://mvnrepository.com/artifact/org.webjars/font-awesome
    implementation("org.webjars:font-awesome:6.5.2")

    //implementation ("de.jensd:fontawesomefx-fontawesome:4.7.0-11")

   // https://mvnrepository.com/artifact/de.jensd/fontawesomefx
    implementation("de.jensd:fontawesomefx:8.2")


}


