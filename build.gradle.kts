buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.7.1")
        classpath("com.google.gms:google-services:4.4.0")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}