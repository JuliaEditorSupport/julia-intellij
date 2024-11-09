fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

dependencies {
    listOf(
        ":core",
    ).forEach {
        compileOnly(project(it))
        testCompileOnly(project(it))
        runtimeOnly(project(it))
    }
    intellijPlatform {
        val platformVersionProvider: Provider<String> by rootProject.extra
        create("IC", platformVersionProvider.get(), useInstaller = properties("useInstaller").get().toBoolean())
        bundledPlugins("com.intellij.java")
    }
}

