group = "com.lkeehl.elevators"
version = "5.0.0-beta.8"
description = "Fifth major semantic for the Elevators Spigot Plugin"
java.sourceCompatibility = JavaVersion.VERSION_21

plugins {
    java
    `maven-publish`
    id("io.github.goooler.shadow") version "8.1.7"
}

repositories {
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://jitpack.io/#TechFortress/GriefPrevention/")
    maven ( "https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
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
    compileOnly(platform("com.intellectualsites.bom:bom-newest:1.32"))
    compileOnly("org.eclipse.jdt:org.eclipse.jdt.annotation:2.2.700")

    //compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    //compileOnly("org.spigotmc:spigot:1.20.6-R0.1-SNAPSHOT") // The full Spigot server with no shadowing. Requires mavenLocal.

    compileOnly("com.github.TechFortress:GriefPrevention:16.18")
    compileOnly("com.intellectualsites.plotsquared:plotsquared-core")
    compileOnly("com.intellectualsites.plotsquared:plotsquared-bukkit") { isTransitive = false }
    compileOnly("world.bentobox:bentobox:1.24.0-SNAPSHOT")
    compileOnly("com.griefdefender:api:2.1.0-SNAPSHOT")
    compileOnly("io.github.fabiozumbi12.RedProtect:RedProtect-Core:8.1.2"){ exclude(group = "*") }
    compileOnly("io.github.fabiozumbi12.RedProtect:RedProtect-Spigot:8.1.2"){ exclude(group = "*") }
    compileOnly("me.clip:placeholderapi:2.11.6")

    compileOnly("com.github.decentsoftware-eu:decentholograms:2.8.3")
    compileOnly("de.oliver:FancyHolograms:2.0.6")

    compileOnly("net.kyori:adventure-text-minimessage:4.14.0")
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.3.0")

    implementation("de.rapha149.signgui:signgui:2.5.0")
    implementation("net.wesjd:anvilgui:1.10.0-SNAPSHOT")
    implementation("com.github.technicallycoded:FoliaLib:main-SNAPSHOT")

}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}


tasks {

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    shadowJar {
        relocate("de.rapha149.signgui", "com.lkeehl.elevators.util.signgui")
        relocate("net.wesjd.anvilgui", "com.lkeehl.elevators.util.anvilgui")
        relocate("com.tcoded.folialib", "com.lkeehl.elevators.util.folialib")
    }

}