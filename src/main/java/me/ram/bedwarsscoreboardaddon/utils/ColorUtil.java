package me.ram.bedwarsscoreboardaddon.utils;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.screamingsandals.bedwars.api.TeamColor;

public class ColorUtil {

	public static String color(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public static List<String> colorList(List<String> list) {
		List<String> clist = new ArrayList<String>();
		for (String l : list) {
			clist.add(ChatColor.translateAlternateColorCodes('&', l));
		}
		return clist;
	}

	public static String removeColor(String s) {
		return ChatColor.stripColor(s);
	}

	public static List<String> removeListColor(List<String> list) {
		List<String> clist = new ArrayList<String>();
		for (String l : list) {
			clist.add(ChatColor.stripColor(l));
		}
		return clist;
	}

	public static Color translateToColor(TeamColor teamColor){
		switch (teamColor) {
			case BLACK:
				return Color.BLACK;
			case BLUE:
				return Color.BLUE;
			case GREEN:
				return Color.GREEN;
			case LIME:
				return Color.LIME;
			case RED:
				return Color.RED;
			case MAGENTA:
			case PINK:
				return Color.FUCHSIA;
			case ORANGE:
				return Color.ORANGE;
			case LIGHT_GRAY:
				return Color.SILVER;
			case GRAY:
				return Color.GRAY;
			case LIGHT_BLUE:
			case CYAN:
				return Color.AQUA;
			case YELLOW:
				return Color.YELLOW;
			case WHITE:
				return Color.WHITE;
			case BROWN:
				return Color.MAROON;
		}
		return null;
	}

	public static ChatColor translateToChatColor(TeamColor teamColor){
		switch (teamColor) {
			case BLACK:
				return ChatColor.BLACK;
			case BLUE:
				return ChatColor.BLUE;
			case GREEN:
            case LIME:
                return ChatColor.GREEN;
			case RED:
				return ChatColor.RED;
			case MAGENTA:
            case PINK:
                return ChatColor.LIGHT_PURPLE;
			case ORANGE:
				return ChatColor.GOLD;
			case LIGHT_GRAY:
				return ChatColor.GRAY;
			case GRAY:
				return ChatColor.DARK_GRAY;
			case LIGHT_BLUE:
            case CYAN:
                return ChatColor.AQUA;
            case YELLOW:
				return ChatColor.YELLOW;
			case WHITE:
				return ChatColor.WHITE;
			case BROWN:
				return ChatColor.DARK_RED;
		}
		return null;
	}

	public static DyeColor translateToDyeColor(TeamColor teamColor){
		switch (teamColor) {
			case BLACK:
				return DyeColor.BLACK;
			case BLUE:
				return DyeColor.BLUE;
			case GREEN:
				return DyeColor.GREEN;
			case LIME:
				return DyeColor.LIME;
			case RED:
				return DyeColor.RED;
			case MAGENTA:
				return DyeColor.MAGENTA;
			case PINK:
				return DyeColor.PINK;
			case ORANGE:
				return DyeColor.ORANGE;
			case LIGHT_GRAY:
				return DyeColor.SILVER;
			case GRAY:
				return DyeColor.GRAY;
			case LIGHT_BLUE:
				return DyeColor.LIGHT_BLUE;
			case CYAN:
				return DyeColor.CYAN;
			case YELLOW:
				return DyeColor.YELLOW;
			case WHITE:
				return DyeColor.WHITE;
			case BROWN:
				return DyeColor.BROWN;
		}
		return null;
	}
}
