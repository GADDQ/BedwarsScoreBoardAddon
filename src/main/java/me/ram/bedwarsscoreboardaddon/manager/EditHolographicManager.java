package me.ram.bedwarsscoreboardaddon.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/*
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.ResourceSpawner;
import io.github.bedwarsrel.game.Team;
*/
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.ItemSpawner;
import org.screamingsandals.bedwars.api.RunningTeam;
import me.ram.bedwarsscoreboardaddon.api.HolographicAPI;
import me.ram.bedwarsscoreboardaddon.config.Config;

public class EditHolographicManager {

	private Map<Player, List<HolographicAPI>> holos;

	public EditHolographicManager() {
		holos = new HashMap<Player, List<HolographicAPI>>();
	}

	public void displayGameLocation(Player player, String g) {
		if (holos.containsKey(player)) {
			remove(player);
		}
		holos.put(player, new ArrayList<HolographicAPI>());
		List<HolographicAPI> list = holos.get(player);
		Game game = BedwarsAPI.getInstance().getGameByName(g);
		if (game != null) {
			if (game.getPos1() != null) {
				HolographicAPI holo = new HolographicAPI(game.getPos1().clone().add(0, -1.75, 0), Config.getLanguage("holographic.edit_game.loc1"));
				list.add(holo);
				holo.display(player);
			}
			if (game.getPos2() != null) {
				HolographicAPI holo = new HolographicAPI(game.getPos2().clone().add(0, -1.75, 0), Config.getLanguage("holographic.edit_game.loc2"));
				list.add(holo);
				holo.display(player);
			}
			if (game.getLobbySpawn() != null) {
				HolographicAPI holo = new HolographicAPI(game.getLobbySpawn().clone().add(0, -1.75, 0), Config.getLanguage("holographic.edit_game.lobby"));
				list.add(holo);
				holo.display(player);
			}
			for (RunningTeam team : game.getRunningTeams()) {
				if (team.getTargetBlock() != null) {
					HolographicAPI holo = new HolographicAPI(team.getTargetBlock().clone().add(0.5, -1.5, 0.5), Config.getLanguage("holographic.edit_game.bed").replace("{team}", ColorUtil.translateToChatColor(team.getColor()) + team.getName()));
					list.add(holo);
					holo.display(player);
				}
				if (team.getTeamSpawn() != null) {
					HolographicAPI holo = new HolographicAPI(team.getTeamSpawn().clone().add(0, -1.75, 0), Config.getLanguage("holographic.edit_game.spawn").replace("{team}", ColorUtil.translateToChatColor(team.getColor()) + team.getName()));
					list.add(holo);
					holo.display(player);
				}
			}
			if (Config.game_team_spawner.containsKey(g)) {
				for (RunningTeam team : game.getRunningTeams()) {
					if (Config.game_team_spawner.get(g).containsKey(team.getName())) {
						Config.game_team_spawner.get(g).get(team.getName()).forEach(loc -> {
							Location location = loc.clone();
							HolographicAPI holo = new HolographicAPI(location.add(0, -1.75, 0), Config.getLanguage("holographic.edit_game.team_spawner").replace("{team}", ColorUtil.translateToChatColor(team.getColor()) + team.getName()));
							list.add(holo);
							holo.display(player);
						});
					}
				}
			}
			for (ItemSpawner spawner : game.getItemSpawners()) {
				HolographicAPI holo = new HolographicAPI(spawner.getLocation().clone().add(0, -1.75, 0), Config.getLanguage("holographic.edit_game.spawner").replace("{resource}", spawner.getName()));
				list.add(holo);
				holo.display(player);
			}
		}
		if (Config.game_shop_item.containsKey(g)) {
			for (String loc : Config.game_shop_item.get(g)) {
				Location location = toLocation(loc);
				if (location != null) {
					try {
						Config.game_shop_shops.forEach((id, pl) -> {
							if (pl.equals(g + ".shop.item - " + loc)) {
								HolographicAPI holo = new HolographicAPI(location.clone().add(0, -1.75, 0), Config.getLanguage("holographic.shop.item").replace("{id}", id));
								list.add(holo);
								holo.display(player);
							}
						});
					} catch (Exception e) {
					}
				}
			}
		}
		if (Config.game_shop_team.containsKey(g)) {
			for (String loc : Config.game_shop_team.get(g)) {
				Location location = toLocation(loc);
				if (location != null) {
					try {
						Config.game_shop_shops.forEach((id, pl) -> {
							if (pl.equals(g + ".shop.team - " + loc)) {
								HolographicAPI holo = new HolographicAPI(location.clone().add(0, -1.75, 0), Config.getLanguage("holographic.shop.team").replace("{id}", id));
								list.add(holo);
								holo.display(player);
							}
						});
					} catch (Exception e) {
					}
				}
			}
		}
	}

	public void remove(Player player) {
		if (holos.containsKey(player)) {
			holos.get(player).forEach(holo -> {
				holo.remove();
			});
			holos.remove(player);
		}
	}

	public void removeAll() {
		holos.keySet().forEach(player -> {
			holos.get(player).forEach(holo -> {
				holo.remove();
			});
		});
		holos.clear();
	}

	private Location toLocation(String loc) {
		try {
			String[] ary = loc.split(", ");
			if (Bukkit.getWorld(ary[0]) != null) {
				Location location = new Location(Bukkit.getWorld(ary[0]), Double.valueOf(ary[1]), Double.valueOf(ary[2]), Double.valueOf(ary[3]));
				if (ary.length > 4) {
					location.setYaw(Float.valueOf(ary[4]));
					location.setPitch(Float.valueOf(ary[5]));
				}
				return location;
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}
}
