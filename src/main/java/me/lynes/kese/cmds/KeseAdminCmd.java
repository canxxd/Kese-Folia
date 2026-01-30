package me.lynes.kese.cmds;

import me.lynes.kese.Kese;
import me.lynes.kese.utils.PlayerUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class KeseAdminCmd implements CommandExecutor, TabCompleter {
    private final Kese plugin = Kese.getInstance();
    private final Economy economy = plugin.getEconomy();
    private static final String PREFIX = "§8[§aTowny§8] ";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length > 0 && args[0].equalsIgnoreCase("set")) {
            if (args.length == 3) {
                double amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(PREFIX + "§fMiktar sayı olmalıdır.");
                    return true;
                }

                if (amount < 0) {
                    sender.sendMessage(PREFIX + "§fMiktar sayı olmalıdır.");
                    return true;
                }

                OfflinePlayer target = PlayerUtil.getOfflinePlayer(args[1]);

                if (target == null) {
                    sender.sendMessage(PREFIX + "§cBelirtilen oyuncu bulunamadı.");
                    return true;
                }

                double current = economy.getBalance(target);
                double delta = amount - current;
                boolean ok = delta >= 0
                        ? economy.depositPlayer(target, delta).transactionSuccess()
                        : economy.withdrawPlayer(target, Math.abs(delta)).transactionSuccess();
                sender.sendMessage(ok
                        ? PREFIX + "§fBaşarıyla §6" + target.getName() + " §fisimli oyuncunun altın miktarı " + economy.format(amount) + " yapıldı."
                        : PREFIX + "§fBir hata oluştu, işlem gerçekleştirilemiyor.");

                return true;
            } else {
                sendHelpMessage(sender);
                return true;
            }
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("add")) {
            if (args.length == 3) {
                double amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(PREFIX + "§fMiktar sayı olmalıdır.");
                    return true;
                }

                if (amount < 0) {
                    sender.sendMessage(PREFIX + "§fMiktar sayı olmalıdır.");
                    return true;
                }

                OfflinePlayer target = PlayerUtil.getOfflinePlayer(args[1]);

                if (target == null) {
                    sender.sendMessage(PREFIX + "§fBelirtilen oyuncu bulunamadı.");
                    return true;
                }

                boolean ok = economy.depositPlayer(target, amount).transactionSuccess();
                sender.sendMessage(ok
                        ? PREFIX + "§fBaşarıyla §6" + target.getName() + " §fisimli oyuncunun altın miktarına " + economy.format(amount) + " eklendi."
                        : PREFIX + "§cBir hata oluştu işlem gerçekleştirilemiyor.");

                return true;
            } else {
                sendHelpMessage(sender);
                return true;
            }
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("remove")) {
            if (args.length == 3) {
                double amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(PREFIX + "§fMiktar sayı olmalıdır.");
                    return true;
                }

                if (amount < 0) {
                    sender.sendMessage(PREFIX + "§fMiktar sayı olmalıdır.");
                    return true;
                }

                OfflinePlayer target = PlayerUtil.getOfflinePlayer(args[1]);

                if (target == null) {
                    sender.sendMessage(PREFIX + "§fBelirtilen oyuncu bulunamadı.");
                    return true;
                }

                double current = economy.getBalance(target);
                boolean ok = economy.withdrawPlayer(target, Math.min(amount, current)).transactionSuccess();
                sender.sendMessage(ok
                        ? PREFIX + "§fBaşarıyla §6" + target.getName() + " §fisimli oyuncunun altın miktarından " + economy.format(Math.min(amount, current)) + " çıkarıldı."
                        : PREFIX + "§cBir hata oluştu işlem gerçekleştirilemiyor.");

                return true;
            } else {
                sendHelpMessage(sender);
                return true;
            }
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("balance")) {
            if (args.length == 2) {
                OfflinePlayer target = PlayerUtil.getOfflinePlayer(args[1]);

                if (target == null) {
                    sender.sendMessage(PREFIX + "§fBelirtilen oyuncu bulunamadı veya aktif değil.");
                    return true;
                }

                sender.sendMessage(PREFIX + "§f" + target.getName() + " §6kesenin miktarı §f" + economy.getBalance(target));
                return true;
            } else {
                sendHelpMessage(sender);
                return true;
            }
        }


        sendHelpMessage(sender);


        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("set");
            completions.add("add");
            completions.add("remove");
            completions.add("balance");
            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        }
        return null;
    }

    private void sendHelpMessage(CommandSender player) {
        player.sendMessage(PREFIX + "§b.oOo._________.[ §6/keseadmin §b]._________.oOo.");
        player.sendMessage(PREFIX + "§6/keseadmin §fset §6oyuncu miktar §fHedef oyuncunun altın miktarını §6miktar §folarak ayarlar.");
        player.sendMessage(PREFIX + "§6/keseadmin §fadd §6oyuncu miktar §fHedef oyuncunun altın miktarına §6miktar §fekler.");
        player.sendMessage(PREFIX + "§6/keseadmin §fremove §6oyuncu miktar §fHedef oyuncunun altın miktarından §6miktar §fçıkarır.");
        player.sendMessage(PREFIX + "§6/keseadmin §fbalance §6oyuncu §fHedef oyuncunun altın miktarını gösterir.");
        player.sendMessage(PREFIX + "§b");
    }

}
