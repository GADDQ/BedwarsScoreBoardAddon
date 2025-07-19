package me.ram.bedwarsscoreboardaddon.addon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.ram.bedwarsscoreboardaddon.listener.EventListener;
import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
/*
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
*/
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.RunningTeam;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.manager.PlaceholderManager;
import me.ram.bedwarsscoreboardaddon.utils.PlaceholderAPIUtil;
import me.ram.bedwarsscoreboardaddon.utils.ScoreboardUtil;

public class ScoreBoard {

	private Arena arena;
	private Game game;
	private int title_index = 0;
	private Map<String, String> timer_placeholder;
	private PlaceholderManager placeholderManager;
	private Map<String, String> team_status;
	private Map<String, String> over_plan_info;

	public ScoreBoard(Arena arena) {
		this.arena = arena;
		game = arena.getGame();
		placeholderManager = new PlaceholderManager(game);
		team_status = new HashMap<String, String>();
		timer_placeholder = new HashMap<String, String>();
		over_plan_info = new HashMap<String, String>();
		for (String id : Config.timer.keySet()) {
			arena.addGameTask(new BukkitRunnable() {
				int i = Config.timer.get(id);

				@Override
				public void run() {
					String format = i / 60 + ":" + ((i % 60 < 10) ? ("0" + i % 60) : (i % 60));
					timer_placeholder.put("{timer_" + id + "}", format);
					i--;
				}
			}.runTaskTimer(Main.getInstance(), 0L, 21L));
		}
		arena.addGameTask(new BukkitRunnable() {
			int i = Config.scoreboard_interval;

			@Override
			public void run() {
				i--;
				if (i <= 0) {
					updateScoreboard();
					i = Config.scoreboard_interval;
				}
			}
		}.runTaskTimer(Main.getInstance(), 0L, 1L));
		/*
		arena.addGameTask(new BukkitRunnable() {
			@Override
			public void run() {
				for (BukkitTask task : game.getRunningTasks()) {
					task.cancel();
				}
				game.getRunningTasks().clear();
				startTimerCountdown(game);
			}
		}.runTaskLater(Main.getInstance(), 19L));
		*/
		// TODO
	}

	public PlaceholderManager getPlaceholderManager() {
		return placeholderManager;
	}

	public void setTeamStatusFormat(String team, String status) {
		team_status.put(team, status);
	}

	public void removeTeamStatusFormat(String team) {
		team_status.remove(team);
	}

	public Map<String, String> getTeamStatusFormat() {
		return team_status;
	}

	private String getGameTime(int time) {
		return String.valueOf(time / 60);
	}

	private void startTimerCountdown(Game game) {
		/*
		game.addRunningTask(new BukkitRunnable() {
			public void run() {
				if (EventListener.countdownMap.get(game) == 0) {
					game.stop();
					//game.setOver(true);
					//game.getCycle().checkGameOver();
					cancel();
					return;
				}
				// TODO
				//game.setTimeLeft(EventListener.countdownMap.get(game) - 1);
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("BedWars"), 0L, 20L));
		 */
		// TODO
	}

