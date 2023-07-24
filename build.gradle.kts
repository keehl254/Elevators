group = "com.lkeehl.elevators"
version = "5.0"
description = "Fifth major semantic for the Elevators Spigot Plugin"
java.sourceCompatibility = JavaVersion.VERSION_17

plugins {
    java
    `maven-publish`
}

repositories {
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://jitpack.io/#TechFortress/GriefPrevention/")
    maven ( "https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://repo.glaremasters.me/repository/bloodshot/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io/")
    maven("https://repo.fancyplugins.de/releases/")
    maven("https://repo.spongepowered.org/maven/")
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(platform("com.intellectualsites.bom:bom-newest:1.32"))
    implementation("org.eclipse.jdt:org.eclipse.jdt.annotation:2.2.700")
    implementation("de.oliver:FancyHolograms:1.1.0")


    compileOnly("org.spigotmc:spigot-api:1.19-R0.1-SNAPSHOT") // The Spigot API with no shadowing. Requires the OSS repo.
    compileOnly("org.spigotmc:spigot:1.19-R0.1-SNAPSHOT") // The full Spigot server with no shadowing. Requires mavenLocal.
    compileOnly("org.spongepowered:configurate-yaml:4.1.2")

    compileOnly("com.github.TechFortress:GriefPrevention:16.18")
    compileOnly("com.plotsquared:PlotSquared-Core:6.11.1")
    compileOnly("com.plotsquared:PlotSquared-Bukkit:6.11.1") { isTransitive = false }
    compileOnly("world.bentobox:bentobox:1.24.0-SNAPSHOT")
    compileOnly("com.griefdefender:api:2.1.0-SNAPSHOT")
    compileOnly("io.github.fabiozumbi12.RedProtect:RedProtect-Core:8.1.1-SNAPSHOT")
    compileOnly("io.github.fabiozumbi12.RedProtect:RedProtect-Spigot:8.1.1-SNAPSHOT")

    compileOnly("com.github.decentsoftware-eu:decentholograms:2.8.3")

}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
