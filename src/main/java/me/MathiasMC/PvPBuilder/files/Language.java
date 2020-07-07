package me.MathiasMC.PvPBuilder.files;

import me.MathiasMC.PvPBuilder.PvPBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Language {

    public FileConfiguration get;
    private final File file;

    private final PvPBuilder plugin;

    public Language(final PvPBuilder plugin) {
        this.plugin = plugin;
        file = new File(plugin.getDataFolder(), "language.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                plugin.copy("language.yml", file);
                plugin.textUtils.info("language.yml ( A change was made )");
            } catch (IOException exception) {
                plugin.textUtils.exception(exception.getStackTrace(), exception.getMessage());
            }
        } else {
            plugin.textUtils.info("language.yml ( Loaded )");
        }
        load();
    }

    public void load() {
        get = YamlConfiguration.loadConfiguration(file);
    }
}