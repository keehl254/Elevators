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
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(platform("com.intellectualsites.bom:bom-newest:1.32"))
    implementation("org.eclipse.jdt:org.eclipse.jdt.annotation:2.2.700")
    implementation("net.kyori:adventure-text-minimessage:4.14.0")
    implementation("net.kyori:adventure-api:4.14.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.0")

    implementation("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")
    implementation("org.spigotmc:spigot:1.19-R0.1-SNAPSHOT") // The full Spigot server with no shadowing. Requires mavenLocal.
    implementation("org.spongepowered:configurate-yaml:4.1.2")

    implementation("com.github.TechFortress:GriefPrevention:16.18")
    implementation("com.plotsquared:PlotSquared-Core:6.11.1")
    implementation("com.plotsquared:PlotSquared-Bukkit:6.11.1") { isTransitive = false }
    implementation("world.bentobox:bentobox:1.24.0-SNAPSHOT")
    implementation("com.griefdefender:api:2.1.0-SNAPSHOT")
    implementation("io.github.fabiozumbi12.RedProtect:RedProtect-Core:8.1.1-SNAPSHOT")
    implementation("io.github.fabiozumbi12.RedProtect:RedProtect-Spigot:8.1.1-SNAPSHOT")
    implementation("me.clip:placeholderapi:2.11.3")

    implementation("com.github.decentsoftware-eu:decentholograms:2.8.3")
    //implementation("de.oliver:FancyHolograms:2.0.0")

}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
