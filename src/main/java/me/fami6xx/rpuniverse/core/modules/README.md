# RPUniverse Module System

The RPUniverse Module System provides a way to organize and manage plugin functionality in a modular way. Modules can be enabled or disabled via configuration, and they provide clear extension points for third-party developers.

## Using Modules

### Enabling/Disabling Modules

Modules can be enabled or disabled in the `config.yml` file:

```yaml
modules:
  # BasicNeeds module settings
  BasicNeeds:
    # enabled: If true, the module is enabled
    enabled: true
```

### Getting a Module

You can get a module from the ModuleManager:

```java
// Get the ModuleManager
ModuleManager moduleManager = RPUniverse.getInstance().getModuleManager();

// Get a module by name
Module module = moduleManager.getModule("BasicNeeds");

// Check if a module is enabled
if (moduleManager.isModuleEnabled("BasicNeeds")) {
    // Do something with the module
}
```

## Creating Modules

### Creating a New Module

To create a new module, extend the `AbstractModule` class:

```java
public class MyModule extends AbstractModule {

    @Override
    public boolean initialize(RPUniverse plugin) {
        boolean result = super.initialize(plugin);
        if (!result) {
            return false;
        }

        // Initialize your module here

        return true;
    }

    @Override
    public boolean enable() {
        // Check if the module is enabled in the configuration
        if (!getConfigBoolean("enabled", true)) {
            ErrorHandler.info("MyModule is disabled in configuration");
            return false;
        }

        // Enable your module here

        ErrorHandler.info("MyModule enabled");
        return true;
    }

    @Override
    public boolean disable() {
        // Disable your module here

        ErrorHandler.info("MyModule disabled");
        return true;
    }

    @Override
    public String getName() {
        return "MyModule";
    }

    @Override
    public String getDescription() {
        return "My custom module";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getAuthor() {
        return "Your Name";
    }
}
```

### Registering a Module

To register a module, add it to the ModuleManager in the RPUniverse class:

```java
// Register modules
moduleManager.registerModule(new MyModule());
```

### Module Lifecycle

Modules have the following lifecycle methods:

1. `initialize(RPUniverse plugin)`: Called when the module is being initialized. This is called before the module is enabled.
2. `enable()`: Called when the module is being enabled. This is called after initialization if the module is enabled in the configuration.
3. `disable()`: Called when the module is being disabled. This is called when the plugin is disabled or when the module is disabled via API.
4. `shutdown()`: Called when the module is being shut down. This is called when the plugin is disabled.
5. `saveData()`: Called periodically by the DataSystem and when the plugin is disabled. This method is used to save the module's data.

### Module Configuration

Modules can access their configuration using the following methods:

```java
// Get a configuration value with a default fallback
String value = getConfigString("path.to.value", "default");
boolean enabled = getConfigBoolean("enabled", true);
int count = getConfigInt("count", 10);
double amount = getConfigDouble("amount", 1.5);
long time = getConfigLong("time", 1000L);

// Get a configuration section
ConfigurationSection section = getConfigSection("path.to.section");
```

### Module Data Persistence

Modules can persist their data using the `saveData()` method. This method is called periodically by the DataSystem and when the plugin is disabled.

The AbstractModule class provides a default implementation that saves the module's configuration:

```java
@Override
public void saveData() {
    // Default implementation - can be overridden by specific modules
    if (config != null && plugin != null) {
        try {
            // Save the module's configuration if it exists
            plugin.saveConfig();
        } catch (Exception e) {
            ErrorHandler.warning("Failed to save data for module " + getName() + ": " + e.getMessage());
        }
    }
}
```

Modules can override this method to save additional data:

```java
@Override
public void saveData() {
    try {
        // Call the parent implementation to save the config
        super.saveData();

        // Save any additional data
        if (myData != null) {
            // Save the data using your preferred method
            saveMyData();
            ErrorHandler.debug("Saved data for MyModule");
        }
    } catch (Exception e) {
        ErrorHandler.warning("Failed to save data for MyModule: " + e.getMessage());
    }
}
```

Modules can also set up their own periodic data saving in the `enable()` method:

```java
@Override
public boolean enable() {
    // ... other initialization code ...

    // Set up periodic data saving
    int saveInterval = getConfigInt("saveInterval", 5);
    if (saveInterval > 0) {
        getPlugin().getServer().getScheduler().runTaskTimerAsynchronously(
                getPlugin(),
                () -> saveData(),
                saveInterval * 1200L, // Convert minutes to ticks (20 ticks/second * 60 seconds/minute)
                saveInterval * 1200L
        );
    }

    return true;
}
```

## Extension Points

The module system provides the following extension points for third-party developers:

1. **Module Interface**: Implement the `Module` interface to create a custom module.
2. **AbstractModule Class**: Extend the `AbstractModule` class to create a module with minimal boilerplate code.
3. **ModuleManager**: Use the `ModuleManager` to register, enable, disable, and get modules.
4. **Configuration**: Use the configuration system to enable/disable modules and configure module settings.
5. **Data Persistence**: Override the `saveData()` method to save module-specific data.

## Example: BasicNeedsModule

The `BasicNeedsModule` is an example of a module that wraps an existing component (the `BasicNeedsHandler`):

```java
public class BasicNeedsModule extends AbstractModule {

    private BasicNeedsHandler handler;

    @Override
    public boolean initialize(RPUniverse plugin) {
        boolean result = super.initialize(plugin);
        if (!result) {
            return false;
        }

        try {
            this.handler = new BasicNeedsHandler();
            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Failed to initialize BasicNeedsModule", e);
            return false;
        }
    }

    @Override
    public boolean enable() {
        try {
            // Check if the module is enabled in the configuration
            if (!getConfigBoolean("enabled", true)) {
                ErrorHandler.info("BasicNeedsModule is disabled in configuration");
                return false;
            }

            // Initialize the handler
            this.handler.initialize(getPlugin());

            // Register commands
            getPlugin().getCommand("consumables").setExecutor(new ConsumablesCommand());
            getPlugin().getCommand("poop").setExecutor(new PoopCommand());
            getPlugin().getCommand("pee").setExecutor(new PeeCommand());

            ErrorHandler.info("BasicNeedsModule enabled");
            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Failed to enable BasicNeedsModule", e);
            return false;
        }
    }

    @Override
    public boolean disable() {
        try {
            if (handler != null) {
                handler.shutdown();
            }

            ErrorHandler.info("BasicNeedsModule disabled");
            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Failed to disable BasicNeedsModule", e);
            return false;
        }
    }

    @Override
    public String getName() {
        return "BasicNeeds";
    }

    @Override
    public String getDescription() {
        return "Provides basic needs functionality like hunger, thirst, poop, and pee";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getAuthor() {
        return "fami6xx";
    }

    /**
     * Gets the BasicNeedsHandler instance.
     * 
     * @return The BasicNeedsHandler instance
     */
    public BasicNeedsHandler getHandler() {
        return handler;
    }

    @Override
    public void saveData() {
        try {
            // Call the parent implementation to save the config
            super.saveData();

            // Save any additional data from the handler if needed
            if (handler != null && handler.getConfig() != null) {
                RPUniverse.getInstance().getDataSystem().getDataHandler().saveConsumables(handler);
                ErrorHandler.debug("Saving data for BasicNeedsModule");
            }
        } catch (Exception e) {
            ErrorHandler.warning("Failed to save data for BasicNeedsModule: " + e.getMessage());
        }
    }
}
```
