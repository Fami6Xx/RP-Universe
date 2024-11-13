package me.fami6xx.rpuniverse.core.properties.process;

import com.destroystokyo.paper.ParticleBuilder;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.locks.LockHandler;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import me.fami6xx.rpuniverse.core.properties.Property;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles the process of creating a property in the RP Universe.
 */
public class CreatePropertyProcess implements Listener {
    private final Player player;
    private final List<Block> lockedBlock = new ArrayList<>();
    private final BukkitTask displayTask;
    private final CreatePropertyProcess instance = this;

    /**
     * Constructs a new CreatePropertyProcess for the specified player.
     *
     * @param player the player who is creating the property
     */
    public CreatePropertyProcess(Player player) {
        this.player = player;
        Bukkit.getPluginManager().registerEvents(this, RPUniverse.getInstance());

        displayTask = startDisplayOfLockedBlocks();

        player.getInventory().addItem(getPropertyCreationWand());
        FamiUtils.sendMessageWithPrefix(player, "&aProperty creation wand has been added to your inventory");
        FamiUtils.sendMessageWithPrefix(player, "&7Left click on a block you want to lock");
        FamiUtils.sendMessageWithPrefix(player, "&7Right click on a block you want to unlock");
        FamiUtils.sendMessageWithPrefix(player, "&7Drop the item to proceed");
    }

    /**
     * Starts a task to display locked blocks to the player.
     *
     * @return the BukkitTask for displaying locked blocks
     */
    protected BukkitTask startDisplayOfLockedBlocks() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                List<Block> localLockedBlocks = new ArrayList<>(lockedBlock);

