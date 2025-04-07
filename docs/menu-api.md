# RPUniverse Menu API

This document provides detailed information about the RPUniverse menu API, including core components, workflow, implementation, and best practices.

## Overview

The RPUniverse menu API is a comprehensive framework for creating and managing in-game GUI menus. It provides a flexible and extensible system for building interactive interfaces for players, with support for both simple and paginated menus.

Key features of the menu API include:

- **Menu Hierarchy**: A class hierarchy that makes it easy to create different types of menus
- **Pagination Support**: Built-in support for paginated menus with navigation controls
- **Event Handling**: Automatic handling of inventory click events
- **Menu State Management**: Tracking of player menu state across interactions
- **Menu Tagging**: A tagging system for categorizing and filtering menus
- **Bulk Operations**: Support for operations on groups of menus based on predicates and tags

## Core Components

### Menu Classes

The menu API is built around a hierarchy of menu classes:

#### Menu

`Menu` is the abstract base class for all menus. It provides the core functionality for creating and displaying menus to players. Key features include:

- Abstract methods that define menu behavior (`getMenuName()`, `getSlots()`, `handleMenu()`, `setMenuItems()`, `getMenuTags()`)
- Menu opening and display logic
- Inventory management
- Helper methods for common tasks like filling empty slots with glass panes

#### PaginatedMenu

`PaginatedMenu` extends `Menu` to provide pagination functionality. It adds:

- Page tracking (`page`, `index`)
- Navigation controls (previous/next buttons)
- A standardized border layout
- Fixed 54-slot (6 rows) inventory size

#### EasyPaginatedMenu

`EasyPaginatedMenu` extends `PaginatedMenu` to provide a more user-friendly way to create paginated menus. It adds:

- Abstract methods for data handling (`getItemFromIndex()`, `getCollectionSize()`, `handlePaginatedMenu()`, `addAdditionalItems()`)
- Automatic pagination implementation
- Slot-to-index mapping for handling clicks
- Standardized layout with items arranged in rows of 7

### Management Classes

#### MenuManager

`MenuManager` is responsible for managing all menus in the system. Key features include:

- Player menu state tracking
- Methods for opening, closing, and reopening menus
- Bulk operations on menus based on predicates and tags
- Integration with other systems (e.g., jobs)

#### PlayerMenu

`PlayerMenu` represents a player's menu state. It tracks:

- The player instance
- The currently open menu
- The job being edited (if any)
- Pending actions (e.g., text input callbacks)

#### MenuInvClickHandler

`MenuInvClickHandler` handles Bukkit inventory events for menus. It:

- Intercepts click events and delegates to the appropriate menu
- Cancels default inventory behavior for menu interactions
- Updates player menu state on menu open/close events

### Utility Classes

#### MenuTag

`MenuTag` is an enum that provides a tagging system for menus. Tags include:

- Access level tags (`ADMIN`, `PLAYER`)
- Feature-specific tags (`JOB`, `BOSS`, etc.)
- Content-specific tags (`ALL_CONSUMABLES`, `ALL_LOCKS`, etc.)

## Menu Workflow

The typical workflow for using the menu API follows these steps:

1. **Menu Creation**: Create a class that extends `Menu` or one of its subclasses
2. **Menu Implementation**: Implement the required abstract methods
3. **Menu Display**: Create an instance of the menu and call the `open()` method
4. **Event Handling**: The menu handles click events through the `handleMenu()` method
5. **Menu Navigation**: For paginated menus, handle navigation through the pagination controls
6. **Menu Closing**: The menu is closed when the player closes the inventory or another menu is opened

### Example Workflow

```java
// 1. Create a menu instance
PlayerMenu playerMenu = RPUniverse.getInstance().getMenuManager().getPlayerMenu(player);
MyCustomMenu menu = new MyCustomMenu(playerMenu);

// 2. Open the menu
menu.open();

// 3. Menu handles click events automatically through handleMenu()
// 4. Player closes the menu or navigates to another menu
```

## Implementation Guide

### Creating a Simple Menu

To create a simple menu, extend the `Menu` class and implement its abstract methods:

```java
public class MySimpleMenu extends Menu {
    public MySimpleMenu(PlayerMenu menu) {
        super(menu);
    }

    @Override
    public String getMenuName() {
        return "My Simple Menu";
    }

    @Override
    public int getSlots() {
        return 27; // 3 rows
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        // Handle click events
        if (e.getCurrentItem().getType() == Material.DIAMOND) {
            // Do something when diamond is clicked
        }
    }

    @Override
    public void setMenuItems() {
        // Set items in the menu
        inventory.setItem(13, new ItemStack(Material.DIAMOND));
    }

    @Override
    public List<MenuTag> getMenuTags() {
        return Arrays.asList(MenuTag.PLAYER);
    }
}
```

