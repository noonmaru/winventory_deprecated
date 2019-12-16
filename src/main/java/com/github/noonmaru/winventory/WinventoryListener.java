/*
 * Copyright (c) 2019 Noonmaru
 *
 * Licensed under the General Public License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/gpl-2.0.php
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.noonmaru.winventory;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;

public class WinventoryListener implements Listener
{
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryOpen(InventoryOpenEvent event)
	{
		InventoryHolder holder = event.getInventory().getHolder();

		if (holder instanceof Winventory)
		{
			Winventory window = (Winventory) holder;
			Player player = (Player) event.getPlayer();
			
			if (window.canOpen(player))
				window.onOpen(player);
			else
				event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClose(InventoryCloseEvent event)
	{
		InventoryHolder holder = event.getInventory().getHolder();

		if (holder instanceof Winventory)
			((Winventory) holder).onClose((Player) event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event)
	{
		InventoryHolder holder = event.getInventory().getHolder();

		if (holder instanceof Winventory)
			((Winventory) holder).onClick(event);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryDrag(InventoryDragEvent event)
	{
		InventoryHolder holder = event.getInventory().getHolder();

		if (holder instanceof Winventory)
			((Winventory) holder).onDrag(event);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityPickupItem(EntityPickupItemEvent event)
	{
		LivingEntity entity = event.getEntity();

		if (entity instanceof Player)
		{
			Player player = (Player) entity;

			InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder();

			if (holder instanceof Winventory)
				((Winventory) holder).onPickupItem(event);
		}
	}
}
