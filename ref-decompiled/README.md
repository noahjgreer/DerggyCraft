# Minecraft Beta 1.7.3 Reference (Decompiled)

This folder contains the fully mapped and decompiled Minecraft Beta 1.7.3 source code for reference.

## Contents

- **`sources/`** - Decompiled Java source files (713 files)
  - `net/minecraft/` - Core Minecraft classes (mapped with Biny b1.7.3+e0778a3)
  - `argo/` - JSON parser library
  - `com/jcraft/` - JOrbis audio codec
  - `paulscode/` - Sound system library

- **`assets/`** - Extracted game assets (83 files)
  - `achievement/` - Achievement icons
  - `armor/` - Armor textures
  - `art/` - Paintings
  - `environment/` - Sky, clouds, etc.
  - `font/` - Font textures
  - `gui/` - GUI elements
  - `item/` - Item textures
  - `lang/` - Language files
  - `mob/` - Mob textures
  - `terrain/` - Block textures
  - `terrain.png` - Main terrain atlas
  - `particles.png` - Particle atlas

## Usage

Use these sources as reference when implementing mixins or understanding vanilla behavior.
Do NOT compile or distribute these files - they are for reference only.

## Mappings

Decompiled using:
- **Mappings**: Biny `b1.7.3+e0778a3:v2` 
- **Decompiler**: CFR 0.152
