plugins {
    base
}

group = "dev.cronos"
version = "0.1.0"

val githubOwner = "ibrahimbayramli"
val githubRepository = "cronos"
val githubPackagesUrl = "https://maven.pkg.github.com/$githubOwner/$githubRepository"
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
    description = "Deploy all Maven modules to GitHub Packages (Maven registry, Gradle-compatible)"
    dependsOn("mavenPackage")
    commandLine(
        "mvn",
        "--batch-mode",
        "deploy",
        "-DskipTests",
        "-s",
        ".github/maven/settings.xml",
    )
    environment(
        mapOf(
            "GITHUB_TOKEN" to (System.getenv("GITHUB_TOKEN") ?: ""),
            "GITHUB_ACTOR" to (System.getenv("GITHUB_ACTOR") ?: githubOwner),
        ),
    )
}

tasks.register("publishToGitHubPackages") {
    group = "publishing"
    description = "Publish Cronos JARs to GitHub Packages for Maven and Gradle consumers"
    dependsOn("mavenDeploy")
}

tasks.register("verifyConsumerGradleSnippet") {
    group = "verification"
    description = "Prints Gradle coordinates and repository URL for consumers"
    doLast {
        println("Repository: $githubPackagesUrl")
        println("Dependency: implementation(\"${project.group}:$starterArtifact:$version\")")
    }
}

tasks.register("verifyConsumerMavenSnippet") {
    group = "verification"
    description = "Prints Maven coordinates and repository URL for consumers"
    doLast {
        println("Repository: $githubPackagesUrl")
        println("Dependency: dev.cronos:$starterArtifact:$version")
    }
}

tasks.register("printPublishingInfo") {
    group = "publishing"
    description = "Show GitHub Packages URLs and published artifact coordinates"
    doLast {
        println(
            """
            |
            |Cronos GitHub Packages
            |------------------------
            |Registry : $githubPackagesUrl
            |Packages : https://github.com/$githubOwner/$githubRepository/packages
            |Release  : https://github.com/$githubOwner/$githubRepository/releases/tag/v$version
            |
            |Artifacts (version $version)
            |  - dev.cronos:cronos-core:$version
            |  - dev.cronos:cronos-spring-boot-starter:$version
            |
            |Publish  : ./gradlew publishToGitHubPackages
            |         (requires GITHUB_TOKEN with write:packages)
            |
            """.trimMargin(),
        )
    }
}