                for (Block block : localLockedBlocks) {
                    float forAdd = 0.25f;
                    World world = block.getWorld();
                    Location blockLoc = block.getLocation();

                    List<Block> blocksToCheck = new ArrayList<>();
                    LockHandler.getAllLockBlocksFromBlock(block, block.getType(), blocksToCheck);
                    if(blocksToCheck.size() == 1) {
                        // Calculate borders of block
                        for (double x = blockLoc.getBlockX(); x <= blockLoc.getBlockX() + 1; x += forAdd) {
                            for (double y = blockLoc.getBlockY(); y <= blockLoc.getBlockY() + 1; y += forAdd) {
                                for (double z = blockLoc.getBlockZ(); z <= blockLoc.getBlockZ() + 1; z += forAdd) {
                                    boolean edge = false;
                                    if ((x == blockLoc.getBlockX() || x == blockLoc.getBlockX() + 1) &&
                                            (y == blockLoc.getBlockY() || y == blockLoc.getBlockY() + 1)) edge = true;
                                    if ((z == blockLoc.getBlockZ() || z == blockLoc.getBlockZ() + 1) &&
                                            (y == blockLoc.getBlockY() || y == blockLoc.getBlockY() + 1)) edge = true;
                                    if ((x == blockLoc.getBlockX() || x == blockLoc.getBlockX() + 1) &&
                                            (z == blockLoc.getBlockZ() || z == blockLoc.getBlockZ() + 1)) edge = true;
                                    if (edge) {
                                        Location newLoc = new Location(world, x, y, z);
                                        new ParticleBuilder(Particle.REDSTONE)
                                                .color(Color.BLACK)
                                                .count(0)
                                                .receivers(player)
                                                .location(newLoc)
                                                .spawn();
                                    }
                                }
                            }
                        }
                    }else if(blocksToCheck.size() == 2) {
                        Location loc1 = blocksToCheck.get(0).getLocation();
                        Location loc2 = blocksToCheck.get(1).getLocation();
                        for (double x = loc1.getBlockX(); x <= loc2.getBlockX(); x += forAdd) {
                            for (double y = loc1.getBlockY(); y <= loc2.getBlockY(); y += forAdd) {
                                for (double z = loc1.getBlockZ(); z <= loc2.getBlockZ(); z += forAdd) {
                                    boolean edge = false;
                                    if ((x == loc1.getBlockX() || x == loc2.getBlockX()) &&
                                            (y == loc1.getBlockY() || y == loc2.getBlockY())) edge = true;
                                    if ((z == loc1.getBlockZ() || z == loc2.getBlockZ()) &&
                                            (y == loc1.getBlockY() || y == loc2.getBlockY())) edge = true;
                                    if ((x == loc1.getBlockX() || x == loc2.getBlockX()) &&
                                            (z == loc1.getBlockZ() || z == loc2.getBlockZ())) edge = true;
                                    if (edge) {
                                        Location newLoc = new Location(world, x, y, z);
                                        new ParticleBuilder(Particle.REDSTONE)
                                                .color(Color.BLACK)
                                                .count(0)
                                                .receivers(player)
                                                .location(newLoc)
                                                .spawn();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(RPUniverse.getInstance(), 0, 10);
    }

    /**
     * Handles the event when a player drops an item.
     *
     * @param e the PlayerDropItemEvent
     */
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if (!e.getPlayer().equals(player)) return;
        if (e.getItemDrop().getItemStack().isSimilar(getPropertyCreationWand())) {
            displayTask.cancel();

            e.getItemDrop().remove();
            e.getPlayer().getInventory().remove(getPropertyCreationWand());
            e.setCancelled(true);

            if (lockedBlock.isEmpty()) {
                FamiUtils.sendMessageWithPrefix(player, "&cYou have not locked any blocks");
                FamiUtils.sendMessageWithPrefix(player, "&cProperty creation has been cancelled");
                instance.cancel();
                return;
            }

            FamiUtils.sendMessageWithPrefix(player, "&aPlease go to where the hologram should be placed and type &chere");
            FamiUtils.sendMessageWithPrefix(player, "&aMake sure the hologram has enough space");

            Listener chatListener = new Listener() {
                private final Listener listener = this;
                @EventHandler(priority = EventPriority.HIGHEST)
                public void onChat(AsyncPlayerChatEvent e) {
                    if (!e.getPlayer().equals(player)) return;
                    e.setCancelled(true);

                    if (e.getMessage().equalsIgnoreCase("here")) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Property property = new Property(UUID.randomUUID());
                                List<UUID> lockUUIDs = new ArrayList<>();
                                LockHandler lockHandler = RPUniverse.getInstance().getLockHandler();
                                for (Block block : lockedBlock) {
                                    try {
                                        lockUUIDs.add(lockHandler.createLock(block.getLocation(), block.getType(), new ArrayList<>(), null, 0).getUUID());
                                    }catch (NullPointerException ex) {
                                        FamiUtils.sendMessageWithPrefix(player, "&cAn error occurred while creating the property");
                                        FamiUtils.sendMessageWithPrefix(player, "&cThe error was about trying to create multiple locks on the same block");
                                        FamiUtils.sendMessageWithPrefix(player, "&cPlease contact the developer of the plugin / server administrator");
                                        HandlerList.unregisterAll(listener);
                                        instance.cancel();
                                        return;
                                    }
                                }
                                property.setLockedBlocks(lockUUIDs);
                                property.setHologramLocation(player.getLocation().add(0, 2, 0));
                                RPUniverse.getInstance().getPropertyManager().createProperty(property);
                                FamiUtils.sendMessageWithPrefix(player, "&aProperty has been created");
                                HandlerList.unregisterAll(listener);
                                instance.cancel();
                            }
                        }.runTask(RPUniverse.getInstance());
                    } else {
                        FamiUtils.sendMessageWithPrefix(player, "&aPlease type &chere &awhen you are ready");
                    }
                }
            };

            Bukkit.getPluginManager().registerEvents(chatListener, RPUniverse.getInstance());
        }
    }

    /**
     * Handles the event when a player interacts with a block.
     *
     * @param e the PlayerInteractEvent
     */
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        if (!e.getPlayer().equals(player)) return;
        if (e.getItem() == null) return;
        if (!e.getItem().isSimilar(getPropertyCreationWand())) return;

        if (e.getClickedBlock() == null) return;
        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            e.setCancelled(true);
            if (lockedBlock.contains(e.getClickedBlock())) {
                FamiUtils.sendMessageWithPrefix(player, "&cBlock is already locked");
                return;
            }

            Block block = e.getClickedBlock();
            LockHandler lockHandler = RPUniverse.getInstance().getLockHandler();
            List<Block> blocksToCheck = new ArrayList<>();
            LockHandler.getAllLockBlocksFromBlock(block, block.getType(), blocksToCheck);
            for (Block b : blocksToCheck) {
                if (lockedBlock.contains(b)) {
                    FamiUtils.sendMessageWithPrefix(player, "&cBlock is already locked");
                    return;
                }else if(lockHandler.getLockByLocation(b.getLocation()) != null) {
                    FamiUtils.sendMessageWithPrefix(player, "&cBlock is already locked");
                    return;
                }
            }

            lockedBlock.add(e.getClickedBlock());
            FamiUtils.sendMessageWithPrefix(player, "&aBlock has been locked");
        } else if (e.getAction().name().contains("RIGHT")) {
            e.setCancelled(true);
            Block block = e.getClickedBlock();
            if (!lockedBlock.contains(block)) {
                FamiUtils.sendMessageWithPrefix(player, "&cBlock is not locked");
                return;
            }

            List<Block> blocksToCheck = new ArrayList<>();
            LockHandler.getAllLockBlocksFromBlock(block, block.getType(), blocksToCheck);
            boolean locked = false;
            for (Block b : blocksToCheck) {
                if (lockedBlock.contains(b)) {
                    locked = true;
                    break;
                }
            }

            if (!locked) {
                FamiUtils.sendMessageWithPrefix(player, "&cBlock is not locked");
                return;
            }

            lockedBlock.remove(e.getClickedBlock());
            FamiUtils.sendMessageWithPrefix(player, "&aBlock has been unlocked");
        }
    }

    /**
     * Cancels the property creation process.
     */
    protected void cancel() {
        displayTask.cancel();
        HandlerList.unregisterAll(this);
    }

    /**
     * Gets the property creation wand item.
     *
     * @return the property creation wand item
     */
    public static ItemStack getPropertyCreationWand() {
        return FamiUtils.makeItem(Material.STICK, "&aProperty Lock Wand",
                "&7Left click on a block you want to lock",
                "&7Right click on a block you want to unlock",
                "&7Drop the item to proceed");
    }
}