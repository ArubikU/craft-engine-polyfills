# Local test server

A headless Paper + CraftEngine harness for runtime-validating the plugin. The
`testserver/` directory is gitignored (jars + world are large/regenerable).

## One-time setup
```bash
mkdir -p testserver/plugins
# Paper 1.21.11
curl -sL "https://api.papermc.io/v2/projects/paper/versions/1.21.11/builds/69/downloads/paper-1.21.11-69.jar" -o testserver/paper.jar
# CraftEngine (free, Modrinth) — match the API version in gradle.properties as closely as available
curl -sL "https://cdn.modrinth.com/data/tRX6FMfQ/versions/2JnyXLo1/craft-engine-paper-plugin-26.6.1.jar" -o testserver/plugins/craft-engine.jar
# PacketEvents (hard runtime dependency of this plugin)
curl -sL "https://cdn.modrinth.com/data/HYKaKraK/versions/vIMIVfSx/packetevents-spigot-2.12.2.jar" -o testserver/plugins/packetevents.jar
echo "eula=true" > testserver/eula.txt
```
`testserver/server.properties` should set `online-mode=false`, a flat world, and a non-default port.

Paper 1.21.11 requires **Java 21** (the repo's main toolchain is 21; a 17 JDK will not boot the server).

## Build + deploy + boot
```bash
./gradlew shadowJar
cp target/craft-engine-polyfills-*.jar testserver/plugins/
cd testserver
# boot headless, auto-stop after startup, capture console
( sleep 75; echo stop ) | "/c/Program Files/Java/jdk-21/bin/java" -Xmx2G -jar paper.jar --nogui > boot.log 2>&1
```

## What "good" looks like
```
[CraftEnginePolyfill] CraftEngine Polyfills Enabled
[CraftEnginePolyfill] Loaded N recipes.
```
and **no** `ClassNotFoundException` / `NoSuchMethodError` / `NoClassDefFoundError` /
`AbstractMethodError` in `boot.log` — those are the runtime symptoms of an API mismatch the
compiler cannot catch. The 26.6.2-API migration was validated this way against the CE 26.6.1 plugin
(plugin enabled, no exceptions).

In-world behavior (placing custom blocks, machines processing, conveyors moving items) additionally
requires CraftEngine resource-pack/config that defines the custom blocks/items, plus a connected client.
