package me.padej.displaycontrol;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Particles {

    public static void startParticleTask() {
        // Планируем выполнение задачи каждый тик
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    // Проверяем, держит ли игрок в руке DEBUG_STICK
                    if (player.getInventory().getItemInMainHand().getType() == Material.DEBUG_STICK ||
                            player.getInventory().getItemInMainHand().getType() == Material.STRUCTURE_BLOCK ||
                            player.getInventory().getItemInMainHand().getType() == Material.JIGSAW ||
                            player.getInventory().getItemInMainHand().getType() == Material.STRUCTURE_VOID) {
                        emitParticles(player);
                    }
                }
            }
        }.runTaskTimer(DisplayControl.getPlugin(DisplayControl.class), 0L, 20L); // 20 тиков = 1 секунда
    }

    private static void emitParticles(Player player) {
        for (Entity entity : player.getNearbyEntities(100, 100, 100)) {
            if (entity.getType() == EntityType.BLOCK_DISPLAY || entity.getType() == EntityType.ITEM_DISPLAY) {
                entity.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, entity.getLocation(), 1, 0, 0, 0, 0);
            }
        }
    }
}