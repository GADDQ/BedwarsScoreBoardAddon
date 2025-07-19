package me.ram.bedwarsscoreboardaddon.addon;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
/*
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameStatus;
import io.github.bedwarsrel.game.ResourceSpawner;
import io.github.bedwarsrel.game.Team;
*/
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.game.ItemSpawner;
import org.screamingsandals.bedwars.api.RunningTeam;
import me.ram.bedwarsscoreboardaddon.config.Config;

public class SpawnNoBuild implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlace(BlockPlaceEvent e) {
		Game game = BedwarsAPI.getInstance().getGameOfPlayer(e.getPlayer());
		if (game == null) {
			return;
		}
		if (game.getStatus() != GameStatus.RUNNING) {
			return;
		}
		Block block = e.getBlock();
		Player player = e.getPlayer();
		if (Config.spawn_no_build_spawn_enabled) {
			for (RunningTeam team : game.getRunningTeams()) {
				if (team.getTeamSpawn().distanceSquared(block.getLocation().clone().add(0.5, 0, 0.5)) <= Math.pow(Config.spawn_no_build_spawn_range, 2)) {
					e.setCancelled(true);
					player.sendMessage(Config.spawn_no_build_message);
					return;
				}
			}
		}
		if (Config.spawn_no_build_resource_enabled) {
			for (ItemSpawner spawner : game.getItemSpawners()) {
				if (spawner.getLocation().distanceSquared(block.getLocation().clone().add(0.5, 0, 0.5)) <= Math.pow(Config.spawn_no_build_resource_range, 2)) {
					e.setCancelled(true);
					player.sendMessage(Config.spawn_no_build_message);
					return;
				}
			}
			if (!Config.game_team_spawner.containsKey(game.getName())) {
				return;
			}
			for (List<Location> locs : Config.game_team_spawner.get(game.getName()).values()) {
				for (Location loc : locs) {
					if (loc.distanceSquared(block.getLocation().clone().add(0.5, 0, 0.5)) <= Math.pow(Config.spawn_no_build_resource_range, 2)) {
						e.setCancelled(true);
						player.sendMessage(Config.spawn_no_build_message);
						return;
					}
				}
			}
		}
	}
}
