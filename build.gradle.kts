group = "me.keehl"
version = "5.0.0-beta.19"

plugins {
    java
    id("xyz.wagyourtail.jvmdowngrader") version "1.3.4"
    id("com.gradleup.shadow") version "8.3.6"
    id("de.eldoria.plugin-yml.bukkit") version "0.8.0"
}

allprojects {
    group = rootProject.group
    version = rootProject.version
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
    maven("https://repo.keehl.me/releases")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Compile against 1.14.4 since it is our min supported version.
    // Exclude SnakeYAML because Bukkits version is missing stuff.
    compileOnly("com.destroystokyo.paper:paper-api:1.14.4-R0.1-SNAPSHOT") {
        exclude(group = "org.yaml", module = "snakeyaml")
    }

    compileOnly("net.kyori:adventure-text-minimessage:4.14.0")
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.3.0")

    implementation(project(":hooks"))
    implementation(project(":api"))
    implementation("org.bstats:bstats-bukkit:3.1.0")
    implementation("dev.faststats.metrics:bukkit:0.7.5")
    implementation("me.keehl:dialog-builder:1.4-SNAPSHOT")
    implementation("com.tcoded:FoliaLib:0.4.3")
    implementation("org.yaml:snakeyaml:2.2")
    implementation("io.papermc:paperlib:1.0.8")
}


bukkit {
    name = "Elevators"
    authors = listOf("Keehl")
    main = "me.keehl.elevators.ElevatorsPlugin"
    description = "A lightweight and simple means of vertical transportation for Spigot"
    apiVersion = "1.14"
    foliaSupported = true

    loadBefore = listOf("SuperiorSkyblock2", "Lands")
    softDepend = listOf(
        "PlaceholderAPI", "RedProtect", "Vault", "HolographicDisplays", "GriefPrevention", "GriefDefender", "CMI",
        "PlotSquared", "BentoBox", "DecentHolograms", "FancyHolograms", "ProtocolLib", "WorldGuard", "Protect",
        "ItemsAdder", "Oraxen"
    )

    commands {
        register("elevators") {
            description = "Access elevators commands"
            usage = "/elevators"
            aliases = listOf("ele")
        }
    }
}

jvmdg.downgradeTo = JavaVersion.VERSION_11
jvmdg.shadePath = {
    it.substringBefore(".").substringBeforeLast("-").replace(Regex("[.;\\[/]"), "-")
}

tasks.shadowJar {
    relocate("com.tcoded.folialib", "me.keehl.elevators.util.folialib")
    relocate("io.papermc.lib", "me.keehl.elevators.util.paperlib")
    relocate("org.yaml.snakeyaml", "me.keehl.elevators.util.config.snakeyaml")
    relocate("org.bstats", "me.keehl.elevators.util.bstats")
    relocate("dev.faststats", "me.keehl.elevators.util.faststats")

    archiveClassifier.set("all")
}


tasks.processResources {
    filteringCharset = Charsets.UTF_8.name()
}

tasks.javadoc {
    options.encoding = Charsets.UTF_8.name()
}

tasks.test {
    useJUnitPlatform()
}

tasks.compileJava {
    options.encoding = Charsets.UTF_8.name()
}

tasks.assemble {
    dependsOn(tasks.named("shadeDowngradedApi"))
}

tasks.build {
    dependsOn(tasks.named("shadeDowngradedApi"))
}