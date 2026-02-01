package me.lynes.kese.cmds;

import me.lynes.kese.Kese;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.milkbowl.vault.economy.Economy;

public class KeseCmd implements CommandExecutor, TabCompleter {
    private final Kese plugin = Kese.getInstance();
    private final Economy economy = plugin.getEconomy();
    private static final String PREFIX = "§8[§aTowny§8] ";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getLogger().log(Level.INFO, PREFIX + "Bu komudu sadece oyuncular kullanabilir.");
            return true;
        }


        Player player = (Player) sender;

        if (args.length > 0 && args[0].equalsIgnoreCase("koy")) {
            if (args.length == 2) {
                double amount;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage(PREFIX + "§cMiktar bir sayı olmalıdır.");
                    return true;
                }

                if (amount < 0) {
                    player.sendMessage(PREFIX + "§cMiktar bir sayı olmalıdır.");
                    return true;
                }

                String formatted = economy.format(amount);
                int z = 0;
                HashMap<Integer, ItemStack> hm = player.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, (int) amount));
                if (hm.isEmpty()) {
                    if (!economy.depositPlayer(player, amount).transactionSuccess()) {
                        player.sendMessage(PREFIX + "§fBir hata oluştu işlem gerçekleştirilemiyor.");
                        return true;
                    }

                    player.sendMessage(PREFIX + "§fKeseye §6" + formatted + " §faltın koyuldu yeni altın miktarı §6" + economy.format(economy.getBalance(player)) + " §fAltın.");

                    return true;
                } else {
                    for (Map.Entry<Integer, ItemStack> entry : hm.entrySet()) {
                        ItemStack value = entry.getValue();
                        z += value.getAmount();
                    }

                    if (!economy.depositPlayer(player, amount - z).transactionSuccess()) {
                        player.sendMessage(PREFIX + "§fBir hata oluştu işlem gerçekleştirilemiyor");
                        return true;
                    }

                    formatted = economy.format(amount - z);
                    player.sendMessage(PREFIX + "§fKeseye §6" + formatted + " §faltın koyuldu yeni altın miktarı §6" + economy.format(economy.getBalance(player)) + " §fAltın.");

                    return true;
                }

            } else {
                player.sendMessage(PREFIX + "§fMiktar sayı olmalıdır.");
                return true;
            }
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("al")) {
            if (args.length == 2) {
                double amount;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage(PREFIX + "§fMiktar sayı olmalıdır.");
                    return true;
                }

                if (amount < 0) {
                    player.sendMessage(PREFIX + "§fMiktar sayı olmalıdır.");
                    return true;
                }

                String formatted = economy.format(amount);

                if (economy.has(player, amount)) {
                    double bal = economy.getBalance(player);
                    if (!economy.withdrawPlayer(player, amount).transactionSuccess()) {
                        player.sendMessage(PREFIX + "§fBir hata oluştu işlem gerçekleştirilemiyor");
                        return true;
                    }
                    player.sendMessage(PREFIX + "§fKeseden §6" + formatted + " §faltın çekildi kalan altın miktarı §6" + economy.format(economy.getBalance(player)) + " §fAltın.");
                    HashMap<Integer, ItemStack> map = player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, (int) amount));
                    if (!map.isEmpty() && map.get(0).getAmount() != 0) {
                        player.sendMessage(PREFIX + "§fEnvanterinde yer kalmadığı için altınlar yere düştü");

                        if (map.get(0).getAmount() <= 64) {
                            player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.GOLD_INGOT, map.get(0).getAmount()));
                        } else {
                            for (int i = map.get(0).getAmount(); i >= 64; i = i - 64) {
                                player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.GOLD_INGOT, 64));
                            }

                            if (map.get(0).getAmount() % 64 != 0) {
                                player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.GOLD_INGOT, map.get(0).getAmount() % 64));
                            }
                        }
                    }
                    return true;
                } else {
                    player.sendMessage(PREFIX + "§fKesenizde yeterli miktarda altın yok");
                    return true;
                }
            } else {
                player.sendMessage(PREFIX + "§fMiktar sayı olmalıdır");
                return true;
            }
        }

        if (args.length > 0 && (args[0].equalsIgnoreCase("altıngönder") || args[0].equalsIgnoreCase("altingonder"))) {
            if (args.length == 3) {
                double amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage(PREFIX + "§fMiktar sayı olmalıdır.");
                    return true;
                }

                if (amount < 1) {
                    player.sendMessage(PREFIX + "§fMiktar birden küçük olamaz");
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
                        player.sendMessage(PREFIX + "§fBir hata oluştu işlem gerçekleştirilemiyor.");
                        return true;
                    }

                    if (!economy.depositPlayer(target, amount).transactionSuccess()) {
                        player.sendMessage(PREFIX + "§fBir hata oluştu işlem gerçekleştirilemiyor.");
                        return true;
                    }

                    String formatted = economy.format(amount);
                    player.sendMessage(PREFIX + "§6" + target.getName() + " §fisimli oyuncuya §6" + formatted + " §faltın gönderdiniz kalan altın miktarı §6" + economy.format(economy.getBalance(player)) + " §fAltın.");
                    target.sendMessage(PREFIX + "§6" + player.getName() + " §fisimli oyuncudan §6" + formatted + " §faltın aldınız yeni altın miktarı §6" + economy.format(economy.getBalance(target)) + " §fAltın.");
                    return true;
                } else {
                    player.sendMessage(PREFIX + "§fKesenizde yeterli miktarda altın bulunamadı.");
                    return true;
                }
            } else {
                player.sendMessage(PREFIX + "§fYanlış Argüman Doğrusu §6/kese altıngönder §foyuncu-ismi para-miktarı");
                return true;
            }
        }

        if (args.length > 0) {
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(PREFIX + "§6" + economy.format(economy.getBalance(player)) + " §faltınınız var.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("al");
            completions.add("koy");
            completions.add("altıngönder");
            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        }
        return null;
    }


    private void sendHelpMessage(Player player) {
        player.sendMessage(PREFIX + "§6/kese [Komut] &fKullanın");
    }

}
