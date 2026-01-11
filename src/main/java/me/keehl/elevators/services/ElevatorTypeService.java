package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.IElevators;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.services.IElevatorTypeService;
import me.keehl.elevators.api.services.configs.versions.IConfigRoot;
import me.keehl.elevators.models.ElevatorType;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.logging.Level;

public class ElevatorTypeService extends ElevatorService implements IElevatorTypeService {

    private IElevatorType defaultElevatorType;

    private boolean initialized = false;

    public ElevatorTypeService(IElevators elevators) {
        super(elevators);
    }

    public void onInitialize() {
        if (this.initialized)
            return;
        ElevatorsAPI.pushAndHoldLog();

        Elevators.getConfigService().addConfigCallback(this::reloadElevatorsFromConfig);

        this.initialized = true;
        ElevatorsAPI.popLog(logData -> ElevatorsAPI.log("Type service enabled. " + ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    public void onUninitialize() {

    }

    private void reloadElevatorsFromConfig(IConfigRoot config) {
        ElevatorsAPI.pushAndHoldLog();
        Map<String, IElevatorType> elevatorTypes = Elevators.getConfigService().getElevatorTypeConfigs();
        try {
            List<String> elevatorsToFix = new ArrayList<>();
            for (String elevatorKey : elevatorTypes.keySet()) {
                if (!elevatorKey.equals(elevatorKey.toUpperCase()))
                    elevatorsToFix.add(elevatorKey);
            }
            for (String elevatorKey : elevatorsToFix) {
                IElevatorType elevatorType = elevatorTypes.get(elevatorKey);
                elevatorTypes.remove(elevatorKey);
                elevatorTypes.put(elevatorKey.toUpperCase(), elevatorType);
            }

            if (!elevatorTypes.containsKey("DEFAULT")) {
                ElevatorsAPI.log("Adding default");
                ElevatorType type = new ElevatorType();
                ElevatorsAPI.log("Adding new type");
                type.setKey("DEFAULT");

                ElevatorsAPI.log("Set key to " + type.getTypeKey());
                elevatorTypes.put(type.getTypeKey(), type);

                ElevatorsAPI.log("No DEFAULT Elevator Type found. Registering new.");
            }

            this.defaultElevatorType = elevatorTypes.get("DEFAULT");

            for (String elevatorKey : elevatorTypes.keySet()) {
                IElevatorType elevatorType = elevatorTypes.get(elevatorKey);
                elevatorType.setKey(elevatorKey);
                elevatorType.onLoad();
            }
        } catch (Exception e) {
            ElevatorsAPI.log(Level.SEVERE, "Error occurred loading elevator types", e);
        }

        ElevatorsAPI.popLog(logData -> ElevatorsAPI.log("Registered and loaded " + elevatorTypes.size() + " elevator types. " + ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    public IElevatorType getElevatorType(String name) {
        return Elevators.getConfigService().getElevatorTypeConfigs().getOrDefault(name.toUpperCase(), null);
    }

    public IElevatorType getDefaultElevatorType() {
        return this.defaultElevatorType;
    }

    public boolean doesElevatorTypeExist(String name) {
        return Elevators.getConfigService().getElevatorTypeConfigs().containsKey(name.toUpperCase());
    }

    public Collection<IElevatorType> getExistingElevatorTypes() {
        return Elevators.getConfigService().getElevatorTypeConfigs().values();
    }

    public Set<String> getExistingElevatorKeys() {
        return Elevators.getConfigService().getElevatorTypeConfigs().keySet();
    }

    public void registerElevatorType(IElevatorType elevatorType) {
        Elevators.getConfigService().getElevatorTypeConfigs().put(elevatorType.getTypeKey().toUpperCase(), elevatorType);
        reloadElevatorsFromConfig(Elevators.getConfigService().getRootConfig());
    }

    public IElevatorType createElevatorType(String typeKey) {
        typeKey = typeKey.toUpperCase();
        ElevatorType type = new ElevatorType();
        Elevators.getConfigService().getElevatorTypeConfigs().put(typeKey, type);
        reloadElevatorsFromConfig(Elevators.getConfigService().getRootConfig());

        return getElevatorType(typeKey);
    }

    public void unregisterElevatorType(IElevatorType elevatorType) {
        Elevators.getConfigService().getElevatorTypeConfigs().remove(elevatorType.getTypeKey());
        reloadElevatorsFromConfig(Elevators.getConfigService().getRootConfig());
    }

}
