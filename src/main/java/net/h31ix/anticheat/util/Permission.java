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

package net.h31ix.anticheat.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Permission {
    /* Check Nodes */
    CHECK_ZOMBE_FLY,
    CHECK_ZOMBE_NOCLIP,
    CHECK_ZOMBE_CHEAT,
    CHECK_FLY,
    CHECK_WATERWALK,
    CHECK_NOSWING,
    CHECK_FASTBREAK,
    CHECK_FASTPLACE,
    CHECK_SPAM,
    CHECK_SPRINT,
    CHECK_SNEAK,
    CHECK_SPEED,
    CHECK_VCLIP,
    CHECK_SPIDER,
    CHECK_NOFALL,
    CHECK_FASTBOW,
    CHECK_FASTEAT,
    CHECK_FASTHEAL,
    CHECK_FORCEFIELD,
    CHECK_XRAY,
    CHECK_LONGREACH,
    CHECK_FASTPROJECTILE,
    CHECK_ITEMSPAM,
    CHECK_VISUAL,
    CHECK_FASTINVENTORY,
    CHECK_AUTOTOOL,
    
    /* System Nodes */
    SYSTEM_LOG,
    SYSTEM_XRAY,
    SYSTEM_RESET,
    SYSTEM_SPY,
    SYSTEM_HELP,
    SYSTEM_UPDATE,
    SYSTEM_REPORT,
    SYSTEM_ALERT,
    SYSTEM_CALIBRATE,
    SYSTEM_RELOAD;
    
    public boolean get(CommandSender cs) {
        return cs.hasPermission(toString());
    }
    
    // Now h31ix, I know this is redundant, but this may actually solve the issue.
    // So please, just give it a try :P
    public boolean get(Player p) {
        return p.hasPermission(toString());
    }
    
    @Override
    public String toString() {
        return "anticheat." + this.name().toLowerCase().replace("_", ".");
    }
    
}