	public void updateScoreboard() {
		List<String> lines = new ArrayList<String>();
		Map<String, String> plan_infos = new HashMap<String, String>();
		for (String plan : Config.planinfo) {
			if (EventListener.countdownMap.get(game) <= Main.getInstance().getConfig().getInt("planinfo." + plan + ".start_time") && EventListener.countdownMap.get(game) > Main.getInstance().getConfig().getInt("planinfo." + plan + ".end_time")) {
				for (String key : Main.getInstance().getConfig().getConfigurationSection("planinfo." + plan + ".plans").getKeys(false)) {
					plan_infos.put(key, Main.getInstance().getConfig().getString("planinfo." + plan + ".plans." + key));
				}
			}
		}
		if (EventListener.countdownMap.get(game) == 1) {
			over_plan_info = plan_infos;
		} else if (EventListener.countdownMap.get(game) < 1) {
			plan_infos = over_plan_info;
		}
		int alive_teams = 0;
		int remain_teams = 0;
		for (RunningTeam team : game.getRunningTeams()) {
			if (!team.isDead()) {
				alive_teams++;
			}
			if (team.getConnectedPlayers().size() > 0) {
				remain_teams++;
			}
		}
		int wither = EventListener.countdownMap.get(game) - Config.witherbow_gametime;
		String format = wither / 60 + ":" + ((wither % 60 < 10) ? ("0" + wither % 60) : (wither % 60));
		String bowtime = null;
		if (wither > 0) {
			bowtime = format;
		}
		if (wither <= 0) {
			bowtime = Config.witherbow_already_starte;
		}
		String score_title = "";
		if (title_index >= Config.scoreboard_title.size()) {
			title_index = 0;
		}
		score_title = Config.scoreboard_title.size() < 1 ? "BedWars" : Config.scoreboard_title.get(title_index).replace("{game}", game.getName()).replace("{time}", getFormattedTimeLeft(EventListener.countdownMap.get(game)));
		title_index++;
		String teams = game.getRunningTeams().size() + "";
		List<String> scoreboard_lines;
		if (Config.scoreboard_lines.containsKey(teams)) {
			scoreboard_lines = Config.scoreboard_lines.get(teams);
		} else if (Config.scoreboard_lines.containsKey("default")) {
			scoreboard_lines = Config.scoreboard_lines.get("default");
		} else {
			scoreboard_lines = Arrays.asList("", "{team_status}", "");
		}
		int alive_players = 0;
		for (Player p : game.getConnectedPlayers()) {
			if (!game.isSpectator(p)) {
				alive_players++;
			}
		}
		for (Player player : game.getConnectedPlayers()) {
			RunningTeam player_team = game.getTeamOfPlayer(player);
			lines.clear();
			String player_total_kills = arena.getPlayerGameStorage().getPlayerTotalKills().getOrDefault(player.getName(), 0) + "";
			String player_kills = arena.getPlayerGameStorage().getPlayerKills().getOrDefault(player.getName(), 0) + "";
			String player_final_kills = arena.getPlayerGameStorage().getPlayerFinalKills().getOrDefault(player.getName(), 0) + "";
			String player_dis = arena.getPlayerGameStorage().getPlayerDies().getOrDefault(player.getName(), 0) + "";
			String player_bes = arena.getPlayerGameStorage().getPlayerBeds().getOrDefault(player.getName(), 0) + "";
			String player_team_color = "§f";
			String player_team_players = "";
			String player_team_name = "";
			String player_team_bed_status = "";
			if (game.getTeamOfPlayer(player) != null) {
				player_team_color = ColorUtil.translateToChatColor(game.getTeamOfPlayer(player).getColor()) + "";
				player_team_players = game.getTeamOfPlayer(player).getConnectedPlayers().size() + "";
				player_team_name = game.getTeamOfPlayer(player).getName();
				player_team_bed_status = getTeamBedStatus(game, game.getTeamOfPlayer(player));
			}
			for (String ls : scoreboard_lines) {
				if (ls.contains("{team_status}")) {
					for (RunningTeam t : game.getRunningTeams()) {
						String you = "";
						if (game.getTeamOfPlayer(player) != null) {
							if (game.getTeamOfPlayer(player) == t) {
								you = Config.scoreboard_you;
							} else {
								you = "";
							}
						}
						if (team_status.containsKey(t.getName())) {
							lines.add(team_status.get(t.getName()).replace("{you}", you));
						} else {
							lines.add(ls.replace("{team_status}", getTeamStatusFormat(game, t).replace("{you}", you)));
						}
					}
				} else {
					String date = new SimpleDateFormat(Config.date_format).format(new Date());
					String add_line = ls;
					for (String key : plan_infos.keySet()) {
						add_line = add_line.replace("{plan_" + key + "}", plan_infos.get(key));
					}
					add_line = add_line.replace("{death_mode}", arena.getDeathMode().getDeathmodeTime()).replace("{remain_teams}", remain_teams + "").replace("{alive_teams}", alive_teams + "").replace("{alive_players}", alive_players + "").replace("{teams}", game.getRunningTeams().size() + "").replace("{color}", player_team_color).replace("{team_peoples}", player_team_players).replace("{player_name}", player.getName()).replace("{team}", player_team_name).replace("{beds}", player_bes).replace("{dies}", player_dis).replace("{totalkills}", player_total_kills).replace("{finalkills}", player_final_kills).replace("{kills}", player_kills).replace("{time}", getGameTime(EventListener.countdownMap.get(game))).replace("{formattime}", getFormattedTimeLeft(EventListener.countdownMap.get(game))).replace("{game}", game.getName()).replace("{date}", date).replace("{online}", game.getConnectedPlayers().size() + "").replace("{bowtime}", bowtime).replace("{team_bed_status}", player_team_bed_status).replace("{no_break_bed}", arena.getNoBreakBed().getTime());
					for (String key : arena.getHealthLevel().getLevelTime().keySet()) {
						add_line = add_line.replace("{sethealthtime_" + key + "}", arena.getHealthLevel().getLevelTime().get(key));
					}
					for (String key : arena.getResourceUpgrade().getUpgTime().keySet()) {
						add_line = add_line.replace("{resource_upgrade_" + key + "}", arena.getResourceUpgrade().getUpgTime().get(key));
					}
					for (String key : placeholderManager.getGamePlaceholder().keySet()) {
						add_line = add_line.replace(key, placeholderManager.getGamePlaceholder().get(key).onGamePlaceholderRequest(game));
					}
					for (RunningTeam t : game.getRunningTeams()) {
						if (add_line.contains("{team_" + t.getName() + "_status}")) {
							String stf = getTeamStatusFormat(game, t);
							if (game.getTeamOfPlayer(player) == null) {
								stf = stf.replace("{you}", "");
							} else if (game.getTeamOfPlayer(player) == t) {
								stf = stf.replace("{you}", Config.scoreboard_you);
							} else {
								stf = stf.replace("{you}", "");
							}
							add_line = add_line.replace("{team_" + t.getName() + "_status}", stf);
						}
						if (add_line.contains("{team_" + t.getName() + "_bed_status}")) {
							add_line = add_line.replace("{team_" + t.getName() + "_bed_status}", getTeamBedStatus(game, t));
						}
						if (add_line.contains("{team_" + t.getName() + "_peoples}")) {
							add_line = add_line.replace("{team_" + t.getName() + "_peoples}", t.getConnectedPlayers().size() + "");
						}
					}
					if (player_team == null || !placeholderManager.getTeamPlaceholders().containsKey(player_team.getName())) {
						for (String teamname : placeholderManager.getTeamPlaceholders().keySet()) {
							for (String placeholder : placeholderManager.getTeamPlaceholders().get(teamname).keySet()) {
								add_line = add_line.replace(placeholder, "");
							}
						}
					} else {
						for (String identifier : placeholderManager.getTeamPlaceholder(player_team.getName()).keySet()) {
							add_line = add_line.replace(identifier, placeholderManager.getTeamPlaceholder(player_team.getName()).get(identifier).onTeamPlaceholderRequest(player_team));
						}
					}
					if (placeholderManager.getPlayerPlaceholders().containsKey(player.getName())) {
						for (String identifier : placeholderManager.getPlayerPlaceholder(player.getName()).keySet()) {
							add_line = add_line.replace(identifier, placeholderManager.getPlayerPlaceholder(player.getName()).get(identifier).onPlayerPlaceholderRequest(game, player));
						}
					} else {
						for (String playername : placeholderManager.getPlayerPlaceholders().keySet()) {
							for (String placeholder : placeholderManager.getPlayerPlaceholders().get(playername).keySet()) {
								add_line = add_line.replace(placeholder, "");
							}
						}
					}
					for (String placeholder : timer_placeholder.keySet()) {
						add_line = add_line.replace(placeholder, timer_placeholder.get(placeholder));
					}
					add_line = PlaceholderAPIUtil.setPlaceholders(player, add_line);
					lines.add(add_line);
				}
			}
			String title = PlaceholderAPIUtil.setPlaceholders(player, score_title);
			ScoreboardUtil.setGameScoreboard(player, title, lines, game);
		}
	}

