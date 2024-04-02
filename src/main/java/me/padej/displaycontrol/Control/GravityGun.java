package me.padej.displaycontrol.Control;

import me.padej.displaycontrol.DisplayControl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;

import static me.padej.displaycontrol.Control.TargetEntity.targetedEntity;

public class GravityGun implements Listener {

    private boolean raycastActive = false;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // Проверяем, что кликнули ПКМ и предмет в руке - DEBUG_STICK
        if (event.getAction().isLeftClick() && itemInHand.getType() == Material.DEBUG_STICK && targetedEntity == null) {
            // Запускаем рэйкаст
            performRaycast(player);
        }
    }

    private void performRaycast(Player player) {
        if (raycastActive) {
            return; // Если рейкаст уже активен, не запускаем его повторно
        }
        raycastActive = true;

        Vector origin = player.getEyeLocation().toVector();
        Vector direction = player.getEyeLocation().getDirection();

        for (double distance = 0; distance < 100; distance += 0.5) {
            Vector currentPosition = origin.clone().add(direction.clone().multiply(distance));
            player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, currentPosition.toLocation(player.getWorld()), 1, 0, 0, 0, 0);
            for (Entity entity : player.getWorld().getNearbyEntities(currentPosition.toLocation(player.getWorld()), 0.3, 0.3, 0.3)) {
                if (entity.getType() == EntityType.BLOCK_DISPLAY || entity.getType() == EntityType.ITEM_DISPLAY) {
                    targetedEntity = entity; // Привязка игрока к сущности
                    targetedEntity.setGlowing(true);
                    raycastActive = false;

                    // Запускаем перемещение сущности к позиции взгляда игрока
                    startMovingTask(player);
                    return;
                }
            }
        }
        raycastActive = false;
    }

    private void startMovingTask(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (targetedEntity != null) {
                    Location targetLocation = player.getEyeLocation().add(player.getEyeLocation().getDirection());
                    targetedEntity.teleport(targetLocation);
                } else {
                    cancel(); // Останавливаем задачу, если нет целевой сущности
                }
            }
        }.runTaskTimer(DisplayControl.getPlugin(DisplayControl.class), 0, 1); // Запускаем задачу на выполнение каждый тик
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        ItemStack droppedItem = event.getItemDrop().getItemStack();

        // Проверяем, является ли выбрасываемый предмет DEBUG_STICK и существует ли связанная сущность
        if (droppedItem.getType() == Material.DEBUG_STICK && targetedEntity != null) {
            event.setCancelled(true);
            targetedEntity.setGlowing(false);
            targetedEntity = null;
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (targetedEntity != null) {
            targetedEntity.setGlowing(false);
            targetedEntity = null;
        }
    }
}