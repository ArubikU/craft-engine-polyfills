# Migration Guide: CraftEngine Update

This guide documents the changes required to update `craft-engine-polyfills` to work with `craft-engine` version `0.0.66.26`.

## Version Updates

| Dependency | Old Version | New Version |
| :--- | :--- | :--- |
| `craft-engine` | `0.0.60` | `0.0.66.26` |
| `paper-api` | `1.21.7` | `1.21.11` |
| `nms-helper` | `1.0.34` | `1.0.163` |
| `fastutil` | `8.5.15` | `8.5.18` |
| `byte-buddy` | `1.17.5` | `1.18.3` |
| `gson` | `2.11.0` | `2.13.2` |
| `asm` | `9.8` | `9.9.1` |

## Package Refactoring

Several core classes in `craft-engine` have been moved to new packages.

| Class | Old Package | New Package |
| :--- | :--- | :--- |
| `BlockBehavior` | `net.momirealms.craftengine.core.block` | `net.momirealms.craftengine.core.block.behavior` |
| `RandomUtils` | `net.momirealms.craftengine.core.util` | `net.momirealms.craftengine.core.util.random` |
| `UseOnContext` | `net.momirealms.craftengine.core.item.context` | `net.momirealms.craftengine.core.world.context` |
| `BlockPlaceContext` | `net.momirealms.craftengine.core.item.context` | `net.momirealms.craftengine.core.world.context` |

## Repository Changes
The `craft-engine` library structure has changed significantly. The main code is now split into modules (e.g., `core`), which affects how classes are organized and imported.

- **Repository**: [https://github.com/Xiao-MoMi/craft-engine/tree/dev](https://github.com/Xiao-MoMi/craft-engine/tree/dev)