### Creating a Paginated Menu

To create a paginated menu, extend the `EasyPaginatedMenu` class:

```java
public class MyPaginatedMenu extends EasyPaginatedMenu {
    private List<ItemStack> items;

    public MyPaginatedMenu(PlayerMenu menu, List<ItemStack> items) {
        super(menu);
        this.items = items;
    }

    @Override
    public String getMenuName() {
        return "My Paginated Menu";
    }

    @Override
    public ItemStack getItemFromIndex(int index) {
        return items.get(index);
    }

    @Override
    public int getCollectionSize() {
        return items.size();
    }

    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        // Handle click events for items
        int slot = e.getSlot();
        int index = getSlotIndex(slot);
        
        if (index != -1) {
            // Do something with the clicked item
        }
    }

    @Override
    public void addAdditionalItems() {
        // Add any additional items to the menu border
    }

    @Override
    public List<MenuTag> getMenuTags() {
        return Arrays.asList(MenuTag.PLAYER);
    }
}
```

## Integration with Other Systems

The menu API integrates with several other systems in RPUniverse:

### Job System

- Menus can be associated with jobs through the `PlayerMenu.editingJob` field
- The `MenuManager` provides methods for operating on job-related menus
- Job-specific menu tags (`JOB`, `BOSS`, etc.) allow for filtering job-related menus

### Hologram System

- Menus can be accessed through hologram interaction
- The configuration system includes settings for menu hologram visibility range

### Permission System

- Menu access can be restricted based on player permissions
- Admin menus are typically tagged with `MenuTag.ADMIN` for permission checks

## Configuration Options

The menu API can be configured through the `config.yml` file:

```yaml
# menuRange: The range in which the holograms are visible for the menus (For example boss menus)
menuRange: 5
```

Configuration options include:

- **menuRange**: The interaction range for menu holograms

## Best Practices

Here are some best practices for working with the RPUniverse menu API:

1. **Use the appropriate menu type**: Choose between `Menu`, `PaginatedMenu`, and `EasyPaginatedMenu` based on your needs
2. **Properly tag your menus**: Use appropriate `MenuTag` values to categorize your menus
3. **Handle menu events efficiently**: Keep menu click handlers lightweight to avoid lag
4. **Use the PlayerMenu for state**: Store menu-related state in the `PlayerMenu` rather than static fields
5. **Close menus when appropriate**: Use `MenuManager.closeMenu()` to close menus when they're no longer needed
6. **Reuse menu instances when possible**: Create menu instances once and reuse them when appropriate
7. **Use the filler glass**: Use `setFillerGlass()` to create a clean, consistent look for your menus

## Troubleshooting

If you encounter issues with the menu API:

1. **Menu not displaying**: Ensure you're calling `open()` on the menu instance
2. **Click events not working**: Check that your `handleMenu()` method is properly implemented
3. **Items not appearing**: Verify that `setMenuItems()` is setting items in the correct slots
4. **Pagination issues**: Check that `getCollectionSize()` and `getItemFromIndex()` are implemented correctly
5. **Menu state issues**: Ensure you're using the correct `PlayerMenu` instance
6. **Menu closing unexpectedly**: Check for other plugins that might be interfering with inventory events

## Advanced Features

### Custom Menu Layouts

While the menu API provides standard layouts, you can create custom layouts by overriding the appropriate methods:

- For simple menus, override `setMenuItems()` to create your custom layout
- For paginated menus, override `addMenuBorder()` to customize the border and navigation controls

### Menu Chaining

Menus can be chained together to create complex workflows:

```java
public void handleMenu(InventoryClickEvent e) {
    if (e.getCurrentItem().getType() == Material.DIAMOND) {
        // Open a new menu when diamond is clicked
        new AnotherMenu(playerMenu).open();
    }
}
```

### Pending Actions

The `PlayerMenu` class supports pending actions for handling text input:

```java
// Set a pending action
playerMenu.setPendingAction(input -> {
    // Handle the input
    player.sendMessage("You entered: " + input);
    
    // Reopen the menu
    this.open();
});

// Close the menu to allow text input
player.closeInventory();
```

## Conclusion

The RPUniverse menu API provides a powerful and flexible system for creating in-game GUIs. By understanding its components and workflow, developers can create rich, interactive interfaces for players that enhance the gameplay experience.