package me.keehl.elevators.actions;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevatorEventData;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.models.actions.ElevatorAction;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Observer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class TriggerObserverAction extends ElevatorAction {

    public TriggerObserverAction(JavaPlugin plugin, IElevatorType elevatorType, String key) {
        super(plugin, elevatorType, key);
    }

    @Override
    protected void onInitialize(String value) {
    }

    @Override
    public void execute(IElevatorEventData eventData, Player player) {

        for (BlockFace face : Arrays.asList(BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST)) {

            Block block = eventData.getOrigin().getLocation().getBlock().getRelative(face);
            if (!block.getType().equals(Material.OBSERVER))
                continue;


            BlockData data = block.getBlockData();
            if(!(data instanceof Observer))
                continue;

            Observer observer = (Observer) data;
            if(observer.getFacing() == face.getOppositeFace()) {
                observer.setPowered(true);
                block.setBlockData(observer, true);
                Elevators.getFoliaLib().getScheduler().runAtLocationLater(block.getLocation(), task -> {
                    if(block.getType() != Material.OBSERVER)
                        return;

                    observer.setPowered(false);
                    block.setBlockData(observer, true);
                }, 2);
            }
        }

    }

}
