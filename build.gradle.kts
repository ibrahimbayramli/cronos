plugins {
    base
}

group = "com.github.ibrahimbayramli"
version = "0.1.1"

val githubOwner = "ibrahimbayramli"
val githubRepository = "cronos"
val mavenCentralArtifactUrl =
    "https://central.sonatype.com/artifact/com.github.$githubOwner/cronos-spring-boot-starter"
val starterArtifact = "cronos-spring-boot-starter"

tasks.register<Exec>("mavenPackage") {
    group = "build"
    description = "Build all Maven modules (includes dashboard UI)"
    commandLine(
        "mvn",
        "--batch-mode",
        "clean",
        "package",
        "-DskipTests",
    )
}

tasks.register<Exec>("mavenDeploy") {
    group = "publishing"
    description = "Deploy starter artifact to Maven Central"
    dependsOn("mavenPackage")
    commandLine(
        "mvn",
        "--batch-mode",
        "deploy",
        "-DskipTests",
        "-Prelease",
        "-s",
        ".github/maven/settings.xml",
    )
    environment(
        mapOf(
            "MAVEN_CENTRAL_USERNAME" to (System.getenv("MAVEN_CENTRAL_USERNAME") ?: ""),
            "MAVEN_CENTRAL_PASSWORD" to (System.getenv("MAVEN_CENTRAL_PASSWORD") ?: ""),
            "GPG_PASSPHRASE" to (System.getenv("GPG_PASSPHRASE") ?: ""),
        ),
    )
}

tasks.register("publishToMavenCentral") {
    group = "publishing"
    description = "Publish Cronos starter to Maven Central"
    dependsOn("mavenDeploy")
}

tasks.register("verifyConsumerGradleSnippet") {
    group = "verification"
    description = "Prints Gradle coordinates for consumers"
    doLast {
        println("Dependency: implementation(\"${project.group}:$starterArtifact:$version\")")
    }
}

tasks.register("verifyConsumerMavenSnippet") {
    group = "verification"
    description = "Prints Maven coordinates for consumers"
    doLast {
        println("Dependency: com.github.ibrahimbayramli:$starterArtifact:$version")
    }
}

tasks.register("printPublishingInfo") {
    group = "publishing"
    description = "Show Maven Central coordinates and publishing info"
    doLast {
        println(
            """
            |
            |Cronos Maven Central
            |--------------------
            |Artifact : https://github.com/$githubOwner/$githubRepository
            |Central  : $mavenCentralArtifactUrl
            |Release  : https://github.com/$githubOwner/$githubRepository/releases/tag/v$version
            |
            |Dependency (version $version)
            |  com.github.ibrahimbayramli:cronos-spring-boot-starter:$version
            |
            |Publish  : ./gradlew publishToMavenCentral
            |         (requires MAVEN_CENTRAL_USERNAME / MAVEN_CENTRAL_PASSWORD / GPG)
            |
            """.trimMargin(),
        )
    }
}
