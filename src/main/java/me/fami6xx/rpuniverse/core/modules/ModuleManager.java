package me.fami6xx.rpuniverse.core.modules;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Manager for all modules in the RPUniverse plugin.
 * <p>
 * This class is responsible for registering, initializing, enabling, disabling, and shutting down modules.
 * It also loads module configurations from the config.yml file.
 * <p>
 * To register a module, use the {@link #registerModule(Module)} method.
 */
public class ModuleManager {
    
    private final RPUniverse plugin;
    private final Map<String, Module> modules;
    private ConfigurationSection modulesConfig;
    
    /**
     * Creates a new ModuleManager.
     *
     * @param plugin The plugin instance
     */
    public ModuleManager(RPUniverse plugin) {
        this.plugin = plugin;
        this.modules = new HashMap<>();
        
        // Load the modules configuration section
        this.modulesConfig = plugin.getConfiguration().getConfigurationSection("modules");
        if (this.modulesConfig == null) {
            // Create the modules section if it doesn't exist
            plugin.getConfiguration().createSection("modules");
            this.modulesConfig = plugin.getConfiguration().getConfigurationSection("modules");
        }
    }
    
    /**
     * Registers a module with the ModuleManager.
     *
     * @param module The module to register
     * @return true if the module was registered successfully, false otherwise
     */
    public boolean registerModule(Module module) {
        if (modules.containsKey(module.getName())) {
            ErrorHandler.warning("Module with name " + module.getName() + " is already registered");
            return false;
        }
        
        modules.put(module.getName(), module);
        ErrorHandler.debug("Registered module: " + module.getName());
        return true;
    }
    
    /**
     * Initializes all registered modules.
     * This should be called during plugin startup.
     */
    public void initializeModules() {
        ErrorHandler.info("Initializing " + modules.size() + " modules");
        
        for (Module module : modules.values()) {
            try {
                // Set the plugin instance
                module.setPlugin(plugin);
                
                // Load module configuration
                ConfigurationSection moduleConfig = modulesConfig.getConfigurationSection(module.getName());
                if (moduleConfig == null) {
                    // Create module configuration if it doesn't exist
                    moduleConfig = modulesConfig.createSection(module.getName());
                    
                    // Set default enabled state to true
                    moduleConfig.set("enabled", true);
                }
                
                // Set the module configuration
                module.setConfig(moduleConfig);
                
                // Initialize the module
                boolean initialized = module.initialize(plugin);
                if (!initialized) {
                    ErrorHandler.severe("Failed to initialize module: " + module.getName());
                    continue;
                }
                
                ErrorHandler.debug("Initialized module: " + module.getName());
                
                // Enable the module if it's enabled in the configuration
                boolean enabled = moduleConfig.getBoolean("enabled", true);
                if (enabled) {
                    enableModule(module);
                }
            } catch (Exception e) {
                ErrorHandler.severe("Error initializing module: " + module.getName(), e);
            }
        }
        
        ErrorHandler.info("Modules initialization complete");
    }
    
    /**
     * Enables a module.
     *
     * @param module The module to enable
     * @return true if the module was enabled successfully, false otherwise
     */
    public boolean enableModule(Module module) {
        if (module.isEnabled()) {
            ErrorHandler.debug("Module " + module.getName() + " is already enabled");
            return true;
        }
        
        try {
            boolean enabled = module.enable();
            if (!enabled) {
                ErrorHandler.warning("Failed to enable module: " + module.getName());
                return false;
            }
            
            module.setEnabled(true);
            ErrorHandler.info("Enabled module: " + module.getName());
            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Error enabling module: " + module.getName(), e);
            return false;
        }
    }
    
    /**
     * Enables a module by name.
     *
     * @param moduleName The name of the module to enable
     * @return true if the module was enabled successfully, false otherwise
     */
    public boolean enableModule(String moduleName) {
        Module module = getModule(moduleName);
        if (module == null) {
            ErrorHandler.warning("Module not found: " + moduleName);
            return false;
        }
        
        return enableModule(module);
    }
    
    /**
     * Disables a module.
     *
     * @param module The module to disable
     * @return true if the module was disabled successfully, false otherwise
     */
    public boolean disableModule(Module module) {
        if (!module.isEnabled()) {
            ErrorHandler.debug("Module " + module.getName() + " is already disabled");
            return true;
        }
        
        try {
            boolean disabled = module.disable();
            if (!disabled) {
                ErrorHandler.warning("Failed to disable module: " + module.getName());
                return false;
            }
            
            module.setEnabled(false);
            ErrorHandler.info("Disabled module: " + module.getName());
            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Error disabling module: " + module.getName(), e);
            return false;
        }
    }
    
    /**
     * Disables a module by name.
     *
     * @param moduleName The name of the module to disable
     * @return true if the module was disabled successfully, false otherwise
     */
    public boolean disableModule(String moduleName) {
        Module module = getModule(moduleName);
        if (module == null) {
            ErrorHandler.warning("Module not found: " + moduleName);
            return false;
        }
        
        return disableModule(module);
    }
    
    /**
     * Shuts down all modules.
     * This should be called during plugin shutdown.
     */
    public void shutdownModules() {
        ErrorHandler.info("Shutting down " + modules.size() + " modules");
        
        for (Module module : modules.values()) {
            try {
                boolean shutdown = module.shutdown();
                if (!shutdown) {
                    ErrorHandler.warning("Failed to shut down module: " + module.getName());
                }
                
                ErrorHandler.debug("Shut down module: " + module.getName());
            } catch (Exception e) {
                ErrorHandler.severe("Error shutting down module: " + module.getName(), e);
            }
        }
        
        ErrorHandler.info("Modules shutdown complete");
    }
    
    /**
     * Gets a module by name.
     *
     * @param name The name of the module to get
     * @return The module, or null if not found
     */
    public Module getModule(String name) {
        return modules.get(name);
    }
    
    /**
     * Gets all registered modules.
     *
     * @return A collection of all registered modules
     */
    public Collection<Module> getModules() {
        return modules.values();
    }
    
    /**
     * Gets the number of registered modules.
     *
     * @return The number of registered modules
     */
    public int getModuleCount() {
        return modules.size();
    }
    
    /**
     * Gets the number of enabled modules.
     *
     * @return The number of enabled modules
     */
    public int getEnabledModuleCount() {
        int count = 0;
        for (Module module : modules.values()) {
            if (module.isEnabled()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Checks if a module is registered.
     *
     * @param name The name of the module to check
     * @return true if the module is registered, false otherwise
     */
    public boolean isModuleRegistered(String name) {
        return modules.containsKey(name);
    }
    
    /**
     * Checks if a module is enabled.
     *
     * @param name The name of the module to check
     * @return true if the module is enabled, false otherwise
     */
    public boolean isModuleEnabled(String name) {
        Module module = getModule(name);
        return module != null && module.isEnabled();
    }
}