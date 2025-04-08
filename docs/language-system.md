# RPUniverse Language System

This document provides detailed information about the RPUniverse language system, including core components, implementation, and how to make your module localizable.

## Overview

The RPUniverse language system is a comprehensive framework for managing translations and localized text in the plugin. It provides a flexible and extensible system for defining, loading, and accessing translations, with support for both core translations and addon-specific translations.

Key features of the language system include:

- **Centralized Translation Management**: All translations are managed through a central `LanguageHandler` class
- **Configuration-Based Storage**: Translations are stored in a YAML configuration file (`languages.yml`)
- **Addon Translation Support**: Addons can define their own translations that integrate with the core system
- **Reflection-Based Loading**: Translations are automatically loaded using reflection
- **In-Game Editor**: A GUI-based editor for modifying translations in-game
- **Placeholder Support**: Support for placeholders in translations (e.g., `{playerName}`)

## Core Components

### LanguageHandler

`LanguageHandler` is the main class responsible for managing translations. It provides methods for loading, saving, and accessing translations. Key features include:

- Public string fields for core translations
- Methods for managing addon translations
- Loading translations from `languages.yml`
- Saving translations to `languages.yml`
- Accessing translations through direct field access or method calls

### AbstractAddonLanguage

`AbstractAddonLanguage` is an abstract base class for addon-specific language classes. It provides functionality for:

- Registering addon translations with the core language system
- Loading translations from the configuration
- Updating translations in the configuration
- Accessing translations through direct field access

### LanguageFieldsManager

`LanguageFieldsManager` provides utility methods for managing language fields, primarily used by the language editor. Key features include:

- Getting all language fields (both core and addon)
- Setting language field values
- Updating both the live instance and the configuration

### LanguageEditor

The language system includes a GUI-based editor for modifying translations in-game. This editor allows server administrators to:

- View all available translations
- Edit translations directly in-game
- Search for specific translations
- Modify both core and addon translations

## How to Make Your Module Localizable

There are two main approaches to making your module localizable:

### Approach 1: Direct Access to LanguageHandler

This approach is simpler but less modular. It involves directly accessing the `LanguageHandler` instance through `RPUniverse.getLanguageHandler()`.

Example:
```java
// Accessing a core translation
player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorOnlyPlayersCanUseThisCommandMessage));

// Adding an addon translation
RPUniverse.getLanguageHandler().addAddonTranslation("myModule.welcomeMessage", "Welcome to my module!");

// Accessing an addon translation
String message = RPUniverse.getLanguageHandler().getAddonTranslation("myModule.welcomeMessage");
player.sendMessage(FamiUtils.formatWithPrefix(message));
```

### Approach 2: Creating an AbstractAddonLanguage Subclass (Recommended)

This approach is more modular and provides better organization for your translations. It involves creating a subclass of `AbstractAddonLanguage` with string fields for your translatable messages.

Step 1: Create a language class for your module
```java
public class MyModuleLanguage extends AbstractAddonLanguage {
    // Define your translatable strings as public fields
    public String welcomeMessage = "Welcome to my module!";
    public String goodbyeMessage = "Goodbye from my module!";
    public String errorMessage = "An error occurred in my module!";
    
    // Create a singleton instance
    private static MyModuleLanguage instance;
    
    public static MyModuleLanguage getInstance() {
        if (instance == null) {
            instance = AbstractAddonLanguage.create(MyModuleLanguage.class);
        }
        return instance;
    }
    
    // Constructor
    public MyModuleLanguage() {
        // Call initLanguage() to register translations
        initLanguage();
    }
}
```

Step 2: Initialize your language class when your module loads
```java
public void onEnable() {
    // Initialize the language class
    MyModuleLanguage.getInstance();
    
    // Now you can use the translations
}
```

Step 3: Access translations in your code
```java
// Get the language instance
MyModuleLanguage lang = MyModuleLanguage.getInstance();

// Use the translations
player.sendMessage(FamiUtils.formatWithPrefix(lang.welcomeMessage));
player.sendMessage(FamiUtils.formatWithPrefix(lang.goodbyeMessage));
player.sendMessage(FamiUtils.formatWithPrefix(lang.errorMessage));
```

## Best Practices

1. **Use AbstractAddonLanguage for Modules**: For better organization and modularity, create a subclass of `AbstractAddonLanguage` for your module.

2. **Initialize Early**: Initialize your language class early in your module's lifecycle to ensure translations are available when needed.

3. **Use Descriptive Field Names**: Use descriptive field names for your translations to make them easier to understand and maintain.

4. **Group Related Translations**: Group related translations together in your language class, and use comments to separate different groups.

5. **Use Placeholders**: Use placeholders (e.g., `{playerName}`) in your translations to make them more flexible.

6. **Provide Good Default Values**: Provide clear and descriptive default values for your translations, as these will be used if no translation is provided.

7. **Document Your Translations**: Document your translations to help users understand what they are for and how they are used.

8. **Use the FamiUtils Format Methods**: Use the `FamiUtils.format()` and `FamiUtils.formatWithPrefix()` methods to format your translations with color codes and prefixes.

## Examples

### Example 1: Simple Message

```java
// In your language class
public String welcomeMessage = "&aWelcome to my module!";

// In your code
player.sendMessage(FamiUtils.format(MyModuleLanguage.getInstance().welcomeMessage));
```

### Example 2: Message with Placeholders

```java
// In your language class
public String playerJoinedMessage = "&a{playerName} has joined the server!";

// In your code
HashMap<String, String> placeholders = new HashMap<>();
placeholders.put("{playerName}", player.getName());
player.sendMessage(FamiUtils.replaceAndFormat(MyModuleLanguage.getInstance().playerJoinedMessage, placeholders));
```

### Example 3: Multi-line Message

```java
// In your language class
public String helpMessage = "&6=== My Module Help ===~&aCommand 1: /mymodule help~&aCommand 2: /mymodule reload";

// In your code
String[] lines = MyModuleLanguage.getInstance().helpMessage.split("~");
for (String line : lines) {
    player.sendMessage(FamiUtils.format(line));
}
```

## Conclusion

The RPUniverse language system provides a powerful and flexible way to manage translations in your modules. By following the guidelines in this document, you can make your module localizable and provide a better experience for users who speak different languages.

For more information, see the source code for the language system in the `me.fami6xx.rpuniverse.core.misc.language` package.