package dev.itscreo.creosound;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CreoSound extends JavaPlugin implements CommandExecutor, TabCompleter {

    @Override
    public void onEnable() {
        getCommand("sound").setExecutor(this);
        getCommand("sound").setTabCompleter(this);
        getCommand("soundradius").setExecutor(this);
        getCommand("soundradius").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("sound")) {
            if (!sender.hasPermission("creosound.sound")) {
                sender.sendMessage(format("§x§F§F§0§0§0§0[✘] Ошибка! &fУ вас нет прав для выполнения этой команды."));
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(format("§x§E§B§0§0§F§F[♪] &fИспользование: &7/sound [Игрок | All] [ID] [Громкость] [Тон]"));
                return true;
            }

            String target = args[0];
            String soundName = args[1];
            float volume = 1.0f;
            float pitch = 1.0f;

            if (args.length >= 3) {
                try {
                    volume = Float.parseFloat(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(format("§x§F§F§0§0§0§0[✘] Ошибка! &fВы неверно ввели громкость звука."));
                    return true;
                }
            }

            if (args.length >= 4) {
                try {
                    pitch = Float.parseFloat(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(format("§x§F§F§0§0§0§0[✘] Ошибка! &fВы неверно ввели тон звука."));
                    return true;
                }
            }

            Sound sound = getSoundByName(soundName);

            if (sound == null) {
                sender.sendMessage(format("§x§F§F§0§0§0§0[✘] Ошибка! &fУказанный звук не найден."));
                return true;
            }

            if (!target.equalsIgnoreCase("all")) {
                Player player = Bukkit.getPlayer(target);
                if (player != null) {
                    player.playSound(player.getLocation(), sound, volume, pitch);
                    sender.sendMessage(format("§x§E§B§0§0§F§F[♪] &fЗвук §x§0§0§F§F§F§F"  + sound.name() + "&f был проигран игроку &7" + player.getName()));
                } else {
                    sender.sendMessage(format("§x§F§F§0§0§0§0[✘] Ошибка! &fУказанный игрок не найден."));
                }
            } else {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getLocation(), sound, volume, pitch);
                }
                sender.sendMessage(format("§x§E§B§0§0§F§F[♪] &fЗвук §x§0§0§F§F§F§F" + sound.name() + "&f был проигран всем игрокам."));
            }

            return true;
        } else if (command.getName().equalsIgnoreCase("soundradius")) {
            if (!sender.hasPermission("creosound.soundradius")) {
                sender.sendMessage(format("§x§F§F§0§0§0§0[✘] Ошибка! &fУ вас нет прав для выполнения этой команды."));
                return true;
            }

            if (args.length < 5) {
                sender.sendMessage(format("§x§E§B§0§0§F§F[♪] &fИспользование: &7/soundradius [Игрок] [ID] [Громкость] [Тон] [Радиус]"));
                return true;
            }

            String targetName = args[0];
            String soundName = args[1];
            float volume;
            float pitch;
            double radius;

            try {
                volume = Float.parseFloat(args[2]);
                pitch = Float.parseFloat(args[3]);
                radius = Double.parseDouble(args[4]);
            } catch (NumberFormatException e) {
                sender.sendMessage(format("§x§F§F§0§0§0§0[✘] Ошибка! &fНеверный формат числа."));
                return true;
            }

            Sound sound = getSoundByName(soundName);

            if (sound == null) {
                sender.sendMessage(format("§x§F§F§0§0§0§0[✘] Ошибка! &fУказанный звук не найден."));
                return true;
            }

            Player targetPlayer = Bukkit.getPlayer(targetName);

            if (targetPlayer == null) {
                sender.sendMessage(format("§x§F§F§0§0§0§0[✘] Ошибка! &fУказанный игрок не найден."));
                return true;
            }

            Location targetLocation = targetPlayer.getLocation();

            int count = 0;

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getLocation().distanceSquared(targetLocation) <= (radius * radius)) {
                    player.playSound(targetLocation, sound, volume, pitch);
                    count++;
                }
            }

            sender.sendMessage(format("§x§E§B§0§0§F§F[♪] &fЗвук §x§0§0§F§F§F§F" + sound.name() + "&f был проигран в радиусе &7" + radius + "&f блоков от игрока &7" + targetName));

            targetPlayer.playSound(targetLocation, sound, volume, pitch);
            return true;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("sound")) {
            if (args.length == 1) {
                completions.add("all");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            } else if (args.length == 2) {
                String input = args[1].toLowerCase();
                for (String soundName : getAvailableSoundNames()) {
                    if (soundName.startsWith(input)) {
                        completions.add(soundName);
                    }
                }
            } else if (args.length == 3) {
                completions.add("1.0");
            } else if (args.length == 4) {
                completions.add("1.0");
            }
        } else if (command.getName().equalsIgnoreCase("soundradius")) {
            if (args.length == 1) {
                for (Player player : Bukkit.getOnlinePlayers()) {

                    completions.add(player.getName());
                }
            } else if (args.length == 2) {
                String input = args[1].toLowerCase();
                for (String soundName : getAvailableSoundNames()) {
                    if (soundName.startsWith(input)) {
                        completions.add(soundName);
                    }
                }
            } else if (args.length == 3) {
                completions.add("1.0");
            } else if (args.length == 4) {
                completions.add("1.0");
            } else if (args.length == 5) {
                completions.add("15");
            }
        }

        return completions;
    }

    private String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private Sound getSoundByName(String soundName) {
        try {
            return Sound.valueOf(soundName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private List<String> getAvailableSoundNames() {
        List<String> soundNames = new ArrayList<>();
        for (Sound sound : Sound.values()) {
            soundNames.add(sound.name().toLowerCase());
        }
        return soundNames;
    }
}
