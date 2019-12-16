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
import com.github.noonmaru.tap.item.TapItemStack;
import com.github.noonmaru.tap.nbt.NBTCompound;
import com.github.noonmaru.tap.nbt.NBTList;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;


public abstract class WinventoryEditor extends WinventoryNumberpad
{
	protected static final int SLOT_TARGET = 20;

	private ButtonOption[] options;
	ButtonOption option;

	public WinventoryEditor(String title)
	{
		super(title, ITEM_BORDER);

		useClear();
		useBackspace();

		TapInventory inventory = getTapInventory();

		for (int i = 0; i < 3; i++)
		{
			inventory.setItem(10 + i, ITEM_OUTLINE);
			inventory.setItem(28 + i, ITEM_OUTLINE);
			inventory.setItem(37 + i, ITEM_OUTLINE);
		}

		inventory.setItem(19, ITEM_OUTLINE);
		inventory.setItem(21, ITEM_OUTLINE);
		setButton(SLOT_TARGET, new ButtonTarget());
	}

	private class ButtonTarget implements Button
	{
		@Override
		public void onClick(InventoryClickEvent event)
		{
			onClickTarget(event);
		}
	}

	public abstract void onClickTarget(InventoryClickEvent event);

	protected void initializeOption(ButtonOption... options)
	{
		int length = options.length;

		if (length > 5)
			throw new IllegalArgumentException();

		this.options = options;

		int offset = 38 - (length >> 1);

		for (int i = 0; i < length; i++)
		{
			ButtonOption option = options[i];

			setButton(offset + i, option, option.displayItemStack);
		}
	}

	static final NBTList GLOW = Tap.NBT.newList();

	static final String ENCH = "ench";

	protected abstract class ButtonOption implements Button
	{
		protected final TapItemStack displayItemStack;

		public ButtonOption(TapItemStack displayItemStack)
		{
			this.displayItemStack = displayItemStack;
		}

		public void onInitialize()
		{}

		@Override
		public final void onClick(InventoryClickEvent event)
		{
			WinventoryEditor editor = WinventoryEditor.this;

			onClickOption(event);

			ButtonOption option = editor.option;
			
			if (option != null && option != this)
			{
				option.disable();
				editor.option = this;
				enable();
			}			
		}

		public boolean isSelected()
		{
			return WinventoryEditor.this.option == this;
		}

		void enable()
		{
			TapItemStack item = this.displayItemStack;
			NBTCompound tag = item.getTag();

			if (tag == null)
			{
				tag = Tap.NBT.newCompound();
				item.setTag(tag);
			}

			tag.setList(ENCH, GLOW);

			onEnable();
		}

		public void onEnable()
		{}

		void disable()
		{
			TapItemStack item = this.displayItemStack;
			NBTCompound tag = item.getTag();

			if (tag != null)
			{
				tag.remove(ENCH);

				if (tag.isEmpty())
				{
					item.setTag(null);
				}
			}

			onDisable();
		}

		public void onDisable()
		{}

		public void onClickOption(InventoryClickEvent event)
		{}

		public void onClickNumber(InventoryClickEvent event, int number)
		{}

		public void onClickClear(InventoryClickEvent event)
		{}

		public void onClickBackspace(InventoryClickEvent event)
		{}

		void clear()
		{
			this.displayItemStack.setTag(null);

			onClear();
		}

		public void onClear()
		{}
	}

	protected void setItemStack(TapItemStack itemStack)
	{
		getTapInventory().setItem(SLOT_TARGET, itemStack);
	}

	@Override
	public void onOpen(Player player)
	{
		ButtonOption[] options = this.options;

		if (options == null)
			return;

		for (ButtonOption option : options)
		{
			option.onInitialize();
		}

		this.option = options[0];
		this.option.enable();
	}

	@Override
	public void onClose(Player player)
	{
		this.option = null;

		ButtonOption[] options = this.options;

		for (ButtonOption option : options)
		{
			option.clear();
		}

		setItemStack(null);
	}

	@Override
	public final void onClickNumber(InventoryClickEvent event, int number)
	{
		if (this.option != null)
		{
			this.option.onClickNumber(event, number);
		}
	}

	@Override
	public final void onClickClear(InventoryClickEvent event)
	{
		if (this.option != null)
		{
			this.option.onClickClear(event);
		}
	}

	@Override
	public final void onClickBackspace(InventoryClickEvent event)
	{
		if (this.option != null)
		{
			this.option.onClickBackspace(event);
		}
	}
}
