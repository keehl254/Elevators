group = "me.keehl"
version = "5.0.0-beta.18"

plugins {
    java
    id("com.gradleup.shadow") version "8.3.6"
    id("de.eldoria.plugin-yml.bukkit") version "0.8.0"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(11))
}

tasks.compileJava {
    options.release.set(11)
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(files("hooks/build/libs/hooks-${version}-downgraded-shaded.jar"))

    compileOnly("com.destroystokyo.paper:paper-api:1.14.4-R0.1-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}

allprojects {
    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
        mavenLocal()

        maven("https://repo.codemc.org/repository/maven-public/")
        maven("https://repo.codemc.io/repository/maven-snapshots/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.keehl.me/releases/")
    }
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
        "ItemsAdder", "Oraxen", "Nexo"
    )

    commands {
        register("elevators") {
            description = "Access elevators commands"
            usage = "/elevators"
            aliases = listOf("ele")
        }
    }
}

tasks.compileJava {
    dependsOn(":hooks:shadeDowngradedApi")
}

tasks.assemble {
    dependsOn(":hooks:shadeDowngradedApi")
}

tasks.build {
    dependsOn(":hooks:shadeDowngradedApi")
}