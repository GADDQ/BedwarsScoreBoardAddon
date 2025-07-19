package me.ram.bedwarsscoreboardaddon.addon;

import me.ram.bedwarsscoreboardaddon.listener.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
/*
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameStartedEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameStatus;
*/
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.events.BedwarsGameStartedEvent;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameStatus;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.events.BoardAddonPlayerShootWitherBowEvent;
import me.ram.bedwarsscoreboardaddon.utils.BedwarsUtil;
import me.ram.bedwarsscoreboardaddon.utils.Utils;

public class WitherBow implements Listener {

	@EventHandler
	public void onStarted(BedwarsGameStartedEvent e) {
		Game game = e.getGame();
		Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
		arena.addGameTask(new BukkitRunnable() {
			@Override
			public void run() {
				if (EventListener.countdownMap.get(game) <= Config.witherbow_gametime && Config.witherbow_enabled) {
					if (!Config.witherbow_title.equals("") || !Config.witherbow_subtitle.equals("")) {
						game.getConnectedPlayers().forEach(player -> {
							Utils.sendTitle(player, 10, 50, 10, Config.witherbow_title, Config.witherbow_subtitle);
						});
					}
					if (!Config.witherbow_message.equals("")) {
						game.getConnectedPlayers().forEach(player -> {
							player.sendMessage(Config.witherbow_message);
						});
					}
					PlaySound.playSound(game, Config.play_sound_sound_enable_witherbow);
					cancel();
				}
			}
		}.runTaskTimer(Main.getInstance(), 0L, 21L));
	}

	@EventHandler
	public void onShoot(EntityShootBowEvent e) {
		if (!Config.witherbow_enabled) {
			return;
		}
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) e.getEntity();
		Game game = BedwarsAPI.getInstance().getGameOfPlayer(player);
		if (game == null || game.getStatus() != GameStatus.RUNNING || BedwarsUtil.isSpectator(game, player) || EventListener.countdownMap.get(game) > Config.witherbow_gametime) {
			return;
		}
		WitherSkull skull = player.launchProjectile(WitherSkull.class);
		BoardAddonPlayerShootWitherBowEvent shootWitherBowEvent = new BoardAddonPlayerShootWitherBowEvent(game, player, skull);
		Bukkit.getPluginManager().getPlugin("BedWars").getServer().getPluginManager().callEvent(shootWitherBowEvent);
		if (shootWitherBowEvent.isCancelled()) {
			skull.remove();
			return;
		}
		player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 5);
		PlaySound.playSound(player, Config.play_sound_sound_witherbow);
		skull.setYield(4.0f);
		skull.setVelocity(e.getProjectile().getVelocity());
		skull.setShooter(player);
		e.setCancelled(true);
		player.updateInventory();
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		Entity entity = e.getEntity();
		Entity damager = e.getDamager();
		if (!(entity instanceof Player) || !(damager instanceof WitherSkull)) {
			return;
		}
		WitherSkull skull = (WitherSkull) damager;
		if (skull.getShooter() == null) {
			return;
		}
		Player shooter = (Player) skull.getShooter();
		Player player = (Player) entity;
		Game game = BedwarsAPI.getInstance().getGameOfPlayer(player);
		if (game == null) {
			return;
		}
		if (BedwarsUtil.isSpectator(game, player) || BedwarsUtil.isSpectator(game, shooter)) {
			e.setCancelled(true);
			return;
		}
		if (game.getTeamOfPlayer(shooter).getName().equals(game.getTeamOfPlayer(player).getName())) {
			e.setCancelled(true);
			return;
		}
		player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 1));
	}
}
