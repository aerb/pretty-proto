import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost

plugins {
  base
  kotlin("multiplatform") version "1.3.71" apply false
  kotlin("jvm") version "1.3.71" apply false
  kotlin("js") version "1.3.71" apply false
  "com.vanniktech.maven.publish.base"
}

buildscript {
  dependencies {
    classpath("com.vanniktech:gradle-maven-publish-plugin:0.20.0")
  }
}


subprojects {
  group = "ca.aerb"
  version = "1.0-SNAPSHOT"

  repositories {
    mavenCentral()
  }
}

allprojects {
  plugins.withId("com.vanniktech.maven.publish.base") {
    configure<PublishingExtension> {
      repositories {
        maven {
          name = "test"
          setUrl("file://${project.rootProject.buildDir}/localMaven")
        }
        /**
         * Want to push to an internal repository for testing?
         * Set the following properties in ~/.gradle/gradle.properties.
         *
         * internalUrl=YOUR_INTERNAL_URL
         * internalUsername=YOUR_USERNAME
         * internalPassword=YOUR_PASSWORD
         */
        val internalUrl = providers.gradleProperty("internalUrl")
        if (internalUrl.isPresent) {
          maven {
            name = "internal"
            setUrl(internalUrl)
            credentials(PasswordCredentials::class)
          }
        }
      }
    }

    configure<MavenPublishBaseExtension> {
      publishToMavenCentral(SonatypeHost.S01)
      val inMemoryKey = project.findProperty("signingInMemoryKey") as String?
      if (!inMemoryKey.isNullOrEmpty()) {
        signAllPublications()
      }
      pom {
        description.set("description")
        name.set(project.name)
        url.set("https://github.com/aerb/pretty-proto")
        licenses {
          license {
            name.set("The Apache Software License, Version 2.0")
            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            distribution.set("repo")
          }
        }
        scm {
          url.set("https://github.com/aerb/pretty-proto")
        }
      }
    }
  }
}


