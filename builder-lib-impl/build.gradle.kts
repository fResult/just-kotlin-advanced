plugins {
  kotlin("jvm")
}

group = "com.fResult"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  testImplementation(kotlin("test"))
  implementation("com.google.devtools.ksp:symbol-processing-api:2.3.6")
  implementation(project(":builder-lib-annotations"))
}

kotlin {
  jvmToolchain(24)
}

tasks.test {
  useJUnitPlatform()
}
