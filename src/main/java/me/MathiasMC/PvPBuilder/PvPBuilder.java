package me.MathiasMC.PvPBuilder;

import com.google.common.io.ByteStreams;
import me.MathiasMC.PvPBuilder.commands.PvPBuilder_Command;
import me.MathiasMC.PvPBuilder.files.BlocksFolder;
import me.MathiasMC.PvPBuilder.files.Config;
import me.MathiasMC.PvPBuilder.files.Language;
import me.MathiasMC.PvPBuilder.listeners.BlockBreak;
import me.MathiasMC.PvPBuilder.listeners.BlockPlace;
import me.MathiasMC.PvPBuilder.listeners.EntityDamageByEntity;
import me.MathiasMC.PvPBuilder.listeners.PlayerVelocity;
import me.MathiasMC.PvPBuilder.managers.*;
import me.MathiasMC.PvPBuilder.utils.Metrics;
import me.MathiasMC.PvPBuilder.utils.TextUtils;
import me.MathiasMC.PvPBuilder.utils.UpdateUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class PvPBuilder extends JavaPlugin {

    public PvPBuilder call;

    public Config config;
    public Language language;
    public TextUtils textUtils;
    public SystemManager systemManager;
    public BlockManager blockManager;
    public CalculateManager calculateManager;
    public AreaManager areaManager;
    public IronGolemManager ironGolemManager;
    public BlocksFolder blocksFolder;
    public ItemStack wand;
    public final ConsoleCommandSender consoleCommandSender = Bukkit.getServer().getConsoleSender();
    public final HashMap<ItemStack, String> handItemStack = new HashMap<>();
    public final HashMap<String, ArrayList<ItemStack>> itemStackArray = new HashMap<>();
    public final HashMap<String, FileConfiguration> blocksFileConfiguration = new HashMap<>();
    public final HashMap<String, File> blocksFile = new HashMap<>();
    public final HashMap<String, Location> pos1 = new HashMap<>();
    public final HashMap<String, Location> pos2 = new HashMap<>();
    public final HashSet<String> action_no_knockback = new HashSet<>();

    @Override
    public void onEnable() {
        call = this;
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        textUtils = new TextUtils(this);
        config = new Config(this);
        language = new Language(this);
        systemManager = new SystemManager(this);
        blockManager = new BlockManager(this);
        calculateManager = new CalculateManager(this);
        areaManager = new AreaManager(this);
        ironGolemManager = new IronGolemManager(this);
        blocksFolder = new BlocksFolder(this);
        if (config.get.getBoolean("events.BlockPlace")) { getServer().getPluginManager().registerEvents(new BlockPlace(this), this); }
        if (config.get.getBoolean("events.BlockBreak")) { getServer().getPluginManager().registerEvents(new BlockBreak(this), this); }
        if (config.get.getBoolean("events.EntityDamageByEntity")) { getServer().getPluginManager().registerEvents(new EntityDamageByEntity(this), this); }
        if (config.get.getBoolean("events.PlayerVelocity")) { getServer().getPluginManager().registerEvents(new PlayerVelocity(this), this); }
        getCommand("pvpbuilder").setExecutor(new PvPBuilder_Command(this));
        this.wand = getWand();
        if (config.get.getBoolean("update-check")) {
            new UpdateUtils(this, 61035).getVersion(version -> {
                if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                    textUtils.info("You are using the latest version of PvPBuilder (" + getDescription().getVersion() + ")");
                } else {
                    textUtils.warning("Version: " + version + " has been released! you are currently using version: " + getDescription().getVersion());
                }
            });
        }
        int pluginId = 3298;
        Metrics metrics = new Metrics(this, pluginId);
    }

    @Override
    public void onDisable() {
        ironGolemManager.removeAll();
        call = null;
    }

    public boolean versionID() {
        if (getServer().getVersion().contains("1.8")) { return true; }
        if (getServer().getVersion().contains("1.9")) { return true; }
        if (getServer().getVersion().contains("1.10")) { return true; }
        if (getServer().getVersion().contains("1.11")) { return true; }
        if (getServer().getVersion().contains("1.12")) { return true; }
        return false;
    }

    public ItemStack getID(String bb, int amount) {
        if (versionID()) {
            try {
                String[] parts = bb.split(":");
                int matId = Integer.parseInt(parts[0]);
                if (parts.length == 2) {
                    short data = Short.parseShort(parts[1]);
                    return new ItemStack(Material.getMaterial(matId), amount, data);
                }
                return new ItemStack(Material.getMaterial(matId));
            } catch (Exception e) {
                return null;
            }
        } else {
            try {
                return new ItemStack(Material.getMaterial(bb), amount);
            } catch (Exception e) {
                return null;
            }
        }
    }

    public ItemStack getItemStack(ItemStack itemStack) {
        ItemStack newItemStack = new ItemStack(itemStack);
        newItemStack.setAmount(1);
        return newItemStack;
    }

    public void copy(String filename, File file) {
        try {
            ByteStreams.copy(getResource(filename), new FileOutputStream(file));
        } catch (IOException exception) {
            textUtils.exception(exception.getStackTrace(), exception.getMessage());
        }
    }

    public boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
    }

    public boolean isFloat(String s) {
        try {
            Float.parseFloat(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public ItemStack getWand() {
        ItemStack itemStack = new ItemStack(Material.STICK);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&bPvPBuilder Wand"));
        ArrayList<String> list = new ArrayList<>();
        list.add(ChatColor.translateAlternateColorCodes('&', "&6This tool is used to select points"));
        itemMeta.setLore(list);
        itemStack.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 0);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}