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
    maven("https://mvn.lumine.io/repository/maven-public/") // MythicMobs
    // ---- Optional plugin-bridge API repositories (see util/plugins/*) - every dependency pulled
    // from these is compileOnly and gated behind Bukkit.getPluginManager().isPluginEnabled(...) at
    // runtime, so this addon compiles and runs fine on a server missing any/all of these plugins.
    maven("https://jitpack.io") // Vault (VaultAPI), GriefPrevention, Towny, DecentHolograms
    maven("https://maven.enginehub.org/repo/") // WorldGuard + WorldEdit
    maven("https://repo.mikeprimm.com/") // Dynmap (DynmapCoreAPI)
    maven("https://repo.bluecolored.de/releases") // BlueMap
    maven("https://maven.citizensnpcs.co/repo") // Citizens
    maven("https://nexus.scarsz.me/content/groups/public/") // DiscordSRV
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") // PlaceholderAPI
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
    // ModelEngine R4 animated model support (compileOnly soft-dependency). All ME API calls
    // are behind ModelEngineMachineRenderer.available() and try/catch so this is safe to omit.
    compileOnly("com.ticxo.modelengine:ModelEngine:R4.0.7")
    // Advanced Slime Paper - compileOnly soft-dependency present only on the ASP fork. asp-api is the public
    // API; asp-nms is a stub of ASP's server classes (SlimeLevelInstance extends ServerLevel, SlimeBootstrap,
    // SlimeNMSBridgeImpl) so AspContraptionLevel can EXTEND SlimeLevelInstance and replicate its world-load
    // flow. Loaded ONLY when the ASP API is found at runtime (ContraptionLevel.create's reflection-guarded
    // factory), so a plain Paper server never touches these classes.
    compileOnly(files("libs/asp-api-4.2.0.jar"))
    compileOnly(files("libs/asp-nms.jar"))
    // ---- Optional plugin-bridge APIs (see util/plugins/*) - every one is compileOnly, and every
    // call into it is gated behind Bukkit.getPluginManager().isPluginEnabled("...") plus a
    // try/catch(Throwable) safety net, so this addon compiles and runs fine on a server missing
    // any/all of these plugins; only the ones actually installed do anything.
    // VaultAPI's POM drags in an ancient org.bukkit:bukkit:1.13.1 as a real (non-optional)
    // compile dependency, which collides with paper-api's own "org.bukkit:bukkit" capability at
    // resolution time - excluded since paper-api already provides everything Vault's API needs.
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {                   // Vault economy
        exclude(group = "org.bukkit", module = "bukkit")
    }
    compileOnly("net.luckperms:api:5.5")                                // LuckPerms
    // WorldGuard/WorldEdit's POMs pin STRICT versions of guava/gson/fastutil ("Mojang provides
    // X") that conflict with the (newer) versions paper-api's own dev bundle resolves to -
    // excluded so paper-api's copies win instead of a hard resolution failure.
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.14") {      // WorldGuard region flags
        exclude(group = "it.unimi.dsi", module = "fastutil")
        exclude(group = "com.google.guava", module = "guava")
        exclude(group = "com.google.code.gson", module = "gson")
    }
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.16") {        // WorldEdit selections/editing
        exclude(group = "it.unimi.dsi", module = "fastutil")
        exclude(group = "com.google.guava", module = "guava")
        exclude(group = "com.google.code.gson", module = "gson")
    }
    compileOnly("io.lumine:Mythic-Dist:5.6.1")                          // MythicMobs
    compileOnly("us.dynmap:DynmapCoreAPI:3.7-beta-6")                   // Dynmap markers
    compileOnly("de.bluecolored:bluemap-api:2.7.4") {                   // BlueMap markers
        exclude(group = "com.google.code.gson", module = "gson")
    }
    compileOnly("net.citizensnpcs:citizens-main:2.0.43-SNAPSHOT") {     // Citizens NPCs
        isTransitive = false
    }
    // GriefPrevention has no separate slim API jar - compiling against the FULL plugin jar (via
    // JitPack) is standard practice for it; isTransitive avoids pulling its own bundled Vault/etc.
    // copies into OUR resolution graph (we only need its own classes to compile against, never
    // anything it in turn depends on).
    // NOTE: Towny (com.github.TownyAdvanced:Towny) was deliberately left OUT - its JitPack build
    // for every version tag tried resolved to an empty stub jar (manifest only, no classes; a
    // build failure on JitPack's end, not a coordinate typo) - verified via javap before writing
    // any code against it, so no TownySupport bridge exists. Revisit if JitPack ever produces a
    // real build, or switch to compiling against a locally-supplied Towny jar instead.
    compileOnly("com.github.TechFortress:GriefPrevention:16.18.1") {    // GriefPrevention claims
        isTransitive = false
    }
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.8.11") { // DecentHolograms
        isTransitive = false
    }
    compileOnly("com.discordsrv:discordsrv:1.29.0") {                  // DiscordSRV
        isTransitive = false
    }
    // PlaceholderAPI - needs a real compile-time class to EXTEND (PlaceholderExpansion) for
    // Plugins.placeholderapi.register_placeholder, unlike every other PlaceholderSupport method
    // (still pure reflection, see that class) which only ever CALLS a static method.
    compileOnly("me.clip:placeholderapi:2.11.6")
    //implementation("net.bytebuddy:byte-buddy:${rootProject.properties["byte_buddy_version"]}")
    //implementation("net.bytebuddy:byte-buddy-agent:${rootProject.properties["byte_buddy_version"]}")
    // Bundled embedded database for SQLDriver's default "sqlite" backend (self-contained, no
    // external server needed - matches this project's "sovereignty" stance). A MySQL/MariaDB
    // backend is also supported via SQLDriver's config, but that driver is NOT bundled here (GPL
    // licensing + jar size) - an admin who wants it points database.yml at a driver already on
    // the server's classpath (very commonly already present via another plugin).
    implementation("org.xerial:sqlite-jdbc:3.46.1.3")
    // Redis client for RedisDriver ("Redis.*" scripting API) - a small, dependency-light client
    // (no separate connection-pool library needed beyond what Jedis itself ships).
    implementation("redis.clients:jedis:5.1.5")
    // JIT backend for ScriptBytecodeCompiler (see that class) - generates real JVM bytecode for the
    // pure-numeric/boolean subset of the .pf expression grammar instead of walking a lambda-closure
    // tree. Tiny (~120KB), so bundled directly rather than reaching for Paper's own internal/shaded
    // copy (a different, version-coupled ASM we don't want a compile-time dependency on).
    implementation("org.ow2.asm:asm:9.7")
    // Human-readable disassembly (Textifier/TraceClassVisitor) for ScriptClassCompilerTest —
    // test-only, never shipped in the jar.
    testImplementation("org.ow2.asm:asm-util:9.7")
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

