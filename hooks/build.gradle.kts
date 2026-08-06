plugins {
    java
    id("xyz.wagyourtail.jvmdowngrader") version "1.3.4"
    id("com.gradleup.shadow") version "8.3.6"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.compileJava {
    options.release.set(21)
}

jvmdg.downgradeTo = JavaVersion.VERSION_11
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
    maven("https://repo.oraxen.com/releases")
    maven("https://repo.nexomc.com/releases")
    maven("https://repo.keehl.me/snapshots")
}

dependencies {
    implementation(project(":core"))

    compileOnly(platform("com.intellectualsites.bom:bom-newest:1.32"))

    // Our hooks project is allowed to reference later versions. We must be very careful, though.
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")

    compileOnly("net.kyori:adventure-text-minimessage:4.14.0")
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.3.0")

    compileOnly("com.github.TechFortress:GriefPrevention:16.18")
    compileOnly("com.intellectualsites.plotsquared:plotsquared-core")
    compileOnly("com.intellectualsites.plotsquared:plotsquared-bukkit") { isTransitive = false }
    compileOnly("world.bentobox:bentobox:1.24.0-SNAPSHOT")
    compileOnly("com.griefdefender:api:2.1.0-SNAPSHOT")
    compileOnly("io.github.fabiozumbi12.RedProtect:RedProtect-Core:8.1.2") { exclude(group = "*") }
    compileOnly("io.github.fabiozumbi12.RedProtect:RedProtect-Spigot:8.1.2") { exclude(group = "*") }
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.bgsoftware:SuperiorSkyblockAPI:2024.4")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.13") {
        exclude("com.google.code.gson", "gson")
    }
    compileOnly("net.thenextlvl:protect:3.1.3")
    compileOnly("com.github.angeschossen:LandsAPI:7.15.20")

    compileOnly("com.github.decentsoftware-eu:decentholograms:2.8.3")
    compileOnly("de.oliver:FancyHolograms:2.4.2")

    compileOnly("dev.lone:api-itemsadder:4.0.10")
    compileOnly("io.th0rgal:oraxen:1.190.0") { isTransitive = false }
    compileOnly("com.nexomc:nexo:1.8.0") //Nexo 1.X -> 1.X.0
    compileOnly("com.tcoded:FoliaLib:0.4.3")

    implementation("org.bstats:bstats-bukkit:3.1.0")
    implementation("dev.faststats.metrics:bukkit:0.7.5")

    implementation("me.keehl:dialog-builder:1.4-SNAPSHOT")
}

tasks.shadowJar {
    relocate("com.tcoded.folialib", "me.keehl.elevators.util.folialib")
    relocate("io.github.rvskele.paperlib", "me.keehl.elevators.util.paperlib")
    relocate("org.yaml.snakeyaml", "me.keehl.elevators.util.config.snakeyaml")
    relocate("org.bstats", "me.keehl.elevators.util.bstats")
    relocate("dev.faststats", "me.keehl.elevators.util.faststats")

    archiveClassifier.set("all")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}

tasks.assemble {
    dependsOn(tasks.named("shadeDowngradedApi"))
}

// Optional: if you publish or copy artifacts, you probably want the shaded jar:
tasks.build {
    dependsOn(tasks.named("shadeDowngradedApi"))
}
