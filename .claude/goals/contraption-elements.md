# Contraption Elements Refactor

## Goal
Refactor contraption system to use ContraptionElement abstraction where each element:
- Stores its own config/data (BlockState, furniture definition, etc.)
- Handles its own rendering via swarm methods
- Manages its own lifecycle (assembly → tick → render → disassemble)

## Scope
- Create ContraptionElement interface
- Implement ContraptionBlock, ContraptionFurniture, ContraptionSeat
- Each element type owns its rendering logic
- Elements handle their own disassembly
- Incremental migration: keep old code working while new system is built

## Status
Design phase - architecture document created

## Constraints
- Nothing breaks during migration
- Can do incremental changes
- Old capture/render paths stay functional until fully migrated
