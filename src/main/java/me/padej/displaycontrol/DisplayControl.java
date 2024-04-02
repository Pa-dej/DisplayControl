package me.padej.displaycontrol;

import me.padej.displaycontrol.Control.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class DisplayControl extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Remover(), this);
        getServer().getPluginManager().registerEvents(new Replacer(), this);
        getServer().getPluginManager().registerEvents(new Rotater(), this);
        getServer().getPluginManager().registerEvents(new GravityGun(), this);


        getServer().getPluginManager().registerEvents(this, this);

        // Запускаем задачу создания частиц каждый тик
        Particles.startParticleTask();
        Rotater.startParticleTask();
    }
}
