package me.padej.displaycontrol.Control;

import me.padej.displaycontrol.DisplayControl;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import static me.padej.displaycontrol.Control.TargetEntity.targetedEntity;

public class Rotater implements Listener {

    private boolean raycastActive = false;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // Проверяем, что кликнули ПКМ и предмет в руке - DEBUG_STICK
        if (event.getAction().isRightClick() && itemInHand.getType() == Material.DEBUG_STICK && targetedEntity == null) {
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
                    return;
                }
            }
        }
        raycastActive = false;
    }



    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        ItemStack droppedItem = event.getItemDrop().getItemStack();


        // Проверяем, является ли выбрасываемый предмет DEBUG_STICK и существует ли связанная сущность
        if (droppedItem.getType() == Material.DEBUG_STICK && targetedEntity != null) {
            event.setCancelled(true);
            targetedEntity.setGlowing(false);
            targetedEntity = null;
        } else if (droppedItem.getType() == Material.STRUCTURE_BLOCK && targetedEntity != null) {
            event.setCancelled(true);
            Location entityLocation = targetedEntity.getLocation();
            entityLocation.setPitch(0);
            entityLocation.setYaw(0);
            targetedEntity.teleport(entityLocation); // Обновляем положение сущности
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (targetedEntity != null) {
            targetedEntity.setGlowing(false);
            targetedEntity = null;
        }
    }

    @EventHandler
    public void Rotating(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        ItemStack itemInOffHand = player.getInventory().getItemInOffHand();

        if (targetedEntity == null || (!event.getAction().isRightClick() && !event.getAction().isLeftClick())) {
            return;
        }

        Location entityLocation = targetedEntity.getLocation();
        float amount = player.isSneaking() ? 10.0f : 1.0f;

        if (event.getAction().isRightClick()) {
            if (itemInHand.getType() == Material.STRUCTURE_BLOCK) {
                entityLocation.setYaw(entityLocation.getYaw() + amount);
                player.sendActionBar(ChatColor.of("#0ce6f2") + "" + targetedEntity.getLocation().getYaw() + ChatColor.of("#0098db") + " / " + ChatColor.of("#0ce6f2") + targetedEntity.getLocation().getPitch());
                player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, 0.5F, 1.3F);
                event.setCancelled(true);
            } else if (itemInOffHand.getType() == Material.STRUCTURE_BLOCK) {
                entityLocation.setPitch(entityLocation.getPitch() + amount);
                player.sendActionBar(ChatColor.of("#0ce6f2") + "" + targetedEntity.getLocation().getYaw() + ChatColor.of("#0098db") + " / " + ChatColor.of("#0ce6f2") + targetedEntity.getLocation().getPitch());
                player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, 0.5F, 1.3F);
                event.setCancelled(true);
            }
        } else if (event.getAction().isLeftClick()) {
            if (itemInHand.getType() == Material.STRUCTURE_BLOCK) {
                entityLocation.setYaw(entityLocation.getYaw() - amount);
                player.sendActionBar(ChatColor.of("#0ce6f2") + "" + targetedEntity.getLocation().getYaw() + ChatColor.of("#0098db") + " / " + ChatColor.of("#0ce6f2") + targetedEntity.getLocation().getPitch());
                player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, 0.5F, 1.3F);
                event.setCancelled(true);
            } else if (itemInOffHand.getType() == Material.STRUCTURE_BLOCK) {
                entityLocation.setPitch(entityLocation.getPitch() - amount);
                player.sendActionBar(ChatColor.of("#0ce6f2") + "" + targetedEntity.getLocation().getYaw() + ChatColor.of("#0098db") + " / " + ChatColor.of("#0ce6f2") + targetedEntity.getLocation().getPitch());
                player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, 0.5F, 1.3F);
                event.setCancelled(true);
            }
        }
        targetedEntity.teleport(entityLocation);
    }

    @EventHandler
    public void Moving(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        ItemStack itemInOffHand = player.getInventory().getItemInOffHand();

        if (targetedEntity == null || (!event.getAction().isRightClick() && !event.getAction().isLeftClick())) {
            return;
        }

        Vector offset = new Vector();
        float amount = player.isSneaking() ? 1.0f : 0.1f;

        if (event.getAction().isRightClick()) {
            if (itemInHand.getType() == Material.JIGSAW) {
                offset.setX(amount / 10);
                player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, 0.5F, 1.3F);
                event.setCancelled(true);
            } else if (itemInOffHand.getType() == Material.JIGSAW) {
                offset.setZ(amount / 10);
                player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, 0.5F, 1.3F);
                event.setCancelled(true);
            } else if (itemInHand.getType() == Material.STRUCTURE_VOID) {
                offset.setY(amount / 10);
                player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, 0.5F, 1.3F);
                event.setCancelled(true);
            }
        } else if (event.getAction().isLeftClick()) {
            if (itemInHand.getType() == Material.JIGSAW) {
                offset.setX(-amount / 10);
                player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, 0.5F, 1.3F);
                event.setCancelled(true);
            } else if (itemInOffHand.getType() == Material.JIGSAW) {
                offset.setZ(-amount / 10);
                player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, 0.5F, 1.3F);
                event.setCancelled(true);
            } else if (itemInHand.getType() == Material.STRUCTURE_VOID) {
                offset.setY(-amount / 10);
                player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, 0.5F, 1.3F);
                event.setCancelled(true);
            }
        }

        Location entityLocation = targetedEntity.getLocation().add(offset);
        targetedEntity.teleport(entityLocation);

        // Проверяем, является ли предмет в руке JIGSAW или STRUCTURE_VOID, и отправляем ActionBar с текущей позицией
        if ((itemInHand.getType() == Material.JIGSAW || itemInHand.getType() == Material.STRUCTURE_VOID) || (itemInOffHand.getType() == Material.JIGSAW || itemInOffHand.getType() == Material.STRUCTURE_VOID)) {
            player.sendActionBar(
                    String.format(ChatColor.of("#0ce6f2") + "%.2f" + ChatColor.of("#0098db") + " / " + ChatColor.of("#0ce6f2") + "%.2f" + ChatColor.of("#0098db") + " / " + ChatColor.of("#0ce6f2") + "%.2f", entityLocation.getX(), entityLocation.getY(), entityLocation.getZ())
            );
        }
    }

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
                            player.getInventory().getItemInOffHand().getType() == Material.JIGSAW ||
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
                if (targetedEntity != null) {
                    Location particleStart = targetedEntity.getLocation();
                    Location particleEndPositiveX = particleStart.clone().add(10, 0, 0);
                    Location particleEndNegativeX = particleStart.clone().add(-10, 0, 0);
                    Location particleEndPositiveZ = particleStart.clone().add(0, 0, 10);
                    Location particleEndNegativeZ = particleStart.clone().add(0, 0, -10);
                    Location particleEndPositiveY = particleStart.clone().add(0, 10, 0);
                    Location particleEndNegativeY = particleStart.clone().add(0, -10, 0);
                    Vector directionPositiveX = particleEndPositiveX.toVector().subtract(particleStart.toVector()).normalize();
                    Vector directionNegativeX = particleEndNegativeX.toVector().subtract(particleStart.toVector()).normalize();
                    Vector directionPositiveZ = particleEndPositiveZ.toVector().subtract(particleStart.toVector()).normalize();
                    Vector directionNegativeZ = particleEndNegativeZ.toVector().subtract(particleStart.toVector()).normalize();
                    Vector directionPositiveY = particleEndPositiveY.toVector().subtract(particleStart.toVector()).normalize();
                    Vector directionNegativeY = particleEndNegativeY.toVector().subtract(particleStart.toVector()).normalize();
                    for (double i = 0; i <= 100; i += 0.5) {
                        if (player.getInventory().getItemInMainHand().getType() == Material.JIGSAW) {
                            Location particleLocationPositiveX = particleStart.clone().add(directionPositiveX.clone().multiply(i));
                            entity.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, particleLocationPositiveX, 1, 0, 0, 0, 0);
                            Location particleLocationNegativeX = particleStart.clone().add(directionNegativeX.clone().multiply(i));
                            entity.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, particleLocationNegativeX, 1, 0, 0, 0, 0);
                        } else if (player.getInventory().getItemInOffHand().getType() == Material.JIGSAW) {
                            Location particleLocationPositiveZ = particleStart.clone().add(directionPositiveZ.clone().multiply(i));
                            entity.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, particleLocationPositiveZ, 1, 0, 0, 0, 0);
                            Location particleLocationNegativeZ = particleStart.clone().add(directionNegativeZ.clone().multiply(i));
                            entity.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, particleLocationNegativeZ, 1, 0, 0, 0, 0);
                        } else if (player.getInventory().getItemInMainHand().getType() == Material.STRUCTURE_VOID) {
                            Location particleLocationPositiveY = particleStart.clone().add(directionPositiveY.clone().multiply(i));
                            entity.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, particleLocationPositiveY, 1, 0, 0, 0, 0);
                            Location particleLocationNegativeY = particleStart.clone().add(directionNegativeY.clone().multiply(i));
                            entity.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, particleLocationNegativeY, 1, 0, 0, 0, 0);
                        }
                    }
                }
            }
        }
    }
}