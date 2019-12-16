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

import java.io.File;
import java.io.IOException;

import com.github.noonmaru.tap.event.ASMEventExecutor;
import com.google.gson.JsonObject;
import com.nemosw.tools.gson.JsonIO;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.java.JavaPlugin;


public class WinventoryPlugin extends JavaPlugin
{
	@Override
	public void onEnable()
	{
		loadConfig();

		ASMEventExecutor.registerEvents(new WinventoryListener(), this);
	}

	private void loadConfig()
	{
		File folder = getDataFolder();
		File uiFile = new File(folder, "ui.json");

		try
		{
			JsonObject json = JsonIO.load(uiFile);
			
			if (json == null)
				json = new JsonObject();
			
			boolean save = WinventoryViewer.load(json);
			save |= WinventoryNumberpad.load(json);
			
			if (save)
			{
				folder.mkdirs();
				JsonIO.save(json, uiFile);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable()
	{
		for (Player player : Bukkit.getOnlinePlayers())
		{
			InventoryView view = player.getOpenInventory();
			InventoryHolder holder = view.getTopInventory().getHolder();

			if (holder instanceof Winventory)
			{
				player.closeInventory();

				try
				{
					((Winventory) holder).onDisable(player);
				}
				catch (Throwable t)
				{
					t.printStackTrace();
				}
			}
		}
	}
}
