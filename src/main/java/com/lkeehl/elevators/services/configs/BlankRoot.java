package com.lkeehl.elevators.services.configs;

import com.lkeehl.elevators.util.config.Comments;
import com.lkeehl.elevators.util.config.Config;

public class BlankRoot implements Config {

    @Comments("Don't Mess With. Deals with config conversion")
    public String version = "1.0.0";

}
