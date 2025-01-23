package me.fami6xx.rpuniverse.core.commands;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.api.BeforeCharacterKilledEvent;
import me.fami6xx.rpuniverse.core.api.CharacterKilledEvent;
import me.fami6xx.rpuniverse.core.api.RegionDeletedEvent;
import me.fami6xx.rpuniverse.core.holoapi.types.holograms.famiHologram;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.PlayerMode;
import me.fami6xx.rpuniverse.core.misc.language.editor.LanguageEditorMainMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import me.fami6xx.rpuniverse.core.properties.PropertyManager;
import me.fami6xx.rpuniverse.core.regions.Region;
import me.fami6xx.rpuniverse.core.regions.RegionManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class RPUCoreCommand implements CommandExecutor {

    // Region command maps
    private final HashMap<UUID, Location> pos1Map = new HashMap<>();
    private final HashMap<UUID, Location> pos2Map = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Handle non-player senders
        if (!(sender instanceof Player)) {
            handleConsoleCommands(sender, args);
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("rpu.core.command")) {
            FamiUtils.sendMessageWithPrefix(player, "&cYou don't have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            showHelp(player);
            return true;
        }

        String mainArg = args[0].toLowerCase();

        switch (mainArg) {
            case "ck":
                handleCharacterKill(player, args);
                break;
            case "addjob":
                handleAddJob(player, args);
                break;
            case "removejob":
                handleRemoveJob(player, args);
                break;
            case "region":
                handleRegionCommand(player, args);
                break;
            case "languages":
                new LanguageEditorMainMenu(RPUniverse.getInstance().getMenuManager().getPlayerMenu(player)).open();
                break;
            default:
                FamiUtils.sendMessageWithPrefix(player, "&cUnknown subcommand. Use &f/rpu &cfor help.");
                break;
        }

        return true;
    }

    private void handleConsoleCommands(CommandSender sender, String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return;
        }

        String mainArg = args[0].toLowerCase();

        switch (mainArg) {
            case "ck":
                handleCharacterKill(sender, args);
                break;
            case "addjob":
                handleAddJob(sender, args);
                break;
            case "removejob":
                handleRemoveJob(sender, args);
                break;
            default:
                sender.sendMessage(FamiUtils.formatWithPrefix("&cUnknown or unsupported subcommand for console."));
                break;
        }
    }

    private void handleCharacterKill(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(FamiUtils.formatWithPrefix("&cUsage: /rpu ck <Player>"));
            return;
        }
        Player target = sender.getServer().getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(FamiUtils.formatWithPrefix("&cPlayer not found!"));
            return;
        }

        BeforeCharacterKilledEvent event = new BeforeCharacterKilledEvent(target, (sender instanceof Player) ? (Player) sender : null, RPUniverse.getInstance().getDataSystem().getPlayerData(target.getUniqueId()));
        RPUniverse.getInstance().getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            sender.sendMessage(FamiUtils.formatWithPrefix("&cCharacter kill cancelled!"));
            return;
        }

        // Resetting player data
        resetData(target);

        CharacterKilledEvent killedEvent = new CharacterKilledEvent(target, (sender instanceof Player) ? (Player) sender : null, RPUniverse.getInstance().getDataSystem().getPlayerData(target.getUniqueId()));
        RPUniverse.getInstance().getServer().getPluginManager().callEvent(killedEvent);

        sender.sendMessage(FamiUtils.formatWithPrefix("&aCharacter killed!"));
    }

    private void handleAddJob(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(FamiUtils.formatWithPrefix("&cUsage: /rpu addjob <Player> <Job name>"));
            return;
        }
        Player target = sender.getServer().getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(FamiUtils.formatWithPrefix("&cPlayer not found!"));
            return;
        }
        Job job = RPUniverse.getInstance().getJobsHandler().getJobByName(args[2]);
        if (job == null) {
            sender.sendMessage(FamiUtils.formatWithPrefix("&cJob not found!"));
            return;
        }
        if (job.isPlayerInJob(target.getUniqueId())) {
            sender.sendMessage(FamiUtils.formatWithPrefix("&cPlayer is already in this job!"));
            return;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            job.addPlayerToJob(target.getUniqueId());
            FamiUtils.sendMessageWithPrefix(player, "&aJob added! &8(&cCheck console if not&8)");
        } else {
            // Console handling
            job.addPlayerToJob(target.getUniqueId());
            sender.sendMessage(FamiUtils.formatWithPrefix("&aJob added!"));
        }
    }

    private void handleRemoveJob(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(FamiUtils.formatWithPrefix("&cUsage: /rpu removejob <Player> <Job name>"));
            return;
        }
        Player target = sender.getServer().getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(FamiUtils.formatWithPrefix("&cPlayer not found!"));
            return;
        }
        Job job = RPUniverse.getInstance().getJobsHandler().getJobByName(args[2]);
        if (job == null) {
            sender.sendMessage(FamiUtils.formatWithPrefix("&cJob not found!"));
            return;
        }
        if (!job.isPlayerInJob(target.getUniqueId())) {
            sender.sendMessage(FamiUtils.formatWithPrefix("&cPlayer is not in this job!"));
            return;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            job.removePlayerFromJob(target.getUniqueId());
            FamiUtils.sendMessageWithPrefix(player, "&aJob removed!");
        } else {
            // Console handling
            job.removePlayerFromJob(target.getUniqueId());
            sender.sendMessage(FamiUtils.formatWithPrefix("&aJob removed!"));
        }
    }

    private void handleRegionCommand(Player player, String[] args) {
        if (args.length < 2) {
            showRegionHelp(player);
            return;
        }

        String subCommand = args[1].toLowerCase();

        switch (subCommand) {
            case "pos1":
                setRegionPos1(player);
                break;
            case "pos2":
                setRegionPos2(player);
                break;
            case "create":
                createRegion(player, args);
                break;
            case "list":
                listRegions(player);
                break;
            case "delete":
                deleteRegion(player, args);
                break;
            case "show":
                showRegionParticles(player, args);
                break;
            case "hide":
                hideRegionParticles(player, args);
                break;
            case "tp":
                teleportToRegion(player, args);
                break;
            default:
                FamiUtils.sendMessageWithPrefix(player, "&cUnknown region subcommand. Use &f/rpu region &cfor help.");
                break;
        }
    }

    private void setRegionPos1(Player player) {
        Location loc = player.getLocation();
        pos1Map.put(player.getUniqueId(), loc);
        FamiUtils.sendMessageWithPrefix(player, "&aFirst position set: &7" +
                loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
    }

    private void setRegionPos2(Player player) {
        Location loc = player.getLocation();
        pos2Map.put(player.getUniqueId(), loc);
        FamiUtils.sendMessageWithPrefix(player, "&aSecond position set: &7" +
                loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
    }

    private void createRegion(Player player, String[] args) {
        if (args.length < 3) {
            FamiUtils.sendMessageWithPrefix(player, "&cPlease provide a region name: /rpu region create <name>");
            return;
        }
        String regionName = args[2];

        Location corner1 = pos1Map.get(player.getUniqueId());
        Location corner2 = pos2Map.get(player.getUniqueId());

        if (corner1 == null || corner2 == null) {
            FamiUtils.sendMessageWithPrefix(player, "&cYou must set both pos1 and pos2 first!");
            return;
        }

        // Check if region with the same name exists
        if (RegionManager.getInstance().getRegionByName(regionName) != null) {
            FamiUtils.sendMessageWithPrefix(player, "&cA region with that name already exists!");
            return;
        }

        // Create region
        Region newRegion = RegionManager.getInstance().createRegion(regionName, corner1, corner2);
        FamiUtils.sendMessageWithPrefix(player, "&aRegion &e" + regionName + "&a created with ID: &e" + newRegion.getRegionId());
    }

    private void listRegions(Player player) {
        Collection<Region> allRegions = RegionManager.getInstance().getAllRegions();
        if (allRegions.isEmpty()) {
            FamiUtils.sendMessageWithPrefix(player, "&eNo regions exist yet.");
            return;
        }
        FamiUtils.sendMessageWithPrefix(player, "&aExisting Regions:");
        for (Region region : allRegions) {
            FamiUtils.sendMessage(player,
                    "&7- &e" + region.getName() + " &7(ID: " + region.getRegionId() + ")");
        }
    }

    private void deleteRegion(Player player, String[] args) {
        if (args.length < 3) {
            FamiUtils.sendMessageWithPrefix(player, "&cUsage: /rpu region delete <name>");
            return;
        }
        String name = args[2];
        Region r = RegionManager.getInstance().getRegionByName(name);
        if (r == null) {
            FamiUtils.sendMessageWithPrefix(player, "&cRegion not found: " + name);
            return;
        }
        boolean removed = RegionManager.getInstance().deleteRegion(r.getRegionId());
        if (removed) {
            Bukkit.getPluginManager().callEvent(new RegionDeletedEvent(r));
            FamiUtils.sendMessageWithPrefix(player, "&aRegion &e" + name + " &ahas been removed. &7(ID: " + r.getRegionId() + ")");
        } else {
            FamiUtils.sendMessageWithPrefix(player, "&cFailed to remove region: " + name);
        }
    }

    public void showHelp(CommandSender sender) {
        sender.sendMessage(FamiUtils.formatWithPrefix("&7&m                                "));
        sender.sendMessage(FamiUtils.formatWithPrefix("&6&lRPUniverse &7- &fHelp"));
        sender.sendMessage(FamiUtils.formatWithPrefix("&7&m                                "));
        sender.sendMessage(FamiUtils.formatWithPrefix("&6/rpu &7- &fShow this help"));
        sender.sendMessage(FamiUtils.formatWithPrefix("&6/rpu ck <Player> &7- &fCharacter kill a player"));
        sender.sendMessage(FamiUtils.formatWithPrefix("&6/rpu addjob <Player> <Job name> &7- &fAdd a job to a player"));
        sender.sendMessage(FamiUtils.formatWithPrefix("&6/rpu removejob <Player> <Job name> &7- &fRemove a job from a player"));
        sender.sendMessage(FamiUtils.formatWithPrefix("&6/rpu region <subcommand> &7- &fManage regions"));
        sender.sendMessage(FamiUtils.formatWithPrefix("&6/rpu languages &7- &fOpen the language editor"));
    }

    private void showRegionHelp(Player player) {
        FamiUtils.sendMessageWithPrefix(player, "&aRegion Command Help:");
        FamiUtils.sendMessage(player, "&a/rpu region pos1 &7- Set your first selection point");
        FamiUtils.sendMessage(player, "&a/rpu region pos2 &7- Set your second selection point");
        FamiUtils.sendMessage(player, "&a/rpu region create <name> &7- Create a region with your selected corners");
        FamiUtils.sendMessage(player, "&a/rpu region list &7- List all regions");
        FamiUtils.sendMessage(player, "&a/rpu region delete <name> &7- Delete a region by name");
        FamiUtils.sendMessage(player, "&a/rpu region show <name> &7- Show region boundaries with particles");
        FamiUtils.sendMessage(player, "&a/rpu region hide <name> &7- Hide region boundaries");
        FamiUtils.sendMessage(player, "&a/rpu region tp <name> &7- Teleport to region center");
    }

    private void resetData(Player player) {
        player.teleport(player.getWorld().getSpawnLocation());
        player.setHealth(20);

        PlayerData data = RPUniverse.getInstance().getDataSystem().getPlayerData(player.getUniqueId());

        Job[] array = data.getPlayerJobs().toArray(new Job[0]);
        for (Job job : array) {
            job.removePlayerFromJob(player.getUniqueId());
        }

        data.setFoodLevel(100);
        data.setWaterLevel(100);
        data.setPeeLevel(0);
        data.setPoopLevel(0);
        data.setPlayerMode(PlayerMode.USER);

        PropertyManager propertyManager = RPUniverse.getInstance().getPropertyManager();
        propertyManager.getAllProperties().forEach(property -> {
            if (property.getOwner() != null && property.getOwner().equals(player.getUniqueId())) {
                property.setOwner(null);
                property.setTrustedPlayers(new ArrayList<>());
                propertyManager.saveProperty(property);
            }

            if (property.getTrustedPlayers().contains(player.getUniqueId())) {
                property.removeTrustedPlayer(player.getUniqueId());
                propertyManager.saveProperty(property);
            }
        });

        player.getInventory().clear();
        player.getEnderChest().clear();
        player.getInventory().getItemInMainHand().setAmount(0);
        player.getInventory().getItemInOffHand().setAmount(0);
        if (player.getInventory().getBoots() != null) player.getInventory().getBoots().setAmount(0);
        if (player.getInventory().getLeggings() != null) player.getInventory().getLeggings().setAmount(0);
        if (player.getInventory().getChestplate() != null) player.getInventory().getChestplate().setAmount(0);
        if (player.getInventory().getHelmet() != null) player.getInventory().getHelmet().setAmount(0);
        Economy econ = RPUniverse.getInstance().getEconomy();
        econ.withdrawPlayer(player, econ.getBalance(player));

        RPUniverse.getInstance().getMenuManager().closeMenu(player);

        RPUniverse.getInstance().getLockHandler().getAllLocks().stream().filter(lock -> {
            if (lock.getOwners() == null) return false;
            return lock.getOwners().contains(player.getUniqueId().toString());
        }).forEach(lock -> RPUniverse.getInstance().getLockHandler().removeLock(lock));

        RPUniverse.getInstance().getActionBarHandler().getMessages(player).clear();
        RPUniverse.getInstance().getHoloAPI().getPlayerHolograms().forEach((uuid, holo) -> {
            if (uuid.equals(player.getUniqueId())) {
                holo.forEach(famiHologram::destroy);
            }
        });
        RPUniverse.getInstance().getHoloAPI().getPlayerHolograms().remove(player.getUniqueId());
    }

    private void showRegionParticles(Player player, String[] args) {
        if (args.length < 3) {
            FamiUtils.sendMessageWithPrefix(player, "&cUsage: /rpu region show <name>");
            return;
        }
        String name = args[2];
        Region region = RegionManager.getInstance().getRegionByName(name);
        if (region == null) {
            FamiUtils.sendMessageWithPrefix(player, "&cRegion not found: " + name);
            return;
        }
        if (RegionManager.getInstance().isShowingRegion(player, region)) {
            FamiUtils.sendMessageWithPrefix(player,
                    "&cYou are already showing this region's boundaries!");
            return;
        }
        RegionManager.getInstance().showRegion(player, region);
        FamiUtils.sendMessageWithPrefix(player,
                "&aShowing region &e" + region.getName() + "&a with particles.");
    }

    private void hideRegionParticles(Player player, String[] args) {
        if (args.length < 3) {
            FamiUtils.sendMessageWithPrefix(player, "&cUsage: /rpu region hide <name>");
            return;
        }
        String name = args[2];
        Region region = RegionManager.getInstance().getRegionByName(name);
        if (region == null) {
            FamiUtils.sendMessageWithPrefix(player, "&cRegion not found: " + name);
            return;
        }
        if (!RegionManager.getInstance().isShowingRegion(player, region)) {
            FamiUtils.sendMessageWithPrefix(player,
                    "&cYou are not currently showing that region!");
            return;
        }
        RegionManager.getInstance().hideRegion(player, region);
        FamiUtils.sendMessageWithPrefix(player,
                "&aRegion &e" + region.getName() + "&a is now hidden.");
    }

    private void teleportToRegion(Player player, String[] args) {
        if (args.length < 3) {
            FamiUtils.sendMessageWithPrefix(player, "&cUsage: /rpu region tp <name>");
            return;
        }
        String name = args[2];
        Region region = RegionManager.getInstance().getRegionByName(name);
        if (region == null) {
            FamiUtils.sendMessageWithPrefix(player, "&cRegion not found: " + name);
            return;
        }
        if (region.getCorner1() == null || region.getCorner2() == null) {
            FamiUtils.sendMessageWithPrefix(player, "&cRegion corners invalid. Cannot teleport.");
            return;
        }

        Location min = region.getMinCorner();
        Location max = region.getMaxCorner();
        World w = min.getWorld();
        if (w == null) {
            FamiUtils.sendMessageWithPrefix(player, "&cRegion's world is invalid. Cannot teleport.");
            return;
        }
        double centerX = (min.getX() + max.getX()) / 2.0;
        double centerY = (min.getY() + max.getY()) / 2.0;
        double centerZ = (min.getZ() + max.getZ()) / 2.0;
        Location center = new Location(w, centerX, centerY, centerZ);
        player.teleport(center);
        FamiUtils.sendMessageWithPrefix(player, "&aTeleported to center of region &e" + region.getName() + "&a!");
    }
}
