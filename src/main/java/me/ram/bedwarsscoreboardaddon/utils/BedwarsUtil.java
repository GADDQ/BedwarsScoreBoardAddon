package me.ram.bedwarsscoreboardaddon.utils;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

//import io.github.bedwarsrel.BedwarsRel;
//import io.github.bedwarsrel.game.Game;
//import io.github.bedwarsrel.game.Team;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.game.Game;

public class BedwarsUtil {

	public static boolean isRespawning(Player player) {
		Game game = BedwarsAPI.getInstance().getGameOfPlayer(player);
		if (game == null) {
			return false;
		}
		return isRespawning(game, player);
	}

	public static boolean isRespawning(Game game, Player player) {
		Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
		if (arena == null) {
			return false;
		}
		return arena.getRespawn().isRespawning(player);
	}

	public static boolean isSpectator(Player player) {
		Game game = BedwarsAPI.getInstance().getGameOfPlayer(player);
		if (game == null) {
			return false;
		}
		return isSpectator(game, player);
	}

	public static boolean isSpectator(Game game, Player player) {
		return game.isSpectator(player) || isRespawning(game, player);
	}

	public static boolean isDieOut(Game game, RunningTeam team) {
		if (!team.isDead()) {
			return false;
		}
		for (Player player : team.getConnectedPlayers()) {
			if (!game.isSpectator(player)) {
				return false;
			}
		}
		return true;
	}

	public static String getCurrentVersion() {
		String packName = Bukkit.getServer().getClass().getPackage().getName();
		return packName.substring(packName.lastIndexOf(46) + 1);
	}
}
