package me.keehl.elevators.services;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.IElevators;
import me.keehl.elevators.api.services.IElevatorUpdateService;
import me.keehl.elevators.helpers.VersionHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class ElevatorUpdateService extends ElevatorService implements IElevatorUpdateService {

    private static String apiEndpoint = "https://hangar.papermc.io/api/v1/projects/Keehl/Elevators/latest?channel=";
    private static String resourceURL = "https://hangar.papermc.io/Keehl/Elevators";

    private WrappedTask task;

    private int currentVersion;

    private boolean updateAvailable = false;
    private boolean checkBetaChannels = false;

    public ElevatorUpdateService(IElevators elevators) {
        super(elevators);
    }

    public void onInitialize() {
        ElevatorsAPI.pushAndHoldLog();

        String version = Elevators.getInstance().getDescription().getVersion();
        this.checkBetaChannels = version.contains("beta");
        this.currentVersion = VersionHelper.getVersionID(version);

        Elevators.getConfigService().addConfigCallback(root -> {
            this.onUninitialize();

            if(root.isUpdateCheckerEnabled())
                this.task = Elevators.getFoliaLib().getScheduler().runTimerAsync(this::checkUpdate, 60, 72000); // Every hour.
        });
        ElevatorsAPI.popLog(logData -> ElevatorsAPI.log("Update service enabled. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    public void onUninitialize() {

        if(this.task != null)
            this.task.cancel();

        this.task = null;
    }

    private void sendUpdateMessage(Player player) {
        if (player.hasPermission("elevators.updatenotify"))
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ELEVATORS " + ChatColor.WHITE + "An update is available for Elevators at " + ChatColor.GOLD + resourceURL);
    }

    public void checkUpdate() {
        if(!this.updateAvailable) {
            int maxChannelID = -1;
            if(this.checkBetaChannels) {
                maxChannelID = Math.max(maxChannelID, checkResource("PreRelease"));
            }

            maxChannelID = Math.max(maxChannelID, checkResource("Release"));
            if(maxChannelID <= this.currentVersion)
                return;

            this.updateAvailable = true;
        }

        Bukkit.getOnlinePlayers().forEach(this::sendUpdateMessage);
        ElevatorsAPI.log("An update for Elevators is available at:");
        ElevatorsAPI.log(resourceURL);
    }

    public int checkResource(String channel) {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(apiEndpoint + channel).openConnection();
            connection.setRequestMethod("GET");

            String raw = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
            return VersionHelper.getVersionID(raw);
        } catch (IOException e) {
            return -1;
        }
    }

}
