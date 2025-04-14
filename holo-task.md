# HoloAPI Multi-Plugin Support Implementation

## Current Implementation
Currently, the HoloAPI relies solely on the DecentHolograms plugin. This is evident in the `HoloAPI.java` file where it checks if the DecentHolograms plugin is enabled:

```java
if(!getPlugin().getServer().getPluginManager().isPluginEnabled("DecentHolograms"))
    return false;
```

Additionally, the `famiHologram` class directly imports and uses the DecentHolograms API:

```java
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
```

## Goal
The goal is to modify the HoloAPI to support multiple hologram plugins, allowing it to select and use any available plugin at startup.

## Implementation Steps

### 1. Create a Hologram Provider Interface
Create an interface that defines all the operations needed for hologram manipulation:

```java
package me.fami6xx.rpuniverse.core.holoapi.providers;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface HologramProvider {
    String getProviderName();
    boolean isAvailable();
    Object createHologram(String id, Location location);
    void deleteHologram(Object hologram);
    void addLine(Object hologram, String text);
    void setLine(Object hologram, int index, String text);
    void showToPlayer(Object hologram, Player player);
    void hideFromPlayer(Object hologram, Player player);
    void teleport(Object hologram, Location location);
    // Add other necessary methods
}
```

### 2. Implement Provider-Specific Adapters
Create concrete implementations of the HologramProvider interface for each supported plugin:

#### DecentHolograms Provider
```java
package me.fami6xx.rpuniverse.core.holoapi.providers.impl;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.fami6xx.rpuniverse.core.holoapi.providers.HologramProvider;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DecentHologramsProvider implements HologramProvider {
    @Override
    public String getProviderName() {
        return "DecentHolograms";
    }

    @Override
    public boolean isAvailable() {
        return org.bukkit.Bukkit.getPluginManager().isPluginEnabled("DecentHolograms");
    }

    @Override
    public Object createHologram(String id, Location location) {
        return DHAPI.createHologram(id, location);
    }

    // Implement other methods
}
```

#### HolographicDisplays Provider (Example)
```java
package me.fami6xx.rpuniverse.core.holoapi.providers.impl;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.holoapi.providers.HologramProvider;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HolographicDisplaysProvider implements HologramProvider {
    @Override
    public String getProviderName() {
        return "HolographicDisplays";
    }

    @Override
    public boolean isAvailable() {
        return org.bukkit.Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
    }

    @Override
    public Object createHologram(String id, Location location) {
        return HologramsAPI.createHologram(RPUniverse.getInstance(), location);
    }

    // Implement other methods
}
```

### 3. Create a Provider Manager
Create a class to manage and select the appropriate provider:

```java
package me.fami6xx.rpuniverse.core.holoapi.providers;

import me.fami6xx.rpuniverse.core.holoapi.providers.impl.DecentHologramsProvider;
import me.fami6xx.rpuniverse.core.holoapi.providers.impl.HolographicDisplaysProvider;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class HologramProviderManager {
    private final List<HologramProvider> providers = new ArrayList<>();
    private HologramProvider activeProvider = null;
    private final Logger logger;

    public HologramProviderManager(Logger logger) {
        this.logger = logger;
        // Register all providers
        providers.add(new DecentHologramsProvider());
        providers.add(new HolographicDisplaysProvider());
        // Add more providers as needed
    }

    public boolean initialize() {
        for (HologramProvider provider : providers) {
            if (provider.isAvailable()) {
                activeProvider = provider;
                logger.info("Using " + provider.getProviderName() + " as hologram provider");
                return true;
            }
        }
        logger.warning("No hologram provider found! Holograms will not work.");
        return false;
    }

    public HologramProvider getProvider() {
        return activeProvider;
    }

    public boolean hasProvider() {
        return activeProvider != null;
    }
}
```

### 4. Modify HoloAPI Class
Update the HoloAPI class to use the provider manager:

