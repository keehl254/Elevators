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
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(platform("com.intellectualsites.bom:bom-newest:1.32"))
    implementation("org.eclipse.jdt:org.eclipse.jdt.annotation:2.2.700")


    compileOnly("org.spigotmc:spigot-api:1.19-R0.1-SNAPSHOT") // The Spigot API with no shadowing. Requires the OSS repo.
    compileOnly("org.spigotmc:spigot:1.19-R0.1-SNAPSHOT") // The full Spigot server with no shadowing. Requires mavenLocal.
    compileOnly("org.spongepowered:configurate-yaml:4.1.2")
    compileOnly("com.github.TechFortress:GriefPrevention:16.18")

    compileOnly("com.plotsquared:PlotSquared-Core:6.11.1")
    compileOnly("com.plotsquared:PlotSquared-Bukkit:6.11.1") { isTransitive = false }
    compileOnly("world.bentobox:bentobox:1.23.2")

}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
