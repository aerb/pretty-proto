plugins {
  base
  kotlin("multiplatform") version "1.3.71" apply false
  kotlin("jvm") version "1.3.71" apply false
  kotlin("js") version "1.3.71" apply false
}

subprojects {
  group = "ca.aerb"
  version = "1.0-SNAPSHOT"

  repositories {
    mavenCentral()
  }
}