```java
package me.fami6xx.rpuniverse.core.holoapi;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.holoapi.handlers.FollowHoloHandler;
import me.fami6xx.rpuniverse.core.holoapi.handlers.VisibilityHoloHandler;
import me.fami6xx.rpuniverse.core.holoapi.providers.HologramProviderManager;
import me.fami6xx.rpuniverse.core.holoapi.types.holograms.famiHologram;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class HoloAPI {
    private final Map<UUID, List<famiHologram>> playerHolograms = new HashMap<>();
    private final HologramProviderManager providerManager;

    FollowHoloHandler followHandler;
    VisibilityHoloHandler VisibilityHandler;

    public HoloAPI() {
        this.providerManager = new HologramProviderManager(getPlugin().getLogger());
    }

    private RPUniverse getPlugin(){
        return RPUniverse.getInstance();
    }

    public boolean enable() {
        if (!providerManager.initialize()) {
            return false;
        }

        this.followHandler = new FollowHoloHandler();
        this.VisibilityHandler = new VisibilityHoloHandler();

        this.followHandler.start();
        this.VisibilityHandler.start();

        return true;
    }

    // Rest of the class remains the same

    public HologramProviderManager getProviderManager() {
        return providerManager;
    }
}
```

### 5. Refactor famiHologram Class
Modify the famiHologram class to use the provider instead of directly using DecentHolograms:

```java
package me.fami6xx.rpuniverse.core.holoapi.types.holograms;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.holoapi.HoloAPI;
import me.fami6xx.rpuniverse.core.holoapi.providers.HologramProvider;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class famiHologram {
    private final Object hologram; // Generic Object instead of Hologram
    private final UUID uuid = UUID.randomUUID();
    private final transient HashMap<UUID, Integer> playerPages = new HashMap<>();

    private double distance;
    private boolean seeThroughBlocks;

    public famiHologram(Object holo) {
        hologram = holo;
    }

    public Object addLine(String line) {
        HologramProvider provider = RPUniverse.getInstance().getHoloAPI().getProviderManager().getProvider();
        provider.addLine(hologram, line);
        return null; // Modify as needed
    }

    // Rest of the class needs to be updated to use the provider
    // ...
}
```

### 6. Update Handlers
Update the handlers to work with the new provider system.

### 7. Create Factory Methods
Create factory methods to create holograms using the active provider:

```java
package me.fami6xx.rpuniverse.core.holoapi;

import me.fami6xx.rpuniverse.core.holoapi.types.holograms.StaticHologram;
import me.fami6xx.rpuniverse.core.holoapi.types.holograms.FollowingHologram;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class HologramFactory {
    public static StaticHologram createStaticHologram(String id, Location location) {
        HologramProvider provider = RPUniverse.getInstance().getHoloAPI().getProviderManager().getProvider();
        Object hologram = provider.createHologram(id, location);
        return new StaticHologram(hologram, location);
    }

    public static FollowingHologram createFollowingHologram(String id, Entity entity, Location offset) {
        HologramProvider provider = RPUniverse.getInstance().getHoloAPI().getProviderManager().getProvider();
        Object hologram = provider.createHologram(id, entity.getLocation().add(offset));
        return new FollowingHologram(hologram, entity, offset);
    }
}
```

### 8. Update Configuration
Add configuration options to specify preferred providers and fallbacks.

### 9. Testing
Test the implementation with different hologram plugins to ensure compatibility.

## Benefits
- **Flexibility**: The system can work with any supported hologram plugin.
- **Resilience**: If one plugin is not available, it can fall back to another.
- **Extensibility**: New hologram plugins can be easily added by implementing the provider interface.

## Potential Challenges
- **API Differences**: Different plugins might have different capabilities or behaviors.
- **Performance**: Adding an abstraction layer might slightly impact performance.
- **Maintenance**: Need to keep up with updates to all supported plugins.