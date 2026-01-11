plugins {
    id("java")
    `maven-publish`
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(11))
}

tasks.compileJava {
    options.release.set(11)
}

repositories {
    mavenCentral()
    maven("https://repo.keehl.me/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    compileOnly("com.destroystokyo.paper:paper-api:1.14.4-R0.1-SNAPSHOT") {
        exclude(group = "org.yaml", module = "snakeyaml")
    }

    compileOnly("com.tcoded:FoliaLib:0.4.3")
    compileOnly("org.jetbrains:annotations:26.0.2-1")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "elevators-api"
        }
    }

    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("../../build/repo"))
        }
    }
}