	private String getFormattedTimeLeft(int time) {
		int min = (int) Math.floor(time / 60);
		int sec = time % 60;
		String minStr = ((min < 10) ? ("0" + String.valueOf(min)) : String.valueOf(min));
		String secStr = ((sec < 10) ? ("0" + String.valueOf(sec)) : String.valueOf(sec));
		return minStr + ":" + secStr;
	}

	private String getTeamBedStatus(Game game, RunningTeam team) {
		return team.isDead() ? Config.scoreboard_team_bed_status_bed_destroyed : Config.scoreboard_team_bed_status_bed_alive;
	}

	private String getTeamStatusFormat(Game game, RunningTeam team) {
		String alive = Config.scoreboard_team_status_format_bed_alive;
		String destroyed = Config.scoreboard_team_status_format_bed_destroyed;
		String status = team.isDead() ? destroyed : alive;
		if (team.isDead() && team.getConnectedPlayers().size() <= 0) {
			status = Config.scoreboard_team_status_format_team_dead;
		}
		return status.replace("{bed_status}", getTeamBedStatus(game, team)).replace("{color}", ColorUtil.translateToChatColor(team.getColor()) + "").replace("{color_initials}", ColorUtil.translateToChatColor(team.getColor()).name().substring(0, 1)).replace("{color_name}", upperInitials(ColorUtil.translateToChatColor(team.getColor()).name())).replace("{players}", team.getConnectedPlayers().size() + "").replace("{team_initials}", team.getName().substring(0, 1)).replace("{team}", team.getName());
	}

	private String upperInitials(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}
}
