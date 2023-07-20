group = "com.lkeehl.elevators"
version = "5.0"
description = "Fifth major semantic for the Elevators Spigot Plugin"
java.sourceCompatibility = JavaVersion.VERSION_1_8

plugins {
    java
    `maven-publish`
}

repositories {
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly("org.spongepowered:configurate-yaml:4.1.2")
    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT") // The Spigot API with no shadowing. Requires the OSS repo.
    compileOnly("org.spigotmc:spigot:1.17-R0.1-SNAPSHOT") // The full Spigot server with no shadowing. Requires mavenLocal.
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}
