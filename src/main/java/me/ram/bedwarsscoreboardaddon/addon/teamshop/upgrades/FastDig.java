package me.ram.bedwarsscoreboardaddon.addon.teamshop.upgrades;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

//import io.github.bedwarsrel.game.Game;
//import io.github.bedwarsrel.game.Team;
import lombok.Getter;
import lombok.Setter;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.BedwarsUtil;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.RunningTeam;

public class FastDig implements Upgrade {

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

	public FastDig(Game game, RunningTeam team, int level) {
		this.game = game;
		this.team = team;
		this.level = level;
	}

	public UpgradeType getType() {
		return UpgradeType.FAST_DIG;
	}

	public String getName() {
		return Config.teamshop_upgrade_name.get(getType());
	}

	public void runUpgrade() {
		if (level < 1) {
			return;
		}
		for (Player p : team.getConnectedPlayers()) {
			if (!BedwarsUtil.isSpectator(game, p)) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 30, getLevel() - 1), true);
			}
		}
	}
}
