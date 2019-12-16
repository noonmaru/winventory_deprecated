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
import com.github.noonmaru.tap.item.TapItem;
import com.github.noonmaru.tap.item.TapItemStack;
import com.google.gson.JsonObject;
import org.bukkit.event.inventory.InventoryClickEvent;


public abstract class WinventoryNumberpad extends WinventoryFrame
{

	public static final TapItemStack[] ITEM_NUMBERS;

	public static final TapItemStack ITEM_CLEAR;

	public static final TapItemStack ITEM_BACKSPACE;
	
	public static final TapItemStack ITEM_BORDER = WinventoryViewer.ITEM_BORDER;

	public static final TapItemStack ITEM_OUTLINE = Tap.ITEM.newItemStack(160, 1, 1).setDisplayName("");

	protected static final int SLOT_ZERO = 42;

	protected static final int SLOT_CLEAR = 41;

	protected static final int SLOT_BACKSPACE = 43;

	static
	{
		TapItem item = Tap.ITEM.getItem(160);
		int ten = 10;
		TapItemStack[] numbers = new TapItemStack[ten];
		for (int i = 0; i < ten; i++)
			numbers[i] = Tap.ITEM.newItemStack(item, 1, 1).setDisplayName("§6" + i);
		ITEM_NUMBERS = numbers;
		ITEM_BACKSPACE = Tap.ITEM.newItemStack(359, 1, 0).setDisplayName("§b←"); //scissors
		ITEM_CLEAR = Tap.ITEM.newItemStack(351, 1, 15).setDisplayName("§cC"); //bone
	}
	
	static boolean load(JsonObject json)
	{
		boolean save = false;
		
		for (int i = 0, length = ITEM_NUMBERS.length; i < length; i++)
			save |= computeItem(json, String.valueOf(i), ITEM_NUMBERS[i]);
		
		save |= computeItem(json, "clear", ITEM_CLEAR);
		save |= computeItem(json, "backspace", ITEM_BACKSPACE);
		save |= computeItem(json, "outline", ITEM_OUTLINE);
		
		return save;
	}

	public WinventoryNumberpad(String title)
	{
		super(54, title);

		ButtonNumber button = new ButtonNumber();

		for (int i = 1; i < 10; i++)
		{
			int j = i - 1;

			setButton(14 + j / 3 * 9 + j % 3, button, ITEM_NUMBERS[i]);
		}

		setButton(SLOT_ZERO, button, ITEM_NUMBERS[0]);
	}

	private class ButtonNumber implements Button
	{
		@Override
		public void onClick(InventoryClickEvent event)
		{
			int slot = event.getRawSlot();
			int number;

			if (slot == SLOT_ZERO)
			{
				number = 0;
			}
			else
			{
				int i = slot - 14;

				number = i / 9 * 3 + i % 9 + 1;
			}

			onClickNumber(event, number);
		}
	}

	public WinventoryNumberpad(String title, TapItemStack border)
	{
		this(title);

		TapInventory inventory = tapInventory;

		for (int i = 0; i < 9; i++)
		{
			inventory.setItem(i, border);
			inventory.setItem(45 + i, border);
		}

		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				inventory.setItem(9 + i * 9 + j * 4, border);
			}
		}
	}

	protected void useClear()
	{
		setButton(SLOT_CLEAR, new ButtonClear(), ITEM_CLEAR);
	}

	private class ButtonClear implements Button
	{
		@Override
		public void onClick(InventoryClickEvent event)
		{
			onClickClear(event);
		}
	}

	protected void useBackspace()
	{
		setButton(SLOT_BACKSPACE, new ButtonBackspace(), ITEM_BACKSPACE);
	}

	private class ButtonBackspace implements Button
	{
		@Override
		public void onClick(InventoryClickEvent event)
		{
			onClickBackspace(event);
		}
	}

	public abstract void onClickNumber(InventoryClickEvent event, int number);

	public void onClickBackspace(InventoryClickEvent event)
	{}

	public void onClickClear(InventoryClickEvent event)
	{}
}
