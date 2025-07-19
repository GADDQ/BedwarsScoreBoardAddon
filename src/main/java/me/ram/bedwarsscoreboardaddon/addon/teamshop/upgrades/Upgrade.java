package me.ram.bedwarsscoreboardaddon.addon.teamshop.upgrades;

//import io.github.bedwarsrel.game.Game;
//import io.github.bedwarsrel.game.Team;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.RunningTeam;

public interface Upgrade {

	public UpgradeType getType();

	public String getName();

	public Game getGame();

	public RunningTeam getTeam();

	public int getLevel();

	public void setLevel(int level);

	public String getBuyer();

	public void setBuyer(String buyer);

	public void runUpgrade();
}
