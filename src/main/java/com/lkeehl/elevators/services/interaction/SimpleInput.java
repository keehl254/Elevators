package com.lkeehl.elevators.services.interaction;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.helpers.MessageHelper;
import com.lkeehl.elevators.services.ElevatorConfigService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Function;

public class SimpleInput implements Listener {

    private final JavaPlugin plugin;
    private final Player player;
    private Function<String, SimpleInputResult> onComplete;
    private Runnable onCancel;
    private boolean allowReset = false;

    public SimpleInput(JavaPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public SimpleInput onComplete(Function<String, SimpleInputResult> onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    public SimpleInput onCancel(Runnable onCancel) {
        this.onCancel = onCancel;
        return this;
    }

    public SimpleInput allowReset() {
        this.allowReset = true;
        return this;
    }

    public void stop(boolean isCancel) {
        HandlerList.unregisterAll(this);
        if(isCancel && this.onCancel != null)
            this.onCancel.run();
    }

    public void start() {
        if(this.allowReset)
            MessageHelper.sendFormattedMessage(this.player, ElevatorConfigService.getRootConfig().locale.chatInputBackOutAllowReset);
        else
            MessageHelper.sendFormattedMessage(this.player, ElevatorConfigService.getRootConfig().locale.chatInputBackOut);

        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }

    @SuppressWarnings("deprecation") // Paper deprecates this, but Spigot doesn't... I may have to only support paper in the future.
    @EventHandler()
    protected void onChat(AsyncPlayerChatEvent event) {
        if(!event.getPlayer().getUniqueId().equals(this.player.getUniqueId())) return;

        event.setCancelled(true);

        Bukkit.getScheduler().runTask(Elevators.getInstance(), () -> {
            String message = event.getMessage();
            if (message.equalsIgnoreCase("cancel")) {
                this.stop(true);
                return;
            }
            if (message.equalsIgnoreCase("reset") && this.allowReset)
                message = null;

            if(this.onComplete.apply(message) == SimpleInputResult.STOP)
                this.stop(false);
        });

    }

    public enum SimpleInputResult {
        STOP,
        CONTINUE
    }

}
