@file:Suppress("UnstableApiUsage")

plugins {
    id("clans.java-conventions")
    id("clans.spigot-conventions")
    id("clans.placeholderapi-conventions")
    id("clans.upstream-conventions")
    id("clans.dynmap-conventions")
    id("clans.publish-conventions")
    id("com.github.johnrengelman.shadow")
}    

repositories {
    mavenLocal()
    maven { url = uri("https://repo.minebench.de/") }  
}

dependencies {
    implementation(project(getSubproject("api")))
//    implementation(project(getSubproject("cli")))
    implementation("com.github.the-h-team:Enterprise:${findProperty("enterpriseVersion")}")
    implementation("com.github.the-h-team.Panther:panther-placeholders:${findProperty("pantherVersion")}")
    implementation("com.github.the-h-team.Labyrinth:labyrinth-gui:${findProperty("labyrinthVersion")}")
    implementation("com.github.the-h-team.Labyrinth:labyrinth-regions:${findProperty("labyrinthVersion")}")
    implementation("com.github.the-h-team.Panther:panther-paste:${findProperty("pantherVersion")}")
    implementation("com.github.the-h-team.Labyrinth:labyrinth-common:${findProperty("labyrinthVersion")}")
    implementation("com.github.the-h-team.Labyrinth:labyrinth-skulls:${findProperty("labyrinthVersion")}")
}

tasks.withType<ProcessResources> {
    inputs.apply{
        // Always check these properties for updates so that the plugin.yml is regenerated if they change (without a clean)
        property("version", project.version)
        property("description", project.description)
        property("url", findProperty("url")!!)
    }
    // Include all resources...
    filesMatching("plugin.yml") {
        // but only expand properties for the plugin.yml
        expand(project.properties)
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
//    dependsOn.add("${getSubproject("cli")}:shadowJar")
    archiveFileName.set("${rootProject.name}-${project.version}.jar")
    archiveClassifier.set("plugin")
    dependencies {
        include(project(getSubproject("api")))
//        include(project(getSubproject("cli")))
    }
}
