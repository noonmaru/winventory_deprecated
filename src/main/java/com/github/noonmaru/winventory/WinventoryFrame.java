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

import com.github.noonmaru.tap.item.TapItemStack;
import org.bukkit.event.inventory.InventoryClickEvent;

public class WinventoryFrame extends WinventoryDisplay
{

    private final Button[] buttons;

    public WinventoryFrame(int size, String title)
    {
        super(size, title);

        this.buttons = new Button[size];
    }

    public void setButton(int i, Button button)
    {
        this.buttons[i] = button;
    }

    public void setButton(int i, Button button, TapItemStack itemStack)
    {
        buttons[i] = button;
        tapInventory.setItem(i, itemStack);
    }

    @Override
    public void onClickInventory(InventoryClickEvent event)
    {
        event.setCancelled(true);

        Button button = this.buttons[event.getSlot()];

        if (button != null)
            button.onClick(event);
    }

    public interface Button
    {
        void onClick(InventoryClickEvent paramInventoryClickEvent);
    }

}
