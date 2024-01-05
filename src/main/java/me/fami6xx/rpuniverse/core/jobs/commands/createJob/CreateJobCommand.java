package me.fami6xx.rpuniverse.core.jobs.commands.createJob;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.commands.createJob.utils.CreateJobStorage;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class CreateJobCommand implements CommandExecutor, Listener {
    private final Set<Player> currentlyCreating = Collections.newSetFromMap(new WeakHashMap<>());
    private final Set<Player> showTypeNameTitle = Collections.newSetFromMap(new WeakHashMap<>());
    private final Set<Player> showSetLocationTitle = Collections.newSetFromMap(new WeakHashMap<>());

    private final CreateJobStarter createJobStarter;

    public CreateJobCommand(CreateJobStarter createJobStarter) {
        this.createJobStarter = createJobStarter;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorOnlyPlayersCanUseThisCommandMessage));
            return true;
        }

        Player player = (Player) sender;

        if(currentlyCreating.contains(player)){
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().errorYouAreAlreadyCreatingAJobMessage);
            return true;
        }

        currentlyCreating.add(player);
        showTypeNameTitle.add(player);
        createJobStarter.addToCreateJobStorage(player.getUniqueId(), new CreateJobStorage(player.getUniqueId()));

        FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().createJobCommandTypeNameMessage);

        return true;
    }

    public void removeFromCurrentlyCreating(Player player){
        currentlyCreating.remove(player);
    }

    public void removeFromShowTitle(Player player){
        showTypeNameTitle.remove(player);
        showSetLocationTitle.remove(player);
        player.resetTitle();
    }

    public Set<Player> getCurrentlyCreating() {
        return currentlyCreating;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        if(currentlyCreating.contains(player)){
            removeFromCurrentlyCreating(player);
            removeFromShowTitle(player);
            createJobStarter.removeFromCreateJobStorage(player.getUniqueId());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();

        if(currentlyCreating.contains(player)){
            event.setCancelled(true);
            CreateJobStorage createJobStorage = createJobStarter.getCreateJobStorage(player.getUniqueId());

            if(event.getMessage().equalsIgnoreCase("cancel")){
                removeFromCurrentlyCreating(player);
                removeFromShowTitle(player);
                createJobStarter.removeFromCreateJobStorage(player.getUniqueId());
                FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().createJobCommandCancelMessage);
                return;
            }

            if(createJobStorage.getJobName() == null){
                createJobStorage.setJobName(event.getMessage());
                FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().createJobCommandBossMenuLocationMessage);

                showTypeNameTitle.remove(player);
                player.resetTitle();

                showSetLocationTitle.add(player);
                return;
            }

            if(event.getMessage().equalsIgnoreCase("here")){
                createJobStorage.setBossMenuLocation(player.getLocation().toCenterLocation());

                currentlyCreating.remove(player);
                showSetLocationTitle.remove(player);
                player.resetTitle();

                FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().createJobCommandJobCreatedMessage);
                // ToDo: Create job
            }
        }
    }

    public BukkitTask showTitleRunnable(){
        return (new BukkitRunnable() {
            @Override
            public void run() {
                showTypeNameTitle.forEach(player -> player.sendTitle(" ", FamiUtils.format(RPUniverse.getLanguageHandler().createJobCommandTypeNameMessage), 0, 999999999, 0));

                showSetLocationTitle.forEach(player -> player.sendTitle(" ", FamiUtils.format(RPUniverse.getLanguageHandler().createJobCommandBossMenuLocationMessage), 0, 999999999, 0));
            }
        }).runTaskTimerAsynchronously(RPUniverse.getInstance(), 0, 20);
    }
}
