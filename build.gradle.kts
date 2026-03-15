plugins {
    java
}

group = "com.chaseoes"
version = "2.4.1"
description = "FirstJoinPlus by chaseoes."

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release = 21
    }

    jar {
        archiveFileName = "FirstJoinPlus.jar"
    }
}
