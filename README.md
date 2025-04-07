# RP Universe - Minecraft Role-Playing Control Plugin

[![](https://jitpack.io/v/Fami6Xx/RP-Universe.svg)](https://jitpack.io/#Fami6Xx/RP-Universe)
[![JavaDocs](https://img.shields.io/badge/JavaDocs-Latest-blue)](https://jitpack.io/com/github/Fami6Xx/RP-Universe/latest/javadoc/)
[![Version](https://img.shields.io/badge/Version-1.4.5-green)](https://polymart.org/resource/rpuniverse-minecraft-roleplay.5845)

## Introduction

RP Universe is a comprehensive Minecraft plugin designed to transform your server into a complete role-playing environment. It provides extensive control over game aspects from communication to property management, jobs, regions, and more. With growing interest in role-playing games, this plugin offers a robust solution for creating immersive RP experiences in Minecraft.

## Features

### Core Role-Playing Features
- **Role-Play Commands**: Express emotions and actions with commands like `/me`, `/do`, `/try`, `/status`, and `/doc`
- **Universal Chat System**: Global OOC (Out of Character) chat and configurable chat formats
- **Region Management**: Create and manage regions with visual boundaries
- **Property System**: Complete property management with buying, selling, and renting capabilities
- **Jobs System**: In-game job creation and management without editing configs
- **Basic Needs System**: Adds realistic needs like hunger, thirst, and bathroom requirements

### Administrative Features
- **Mod Mode & Admin Mode**: Special modes for server staff with enhanced capabilities
- **Player Tags**: Assign and manage custom tags for players
- **Comprehensive Permissions**: Detailed permission system for fine-grained access control
- **Inventory & Chest Limits**: Control how many items players can store

### Technical Features
- **Flexible Data Storage**: Store data locally (JSON) or in a database
- **Hologram Integration**: Uses DecentHolograms for visual elements
- **Economy Integration**: Works with any economy plugin that supports Vault
- **PlaceholderAPI Support**: Extends functionality with placeholders
- **Localization System**: Multi-language support for global servers
- **Developer API**: Extend the plugin with your own addons

## Requirements

- **Server Software**: Paper/Spigot 1.14 or higher
- **Java**: Java 21 or higher
- **Required Plugins**:
  - [Vault](https://www.spigotmc.org/resources/vault.34315/) + Any economy plugin
  - [DecentHolograms](https://www.spigotmc.org/resources/decentholograms.96927/)
- **Optional Plugins**:
  - [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
  - [OpenAudioMc](https://github.com/Mindgamesnl/OpenAudioMc) (recommended for voice chat)

## Installation

1. Download the latest version of RP Universe from [Polymart](https://polymart.org/resource/rpuniverse-minecraft-roleplay.5845)
2. Place the JAR file in your server's `plugins` folder
3. Install the required dependencies (Vault, an economy plugin, and DecentHolograms)
4. Start or restart your server
5. Configure the plugin using the generated config.yml file

## Configuration

After first run, the plugin will generate a `config.yml` file in the `plugins/RPUniverse` folder. This file contains various settings to customize the plugin's behavior:

- **General Settings**: Plugin prefix, language, and basic functionality options
- **Data Storage**: Configure how and where player data is stored
- **Economy Settings**: Configure economy integration and balance tracking
- **Region Settings**: Customize region visualization and behavior
- **Job Settings**: Configure job system behavior
- **Basic Needs Settings**: Adjust hunger, thirst, and other needs parameters
- **Inventory & Chest Limits**: Set limits for player inventories and chests

## Commands

### Role-Playing Commands
- `/me <message>` - Send a message as an action
- `/do <message>` - Send a message as a temporary status
- `/doc <seconds> <message>` - Create a hologram showing an action for a period of time
- `/try` - Perform an action with a 50% chance of success
- `/status <message>` - Set a persistent status
- `/stopstatus` - Remove your current status

### Job Commands
- `/jobs` - View available jobs
- `/createjob` - Start the job creation process (admin)
- `/switchjob [jobId]` - Switch between jobs

### Admin Commands
- `/modmode [player]` - Toggle moderator mode
- `/adminmode [player]` - Toggle administrator mode
- `/settag <player> <tag>` - Set a player's tag
- `/tag` - View your current tag

### Chat Commands
- `/globalooc <message>` - Send a message to global OOC chat (aliases: `/ooc`, `/gchat`)

### Other Commands
- `/consumables` - Open the basic needs menu
- `/poop` - Empty poop meter
- `/pee` - Empty pee meter
- `/properties` - Open the properties menu
- `/rpuniverse` - Main plugin command (alias: `/rpu`)

## Permissions

### User Permissions
- `rpu.user` - Grants access to all user commands
- `rpu.me` - Allows use of the `/me` command
- `rpu.do` - Allows use of the `/do` command
- `rpu.try` - Allows use of the `/try` command
- `rpu.status` - Allows use of the `/status` command
- `rpu.stopstatus` - Allows use of the `/stopstatus` command
- `rpu.globalooc` - Allows use of the global OOC chat
- `rpu.doc` - Allows use of the `/doc` command

### Admin Permissions
- `rpu.admin` - Grants access to all admin commands
- `rpu.createjob` - Allows creation of jobs
- `rpu.modmode` - Allows use of moderator mode
- `rpu.adminmode` - Allows use of administrator mode
- `rpu.settag` - Allows setting player tags
- `rpu.properties` - Allows access to the properties system
- `rpu.core.command` - Allows use of the core `/rpu` command

### Special Permissions
- `rpu.multiplejobs` - Allows having multiple jobs simultaneously
- `rpu.basicneeds.edit` - Allows editing basic needs settings

## For Developers

### API Usage
RP Universe provides a comprehensive API for developers to extend the plugin's functionality. The API allows you to:

- Access and modify player data
- Interact with the jobs system
- Manage properties and regions
- Create custom menus
- Add custom data storage options

### Maven/Gradle Integration
Add RP Universe to your project using JitPack:

**Maven:**
```
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.Fami6Xx</groupId>
    <artifactId>RP-Universe</artifactId>
    <version>[VERSION]</version>
    <scope>provided</scope>
</dependency>
```

**Gradle:**
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compileOnly 'com.github.Fami6Xx:RP-Universe:[VERSION]'
}
```

For detailed API documentation, see the [JavaDocs](https://jitpack.io/com/github/Fami6Xx/RP-Universe/latest/javadoc/).

## Support & Contribution

- **Issues & Suggestions**: Report issues or suggest features on our [GitHub repository](https://github.com/Fami6Xx/RP-Universe)
- **Discord**: Join our [community](https://discord.gg/NyyJa5xQV9) for support and discussions (coming soon)
- **Contributing**: Pull requests are welcome! See our contribution guidelines for more information

## License

RP Universe is licensed under the [MIT License](LICENSE).

## Download

[<img src="https://images.polymart.org/resource/5845/default.jpg" width="480" alt="Download RPUniverse | Minecraft Roleplay on Polymart.org" title="Download RPUniverse | Minecraft Roleplay on Polymart.org">](https://polymart.org/resource/rpuniverse-minecraft-roleplay.5845?utm_source=product-materials-image&utm_medium=referral&utm_campaign=product-5845-materials-image-default&utm_content=product-5845-user-2406-markdown)
