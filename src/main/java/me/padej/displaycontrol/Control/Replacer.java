package me.padej.displaycontrol.Control;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Replacer implements Listener {

    private boolean raycastActive = false;
    private float savedYaw;
    private float savedPitch;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // Проверяем, что кликнули шифт+ПКМ и предмет в руке - DEBUG_STICK
        if (event.getAction().isLeftClick() && event.getPlayer().isSneaking() && itemInHand.getType() == Material.DEBUG_STICK) {
            // Запускаем рэйкаст
            performRaycast(player);
        }
    }

    private void performRaycast(Player player) {
        if (raycastActive) {
            return; // Если рейкаст уже активен, не запускаем его повторно
        }
        raycastActive = true;

        // Получаем начальные координаты и направление взгляда игрока
        Vector origin = player.getEyeLocation().toVector();
        Vector direction = player.getEyeLocation().getDirection();

        // Продвигаемся вперед по направлению взгляда с шагом 0.5 и проверяем наличие сущностей BLOCK_DISPLAY и ITEM_FRAME в радиусе 0.5
        for (double distance = 0; distance < 100; distance += 0.5) {
            Vector currentPosition = origin.clone().add(direction.clone().multiply(distance));
            player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, currentPosition.toLocation(player.getWorld()), 1, 0, 0, 0, 0);
            for (Entity entity : player.getWorld().getNearbyEntities(currentPosition.toLocation(player.getWorld()), 0.3, 0.3, 0.3)) {
                if (entity.getType() == EntityType.BLOCK_DISPLAY || entity.getType() == EntityType.ITEM_DISPLAY) {
                    // Сохраняем yaw и pitch сущности
                    savedYaw = entity.getLocation().getYaw();
                    savedPitch = entity.getLocation().getPitch();
                    // Перемещаем сущность к игроку
                    entity.teleport(player.getLocation());
                    // Устанавливаем сохраненные yaw и pitch после телепортации
                    entity.teleport(entity.getLocation().setDirection(player.getLocation().getDirection()));
                    entity.getLocation().setYaw(savedYaw);
                    entity.getLocation().setPitch(savedPitch);
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1.3F);
                    raycastActive = false; // Прерываем рейкаст после перемещения первой сущности
                    return;
                }
            }
        }

        // Отключаем рэйкаст после выполнения одного цикла
        raycastActive = false;
    }
}