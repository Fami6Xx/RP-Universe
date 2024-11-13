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
import org.bukkit.event.player.PlayerQuitEvent;
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
    private final List<Material> lockedMaterials = new ArrayList<>();
    private final BukkitTask displayTask;
    private final BukkitTask checkMaterialsTask;
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
        checkMaterialsTask = startControlOfLockedMaterials();

        player.getInventory().addItem(getPropertyCreationWand());
        FamiUtils.sendMessageWithPrefix(player, "&aProperty creation wand has been added to your inventory");
        FamiUtils.sendMessageWithPrefix(player, "&7Left click on a block you want to lock");
        FamiUtils.sendMessageWithPrefix(player, "&7Right click on a block you want to unlock");
        FamiUtils.sendMessageWithPrefix(player, "&7Drop the item to proceed");
    }

    /**
     * Starts a task to control locked materials. If the material of a locked block changes, the block will be unlocked.
     * @return the BukkitTask for controlling locked materials
     */
    protected BukkitTask startControlOfLockedMaterials() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                List<Integer> indexesToRemove = new ArrayList<>();
                for (int i = 0; i < lockedBlock.size(); i++) {
                    Block block = lockedBlock.get(i);
                    if (!(block.getType().isBlock() && block.getType().isInteractable())) {
                        indexesToRemove.add(i);
                        FamiUtils.sendMessageWithPrefix(player, "&cBlock has been unlocked because it is not lockable");
                    }
                }

                for (int i : indexesToRemove) {
                    lockedBlock.remove(i);
                    lockedMaterials.remove(i);
                }
            }
        }.runTaskTimer(RPUniverse.getInstance(), 0, 10);
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

                    List<Block> blocksToCheck = new ArrayList<>();
                    LockHandler.getAllLockBlocksFromBlock(block, block.getType(), blocksToCheck);

                    if (blocksToCheck.size() == 1) {
                        // Single block case
                        Block singleBlock = blocksToCheck.get(0);
                        Location blockLoc = singleBlock.getLocation();

                        double minX = blockLoc.getBlockX();
                        double minY = blockLoc.getBlockY();
                        double minZ = blockLoc.getBlockZ();
                        double maxX = minX + 1;
                        double maxY = minY + 1;
                        double maxZ = minZ + 1;

                        for (double x = minX; x <= maxX; x += forAdd) {
                            for (double y = minY; y <= maxY; y += forAdd) {
                                for (double z = minZ; z <= maxZ; z += forAdd) {
                                    boolean edge = (
                                            (x == minX || x == maxX) && (y == minY || y == maxY)
                                    ) || (
                                            (x == minX || x == maxX) && (z == minZ || z == maxZ)
                                    ) || (
                                            (y == minY || y == maxY) && (z == minZ || z == maxZ)
                                    );

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

                    } else if (blocksToCheck.size() == 2) {
                        // Two blocks case
                        Block block1 = blocksToCheck.get(0);
                        Block block2 = blocksToCheck.get(1);
                        Location loc1 = block1.getLocation();
                        Location loc2 = block2.getLocation();

                        // Determine the bounding box
                        double minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
                        double minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
                        double minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
                        double maxX = Math.max(loc1.getBlockX(), loc2.getBlockX()) + 1;
                        double maxY = Math.max(loc1.getBlockY(), loc2.getBlockY()) + 1;
                        double maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ()) + 1;

                        for (double x = minX; x <= maxX; x += forAdd) {
                            for (double y = minY; y <= maxY; y += forAdd) {
                                for (double z = minZ; z <= maxZ; z += forAdd) {
                                    boolean edge = (
                                            (x == minX || x == maxX) && (y == minY || y == maxY)
                                    ) || (
                                            (x == minX || x == maxX) && (z == minZ || z == maxZ)
                                    ) || (
                                            (y == minY || y == maxY) && (z == minZ || z == maxZ)
                                    );

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
        }.runTaskTimer(RPUniverse.getInstance(), 0, 5);
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
     * Handles the event when a player leaves the server.
     *
     * @param e the PlayerQuitEvent
     */
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (!e.getPlayer().equals(player)) return;
        instance.cancel();
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
                }else if(!(b.getType().isBlock() && b.getType().isInteractable())) {
                    FamiUtils.sendMessageWithPrefix(player, "&cBlock is not lockable");
                    return;
                }
            }

            lockedMaterials.add(e.getClickedBlock().getType());
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
        checkMaterialsTask.cancel();
        player.getInventory().remove(getPropertyCreationWand());
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