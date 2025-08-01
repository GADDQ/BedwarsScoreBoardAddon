package me.ram.bedwarsscoreboardaddon.addon.teamshop.upgrades;

import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
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
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.game.Game;

public class AlarmTrap implements Upgrade {

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

	public AlarmTrap(Game game, RunningTeam team, int level) {
		this.game = game;
		this.team = team;
		this.level = level;
	}

	public UpgradeType getType() {
		return UpgradeType.ALARM_TRAP;
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

			if (team != game.getTeamOfPlayer(player) && team.getTargetBlock().distanceSquared(player.getLocation()) <= Math.pow(Config.teamshop_upgrade_alarm_trap_trigger_range, 2) && player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
				if (teamShop.isCoolingPlayer(team, player) || teamShop.isImmunePlayer(player)) {
					continue;
				}
				level = 0;
				teamShop.removeTrap(this);
				teamShop.addCoolingPlayer(team, player);
				if (Config.invisibility_player_damage_show_player) {
					Main.getInstance().getArenaManager().getArenas().get(game.getName()).getInvisiblePlayer().removePlayer(player);
				} else {
					Main.getInstance().getArenaManager().getArenas().get(game.getName()).getInvisiblePlayer().showPlayerArmor(player);
				}
				if (!Config.teamshop_upgrade_alarm_trap_trigger_title.equals("") || !Config.teamshop_upgrade_alarm_trap_trigger_subtitle.equals("")) {
					RunningTeam t = game.getTeamOfPlayer(player);
					for (Player teamplayers : team.getConnectedPlayers()) {
						Utils.sendTitle(teamplayers, 5, 80, 5, Config.teamshop_upgrade_alarm_trap_trigger_title.replace("{player}", player.getName()).replace("{team}", t.getName()).replace("{team_color}", ColorUtil.translateToChatColor(team.getColor()).toString()), Config.teamshop_upgrade_alarm_trap_trigger_subtitle.replace("{player}", player.getName()).replace("{team}", t.getName()).replace("{team_color}", ColorUtil.translateToChatColor(team.getColor()).toString()));
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
