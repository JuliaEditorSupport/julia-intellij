pluginManagement {
	repositories {
		maven("https://oss.sonatype.org/content/repositories/snapshots/")
		gradlePluginPortal()
	}
}
rootProject.name = "julia-intellij"
include("core")
project(":core").projectDir = file("core")
