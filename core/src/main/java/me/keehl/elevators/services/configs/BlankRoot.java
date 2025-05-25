package me.keehl.elevators.services.configs;

import me.keehl.elevators.util.config.Comments;
import me.keehl.elevators.util.config.Config;

public class BlankRoot implements Config {

    @Comments("Don't Mess With. Deals with config conversion")
    public String version = "1.0.0";

}
