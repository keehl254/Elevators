plugins {
    java
    `maven-publish`
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

repositories {
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://jitpack.io/#TechFortress/GriefPrevention/")
    maven ("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.glaremasters.me/repository/bloodshot/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io/")
    maven("https://repo.fancyinnovations.com/releases")
    maven("https://repo.spongepowered.org/maven/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.bg-software.com/repository/api/")
    maven("https://repo.keehl.me/releases/")
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly(platform("com.intellectualsites.bom:bom-newest:1.32"))

    compileOnly("com.destroystokyo.paper:paper-api:1.14.4-R0.1-SNAPSHOT") {
        exclude(group = "org.yaml", module = "snakeyaml")
    }
    implementation ("io.papermc:paperlib:1.0.8")

    compileOnly("net.kyori:adventure-text-minimessage:4.14.0")
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.3.0")

    implementation("de.rapha149.signgui:signgui:2.5.0")
    implementation("net.wesjd:anvilgui:1.10.5-SNAPSHOT")
    implementation("com.tcoded:FoliaLib:0.4.3")
    implementation("org.yaml:snakeyaml:2.2")
    implementation("org.bstats:bstats-bukkit:3.1.1-SNAPSHOT") // Snapshot because the release does not have Folia support

}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "elevators-core"
        }
    }

    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("../../build/repo"))
        }
    }
}