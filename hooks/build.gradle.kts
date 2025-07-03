plugins {
    java
    id("xyz.wagyourtail.jvmdowngrader") version "1.3.0"
    id("com.gradleup.shadow") version "8.3.6"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

jvmdg.downgradeTo = JavaVersion.VERSION_1_8
jvmdg.shadePath = {
    it.substringBefore(".").substringBeforeLast("-").replace(Regex("[.;\\[/]"), "-")
}

repositories {
    mavenCentral()
    mavenLocal()

    maven("https://repo.maven.apache.org/maven2/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://jitpack.io/#TechFortress/GriefPrevention/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.glaremasters.me/repository/bloodshot/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io/")
    maven("https://repo.fancyinnovations.com/releases")
    maven("https://repo.spongepowered.org/maven/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.bg-software.com/repository/api/")
    maven("https://repo.thenextlvl.net/releases/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://maven.devs.beer/")
}

dependencies {
    implementation(project(":Core"))

    compileOnly(platform("com.intellectualsites.bom:bom-newest:1.32"))

    // Our hooks project is allowed to reference later versions. We must be very careful, though.
    compileOnly("io.papermc.paper:paper-api:1.21.6-R0.1-SNAPSHOT")

    compileOnly("net.kyori:adventure-text-minimessage:4.14.0")
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.3.0")

    compileOnly("com.github.TechFortress:GriefPrevention:16.18")
    compileOnly("com.intellectualsites.plotsquared:plotsquared-core")
    compileOnly("com.intellectualsites.plotsquared:plotsquared-bukkit") { isTransitive = false }
    compileOnly("world.bentobox:bentobox:1.24.0-SNAPSHOT")
    compileOnly("com.griefdefender:api:2.1.0-SNAPSHOT")
    compileOnly("io.github.fabiozumbi12.RedProtect:RedProtect-Core:8.1.2"){ exclude(group = "*") }
    compileOnly("io.github.fabiozumbi12.RedProtect:RedProtect-Spigot:8.1.2"){ exclude(group = "*") }
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.bgsoftware:SuperiorSkyblockAPI:2024.4")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.13")
    compileOnly("net.thenextlvl.protect:api:3.0.3")
    compileOnly("net.thenextlvl.protect:plugin:2.1.2")
    compileOnly("net.thenextlvl.core:nbt:2.3.2") { isTransitive = false }
    compileOnly ("com.github.angeschossen:LandsAPI:7.15.20")

    compileOnly("com.github.decentsoftware-eu:decentholograms:2.8.3")
    compileOnly("de.oliver:FancyHolograms:2.4.2")

    compileOnly("dev.lone:api-itemsadder:4.0.10")
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    relocate("de.rapha149.signgui", "me.keehl.elevators.util.signgui")
    relocate("net.wesjd.anvilgui", "me.keehl.elevators.util.anvilgui")
    relocate("com.tcoded.folialib", "me.keehl.elevators.util.folialib")
    relocate("io.papermc.lib", "me.keehl.elevators.util.paperlib")
    relocate("org.yaml.snakeyaml", "me.keehl.elevators.util.config.snakeyaml")
    relocate("org.bstats", "me.keehl.elevators.util.bstats")

    archiveClassifier.set("all")
}

tasks.named("build") {
    dependsOn("downgradeJar")
}

tasks {

    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

}
