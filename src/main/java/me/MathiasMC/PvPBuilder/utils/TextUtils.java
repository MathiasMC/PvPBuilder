package me.MathiasMC.PvPBuilder.utils;

import me.MathiasMC.PvPBuilder.PvPBuilder;
import org.bukkit.Bukkit;

import java.util.logging.Logger;

public class TextUtils {

    private final PvPBuilder plugin;

    private final Logger logger = Bukkit.getLogger();

    private final String prefix;

    public TextUtils(final PvPBuilder plugin) {
        this.plugin = plugin;
        this.prefix = plugin.getDescription().getName();
    }

    public void info(String text) {
        logger.info("[" + prefix + "] " + text);
    }

    public void warning(String text) {
        logger.warning("[" + prefix + "] " + text);
    }

    public void error(String text) {
        logger.severe("[" + prefix + "] " + text);
    }

    public void debug(String text) {
        logger.warning("[" + prefix + "] [DEBUG] " + text);
    }

    public void exception(StackTraceElement[] stackTraceElement, String text) {
        info("(!) " + prefix + " has being encountered an error, pasting below for support (!)");
        for (int i = 0; i < stackTraceElement.length; i++) {
            error(stackTraceElement[i].toString());
        }
        info("Message: " + text);
        info(prefix + " version: " + plugin.getDescription().getVersion());
        info("Please report this error to me on spigot");
        info("(!) " + prefix + " (!)");
    }
}