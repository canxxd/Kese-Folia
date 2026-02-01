package me.lynes.kese.cmds;

import me.lynes.kese.Kese;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.milkbowl.vault.economy.Economy;

public class AltinCmd implements CommandExecutor, TabCompleter {
    private final Kese plugin = Kese.getInstance();
    private final Economy economy = plugin.getEconomy();
    private static final String PREFIX = "§8[§aTowny§8] ";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getLogger().log(Level.INFO, PREFIX + "Bu komudu sadece oyuncular kullanabilir");
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0 && args[0].equalsIgnoreCase("gonder")) {
            if (args.length == 3) {
                double amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage(PREFIX + "§fMiktar bir sayı olmalıdır.");
                    return true;
                }

                if (amount < 1) {
                    player.sendMessage(PREFIX + "§fMiktar bir birden küçük olamaz.");
                    return true;
                }

                if (economy.has(player, amount)) {
                    Player target = Bukkit.getPlayer(args[1]);

                    if (target == null) {
                        player.sendMessage(PREFIX + "§fBelirtilen oyuncu bulunamadı ya da çevrimiçi değil.");
                        return true;
                    }

                    if (target == player) {
                        player.sendMessage(PREFIX + "§fKendine altın gönderemezsin");
                        return true;
                    }

                    if (!economy.withdrawPlayer(player, amount).transactionSuccess()) {
                        player.sendMessage(PREFIX + "§4Bir hata oluştu işlem gerçekleştirilemiyor.");
                        return true;
                    }

                    if (!economy.depositPlayer(target, amount).transactionSuccess()) {
                        player.sendMessage(PREFIX + "§4Bir hata oluştu işlem gerçekleştirilemiyor.");
                        return true;
                    }

                    String formatted = economy.format(amount);
                    player.sendMessage(PREFIX + "§6" + target.getName() + " §fisimli oyuncuya §6" + formatted + " §faltın gönderdiniz kalan altın miktarı §6" + economy.format(economy.getBalance(player)) + " §fAltın.");
                    target.sendMessage(PREFIX + "§6" + player.getName() + " §fisimli oyuncudan §6" + formatted + " §faltın aldınız yeni altın miktarı §6" + economy.format(economy.getBalance(target)) + " §fAltın.");
                    return true;
                } else {
                    player.sendMessage(PREFIX + "§4Kesenizde yeterli miktarda altın yok.");
                    return true;
                }

            } else {
                sendHelpMessage(player);
                return true;
            }
        } else {
            sendHelpMessage(player);
        }


        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("gonder");
            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        }
        return null;
    }


    private void sendHelpMessage(Player player) {
        player.sendMessage(PREFIX + "§6/altin gonder (oyuncu) [miktar] §fOyuncuya altın gönderir");
    }


}
