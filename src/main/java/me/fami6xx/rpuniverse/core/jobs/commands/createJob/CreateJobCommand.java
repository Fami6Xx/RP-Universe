package me.fami6xx.rpuniverse.core.jobs.commands.createJob;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.jobs.commands.createJob.utils.CreateJobStorage;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.chatapi.UniversalChatHandler;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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
        PlayerData data = RPUniverse.getPlayerData(player.getUniqueId().toString());

        if(!data.hasPermissionForCreatingJobs()){
            player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorYouDontHavePermissionToUseThisCommandMessage));
            return true;
        }

        if(currentlyCreating.contains(player)){
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().errorYouAreAlreadyCreatingAJobMessage);
            return true;
        }

        UniversalChatHandler universalChatHandler = RPUniverse.getInstance().getUniversalChatHandler();
        if(!universalChatHandler.canAddToQueue(player)){
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().errorYouAlreadyHaveSomethingToType);
            return true;
        }

        currentlyCreating.add(player);
        showTypeNameTitle.add(player);
        createJobStarter.addToCreateJobStorage(player.getUniqueId(), new CreateJobStorage(player.getUniqueId()));

        FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().createJobCommandTypeNameMessage);
        FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().cancelActivityMessage);

        universalChatHandler.addToQueue(player, (player1, message) -> {
            if(message.equalsIgnoreCase("cancel")){
                removeFromCurrentlyCreating(player);
                removeFromShowTitle(player);
                createJobStarter.removeFromCreateJobStorage(player.getUniqueId());
                FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().createJobCommandCancelMessage);
                return true;
            }

            if(message.length() > 16){
                FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().errorJobNameTooLongMessage);
                return false;
            }

            if(RPUniverse.getInstance().getJobsHandler().getJobByName(message) != null){
                FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().errorJobNameAlreadyExistsMessage);
                return false;
            }

            createJobStarter.getCreateJobStorage(player.getUniqueId()).setJobName(message);
            player.resetTitle();
            showTypeNameTitle.remove(player);
            showSetLocationTitle.add(player);

            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().createJobCommandBossMenuLocationMessage);

            universalChatHandler.addToQueue(player1, (player2, message1) -> {
                if(message1.equalsIgnoreCase("cancel")){
                    removeFromCurrentlyCreating(player);
                    removeFromShowTitle(player);
                    createJobStarter.removeFromCreateJobStorage(player.getUniqueId());
                    player.resetTitle();
                    FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().createJobCommandCancelMessage);
                    return true;
                }

                if(message1.equalsIgnoreCase("here")){
                    createJobStarter.getCreateJobStorage(player.getUniqueId()).setBossMenuLocation(player.getLocation().toCenterLocation());

                    currentlyCreating.remove(player);
                    showSetLocationTitle.remove(player);
                    player.resetTitle();

                    FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().createJobCommandJobCreatedMessage);
                    RPUniverse.getInstance().getJobsHandler().addJob(new Job(createJobStarter.getCreateJobStorage(player.getUniqueId()).getJobName(), 0, createJobStarter.getCreateJobStorage(player.getUniqueId()).getBossMenuLocation()));
                    createJobStarter.removeFromCreateJobStorage(player.getUniqueId());
                    return true;
                }

                return false;
            });
            return false;
        });
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
