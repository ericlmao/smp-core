package games.negative.plugin.core;

import games.negative.alumina.message.Message;
import games.negative.plugin.Plugin;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

public enum Locale {

    ;

    private final String[] defMessage;
    private Message message;

    Locale(@NotNull String... defMessage) {
        this.defMessage = defMessage;
    }

    public static void init(@NotNull Plugin plugin) {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        validateFile(plugin, file);

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        boolean changed = false;
        for (Locale entry : values()) {
            if (config.isSet(entry.name())) continue;

            List<String> message = List.of(entry.defMessage);
            config.set(entry.name(), message);
            changed = true;
        }

        if (changed) saveFile(plugin, file, config);

        for (Locale entry : values()) {
            entry.message = Message.of(config.getStringList(entry.name()));
        }
    }

    private static void saveFile(@NotNull Plugin plugin, @NotNull File file, @NotNull FileConfiguration config) {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save messages.yml file!");
        }
    }

    private static void validateFile(@NotNull Plugin plugin, @NotNull File file) {
        if (!file.exists()) {
            boolean dirSuccess = file.getParentFile().mkdirs();
            if (dirSuccess) plugin.getLogger().info("Created new plugin directory file!");

            try {
                boolean success = file.createNewFile();
                if (!success) return;

                plugin.getLogger().info("Created messages.yml file!");
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create messages.yml file!");
            }
        }
    }


    public void send(CommandSender sender) {
        message.send(sender);
    }

    public <T extends Iterable<? extends CommandSender>> void send(T iterable) {
        message.send(iterable);
    }

    public void broadcast() {
        message.broadcast();
    }

    public Message replace(String placeholder, String replacement) {
        return message.replace(placeholder, replacement);
    }
}