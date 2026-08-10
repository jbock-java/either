import org.gradle.api.JavaVersion;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.api.tasks.bundling.Jar;

plugins {
  id("java-library")
  id("com.vanniktech.maven.publish") version "0.37.0"
}

group = "io.github.jbock-java"

tasks.withType<JavaCompile>().configureEach {
  options.encoding = "UTF-8"
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<Javadoc>().configureEach {
  options.encoding = "UTF-8"
  (options as CoreJavadocOptions).addBooleanOption("Xdoclint:none", true)
}

repositories {
  mavenCentral()
}

dependencies {
  testImplementation(platform("org.junit:junit-bom:6.1.2"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Jar> {
  manifest {
    attributes["Implementation-Version"] = project.version
  }
}

tasks.named<Test>("test") {
  useJUnitPlatform()
}

// https://vanniktech.github.io/gradle-maven-publish-plugin/central/
mavenPublishing {
  coordinates("io.github.jbock-java", "either", project.version?.toString())

  pom {
    name = "either"
    packaging = "jar"
    description = "Functional error handling for Java"
    url = "https://github.com/jbock-java/either"

    licenses {
      license {
        name = "MIT License"
        url = "https://opensource.org/licenses/MIT"
      }
    }
    developers {
      developer {
        id = "Various"
        name = "Various"
        email = "jbock-java@gmx.de"
      }
    }
    scm {
      connection = "scm:git:https://github.com/jbock-java/either.git"
      developerConnection = "scm:git:https://github.com/jbock-java/either.git"
      url = "https://github.com/jbock-java/either"
    }
  }
  publishToMavenCentral()
  signAllPublications()
}
