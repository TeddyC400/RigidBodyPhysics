plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "io.github.tblaze"
version = "1.0"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.github.Minestom:Minestom:f1d5940855")
    implementation("com.github.stephengold:Libbulletjme:20.2.0")
}

tasks.test {
    useJUnitPlatform()
}