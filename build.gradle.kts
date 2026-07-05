import java.util.Properties

plugins {
    base
}

group = "dev.cronos"
version = "0.1.1"

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
    description = "Deploy all Maven modules to GitHub Packages"
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
    description = "Alias for mavenDeploy — publishes Cronos to GitHub Packages"
    dependsOn("mavenDeploy")
}

tasks.register("verifyConsumerJitPackSnippet") {
    group = "verification"
    description = "Prints JitPack coordinates (no GitHub token required)"
    doLast {
        println("Repository: https://jitpack.io")
        println("Dependency: implementation(\"com.github.$githubOwner:$starterArtifact:$version\")")
    }
}

tasks.register("verifyConsumerGradleSnippet") {
    group = "verification"
    description = "Prints GitHub Packages Gradle coordinates"
    doLast {
        println("implementation(\"${project.group}:cronos-spring-boot-starter:$version\")")
        println("Repository: $githubPackagesUrl")
    }
}
