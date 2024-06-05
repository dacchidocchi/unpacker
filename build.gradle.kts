import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("com.vanniktech.maven.publish") version "0.28.0"
    kotlin("jvm") version "1.9.20"
    `java-library`
}

group = "moe.nero"
version = "0.1.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}

mavenPublishing {
    publishToMavenCentral(host = SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()
    coordinates(
        groupId = project.group.toString(),
        artifactId = project.name,
        version = project.version.toString(),
    )
    pom {
        name.set(project.name)
        description.set("Unpacker for Dean Edward's p.a.c.k.e.r")
        url.set("https://github.com/nerocchi/unpacker")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/license/mit")
            }
        }
        developers {
            developer {
                id.set("dacchidocchi")
                name.set("Dacchi")
                url.set("https://github.com/dacchidocchi")
            }
        }
        scm {
            url.set("https://github.com/nerocchi/unpacker")
            connection.set("scm:git:git://github.com/nerocchi/unpacker.git")
            developerConnection.set("scm:git:ssh://git@github.com/nerocchi/unpacker.git")
        }
    }
}
