package me.ram.bedwarsscoreboardaddon.addon;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import me.ram.bedwarsscoreboardaddon.listener.EventListener;
import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
/*
import io.github.bedwarsrel.events.BedwarsGameStartedEvent;
import io.github.bedwarsrel.events.BedwarsPlayerJoinedEvent;
import io.github.bedwarsrel.events.BedwarsTargetBlockDestroyedEvent;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameOverEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameStatus;
import io.github.bedwarsrel.game.Team;
*/
import org.screamingsandals.bedwars.api.events.BedwarsGameStartedEvent;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerJoinedEvent;
import org.screamingsandals.bedwars.api.events.BedwarsTargetBlockDestroyedEvent;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.events.BedwarsGameEndingEvent;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.RunningTeam;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.BedwarsUtil;
import me.ram.bedwarsscoreboardaddon.utils.Utils;

public class Title implements Listener {

	private Map<String, Integer> Times = new HashMap<String, Integer>();

	@EventHandler
	public void onStarted(BedwarsGameStartedEvent e) {
		Game game = e.getGame();
		Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
		Times.put(e.getGame().getName(), EventListener.countdownMap.get(game));
		if (Config.start_title_enabled) {
			for (Player player : e.getGame().getConnectedPlayers()) {
				Utils.clearTitle(player);
			}
			int delay = game.getGameWorld().getName().equals(game.getLobbySpawn().getWorld().getName()) ? 5 : 30;
			arena.addGameTask(new BukkitRunnable() {
				int rn = 0;

				@Override
				public void run() {
					if (rn < Config.start_title_title.size()) {
						for (Player player : e.getGame().getConnectedPlayers()) {
							Utils.sendTitle(player, 0, 80, 5, Config.start_title_title.get(rn), Config.start_title_subtitle);
						}
						rn++;
					} else {
						this.cancel();
					}
				}
			}.runTaskTimer(Main.getInstance(), delay, 0L));
		}
		if (game.getLobbySpawn().getWorld().equals(game.getGameWorld())) {
			PlaySound.playSound(e.getGame(), Config.play_sound_sound_start);
		} else {
			arena.addGameTask(new BukkitRunnable() {
				@Override
				public void run() {
					PlaySound.playSound(e.getGame(), Config.play_sound_sound_start);
				}
			}.runTaskLater(Main.getInstance(), 30L));
		}
	}

