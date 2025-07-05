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

    private final static String apiEndpoint = "https://hangar.papermc.io/api/v1/projects/Keehl/Elevators/latest?channel=PreRelease";
    private final static String resourceURL = "https://hangar.papermc.io/Keehl/Elevators";

    private static WrappedTask task;

    private static String currentVersion;

    private static boolean updateAvailable = false;

    public static void init(String version) {
        Elevators.pushAndHoldLog();
        currentVersion = (version.contains("-") ? version.split("-")[0].trim() : version);

        ElevatorConfigService.addConfigCallback(root -> {
            unInitialize();

            if(root.updateCheckerEnabled)
                task = Elevators.getFoliaLib().getScheduler().runTimerAsync(ElevatorUpdateService::checkUpdate, 1200, 288000);
        });
        Elevators.popLog(logData -> Elevators.log("Update service enabled. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    public static void unInitialize() {

        if(task != null)
            task.cancel();

        task = null;
    }

    private static boolean isNewer(String version) {
        version = (version.contains("-") ? version.split("-")[0].trim() : version);

        int versionNumber = VersionHelper.getVersionID(version);
        int myVersionNumber = VersionHelper.getVersionID(currentVersion);

        return versionNumber > myVersionNumber;
    }

    private static void sendUpdateMessage(Player player) {
        if (!updateAvailable)
            return;
        if (player.hasPermission("elevators.updatenotify"))
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ELEVATORS " + ChatColor.WHITE + "An update is available for Elevators at " + ChatColor.GOLD + resourceURL);
    }

    public static void checkUpdate() {
        if(!updateAvailable) {
            try {
                HttpsURLConnection connection = (HttpsURLConnection) new URL(apiEndpoint).openConnection();
                connection.setRequestMethod("GET");
                String raw = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();

                updateAvailable = isNewer(raw);
            } catch (IOException e) {
                updateAvailable = false;
            }
        }
        if (updateAvailable) {
            Bukkit.getOnlinePlayers().forEach(ElevatorUpdateService::sendUpdateMessage);
            Elevators.getElevatorsLogger().warning("An update for Elevators is available at:");
            Elevators.getElevatorsLogger().warning(resourceURL);
        }
    }

}
