package me.ram.bedwarsscoreboardaddon.addon;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
/*
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsPlayerJoinedEvent;
import io.github.bedwarsrel.events.BedwarsPlayerLeaveEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameManager;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
*/
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerJoinedEvent;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerLeaveEvent;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.RunningTeam;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.BedwarsUtil;

public class HidePlayer implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (!Config.hide_player) {
			return;
		}
		Player player = e.getPlayer();
		//GameManager man = BedwarsRel.getInstance().getGameManager();
		Bukkit.getOnlinePlayers().forEach(p -> {
			if (BedwarsAPI.getInstance().getGameOfPlayer(p) != null) {
				hidePlayer(p, player);
				hidePlayer(player, p);
			}
		});
	}

	@EventHandler
	public void onJoined(BedwarsPlayerJoinedEvent e) {
		if (!Config.hide_player) {
			return;
		}
		Game game = e.getGame();
		Player player = e.getPlayer();
		if (game.getStatus().equals(GameStatus.WAITING)) {
			List<Player> list = game.getConnectedPlayers();
			Bukkit.getOnlinePlayers().forEach(p -> {
				if (list.contains(p)) {
					showPlayer(p, player);
					showPlayer(player, p);
				} else {
					hidePlayer(p, player);
					hidePlayer(player, p);
				}
			});
		} else if (game.getStatus().equals(GameStatus.RUNNING)) {
			List<Player> list = game.getConnectedPlayers();
			Bukkit.getOnlinePlayers().forEach(p -> {
				if (list.contains(p)) {
					if (BedwarsUtil.isSpectator(game, p)) {
						hidePlayer(p, player);
						hidePlayer(player, p);
					} else {
						hidePlayer(p, player);
						showPlayer(player, p);
					}
				} else {
					hidePlayer(p, player);
					hidePlayer(player, p);
				}
			});
		}
	}

	@EventHandler
	public void onLeave(BedwarsPlayerLeaveEvent e) {
		if (!Config.hide_player) {
			return;
		}
		Player player = e.getPlayer();
		//GameManager man = BedwarsRel.getInstance().getGameManager();
		Bukkit.getOnlinePlayers().forEach(p -> {
			if (BedwarsAPI.getInstance().getGameOfPlayer(p) == null) {
				showPlayer(p, player);
				showPlayer(player, p);
			} else {
				hidePlayer(p, player);
				hidePlayer(player, p);
			}
		});
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();
		Game game = BedwarsAPI.getInstance().getGameOfPlayer(player);
		if (game == null) {
			return;
		}
		RunningTeam team = game.getTeamOfPlayer(player);
		if (team == null) {
			return;
		}
		if (team.isDead()) {
			Bukkit.getOnlinePlayers().forEach(p -> {
				hidePlayer(p, player);
			});
		}
	}

	private void hidePlayer(Player p1, Player p2) {
		if (p1.getUniqueId().equals(p2.getUniqueId())) {
			return;
		}
		p1.hidePlayer(p2);
	}

	private void showPlayer(Player p1, Player p2) {
		if (p1.getUniqueId().equals(p2.getUniqueId())) {
			return;
		}
		p1.showPlayer(p2);
	}
}
