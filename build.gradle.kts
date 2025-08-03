group = "me.keehl"
version = "5.0.0-beta.14"

plugins {
    kotlin("jvm") version "1.9.0"
    id("com.gradleup.shadow") version "8.3.6"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    compileOnly(kotlin("stdlib"))
    implementation(files("Hooks${File.separator}build${File.separator}libs${File.separator}Hooks-${version}-downgraded.jar"))

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
        maven ("https://repo.papermc.io/repository/maven-public/")
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

// Custom task to build Core and Hooks together
tasks.register("buildElevators") {
    group = "build"
    description = "Builds the Elevators plugin"

    val gradleCommand = if (System.getProperty("os.name").startsWith("Windows")) {
        listOf("cmd", "/c", "gradlew.bat")
    } else {
        listOf("./gradlew")
    }

    exec {
        commandLine = gradleCommand + ":Hooks:downgradeJar"
    }

    dependsOn(updatePluginYml)
    dependsOn(tasks.named("shadowJar"))

    doLast {
        println("JAR built at: ${tasks.named("jar").get().outputs.files.singleFile}")
    }

}