	@EventHandler
	public void onDestroyed(BedwarsTargetBlockDestroyedEvent e) {
		if (Config.destroyed_title_enabled) {
			for (Player player : e.getTeam().getConnectedPlayers()) {
				Utils.sendTitle(player, 1, 30, 1, Config.destroyed_title_title, Config.destroyed_title_subtitle);
			}
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		Player player = e.getPlayer();
		Game game = BedwarsAPI.getInstance().getGameOfPlayer(player);
		if (game == null) {
			return;
		}
		if (Config.die_out_title_enabled) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if (game.getStatus() == GameStatus.RUNNING && game.isSpectator(player)) {
						Utils.sendTitle(player, 1, 80, 5, Config.die_out_title_title, Config.die_out_title_subtitle);
					}
				}
			}.runTaskLater(Main.getInstance(), 5L);
		}
	}

	@EventHandler
	public void onOver(BedwarsGameEndingEvent e) {
		if (Config.victory_title_enabled) {
			Game game = e.getGame();
			Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
			RunningTeam team = e.getWinningTeam();
			int time = Times.getOrDefault(e.getGame().getName(), 3600) - EventListener.countdownMap.get(game);
			String formattime = time / 60 + ":" + ((time % 60 < 10) ? ("0" + time % 60) : (time % 60));
			new BukkitRunnable() {
				@Override
				public void run() {
					if (team != null && team.getConnectedPlayers() != null) {
						for (Player player : team.getConnectedPlayers()) {
							if (player.isOnline()) {
								Utils.clearTitle(player);
							}
						}
					}
				}
			}.runTaskLater(Main.getInstance(), 1L);
			arena.addGameTask(new BukkitRunnable() {
				int rn = 0;

				@Override
				public void run() {
					if (rn < Config.victory_title_title.size()) {
						if (team != null && team.getConnectedPlayers() != null) {
							for (Player player : team.getConnectedPlayers()) {
								if (player.isOnline()) {
									Utils.sendTitle(player, 0, 80, 5, Config.victory_title_title.get(rn).replace("{time}", formattime).replace("{color}", ColorUtil.translateToChatColor(team.getColor()) + "").replace("{team}", team.getName()), Config.victory_title_subtitle.replace("{time}", formattime).replace("{color}", ColorUtil.translateToChatColor(team.getColor()) + "").replace("{team}", team.getName()));
								}
							}
							rn++;
						} else {
							this.cancel();
						}
					} else {
						this.cancel();
					}
				}
			}.runTaskTimer(Main.getInstance(), 40L, 0L));
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				PlaySound.playSound(e.getGame(), Config.play_sound_sound_over);
			}
		}.runTaskLater(Main.getInstance(), 40L);
	}

	@EventHandler
	public void onJoined(BedwarsPlayerJoinedEvent e) {
		for (Player player : e.getGame().getConnectedPlayers()) {
			if (player.getName().contains(",") || player.getName().contains("[") || player.getName().contains("]")) {
				player.kickPlayer("");
			}
			if (!(e.getGame().getStatus() != GameStatus.WAITING && e.getGame().getStatus() == GameStatus.RUNNING)) {
				if (Config.jointitle_enabled) {
					Utils.sendTitle(player, e.getPlayer(), 5, 50, 5, Config.jointitle_title.replace("{player}", e.getPlayer().getName()), Config.jointitle_subtitle.replace("{player}", e.getPlayer().getName()));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamageTitle(EntityDamageByEntityEvent e) {
		if (!Config.damagetitle_enabled || e.isCancelled() || !(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player)) {
			return;
		}
		Game game = BedwarsAPI.getInstance().getGameOfPlayer((Player) e.getDamager());
		if (game == null || game.getStatus() != GameStatus.RUNNING) {
			return;
		}
		if (!(game.getConnectedPlayers().contains((Player) e.getDamager()) && game.getConnectedPlayers().contains((Player) e.getEntity()))) {
			return;
		}
		Player player = (Player) e.getEntity();
		Player damager = (Player) e.getDamager();
		if (BedwarsUtil.isSpectator(game, damager) || BedwarsUtil.isSpectator(game, player)) {
			return;
		}
		if (!Config.damagetitle_title.equals("") || !Config.damagetitle_subtitle.equals("")) {
			DecimalFormat df = new DecimalFormat("0.00");
			DecimalFormat df2 = new DecimalFormat("#");
			double health = player.getHealth() - e.getFinalDamage();
			health = health < 0 ? 0 : health;
			Utils.sendTitle((Player) e.getDamager(), player, 0, 20, 0, Config.damagetitle_title.replace("{player}", player.getName()).replace("{damage}", df.format(e.getDamage())).replace("{health}", df2.format(health)).replace("{maxhealth}", df2.format(player.getMaxHealth())), Config.damagetitle_subtitle.replace("{player}", player.getName()).replace("{damage}", df.format(e.getDamage())).replace("{health}", df2.format(health)).replace("{maxhealth}", df2.format(player.getMaxHealth())));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBowDamage(EntityDamageByEntityEvent e) {
		if (!Config.bowdamage_enabled || e.isCancelled()) {
			return;
		}
		if (!(e.getDamager() instanceof Arrow) || !(e.getEntity() instanceof Player)) {
			return;
		}
		Arrow arrow = (Arrow) e.getDamager();
		if (!(arrow.getShooter() instanceof Player)) {
			return;
		}
		Player shooter = (Player) arrow.getShooter();
		Game game = BedwarsAPI.getInstance().getGameOfPlayer(shooter);
		if (game == null) {
			return;
		}
		if (game.getStatus() != GameStatus.RUNNING) {
			return;
		}
		Player player = (Player) e.getEntity();
		Integer damage = (int) e.getFinalDamage();
		if (game.getTeamOfPlayer(shooter) == game.getTeamOfPlayer(player)) {
			e.setCancelled(true);
		}
		if (player.isDead()) {
			return;
		}
		double health = player.getHealth() - e.getFinalDamage();
		health = health < 0 ? 0 : health;
		DecimalFormat df = new DecimalFormat("#");
		if (!Config.bowdamage_title.equals("") || !Config.bowdamage_subtitle.equals("")) {
			Utils.sendTitle(shooter, player, 0, 20, 0, Config.bowdamage_title.replace("{player}", player.getName()).replace("{damage}", damage + "").replace("{health}", df.format(health)).replace("{maxhealth}", df.format(player.getMaxHealth())), Config.bowdamage_subtitle.replace("{player}", player.getName()).replace("{damage}", damage + "").replace("{health}", df.format(health)).replace("{maxhealth}", df.format(player.getMaxHealth())));
		}
		if (!Config.bowdamage_message.equals("")) {
			Utils.sendMessage(shooter, player, Config.bowdamage_message.replace("{player}", player.getName()).replace("{damage}", damage + "").replace("{health}", df.format(health)).replace("{maxhealth}", df.format(player.getMaxHealth())));
		}
	}
}
