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
}

kotlin {
  jvmToolchain(24)
}

tasks.test {
  useJUnitPlatform()
}
