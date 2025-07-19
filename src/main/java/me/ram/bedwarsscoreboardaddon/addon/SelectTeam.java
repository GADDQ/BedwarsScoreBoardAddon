package me.ram.bedwarsscoreboardaddon.addon;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
//import io.github.bedwarsrel.BedwarsRel;
//import io.github.bedwarsrel.game.Game;
//import io.github.bedwarsrel.game.Team;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.RunningTeam;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.config.LocaleConfig;

public class SelectTeam {
	@Getter
	private static LocaleConfig localeConfig;

	public static void openSelectTeam(Game game, Player player) {
		String CHOOSETEAM = (String) getLocaleConfig().getLanguage("chooseteam");
		int size = 27 + (9 * (game.getRunningTeams().size() / 7));
		Inventory inventory = Bukkit.createInventory(null, size, CHOOSETEAM); // get locale
		int slot = 10;
		for (RunningTeam team : game.getRunningTeams()) {
			switch (slot) {
			case 17:
				slot = 19;
				break;
			case 26:
				slot = 28;
				break;
			default:
				break;
			}
			Wool wool = new Wool(ColorUtil.translateToDyeColor(team.getColor()));
			ItemStack itemStack = wool.toItemStack(1);
			ItemMeta itemMeta = itemStack.getItemMeta();
			String color = ColorUtil.translateToChatColor(team.getColor()).toString();
			String status = Config.select_team_status_select;
			if (team.getConnectedPlayers().contains(player)) {
				status = Config.select_team_status_inteam;
			} else if (team.getConnectedPlayers().size() >= team.getMaxPlayers()) {
				status = Config.select_team_status_team_full;
			}
			itemMeta.setDisplayName(Config.select_team_item_name.replace("{status}", status).replace("{team}", team.getName()).replace("{color}", color).replace("{players}", team.getConnectedPlayers().size() + "").replace("{maxplayers}", team.getMaxPlayers() + ""));
			List<String> lore = new ArrayList<String>();
			for (String l : Config.select_team_item_lore) {
				if (l.contains("{players_list}")) {
					if (team.getConnectedPlayers().size() > 0) {
						for (Player p : team.getConnectedPlayers()) {
							lore.add(l.replace("{status}", status).replace("{team}", team.getName()).replace("{color}", color).replace("{players}", team.getConnectedPlayers().size() + "").replace("{maxplayers}", team.getMaxPlayers() + "").replace("{players_list}", p.getDisplayName()));
						}
					} else {
						lore.add(l.replace("{status}", status).replace("{team}", team.getName()).replace("{color}", color).replace("{players}", team.getConnectedPlayers().size() + "").replace("{maxplayers}", team.getMaxPlayers() + "").replace("{players_list}", Config.select_team_no_players));
					}
				} else {
					lore.add(l.replace("{status}", status).replace("{team}", team.getName()).replace("{color}", color).replace("{players}", team.getConnectedPlayers().size() + "").replace("{maxplayers}", team.getMaxPlayers() + ""));
				}
			}
			itemMeta.setLore(lore);
			itemStack.setItemMeta(itemMeta);
			inventory.setItem(slot, itemStack);
			slot++;
		}
		player.closeInventory();
		player.openInventory(inventory);
	}
}
