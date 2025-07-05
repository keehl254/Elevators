<div align="center">

![](https://keehl.me/static/logo.png)

<div style="text-align: center;
        color: #FFFFFF;
        border-radius: 10px;
		background-color: #9B050B;
        border: 1px solid #9B050B;
        margin-bottom: 20px;"><p style="color: white">A lightweight and simple means of vertical transportation.</p></div>


[![Hangar](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/hangar_vector.svg)](https://hangar.papermc.io/Keehl/Elevators)
&nbsp;
[![Supported spigot](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3.2.0/assets/compact/available/spigot_vector.svg)](https://www.spigotmc.org/resources/elevators.34229/)

[![Documentation](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/documentation/ghpages_vector.svg)](https://ele.keehl.me)

![Compiled with Java 8](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3.2.0/assets/compact-minimal/built-with/java8_vector.svg)
![Supports Paper](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3.2.0/assets/compact-minimal/supported/paper_vector.svg)
![Supports Spigot](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3.2.0/assets/compact-minimal/supported/spigot_vector.svg)
[![Supports Folia](assets/Folia.svg)]()

[![CodeFactor](https://www.codefactor.io/repository/github/keehl254/elevators/badge/main)](https://www.codefactor.io/repository/github/keehl254/elevators/overview/main)</div>

# Features

- Jump/crouch to move between floors
- Customizable item names, lore, and effects
- Multiple elevator types and crafting recipes
- Per-elevator customization through right-click menus
- Admin GUI for configuration and management
- Supports protection, hologram, and placeholder plugins
- No external database — everything is stored in-block

# Quick Start

1. Download the plugin through one of the "Available on" buttons at the top of the page.
2. Drop the `.jar` file into your `plugins` folder.
3. Restart your server.

You're now ready to place elevators and start teleporting!

# Documentation

Looking for recipes, configuration help, or how to add effects and messages?  
**Visit the full documentation site:** [Elevators Docs](https://ele.keehl.me)

# Building

To build Elevators from source using Gradle:

1. Clone the repository:
   ```bash
   git clone https://github.com/keehl254/Elevators.git
   cd Elevators
   ```
2. Run the custom Gradle task:
    ```bash
   ./gradlew :buildElevators
    ```
3. The compiled plugin JAR will be located in
   ```bash
   build/libs/
    ```

## Java Version Notes

The core project and all submodules except Hooks are built using Java 8. The Hooks subproject is written in Java 21.
A downgrade plugin is used to compile Hooks into a Java 8-compatible format for use in the final build.
Be sure you have both JDK 8 and JDK 21 installed and properly configured if you're developing or building locally.

# License

[GPL-3.0](LICENSE) — Fork, modify, redistribute — just keep it open source under GPL.