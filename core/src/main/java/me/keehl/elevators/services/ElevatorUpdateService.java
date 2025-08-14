package me.keehl.elevators.services;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.VersionHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class ElevatorUpdateService {

    private final static String apiEndpoint = "https://hangar.papermc.io/api/v1/projects/Keehl/Elevators/latest?channel=";
    private final static String resourceURL = "https://hangar.papermc.io/Keehl/Elevators";

    private static WrappedTask task;

    private static int currentVersion;

    private static boolean updateAvailable = false;
    private static boolean checkBetaChannels = false;

    public static void init(String version) {
        Elevators.pushAndHoldLog();

        checkBetaChannels = version.contains("beta");
        currentVersion = VersionHelper.getVersionID(version);

        ElevatorConfigService.addConfigCallback(root -> {
            unInitialize();

            if(root.updateCheckerEnabled)
                task = Elevators.getFoliaLib().getScheduler().runTimerAsync(ElevatorUpdateService::checkUpdate, 60, 72000); // Every hour.
        });
        Elevators.popLog(logData -> Elevators.log("Update service enabled. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    public static void unInitialize() {

        if(task != null)
            task.cancel();

        task = null;
    }

    private static void sendUpdateMessage(Player player) {
        if (player.hasPermission("elevators.updatenotify"))
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ELEVATORS " + ChatColor.WHITE + "An update is available for Elevators at " + ChatColor.GOLD + resourceURL);
    }

    public static void checkUpdate() {
        if(!updateAvailable) {
            int maxChannelID = -1;
            if(checkBetaChannels) {
                maxChannelID = Math.max(maxChannelID, checkResource("PreRelease"));
            }

            maxChannelID = Math.max(maxChannelID, checkResource("Release"));
            if(maxChannelID <= currentVersion)
                return;

            updateAvailable = true;
        }

        Bukkit.getOnlinePlayers().forEach(ElevatorUpdateService::sendUpdateMessage);
        Elevators.log("An update for Elevators is available at:");
        Elevators.log(resourceURL);
    }

    public static int checkResource(String channel) {
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
