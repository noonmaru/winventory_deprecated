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
import com.github.noonmaru.tap.inventory.TapInventory;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;


public class WinventoryDisplay extends WinventoryAdapter
{

    final Inventory inventory;

    final TapInventory tapInventory;

    public WinventoryDisplay(int size, String title)
    {
        inventory = Bukkit.createInventory(this, size, title);
        tapInventory = Tap.INVENTORY.fromInventory(inventory);
    }

    @Override
    public final Inventory getInventory()
    {
        return this.inventory;
    }

    @Override
    public final TapInventory getTapInventory()
    {
        return this.tapInventory;
    }

}
