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

import com.github.noonmaru.tap.inventory.TapInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;


public interface Winventory extends InventoryHolder
{

	TapInventory getTapInventory();

	boolean canOpen(Player player);

	void onOpen(Player player);

	void onClose(Player player);

	void onClick(InventoryClickEvent event);

	void onDrag(InventoryDragEvent event);

	void onPickupItem(EntityPickupItemEvent event);

	void onDisable(Player player);

}
