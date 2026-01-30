package me.lynes.kese;

import me.lynes.kese.cmds.AltinCmd;
import me.lynes.kese.cmds.KeseAdminCmd;
import me.lynes.kese.cmds.KeseCmd;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.plugin.RegisteredServiceProvider;

public final class Kese extends JavaPlugin {

    private static Kese instance;
    private Economy economy;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();

        Metrics metrics = new Metrics(this, 13183);

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economy = rsp.getProvider();
        } else {
            getLogger().severe("Vault Economy sağlayıcısı bulunamadı Lütfen bir vault eklentisi kurun");
        }
        getCommand("kese").setExecutor(new KeseCmd());
        getCommand("altin").setExecutor(new AltinCmd());
        getCommand("keseadmin").setExecutor(new KeseAdminCmd());
 
    }

    @Override
    public void onDisable() {
    }

    public static Kese getInstance() {
        return instance;
    }
    public Economy getEconomy() { return economy;}

}
