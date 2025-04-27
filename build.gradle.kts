import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val projectVersion: String by project

plugins {
  idea
  kotlin("jvm") version "2.1.0" apply false
}

// Remove unnecessary mirrors
allprojects {
  repositories {
    mavenCentral()
    maven(url = "https://s01.oss.sonatype.org")
    gradlePluginPortal()
    mavenLocal()
    google()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven(url = "https://jitpack.io")
  }
}

subprojects {
  apply(plugin = "java-library")
  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "idea")

  extensions.configure<JavaPluginExtension> {
    toolchain {
      languageVersion.set(JavaLanguageVersion.of(21))
    }
  }

  tasks.register("generateAssetList") {
    val assetsFolder = file("${rootDir}/assets/")
    val assetsFile = file("${assetsFolder}/assets.txt")
    inputs.dir(assetsFolder)

    doLast {
      if (assetsFile.exists()) {
        assetsFile.delete()
      }
      assetsFolder.walkTopDown()
        .filter { it.isFile }
        .map { assetsFolder.toPath().relativize(it.toPath()).toString() }
        .sorted()
        .forEach { relativePath ->
          assetsFile.appendText("$relativePath\n")
        }
    }
  }

  tasks.named<ProcessResources>("processResources") {
    dependsOn("generateAssetList")
  }

  tasks.withType<JavaCompile> {
    options.isIncremental = true
  }

  tasks.withType<KotlinCompile> {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
  }

  version = projectVersion
  extra["appName"] = "ImpactGame"
}
