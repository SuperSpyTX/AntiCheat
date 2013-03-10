/*
 * AntiCheat for Bukkit.
 * Copyright (C) 2012-2013 AntiCheat Team | http://gravitydevelopment.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package net.h31ix.anticheat.event;

import net.h31ix.anticheat.Anticheat;
import net.h31ix.anticheat.manage.CheckType;
import net.h31ix.anticheat.manage.User;
import net.h31ix.anticheat.util.CheckResult;
import net.h31ix.anticheat.util.Distance;
import net.h31ix.anticheat.util.Permission;
import net.h31ix.anticheat.util.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.PlayerInventory;

public class PlayerListener extends EventListener {
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (getCheckManager().willCheck(player, CheckType.SPAM) && getConfig().commandSpam()) {
            CheckResult result = getBackend().checkSpam(player, event.getMessage());
            if (result.failed()) {
                event.setCancelled(!silentMode());
                getBackend().processChatSpammer(player);
                log(result.getMessage(), player, CheckType.SPAM);
            }
        }
        
        Anticheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        if (!event.isFlying()) {
            getBackend().logEnterExit(event.getPlayer());
        }
        
        Anticheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        if (event.getNewGameMode() != GameMode.CREATIVE) {
            getBackend().logEnterExit(event.getPlayer());
        }
        
        Anticheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            
            if (event.getEntity() instanceof Arrow) { return; }
            
            if (getCheckManager().willCheck(player, CheckType.FAST_PROJECTILE)) {
                CheckResult result = getBackend().checkProjectile(player);
                if(result.failed()) {
                    event.setCancelled(!silentMode());
                    log(result.getMessage(), player, CheckType.FAST_PROJECTILE);                    
                }
            }
        }
        
        Anticheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == TeleportCause.ENDER_PEARL || event.getCause() == TeleportCause.PLUGIN) {
            getBackend().logTeleport(event.getPlayer());
        }
        
        Anticheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onPlayerChangeWorlds(PlayerChangedWorldEvent event) {
        getBackend().logTeleport(event.getPlayer());
        
        Anticheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            getBackend().logToggleSneak(event.getPlayer());
        }
        
        Anticheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onPlayerVelocity(PlayerVelocityEvent event) {
        Player player = event.getPlayer();
        if (getCheckManager().willCheck(player, CheckType.FLY) && getCheckManager().willCheck(player, CheckType.ZOMBE_FLY)) // @h31ix: Change if necessary. I'm not sure what perms should go here :3
        {
            if (getBackend().justVelocity(player) && getBackend().extendVelocityTime(player)) {
                event.setCancelled(!silentMode());
                return; // don't log it lol.
            }
            getBackend().logVelocity(player);
        }
        
        Anticheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (getCheckManager().willCheck(player, CheckType.SPAM) && getConfig().chatSpam()) {
            CheckResult result = getBackend().checkSpam(player, event.getMessage());
            if (result.failed()) {
                event.setCancelled(!silentMode());
                player.sendMessage(ChatColor.RED + result.getMessage());
                getBackend().processChatSpammer(player);
            }
        }
        
        Anticheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        getBackend().clearChatLevel(event.getPlayer());
        getBackend().garbageClean(event.getPlayer());
        
        Anticheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        getBackend().garbageClean(event.getPlayer());
        
        Anticheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
        Player player = event.getPlayer();
        if (getCheckManager().willCheck(player, CheckType.SPRINT)) {
            CheckResult result = getBackend().checkSprintHungry(event);
            if (result.failed()) {
                event.setCancelled(!silentMode());
                log(result.getMessage(), player, CheckType.SPRINT);
            } else {
                decrease(player);
            }
        }
        
        Anticheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inv = player.getInventory();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Material m = inv.getItemInHand().getType();
            if (m == Material.BOW) {
                getBackend().logBowWindUp(player);
            } else if (Utilities.isFood(m)) {
                getBackend().logEatingStart(player);
            }
        }
        Block block = event.getClickedBlock();
        
        if (block != null) {
            Distance distance = new Distance(player.getLocation(), block.getLocation());
            getBackend().checkLongReachBlock(player, distance.getXDifference(), distance.getYDifference(), distance.getZDifference());
        }
        
        Anticheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (getCheckManager().willCheck(player, CheckType.ITEM_SPAM)) {
            CheckResult result = getBackend().checkFastDrop(player);
            if(result.failed()) {
                event.setCancelled(!silentMode());
                log(result.getMessage(), player, CheckType.ITEM_SPAM);
            }
        }
        
        Anticheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerEnterBed(PlayerBedEnterEvent event) {
        getBackend().logEnterExit(event.getPlayer());
        
        Anticheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerExitBed(PlayerBedLeaveEvent event) {
        getBackend().logEnterExit(event.getPlayer());
        
        Anticheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        Player player = event.getPlayer();
        getBackend().logAnimation(player);
        
        Anticheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String section = "\u00a7";
        if (getCheckManager().willCheck(player, CheckType.ZOMBE_FLY)) {
            player.sendMessage(section + "f " + section + "f " + section + "1 " + section + "0 " + section + "2 " + section + "4");
        }
        if (getCheckManager().willCheck(player, CheckType.ZOMBE_CHEAT)) {
            player.sendMessage(section + "f " + section + "f " + section + "2 " + section + "0 " + section + "4 " + section + "8");
        }
        if (getCheckManager().willCheck(player, CheckType.ZOMBE_NOCLIP)) {
            player.sendMessage(section + "f " + section + "f " + section + "4 " + section + "0 " + section + "9 " + section + "6");
        }

        getBackend().logJoin(player);

        if (getUserManager().getUser(player.getName()) == null) {
            getUserManager().addUser(new User(player.getName()));
        } else {
            getUserManager().addUserFromFile(player.getName());
        }
        if (player.hasMetadata(Utilities.SPY_METADATA)) {
            for (Player p : player.getServer().getOnlinePlayers()) {
                if (!Permission.SYSTEM_SPY.get(p)) {
                    p.hidePlayer(player);
                }
            }
        }
        
        Anticheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        long total = System.nanoTime();
        long time = System.nanoTime();
        final Player player = event.getPlayer();
        if(getCheckManager().checkInWorld(player) && !getCheckManager().isOpExempt(player)) {
            System.out.println("event.getPlayer(): "+(System.nanoTime()-time)); time = System.nanoTime();
            final Location from = event.getFrom();
            System.out.println("player.getFrom(): "+(System.nanoTime()-time)); time = System.nanoTime();
            final Location to = event.getTo();
            System.out.println("player.getTo(): "+(System.nanoTime()-time)); time = System.nanoTime();

            final Distance distance = new Distance(from, to);
            System.out.println("new Distance(from, to): "+(System.nanoTime()-time)); time = System.nanoTime();
            final double y = distance.getYDifference();
            System.out.println("distance.getYDifference(): "+(System.nanoTime()-time)); time = System.nanoTime();

            getBackend().logAscension(player, from.getY(), to.getY());
            System.out.println("logAscension(player, from.getY(), to.getY()): "+(System.nanoTime()-time)); time = System.nanoTime();

            final User user = getUserManager().getUser(player.getName());
            System.out.println("getUserManager().getUser(player.getName()): "+(System.nanoTime()-time)); time = System.nanoTime();
            user.setTo(to.getX(), to.getY(), to.getZ());
            System.out.println("user.setTo(to.getX(), to.getY(), to.getZ()): "+(System.nanoTime()-time)); time = System.nanoTime();

            if (getCheckManager().willCheckQuick(player, CheckType.SPEED)) {
                System.out.println("getCheckManager().willCheck(player, CheckType.SPEED): "+(System.nanoTime()-time)); time = System.nanoTime();
                CheckResult result = getBackend().checkFreeze(player, from.getY(), to.getY());
                System.out.println("checkFreeze(): "+(System.nanoTime()-time)); time = System.nanoTime();
                if(result.failed()) {
                    log(result.getMessage(), player, CheckType.SPEED);
                    if(!silentMode()) {
                        player.kickPlayer("Freezing client");
                    }
                }
            }
            time = System.nanoTime();
            if (getCheckManager().willCheckQuick(player, CheckType.SPRINT)) {
                System.out.println("getCheckManager().willCheck(player, CheckType.SPRINT): "+(System.nanoTime()-time)); time = System.nanoTime();
                CheckResult result = getBackend().checkSprintStill(player, from, to);
                System.out.println("checkSprintStill(): "+(System.nanoTime()-time)); time = System.nanoTime();
                if (result.failed()) {
                    event.setCancelled(!silentMode());
                    log(result.getMessage(), player, CheckType.SPRINT);
                }
            }
            time = System.nanoTime();
            if (getCheckManager().willCheckQuick(player, CheckType.FLY) && !player.isFlying() && getCheckManager().willCheck(player, CheckType.ZOMBE_FLY)) {
                System.out.println("getCheckManager().willCheck(player, CheckType.FLY): "+(System.nanoTime()-time)); time = System.nanoTime();
                CheckResult result = getBackend().checkFlight(player, distance);
                System.out.println("checkFlight(): "+(System.nanoTime()-time)); time = System.nanoTime();
                if(result.failed()) {
                    if (!silentMode()) {
                        event.setTo(user.getGoodLocation(from.clone()));
                    }
                    log(result.getMessage(), player, CheckType.FLY);
                }
            }
            time = System.nanoTime();
            if (getCheckManager().willCheckQuick(player, CheckType.VCLIP) && getCheckManager().willCheck(player, CheckType.ZOMBE_FLY) && getCheckManager().willCheck(player, CheckType.FLY) && event.getFrom().getY() > event.getTo().getY()) {
                System.out.println("getCheckManager().willCheck(player, CheckType.VCLIP): "+(System.nanoTime()-time)); time = System.nanoTime();
                CheckResult result = getBackend().checkVClip(player, new Distance(event.getFrom(), event.getTo()));
                System.out.println("checkVClip(): "+(System.nanoTime()-time)); time = System.nanoTime();

                if (result.failed()) {
                    if (!silentMode()) {
                        int data = result.getData() > 3 ? 3 : result.getData();
                        Location newloc = new Location(player.getWorld(), event.getFrom().getX(), event.getFrom().getY() + data, event.getFrom().getZ());
                        if (newloc.getBlock().getTypeId() == 0) {
                            event.setTo(newloc);
                        } else {
                            event.setTo(user.getGoodLocation(from.clone()));
                        }
                        player.damage(3);
                    }
                    log(result.getMessage(), player, CheckType.VCLIP);
                }
            }
            time = System.nanoTime();
            if (getCheckManager().willCheckQuick(player, CheckType.NOFALL) && getCheckManager().willCheck(player, CheckType.ZOMBE_FLY) && getCheckManager().willCheck(player, CheckType.FLY) && !Utilities.isClimbableBlock(player.getLocation().getBlock()) && event.getFrom().getY() > event.getTo().getY()) {
                System.out.println("getCheckManager().willCheck(player, CheckType.NOFALL): "+(System.nanoTime()-time)); time = System.nanoTime();
                CheckResult result = getBackend().checkNoFall(player, y);
                System.out.println("checkNoFall(): "+(System.nanoTime()-time)); time = System.nanoTime();
                if(result.failed()) {
                    if (!silentMode()) {
                        event.setTo(user.getGoodLocation(from.clone()));
                        player.damage(1); // I added this in here so the player would still receive damage.
                    }
                    log(result.getMessage(), player, CheckType.NOFALL);
                }
            }

            boolean changed = false;
            time = System.nanoTime();
            if (event.getTo() != event.getFrom()) {
                double x = distance.getXDifference();
                double z = distance.getZDifference();
                if (getCheckManager().willCheckQuick(player, CheckType.SPEED) && getCheckManager().willCheck(player, CheckType.ZOMBE_FLY) && getCheckManager().willCheck(player, CheckType.FLY)) {
                    System.out.println("getCheckManager().willCheck(player, CheckType.SPEED): "+(System.nanoTime()-time)); time = System.nanoTime();
                    if (event.getFrom().getY() < event.getTo().getY()) {
                        CheckResult result = getBackend().checkYSpeed(player, y);
                        System.out.println("checkYSpeed(): "+(System.nanoTime()-time)); time = System.nanoTime();
                        if(result.failed()) {
                            if (!silentMode()) {
                                event.setTo(user.getGoodLocation(from.clone()));
                            }
                            log(result.getMessage(), player, CheckType.SPEED);
                            changed = true;
                        }
                    }
                    time = System.nanoTime();
                    CheckResult result = getBackend().checkXZSpeed(player, x, z);
                    System.out.println("checkXZSpeed(): "+(System.nanoTime()-time)); time = System.nanoTime();
                    if (result.failed()) {
                        if (!silentMode()) {
                            event.setTo(user.getGoodLocation(from.clone()));
                        }
                        log(result.getMessage(), player, CheckType.SPEED);
                        changed = true;
                    }
                    /*if ((event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ())) {
                        result = backend.checkTimer(player);
                        if(result.failed()) {
                            if (!config.silentMode()) {
                                event.setTo(user.getGoodLocation(from.clone()));
                            }
                            log("tried to alter their timer.", player, CheckType.SPEED);
                            changed = true;
                        }
                    }
                    */
                }
                time = System.nanoTime();
                if (getCheckManager().willCheckQuick(player, CheckType.WATER_WALK)) {
                    System.out.println("getCheckManager().willCheck(player, CheckType.WATER_WALK): "+(System.nanoTime()-time)); time = System.nanoTime();
                    CheckResult result = getBackend().checkWaterWalk(player, x, y, z);
                    System.out.println("checkWaterWalk(): "+(System.nanoTime()-time)); time = System.nanoTime();
                    if(result.failed()) {
                        if (!silentMode()) {
                            player.teleport(user.getGoodLocation(player.getLocation().add(0, -1, 0)));
                        }
                        log(result.getMessage(), player, CheckType.WATER_WALK);
                        changed = true;
                    }
                }
                time = System.nanoTime();
                if (getCheckManager().willCheckQuick(player, CheckType.SNEAK)) {
                    System.out.println("getCheckManager().willCheck(player, CheckType.SNEAK): "+(System.nanoTime()-time)); time = System.nanoTime();
                    CheckResult result = getBackend().checkSneak(player, x, z);
                    System.out.println("checkSneak(): "+(System.nanoTime()-time)); time = System.nanoTime();
                    if(result.failed()) {
                        if (!silentMode()) {
                            event.setTo(user.getGoodLocation(from.clone()));
                            player.setSneaking(false);
                        }
                        log(result.getMessage(), player, CheckType.SNEAK);
                        changed = true;
                    }
                }
                time = System.nanoTime();
                if (getCheckManager().willCheckQuick(player, CheckType.SPIDER)) {
                    System.out.println("getCheckManager().willCheck(player, CheckType.SPIDER): "+(System.nanoTime()-time)); time = System.nanoTime();
                    CheckResult result = getBackend().checkSpider(player, y);
                    System.out.println("checkSpider(): "+(System.nanoTime()-time)); time = System.nanoTime();
                    if(result.failed()) {
                        if (!silentMode()) {
                            event.setTo(user.getGoodLocation(from.clone()));
                        }
                        log(result.getMessage(), player, CheckType.SPIDER);
                        changed = true;
                    }
                }
                time = System.nanoTime();
                if (!changed) {
                    user.setGoodLocation(event.getFrom());
                    System.out.println("changed: "+(System.nanoTime()-time)); time = System.nanoTime();
                }
            }
        }
        time = System.nanoTime();
        Anticheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
        System.out.println("addEvent(): "+(System.nanoTime()-time));
        System.out.println("TOTAL: "+(System.nanoTime()-total));
        System.out.println("------------------------");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void checkFly(PlayerMoveEvent event) {
        // Check flight on highest to make sure other plugins have a chance to change the values first.
        final Player player = event.getPlayer();
        final User user = getUserManager().getUser(player.getName());
        final Location from = event.getFrom();
        final Location to = event.getTo();

        if(!user.checkTo(to.getX(), to.getY(), to.getZ())) {
            // The to value has been modified by another plugin
            return;
        }

        if (getCheckManager().willCheck(player, CheckType.FLY) && !player.isFlying() && getCheckManager().willCheck(player, CheckType.ZOMBE_FLY)) {
            CheckResult result1 = getBackend().checkYAxis(player, new Distance(from, to));
            CheckResult result2 = getBackend().checkAscension(player, from.getY(), to.getY());
            String log = result1.failed() ? result1.getMessage() : result2.failed() ? result2.getMessage() : "";
            if(!log.equals("")) {
                if (!silentMode()) {
                    event.setTo(user.getGoodLocation(from.clone()));
                }
                log(log, player, CheckType.FLY);
            }
        }
    }
}