sourceSets.test {
    java.exclude(
        "**/chainery/**",
        "**/contraption/**",
        "**/conveyor/**",
        "**/data/**",
        "**/fluid/**",
        "**/machine/menu/**",
        "**/machine/upgrade/**",
        "**/rotation/**"
    )
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
    softDepend = listOf("BetterModel", "ModelEngine", "Vault", "LuckPerms", "WorldGuard", "WorldEdit",
        "FastAsyncWorldEdit", "MythicMobs", "dynmap", "BlueMap", "PlaceholderAPI", "Citizens",
        "GriefPrevention", "DecentHolograms", "DiscordSRV")
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
    relocate("org.objectweb.asm", "dev.arubik.libs.asm")
    // NOT relocating org.sqlite: sqlite-jdbc's native library is a platform .dll/.so compiled with
    // JNI method symbols baked in for the ORIGINAL package name (Java_org_sqlite_core_NativeDB_...).
    // Relocating the Java class breaks that binding at runtime (UnsatisfiedLinkError - observed
    // live on a real server boot) since the .so's exported symbols don't move with the class.
    // Shipped unrelocated is safe here: Bukkit gives each plugin jar its own classloader, so even
    // another plugin bundling a different sqlite-jdbc version doesn't actually collide with this one.
    relocate("redis.clients.jedis", "dev.arubik.libs.jedis")
    relocate("org.apache.commons.pool2", "dev.arubik.libs.commonspool2")
    minimize {
        // Both are only ever reached via Class.forName/ServiceLoader reflection (JDBC driver
        // registration, Jedis's connection-pool wiring) - invisible to minimize()'s static
        // reference analysis, so it silently strips them as "unused" otherwise. That exact failure
        // was observed live: SQLDriver.init() threw ClassNotFoundException for the relocated
        // org.sqlite.JDBC class on a real server boot before this exclusion was added.
        exclude(dependency("org.xerial:sqlite-jdbc:.*"))
        exclude(dependency("redis.clients:jedis:.*"))
    }
    }
}