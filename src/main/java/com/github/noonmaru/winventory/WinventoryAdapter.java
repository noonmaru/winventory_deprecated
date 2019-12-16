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

import com.github.noonmaru.tap.Tap;
import com.github.noonmaru.tap.item.TapItem;
import com.github.noonmaru.tap.item.TapItemStack;
import com.github.noonmaru.tap.util.ChatColorSupport;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.ArrayList;
import java.util.List;


public abstract class WinventoryAdapter implements Winventory
{
	
	private static final String ID = "id";
	
	private static final String DATA = "data";
	
	private static final String NAME = "name";
	
	private static final String LORE = "lore";
	
	protected static boolean computeItem(JsonObject parent, String name, TapItemStack itemStack)
	{	
		boolean save = false;
		JsonElement element;
		JsonObject json;
		
		if ((element = parent.get(name)) != null && element.isJsonObject())
			json = element.getAsJsonObject();
		else
		{
			parent.add(name, json = new JsonObject());
			save = true;
		}
		
		if ((element = json.get(ID)) != null && element.isJsonPrimitive())
		{
			TapItem item = Tap.ITEM.getItem(element.getAsInt());
			
			if (item != null)
			{
				itemStack.setItem(item);
			}
		}
		else
		{
			json.addProperty(ID, itemStack.getId());
			save = true;
		}
		
		if ((element = json.get(DATA)) != null && element.isJsonPrimitive())
		{
			itemStack.setData(element.getAsInt());
		}
		else
		{
			json.addProperty(DATA, itemStack.getData());
			save = true;
		}
		
		if ((element = json.get(NAME)) != null && element.isJsonPrimitive())
		{
			itemStack.setDisplayName(ChatColorSupport.frontColor(ChatColor.RESET, ChatColorSupport.color(element.getAsString())));
		}
		else
		{
			String displayName = itemStack.getDisplayName();
			
			if (displayName != null)
			{
				json.addProperty(NAME, ChatColorSupport.strip(displayName));
				save = true;
			}
		}
		
		if ((element = json.get(LORE)) != null && element.isJsonArray())
		{
			JsonArray array = element.getAsJsonArray();
			int size = array.size();
			
			if (size > 0)
			{
				ArrayList<String> lore = new ArrayList<>(size);
				
				for (int i = 0; i < size; i++)
					lore.add(ChatColorSupport.frontColor(ChatColor.RESET, ChatColorSupport.color(array.get(i).getAsString())));
				
				itemStack.setLore(lore);
			}
		}
		else
		{
			List<String> lore = itemStack.getLore();
			
			if (lore != null && lore.size() > 0)
			{
				JsonArray array = new JsonArray();
				
				for (String s : lore)
					array.add(ChatColorSupport.strip(s));
				
				json.add(LORE, array);
				save = true;
			}
		}
		
		return save;
	}
	
	@Override
	public boolean canOpen(Player player)
	{
		return true;
	}
	
	@Override
	public void onOpen(Player player)
	{}

	@Override
	public void onClose(Player player)
	{}
	
	@Override
	public void onDisable(Player player)
	{}

	@Override
	public final void onClick(InventoryClickEvent event)
	{
		int slot = event.getRawSlot();

		if (slot == -999)
			onClickOutside(event);
		else if (slot == -1)
			onClickBorder(event);
		else if (slot < getInventory().getSize())
			onClickInventory(event);
		else
			onClickPlayerInventory(event);
	}

	public void onClickBorder(InventoryClickEvent event)
	{
		event.setCancelled(true);
	}

	public void onClickInventory(InventoryClickEvent event)
	{
		event.setCancelled(true);
	}

	public void onClickPlayerInventory(InventoryClickEvent event)
	{
		event.setCancelled(true);
	}

	public void onClickOutside(InventoryClickEvent event)
	{
		event.setCancelled(true);
	}

	@Override
	public void onDrag(InventoryDragEvent event)
	{
		event.setCancelled(true);
	}

	@Override
	public void onPickupItem(EntityPickupItemEvent event)
	{
		event.setCancelled(true);
	}

	public boolean open(Player player)
	{
		player.closeInventory();

		return player.openInventory(getInventory()) != null;
	}
}
