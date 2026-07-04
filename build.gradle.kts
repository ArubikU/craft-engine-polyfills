plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-rc1"
    id("de.eldoria.plugin-yml.bukkit") version "0.7.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.momirealms.net/releases/")
    maven { url = uri("https://repo.codemc.io/repository/maven-releases/") }
    maven { url = uri("https://repo.codemc.io/repository/maven-snapshots/") }
    maven("https://maven.blamejared.com/")
    maven("https://maven.nucleoid.xyz/")
}

dependencies {
    paperweight.paperDevBundle("${rootProject.properties["paper_version"]}-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:${rootProject.properties["paper_version"]}-R0.1-SNAPSHOT")
    compileOnly("net.momirealms:craft-engine-core:${rootProject.properties["craftengine_version"]}")
    compileOnly("net.momirealms:craft-engine-bukkit:${rootProject.properties["craftengine_version"]}")
    compileOnly("net.momirealms:craft-engine-nms-helper:${rootProject.properties["nms_helper_version"]}")
    compileOnly("it.unimi.dsi:fastutil:${rootProject.properties["fastutil_version"]}")
    compileOnly("net.kyori:adventure-platform-bukkit:4.4.1")
    compileOnly("com.github.retrooper:packetevents-spigot:2.9.5")
    // BetterModel animated render API (compileOnly soft-dependency). The bukkit-api jar
    // transitively pulls bettermodel-api which holds kr.toxicity.model.api.BetterModel.
    // NOTE: 3.x targets JVM 25; this project is Java 21, so we pin the latest Java-21
    // compatible release (2.2.0). Its API (BetterModel.model/create, Tracker.animate,
    // AnimationModifier) is identical to the 3.x sketch used by CrusherModelRenderer.
    compileOnly("io.github.toxicity188:bettermodel-bukkit-api:2.2.0")
    //implementation("net.bytebuddy:byte-buddy:${rootProject.properties["byte_buddy_version"]}")
    //implementation("net.bytebuddy:byte-buddy-agent:${rootProject.properties["byte_buddy_version"]}")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("io.papermc.paper:paper-api:${rootProject.properties["paper_version"]}-R0.1-SNAPSHOT")
    testImplementation("net.momirealms:craft-engine-core:${rootProject.properties["craftengine_version"]}")
    testImplementation("net.momirealms:craft-engine-bukkit:${rootProject.properties["craftengine_version"]}")
}


java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
    dependsOn(tasks.clean)
    // Temporary: cap the forked compiler JVM heap so it starts on a host with an exhausted page file.
    options.isFork = true
    options.forkOptions.jvmArgs = listOf("-Xmx384m", "-XX:+UseSerialGC", "-XX:MaxMetaspaceSize=256m")
}

tasks.test {
    useJUnitPlatform()
}

tasks.processResources {
    filteringCharset = "UTF-8"
    filesMatching(arrayListOf("craft-engine-polyfills.properties")) {
        expand(rootProject.properties)
    }
}

bukkit {
    main = "dev.arubik.craftengine.CraftEnginePolyfills"
    version = rootProject.properties["project_version"] as String
    name = "CraftEnginePolyfill"
    apiVersion = "1.20"
    author = "ArubikU"
    website = "https://github.com/ArubikU"
    depend = listOf("CraftEngine")
    softDepend = listOf("BetterModel")
    foliaSupported = true
    commands {
        create("cepolyfill") {
            description = "CraftEngine Polyfill main command"
            usage = "/<command>"
        }
    }
}

artifacts {
    archives(tasks.shadowJar)
}

tasks {
    shadowJar {
        archiveFileName = "${rootProject.name}-${rootProject.properties["project_version"]}.jar"
        destinationDirectory.set(file("$rootDir/target"))
    archiveClassifier.set("")
    dependencies{
        include { true }
    }
    relocate("net.bytebuddy", "dev.arubik.libs.bytebuddy") 
    minimize()
    }
}