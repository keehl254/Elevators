group = "me.keehl"
version = "5.0.0-beta.15"

plugins {
    kotlin("jvm") version "1.9.0"
    id("com.gradleup.shadow") version "8.3.6"
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

    implementation(files("hooks/build/libs/hooks-${version}-downgraded.jar"))

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

val updatePluginYml by tasks.registering {
    val pluginYmlFile = file("src/main/resources/plugin.yml")
    val versionString = project.version.toString()

    doLast {
        if (!pluginYmlFile.exists())
            throw GradleException("plugin.yml does not exist: ${pluginYmlFile.absolutePath}")

        val lines = pluginYmlFile.readLines().toMutableList()

        lines.removeAll { it.startsWith("version:") }

        lines.add("version: $versionString")

        pluginYmlFile.writeText(lines.joinToString(System.lineSeparator()))

        println("Updated plugin.yml with version: $versionString")
    }
}

tasks.compileJava {
    dependsOn(":hooks:downgradeJar")
}

tasks.shadowJar {
    dependsOn(updatePluginYml)
}