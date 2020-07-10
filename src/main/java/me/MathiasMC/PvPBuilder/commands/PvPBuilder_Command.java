package me.MathiasMC.PvPBuilder.commands;

import me.MathiasMC.PvPBuilder.PvPBuilder;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;

public class PvPBuilder_Command implements CommandExecutor {

    private final PvPBuilder plugin;

    public PvPBuilder_Command(final PvPBuilder plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("pvpbuilder")) {
            if (sender.hasPermission("pvpbuilder.command")) {
                boolean unknown = true;
                String path;
                if (sender instanceof Player) {
                    path = "player";
                } else {
                    path = "console";
                }
                if (args.length == 0) {
                    for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.command.message")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{pvpbuilder_version}", plugin.getDescription().getVersion())));
                    }
                } else {
                    if (args[0].equalsIgnoreCase("help")) {
                        unknown = false;
                        if (sender.hasPermission("pvpbuilder.command.help")) {
                            for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.help.message")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        } else {
                            for (String message : plugin.language.get.getStringList("player.pvpbuilder.help.permission")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("reload")) {
                        unknown = false;
                        if (sender.hasPermission("pvpbuilder.command.reload")) {
                            plugin.config.load();
                            plugin.language.load();
                            plugin.blocksFolder.loadFiles();
                            for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.reload.reloaded")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        } else {
                            for (String message : plugin.language.get.getStringList("player.pvpbuilder.reload.permission")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }

                    } else if (args[0].equalsIgnoreCase("message")) {
                        unknown = false;
                        if (sender.hasPermission("pvpbuilder.command.message")) {
                            if (args.length <= 2) {
                                for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.message.usage")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            } else {
                                Player target = plugin.getServer().getPlayer(args[1]);
                                if (target != null) {
                                    StringBuilder sb = new StringBuilder();
                                    for (int i = 2; i < args.length; i++) {
                                        sb.append(args[i]).append(" ");
                                    }
                                    String text = sb.toString().trim();
                                    if (!text.contains("\\n")) {
                                        target.sendMessage(ChatColor.translateAlternateColorCodes('&', text));
                                    } else {
                                        for (String message : text.split(Pattern.quote("\\n"))) {
                                            target.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                        }
                                    }
                                } else {
                                    for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.message.online")) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                    }
                                }
                            }
                        } else {
                            for (String message : plugin.language.get.getStringList("player.pvpbuilder.message.permission")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("wand")) {
                        unknown = false;
                        if (path.equalsIgnoreCase("player")) {
                            if (sender.hasPermission("pvpbuilder.command.wand")) {
                                Player player = (Player) sender;
                                player.getInventory().addItem(plugin.wand);
                                for (String message : plugin.language.get.getStringList("player.pvpbuilder.wand.add")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            } else {
                                for (String message : plugin.language.get.getStringList("player.pvpbuilder.wand.permission")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            }
                        } else {
                            for (String message : plugin.language.get.getStringList("console.pvpbuilder.wand")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("setup")) {
                        unknown = false;
                        if (path.equalsIgnoreCase("player")) {
                            if (sender.hasPermission("pvpbuilder.command.setup")) {
                                if (args.length == 2) {
                                    String fileName = args[1];
                                    if (plugin.blocksFileConfiguration.containsKey(fileName)) {
                                        Player player = (Player) sender;
                                        ItemStack itemStack;
                                        try {
                                            itemStack = player.getInventory().getItemInMainHand();
                                        } catch (NoSuchMethodError error) {
                                            itemStack = player.getInventory().getItemInHand();
                                        }
                                        if (itemStack != null && itemStack.getType() != null && !itemStack.getType().equals(Material.AIR)) {
                                            FileConfiguration fileConfiguration = plugin.blocksFileConfiguration.get(fileName);
                                            if (fileConfiguration.getConfigurationSection("hand") != null) {
                                                plugin.handItemStack.remove(ItemStack.deserialize(fileConfiguration.getConfigurationSection("hand").getValues(false)));
                                            }
                                            itemStack.setAmount(1);
                                            fileConfiguration.set("hand", itemStack.serialize());
                                            plugin.blocksFolder.save(fileName);
                                            for (String message : plugin.language.get.getStringList("player.pvpbuilder.setup.add")) {
                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{pvpbuilder_file}", args[1])));
                                            }
                                        } else {
                                            for (String message : plugin.language.get.getStringList("player.pvpbuilder.setup.material")) {
                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                            }
                                        }
                                    } else {
                                        for (String message : plugin.language.get.getStringList("player.pvpbuilder.setup.file")) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{pvpbuilder_file}", args[1])));
                                        }
                                    }
                                } else {
                                    ArrayList<String> list = new ArrayList<>();
                                    for (Map.Entry<String, File> map : plugin.blocksFile.entrySet()) {
                                        list.add(map.getKey());
                                    }
                                    for (String message : plugin.language.get.getStringList("player.pvpbuilder.setup.list")) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{pvpbuilder_files}", list.toString().replace("[", "").replace("]", ""))));
                                    }
                                }
                            } else {
                                for (String message : plugin.language.get.getStringList("player.pvpbuilder.setup.permission")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            }
                        } else {
                            for (String message : plugin.language.get.getStringList("console.pvpbuilder.setup")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("zone")) {
                        unknown = false;
                        if (path.equalsIgnoreCase("player")) {
                            if (sender.hasPermission("pvpbuilder.command.zone")) {
                                if (args.length > 1) {
                                    if (args[1].equalsIgnoreCase("set")) {
                                        if (sender.hasPermission("pvpbuilder.command.zone.set")) {
                                            if (args.length == 3) {
                                                Player player = (Player) sender;
                                                String uuid = player.getUniqueId().toString();
                                                if (plugin.pos1.containsKey(uuid) && plugin.pos2.containsKey(uuid)) {
                                                    if (plugin.blocksFileConfiguration.containsKey(args[2])) {
                                                        FileConfiguration fileConfiguration = plugin.blocksFileConfiguration.get(args[2]);
                                                        Location loc1 = plugin.pos1.get(uuid);
                                                        Location loc2 = plugin.pos2.get(uuid);
                                                        fileConfiguration.set("zone.start", loc1.getBlockX() + " " + loc1.getBlockY() + " " + loc1.getBlockZ());
                                                        fileConfiguration.set("zone.end", loc2.getBlockX() + " " + loc2.getBlockY() + " " + loc2.getBlockZ());
                                                        ArrayList<String> list = new ArrayList<>();
                                                        list.add(player.getWorld().getName());
                                                        fileConfiguration.set("zone.world", list);
                                                        plugin.blocksFolder.save(args[2]);
                                                        for (String message : plugin.language.get.getStringList("player.pvpbuilder.zone.set.set")) {
                                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{pvpbuilder_file}", args[2])));
                                                        }
                                                    } else {
                                                        for (String message : plugin.language.get.getStringList("player.pvpbuilder.zone.file")) {
                                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                        }
                                                    }
                                                } else {
                                                    for (String message : plugin.language.get.getStringList("player.pvpbuilder.zone.select")) {
                                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                    }
                                                }
                                            } else {
                                                for (String message : plugin.language.get.getStringList("player.pvpbuilder.zone.set.usage")) {
                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                }
                                            }
                                        } else {
                                            for (String message : plugin.language.get.getStringList("player.pvpbuilder.zone.set.permission")) {
                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                            }
                                        }
                                    } else if (args[1].equalsIgnoreCase("remove")) {
                                        if (sender.hasPermission("pvpbuilder.command.zone.remove")) {
                                            if (args.length == 3) {
                                                    if (plugin.blocksFileConfiguration.containsKey(args[2])) {
                                                        FileConfiguration fileConfiguration = plugin.blocksFileConfiguration.get(args[2]);
                                                        fileConfiguration.set("zone", null);
                                                        plugin.blocksFolder.save(args[2]);
                                                        for (String message : plugin.language.get.getStringList("player.pvpbuilder.zone.remove.removed")) {
                                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{pvpbuilder_file}", args[2])));
                                                        }
                                                    } else {
                                                        for (String message : plugin.language.get.getStringList("player.pvpbuilder.zone.file")) {
                                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                        }
                                                    }
                                            } else {
                                                for (String message : plugin.language.get.getStringList("player.pvpbuilder.zone.remove.usage")) {
                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                }
                                            }
                                        } else {
                                            for (String message : plugin.language.get.getStringList("player.pvpbuilder.zone.remove.permission")) {
                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                            }
                                        }
                                    } else {
                                        for (String message : plugin.language.get.getStringList("player.pvpbuilder.zone.usage")) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                        }
                                    }
                                } else {
                                    for (String message : plugin.language.get.getStringList("player.pvpbuilder.zone.usage")) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                    }
                                }
                            } else {
                                for (String message : plugin.language.get.getStringList("player.pvpbuilder.zone.permission")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            }
                        } else {
                            for (String message : plugin.language.get.getStringList("console.pvpbuilder.zone")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("give")) {
                        unknown = false;
                        if (sender.hasPermission("pvpbuilder.command.give")) {
                            if (args.length > 1) {
                                if (args[1].equalsIgnoreCase("block")) {
                                    if (args.length == 5) {
                                        Player target = plugin.getServer().getPlayer(args[2]);
                                        if (target != null) {
                                            if (plugin.blocksFileConfiguration.containsKey(args[3])) {
                                                if (plugin.isInt(args[4]) && Integer.parseInt(args[4]) > 0) {
                                                    FileConfiguration fileConfiguration = plugin.blocksFileConfiguration.get(args[3]);
                                                    if (fileConfiguration.getConfigurationSection("hand") != null) {
                                                        ItemStack itemStack = ItemStack.deserialize(fileConfiguration.getConfigurationSection("hand").getValues(false));
                                                        itemStack.setAmount(Integer.parseInt(args[4]));
                                                        target.getInventory().addItem(itemStack);
                                                        for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.give.block.block")) {
                                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{pvpbuilder_file}", args[3]).replace("{pvpbuilder_amount}", args[4]).replace("{pvpbuilder_target}", target.getName())));
                                                        }
                                                    } else {
                                                        for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.give.block.setup")) {
                                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{pvpbuilder_file}", args[3])));
                                                        }
                                                    }
                                                } else {
                                                    for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.give.block.number")) {
                                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                    }
                                                }
                                            } else {
                                                for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.give.block.file")) {
                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{pvpbuilder_file}", args[3])));
                                                }
                                            }
                                        } else {
                                            for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.give.block.online")) {
                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                            }
                                        }
                                    } else {
                                        for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.give.block.usage")) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                        }
                                    }
                                } else {
                                    for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.give.usage")) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                    }
                                }
                            } else {
                                for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.give.usage")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            }
                        } else {
                            for (String message : plugin.language.get.getStringList("player.pvpbuilder.give.permission")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("spawn")) {
                        unknown = false;
                        if (sender.hasPermission("pvpbuilder.command.spawn")) {
                            if (args.length > 1) {
                                if (args[1].equalsIgnoreCase("effect")) {
                                    if (args.length > 2) {
                                        try {
                                            if (args.length == 8) {
                                                final Player target = plugin.getServer().getPlayer(args[3]);
                                                if (target != null) {
                                                    if (plugin.isInt(args[4]) && plugin.isInt(args[5])) {
                                                        if (args[6].equalsIgnoreCase("true") || (args[6].equalsIgnoreCase("false") && args[7].equalsIgnoreCase("true")) || (args[7].equalsIgnoreCase("false"))) {
                                                            PotionEffectType potion = PotionEffectType.getByName(args[2].toUpperCase());
                                                            try {
                                                                target.addPotionEffect(new PotionEffect(potion, Integer.parseInt(args[4]) * 20, Integer.parseInt(args[5]), Boolean.parseBoolean(args[6]), Boolean.parseBoolean(args[7])));
                                                                for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.effect.spawn")) {
                                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{pvpbuilder_type}", args[2]).replace("{pvpbuilder_player}", target.getName()).replace("{pvpbuilder_duration}", args[4]).replace("{pvpbuilder_amplifier}", args[5])));
                                                                }
                                                            } catch (NoSuchMethodError e) {
                                                                for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.effect.cannot")) {
                                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                                }
                                                            }
                                                        } else {
                                                            for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.effect.boolean")) {
                                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                            }
                                                        }
                                                    } else {
                                                        for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.effect.number")) {
                                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                        }
                                                    }
                                                } else {
                                                    for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.effect.online")) {
                                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                    }
                                                }
                                            } else {
                                                for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.effect.usage")) {
                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                }
                                            }
                                        } catch (IllegalArgumentException e) {
                                            for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.effect.found")) {
                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                            }
                                        }
                                    } else {
                                        ArrayList<String> list = new ArrayList<>();
                                        for (PotionEffectType type : PotionEffectType.values()) {
                                            list.add(String.valueOf(type).toLowerCase());
                                        }
                                        for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.effect.types")) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{pvpbuilder_effect_types}", list.toString().replace("[", "").replace("]", ""))));
                                        }
                                    }
                                } else if (args[1].equalsIgnoreCase("sound")) {
                                    if (args.length > 2) {
                                        try {
                                            Sound type = Sound.valueOf(args[2].toUpperCase());
                                            if (args.length == 9) {
                                                World world = plugin.getServer().getWorld(args[3]);
                                                if (world != null) {
                                                    if (plugin.isInt(args[4]) && plugin.isInt(args[5]) && plugin.isInt(args[6])) {
                                                        if (plugin.isFloat(args[7]) && plugin.isFloat(args[8])) {
                                                            int x = Integer.parseInt(args[4]);
                                                            int y = Integer.parseInt(args[5]);
                                                            int z = Integer.parseInt(args[6]);
                                                            Location loc = new Location(world, x, y, z);
                                                            world.playSound(loc, type, Float.parseFloat(args[7]), Float.parseFloat(args[8]));
                                                            for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.sound.spawn")) {
                                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{pvpbuilder_type}", args[2]).replace("{pvpbuilder_world}", world.getName()).replace("{pvpbuilder_x}", String.valueOf(x)).replace("{pvpbuilder_y}", String.valueOf(y)).replace("{pvpbuilder_z}", String.valueOf(z))));
                                                            }
                                                        } else {
                                                            for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.sound.float")) {
                                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                            }
                                                        }
                                                    } else {
                                                        for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.sound.number")) {
                                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                        }
                                                    }
                                                } else {
                                                    for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.sound.world")) {
                                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                    }
                                                }
                                            } else {
                                                for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.sound.usage")) {
                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                }
                                            }
                                        } catch (IllegalArgumentException e) {
                                            for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.sound.found")) {
                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                            }
                                        }
                                    } else {
                                        ArrayList<String> list = new ArrayList<>();
                                        for (Sound type : Sound.values()) {
                                            list.add(type.name().toLowerCase());
                                        }
                                        for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.sound.types")) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{pvpbuilder_sound_types}", list.toString().replace("[", "").replace("]", ""))));
                                        }
                                    }

                                } else if (args[1].equalsIgnoreCase("type")) {
                                    if (args.length > 2) {
                                        try {
                                            EntityType type = EntityType.valueOf(args[2].toUpperCase());
                                            if (args.length == 7) {
                                                World world = plugin.getServer().getWorld(args[3]);
                                                if (world != null) {
                                                    if (plugin.isInt(args[4]) && plugin.isInt(args[5]) && plugin.isInt(args[6])) {
                                                        int x = Integer.parseInt(args[4]);
                                                        int y = Integer.parseInt(args[5]);
                                                        int z = Integer.parseInt(args[6]);
                                                        Location loc = new Location(world, x, y, z);
                                                        world.spawnEntity(loc, type);
                                                        for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.type.spawn")) {
                                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{pvpbuilder_type}", args[2]).replace("{pvpbuilder_world}", world.getName()).replace("{pvpbuilder_x}", String.valueOf(x)).replace("{pvpbuilder_y}", String.valueOf(y)).replace("{pvpbuilder_z}", String.valueOf(z))));
                                                        }
                                                    } else {
                                                        for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.type.number")) {
                                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                        }
                                                    }
                                                } else {
                                                    for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.type.world")) {
                                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                    }
                                                }
                                            } else {
                                                for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.type.usage")) {
                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                }
                                            }
                                        } catch (IllegalArgumentException e) {
                                            for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.type.found")) {
                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                            }
                                        }
                                    } else {
                                        ArrayList<String> list = new ArrayList<>();
                                        for (EntityType type : EntityType.values()) {
                                            list.add(type.name().toLowerCase());
                                        }
                                        for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.type.types")) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{pvpbuilder_types}", list.toString().replace("[", "").replace("]", ""))));
                                        }
                                    }
                                } else if (args[1].equalsIgnoreCase("custom")) {
                                    if (args.length > 2) {
                                        if (args[2].equalsIgnoreCase("lightning_strike")) {
                                            if (args.length == 7) {
                                                World world = plugin.getServer().getWorld(args[3]);
                                                if (world != null) {
                                                    if (plugin.isInt(args[4]) && plugin.isInt(args[5]) && plugin.isInt(args[6])) {
                                                        int x = Integer.parseInt(args[4]);
                                                        int y = Integer.parseInt(args[5]);
                                                        int z = Integer.parseInt(args[6]);
                                                        Location loc = new Location(world, x, y, z);
                                                        world.strikeLightning(loc);
                                                        for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.custom.lightning_strike.spawn")) {
                                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{pvpbuilder_type}", args[2]).replace("{pvpbuilder_world}", world.getName()).replace("{pvpbuilder_x}", String.valueOf(x)).replace("{pvpbuilder_y}", String.valueOf(y)).replace("{pvpbuilder_z}", String.valueOf(z))));
                                                        }
                                                    } else {
                                                        for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.custom.lightning_strike.number")) {
                                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                        }
                                                    }
                                                } else {
                                                    for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.custom.lightning_strike.world")) {
                                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                    }
                                                }
                                            } else {
                                                for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.custom.lightning_strike.usage")) {
                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                }
                                            }
                                        } else if (args[2].equalsIgnoreCase("firework")) {
                                            if (args.length > 9) {
                                                World world = plugin.getServer().getWorld(args[3]);
                                                if (world != null) {
                                                    if (plugin.isInt(args[4]) && plugin.isInt(args[5]) && plugin.isInt(args[6])) {
                                                        int x = Integer.parseInt(args[4]);
                                                        int y = Integer.parseInt(args[5]);
                                                        int z = Integer.parseInt(args[6]);
                                                        Location loc = new Location(world, x, y, z);
                                                        boolean error = false;
                                                        if (!plugin.isInt(args[7])) {
                                                            error = true;
                                                        }
                                                        String[] multiColor = args[8].split(":");
                                                        String[] multiColorFade = args[9].split(":");
                                                        ArrayList<Color> colors = new ArrayList<>();
                                                        ArrayList<Color> colorsFade = new ArrayList<>();
                                                        for (String string : multiColor) {
                                                            String[] colorSplit = string.split(",");
                                                            if (colorSplit.length == 3) {
                                                                if (plugin.isInt(colorSplit[0]) && plugin.isInt(colorSplit[1]) && plugin.isInt(colorSplit[2])) {
                                                                    colors.add(Color.fromRGB(Integer.parseInt(colorSplit[0]), Integer.parseInt(colorSplit[1]), Integer.parseInt(colorSplit[1])));
                                                                } else {
                                                                    error = true;
                                                                }
                                                            } else {
                                                                error = true;
                                                            }
                                                        }
                                                        if (!args[9].equalsIgnoreCase("null"))
                                                            for (String string : multiColorFade) {
                                                                String[] colorSplit = string.split(",");
                                                                if (colorSplit.length == 3) {
                                                                    if (plugin.isInt(colorSplit[0]) && plugin.isInt(colorSplit[1]) && plugin.isInt(colorSplit[2])) {
                                                                        colorsFade.add(Color.fromRGB(Integer.parseInt(colorSplit[0]), Integer.parseInt(colorSplit[1]), Integer.parseInt(colorSplit[1])));
                                                                    } else {
                                                                        error = true;
                                                                    }
                                                                } else {
                                                                    error = true;
                                                                }
                                                            }
                                                        if (!error) {
                                                            if (args.length > 10) {
                                                                try {
                                                                    if (args.length == 13) {
                                                                        if (args[11].equalsIgnoreCase("true") || (args[11].equalsIgnoreCase("false") && args[12].equalsIgnoreCase("true")) || args[12].equalsIgnoreCase("false")) {
                                                                            Firework firework = (Firework) world.spawnEntity(loc, EntityType.FIREWORK);
                                                                            FireworkMeta fireworkMeta = firework.getFireworkMeta();
                                                                            fireworkMeta.setPower(Integer.parseInt(args[7]));
                                                                            fireworkMeta.addEffect(FireworkEffect.builder().withColor(colors).withFade(colorsFade).with(FireworkEffect.Type.valueOf(args[10].toUpperCase())).flicker(Boolean.parseBoolean(args[11])).trail(Boolean.parseBoolean(args[12])).build());
                                                                            firework.setFireworkMeta(fireworkMeta);
                                                                            for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.custom.firework.spawn")) {
                                                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{pvpbuilder_type}", args[2]).replace("{pvpbuilder_world}", world.getName()).replace("{pvpbuilder_x}", String.valueOf(x)).replace("{pvpbuilder_y}", String.valueOf(y)).replace("{pvpbuilder_z}", String.valueOf(z))));
                                                                            }
                                                                        } else {
                                                                            for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.custom.firework.boolean")) {
                                                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                                            }
                                                                        }
                                                                    } else {
                                                                        for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.custom.firework.usage")) {
                                                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                                        }
                                                                    }
                                                                } catch (IllegalArgumentException e) {
                                                                    for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.custom.firework.found")) {
                                                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                                    }
                                                                }
                                                            } else {
                                                                ArrayList<String> list = new ArrayList<>();
                                                                for (FireworkEffect.Type type : FireworkEffect.Type.values())
                                                                    list.add(type.name().toLowerCase());
                                                                for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.custom.firework.types")) {
                                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{pvpbuilder_firework_types}", list.toString().replace("[", "").replace("]", ""))));
                                                                }
                                                            }
                                                        } else {
                                                            for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.custom.firework.color")) {
                                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                            }
                                                        }
                                                    } else {
                                                        for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.custom.firework.number")) {
                                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                        }
                                                    }
                                                } else {
                                                    for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.custom.firework.world")) {
                                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                    }
                                                }
                                            } else {
                                                for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.custom.firework.usage")) {
                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                }
                                            }
                                        } else if (args[2].equalsIgnoreCase("iron_golem")) {
                                            if (args.length == 9) {
                                                World world = plugin.getServer().getWorld(args[3]);
                                                if (world != null) {
                                                    if (plugin.isInt(args[4]) && plugin.isInt(args[5]) && plugin.isInt(args[6])) {
                                                        Player target = plugin.getServer().getPlayer(args[7]);
                                                        if (target != null) {
                                                            if (plugin.config.get.contains("iron_golem." + args[8])) {
                                                                IronGolem ironGolem = (IronGolem) world.spawnEntity(new Location(world, Integer.parseInt(args[4]), Integer.parseInt(args[5]), Integer.parseInt(args[6])), EntityType.IRON_GOLEM);
                                                                ironGolem.setPlayerCreated(false);
                                                                ironGolem.setRemoveWhenFarAway(false);
                                                                ironGolem.setHealth(plugin.config.get.getInt("iron_golem." + args[8] + ".health"));
                                                                ironGolem.setCustomName(ChatColor.translateAlternateColorCodes('&', plugin.config.get.getString("iron_golem." + args[8] + ".name").replace("{pvpbuilder_player}", target.getName())));
                                                                ironGolem.setCustomNameVisible(true);
                                                                plugin.ironGolemManager.ironGolemGroup.put(ironGolem, args[8]);
                                                                plugin.ironGolemManager.ironGolemOwner.put(ironGolem, target);
                                                                plugin.ironGolemManager.run(plugin.config.get.getLong("iron_golem." + args[8] + ".update"),
                                                                        plugin.config.get.getLong("iron_golem." + args[8] + ".delay-disappear"),
                                                                        plugin.config.get.getLong("iron_golem." + args[8] + ".radius"),
                                                                        plugin.config.get.getLong("iron_golem." + args[8] + ".tp-radius"),
                                                                        target, ironGolem);
                                                                for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.custom.iron_golem.spawn")) {
                                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                                }
                                                            } else {
                                                                for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.custom.iron_golem.found")) {
                                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                                }
                                                            }
                                                        } else {
                                                            for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.custom.iron_golem.online")) {
                                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                            }
                                                        }
                                                    } else {
                                                        for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.custom.iron_golem.number")) {
                                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                        }
                                                    }
                                                } else {
                                                    for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.custom.iron_golem.world")) {
                                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                    }
                                                }
                                            } else {
                                                for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.custom.iron_golem.usage")) {
                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                }
                                            }
                                        } else {
                                            for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.custom.found")) {
                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                            }
                                        }
                                    } else {
                                        ArrayList<String> list = new ArrayList<>();
                                        list.add("lightning_strike");
                                        list.add("firework");
                                        list.add("iron_golem");
                                        for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.custom.types")) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{pvpbuilder_custom_types}", list.toString().replace("[", "").replace("]", ""))));
                                        }
                                    }
                                } else {
                                    for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.usage")) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                    }
                                }
                            } else {
                                for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.spawn.usage")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            }
                        } else {
                            for (String message : plugin.language.get.getStringList("player.pvpbuilder.spawn.permission")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    }
                    if (unknown) {
                        for (String message : plugin.language.get.getStringList(path + ".pvpbuilder.command.unknown")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{pvpbuilder_command}", args[0])));
                        }
                    }
                }
            } else {
                for (String message : plugin.language.get.getStringList("player.pvpbuilder.command.permission")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
            }
        }
        return true;
    }
}