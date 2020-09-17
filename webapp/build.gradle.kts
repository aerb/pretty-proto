plugins {
  kotlin("js")
  distribution
}

group = "ca.aerb"
version = "1.0-SNAPSHOT"

repositories {
  maven("https://kotlin.bintray.com/kotlin-js-wrappers/")
  jcenter()
}

kotlin {
  target {
    browser
  }
}

dependencies {
  implementation(project(":pretty-proto"))

  implementation(kotlin("stdlib-js"))
  implementation("org.jetbrains:kotlin-react:16.13.0-pre.94-kotlin-1.3.70")
  implementation("org.jetbrains:kotlin-react-dom:16.13.0-pre.94-kotlin-1.3.70")
  implementation(npm("react", "16.13.1"))
  implementation(npm("react-dom", "16.13.1"))
  implementation("org.jetbrains:kotlin-css:1.0.0-pre.94-kotlin-1.3.70")
  implementation("org.jetbrains:kotlin-styled:1.0.0-pre.94-kotlin-1.3.70")
  implementation(npm("inline-style-prefixer", "3.0.5"))
  implementation(npm("styled-components", "5.2.0"))
}

distributions {
  main {
    contents {
      from("src/main/resources")
      from("$buildDir/distributions/pretty-proto.js")
      into("/")
    }
  }
}

listOf("distZip", "installDist").forEach {
  tasks.named(it).configure {
    dependsOn(tasks.getByName("browserWebpack"))
  }
}

tasks.named("distTar").configure {
  enabled = false
}