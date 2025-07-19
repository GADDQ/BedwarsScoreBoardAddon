package me.ram.bedwarsscoreboardaddon.addon.teamshop.upgrades;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

//import io.github.bedwarsrel.game.Game;
//import io.github.bedwarsrel.game.Team;
import lombok.Getter;
import lombok.Setter;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.addon.teamshop.TeamShop;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.BedwarsUtil;
import me.ram.bedwarsscoreboardaddon.utils.Utils;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.RunningTeam;

public class Trap implements Upgrade {

	@Getter
	private Game game;
	@Getter
	private RunningTeam team;
	@Getter
	@Setter
	private int level;
	@Getter
	@Setter
	private String buyer;

	public Trap(Game game, RunningTeam team, int level) {
		this.game = game;
		this.team = team;
		this.level = level;
	}

	public UpgradeType getType() {
		return UpgradeType.TRAP;
	}

	public String getName() {
		return Config.teamshop_upgrade_name.get(getType());
	}

	public void runUpgrade() {
		if (level < 1) {
			return;
		}
		TeamShop teamShop = Main.getInstance().getArenaManager().getArena(game.getName()).getTeamShop();
		for (Player player : game.getConnectedPlayers()) {
			if (BedwarsUtil.isSpectator(game, player) || player.getGameMode() == GameMode.SPECTATOR) {
				continue;
			}
			if (team != game.getTeamOfPlayer(player) && team.getTargetBlock().distanceSquared(player.getLocation()) <= Math.pow(Config.teamshop_upgrade_trap_trigger_range, 2)) {
				if (teamShop.isCoolingPlayer(team, player) || teamShop.isImmunePlayer(player)) {
					continue;
				}
				level = 0;
				teamShop.removeTrap(this);
				teamShop.addCoolingPlayer(team, player);
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1), true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 1), true);
				if (!Config.teamshop_upgrade_trap_trigger_title.equals("") || !Config.teamshop_upgrade_trap_trigger_subtitle.equals("")) {
					for (Player teamplayers : team.getConnectedPlayers()) {
						Utils.sendTitle(teamplayers, 5, 80, 5, Config.teamshop_upgrade_trap_trigger_title, Config.teamshop_upgrade_trap_trigger_subtitle);
					}
				}
				if (team.getConnectedPlayers().size() > 0) {
					Main.getInstance().getArenaManager().getArena(game.getName()).getTeamShop().updateTeamShop(team.getConnectedPlayers().get(0));
				}
				break;
			}
		}
	}
}
