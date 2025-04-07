# RPUniverse Configuration System

This document provides detailed information about the RPUniverse configuration system, including validation, migration, and best practices.

## Overview

The RPUniverse configuration system has been enhanced to provide the following features:

- **Configuration Validation**: Automatically validates the configuration file when the plugin starts
- **Configuration Migration**: Automatically migrates older configuration versions to the latest version
- **Error Handling**: Provides detailed error messages for invalid configurations
- **Backup System**: Creates backups of configurations before migration

## Configuration Validation

When the plugin starts, the configuration file is automatically validated to ensure that all required fields are present and have the correct data types. If there are any errors in the configuration, they will be reported in the console.

The validation system checks:

- Required sections and fields
- Data types (boolean, integer, string)
- Valid values for enumeration fields (e.g., bossBarColor must be one of: BLUE, GREEN, PINK, PURPLE, RED, WHITE, YELLOW)

If validation fails, the plugin will attempt to fix the issues by using default values. However, it's recommended to fix the issues manually to ensure the configuration matches your desired settings.

## Configuration Migration

The configuration system includes a version number (`configVersion`) that is used to track changes to the configuration structure. When you upgrade to a new version of RPUniverse, the plugin will automatically detect if your configuration needs to be migrated.

Migration process:

1. The plugin detects that your configuration version is older than the current version
2. A backup of your current configuration is created (e.g., `config_backup_v3.yml`)
3. The migration process updates your configuration to the latest version
4. The updated configuration is saved

The migration process preserves your existing settings while adding new fields and sections introduced in the latest version.

## Best Practices

Here are some best practices for working with the RPUniverse configuration:

1. **Always stop the server before editing the configuration file**
2. **Make a backup of your configuration before making significant changes**
3. **Pay attention to console warnings about invalid configuration values**
4. **Use the configuration validation system to check your changes**
5. **Don't modify the `configVersion` field manually**

## Troubleshooting

If you encounter issues with your configuration:

1. Check the console for validation errors
2. Restore from a backup if needed
3. If all else fails, delete the configuration file and let the plugin generate a new one

## Configuration Sections

The configuration file is organized into the following sections:

- **General**: Basic plugin settings
- **Jobs**: Job system settings
- **Holograms**: Hologram settings
- **Data**: Data storage settings
- **Properties**: Property system settings
- **BasicNeeds**: Basic needs system settings
- **ChestLimit**: Chest limit settings
- **InventoryLimit**: Inventory limit settings
- **Balance**: Balance tracker settings
- **Debug**: Debug mode settings
- **Modules**: Module system settings

Each section contains specific settings related to that feature. See the comments in the configuration file for details about each setting.