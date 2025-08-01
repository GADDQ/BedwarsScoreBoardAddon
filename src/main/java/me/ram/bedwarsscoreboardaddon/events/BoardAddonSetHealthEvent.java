package me.ram.bedwarsscoreboardaddon.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
//import io.github.bedwarsrel.game.Game;
import org.screamingsandals.bedwars.api.game.Game;

public class BoardAddonSetHealthEvent extends Event {

	private static HandlerList handlers = new HandlerList();
	private Game game;
	private Boolean cancelled = false;

	public BoardAddonSetHealthEvent(Game game) {
		this.game = game;
	}

	public Game getGame() {
		return game;
	}

	public boolean isCancelled() {
		return this.cancelled;
	}

	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
