package me.MathiasMC.PvPBuilder.files;

import me.MathiasMC.PvPBuilder.PvPBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class BlocksFolder {

    private final PvPBuilder plugin;

    private File folder;


    public BlocksFolder(final PvPBuilder plugin) {
        this.plugin = plugin;
        this.folder = new File(plugin.getDataFolder() + File.separator + "blocks");
        if (!folder.exists()) {
            folder.mkdir();
            File smallWoodWall = new File(folder, "small_wood_wall.yml");
            File tinyCobblestoneStairCase = new File(folder, "small_cobblestone_staircase.yml");
            File hugeTower = new File(folder, "huge_tower.yml");
            File fireworkShow = new File(folder, "firework_show.yml");
            File jumpBoost = new File(folder, "jump_boost.yml");
            File trampolineSmall = new File(folder, "trampoline_small.yml");
            File trampolineBig = new File(folder, "trampoline_big.yml");
            try {
                smallWoodWall.createNewFile();
                tinyCobblestoneStairCase.createNewFile();
                hugeTower.createNewFile();
                fireworkShow.createNewFile();
                jumpBoost.createNewFile();
                trampolineSmall.createNewFile();
                trampolineBig.createNewFile();
                if (!plugin.versionID()) {
                    plugin.copy("blocks/small_wood_wall.yml", smallWoodWall);
                    plugin.copy("blocks/small_cobblestone_staircase.yml", tinyCobblestoneStairCase);
                    plugin.copy("blocks/huge_tower.yml", hugeTower);
                    plugin.copy("blocks/firework_show.yml", fireworkShow);
                    plugin.copy("blocks/jump_boost.yml", jumpBoost);
                    plugin.copy("blocks/trampoline_small.yml", trampolineSmall);
                    plugin.copy("blocks/trampoline_big.yml", trampolineBig);
                } else {
                    plugin.copy("old/blocks/small_wood_wall.yml", smallWoodWall);
                    plugin.copy("old/blocks/small_cobblestone_staircase.yml", tinyCobblestoneStairCase);
                    plugin.copy("old/blocks/huge_tower.yml", hugeTower);
                    plugin.copy("old/blocks/firework_show.yml", fireworkShow);
                    plugin.copy("old/blocks/jump_boost.yml", jumpBoost);
                    plugin.copy("old/blocks/trampoline_small.yml", trampolineSmall);
                    plugin.copy("old/blocks/trampoline_big.yml", trampolineBig);
                }
            } catch (IOException exception) {
                plugin.textUtils.exception(exception.getStackTrace(), exception.getMessage());
            }
        }
        loadFiles();
    }

    public void loadFiles() {
        for (File file : folder.listFiles()) {
            String fileName = file.getName();
            if (fileName.endsWith(".yml")) {
                FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
                plugin.blocksFileConfiguration.put(fileName, fileConfiguration);
                plugin.blocksFile.put(fileName, file);
                plugin.systemManager.convertItemStacks(fileName);
                plugin.textUtils.info("[Blocks] " + fileName + " ( Loaded )");
            } else {
                plugin.textUtils.error("[Blocks] " + fileName + " ( Error Loading )");
            }
        }
    }

    public void save(String fileName) {
        try {
            plugin.blocksFileConfiguration.get(fileName).save(plugin.blocksFile.get(fileName));
        } catch (IOException exception) {
            plugin.textUtils.exception(exception.getStackTrace(), exception.getMessage());
        }
    }
}