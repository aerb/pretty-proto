plugins {
  kotlin("multiplatform")
}

kotlin {
  js {
    browser
  }

  sourceSets {
    commonMain {
      dependencies {
        api(kotlin("stdlib-common"))
      }
    }
    commonTest {
      dependencies {
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
      }
    }
    js().compilations["main"].defaultSourceSet {
      dependencies {
        api(kotlin("stdlib-js"))
      }
    }
    js().compilations["test"].defaultSourceSet {
      dependencies {
        implementation(kotlin("test-js"))
      }
    }
  }
}