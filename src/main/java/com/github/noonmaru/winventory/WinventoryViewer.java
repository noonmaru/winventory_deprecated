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
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;
import java.util.List;

public class WinventoryViewer extends WinventoryFrame
{

	public static final TapItemStack ITEM_BORDER = Tap.ITEM.newItemStack(160, 1, 3).setDisplayName("");

	public static final TapItemStack ITEM_UP = Tap.ITEM.newItemStack(2256, 1, 0).setDisplayName("§6▲");

	public static final TapItemStack ITEM_DOWN = Tap.ITEM.newItemStack(2257, 1, 0).setDisplayName("§6▼");

	public static final TapItemStack ITEM_PREV = Tap.ITEM.newItemStack(2258, 1, 0).setDisplayName("§6◀");

	public static final TapItemStack ITEM_NEXT = Tap.ITEM.newItemStack(2259, 1, 0).setDisplayName("§6▶");

	public static final TapItemStack ITEM_PAGE = Tap.ITEM.newItemStack(2260, 1, 0); // minecraft:nether_star
	
	static boolean load(JsonObject json)
	{
		boolean save = computeItem(json, "border", ITEM_BORDER);
		save |= computeItem(json, "prev", ITEM_PREV);
		save |= computeItem(json, "next", ITEM_NEXT);
		save |= computeItem(json, "up", ITEM_UP);
		save |= computeItem(json, "down", ITEM_DOWN);
		save |= computeItem(json, "page", ITEM_PAGE);
		
		return save;
	}
	
	private Viewer<?>[] viewers;

	public WinventoryViewer(int size, String title)
	{
		super(size, title);
	}

	protected void initializeViewer(Viewer<?>... viewers)
	{
		if (this.viewers != null)
			throw new IllegalArgumentException("viewers already initialized");
		
		this.viewers = viewers;
		
		for (Viewer<?> viewer : viewers)
		{
			ButtonDisplay button = new ButtonDisplay(viewer);
			
			for (int i = 0, length = viewer.elements.length; i < length; i++)
			{
				setButton(viewer.toSlot(i), button);
			}
			
			viewer.initializeUI();
		}
	}

	public abstract class Viewer<E>
	{

		final int offset;

		final int width;

		final int height;

		final Object[] elements;

		int page;

		int totalPage;

		int count;

		protected final TapItemStack pageItemStack = ITEM_PAGE.copy();

		public Viewer(int offset, int width, int height)
		{
			if (offset < 0)
				throw new IllegalArgumentException("offset cannot be lesser than 0");
			if (width < 0)
				throw new IllegalArgumentException("width cannot be lesser than 0");
			if (width > 9)
				throw new IllegalArgumentException("width cannot be greater than 9");
			if (height < 0)
				throw new IllegalArgumentException("height cannot be lesser than 0");
			if (height > 6)
				throw new IllegalArgumentException("height cannot be greater than 6");

			this.offset = offset;
			this.width = width;
			this.height = height;
			this.elements = new Object[width * height];
		}

		protected void initializeUI()
		{			
			int slot = getUIOffset();
			WinventoryViewer inventory = WinventoryViewer.this;
			inventory.setButton(slot, new ButtonPrev(), ITEM_PREV);
			inventory.setButton(slot + 1, new ButtonPage(), this.pageItemStack);
			inventory.setButton(slot + 2, new ButtonNext(), ITEM_NEXT);
		}
		
		protected int getUIOffset()
		{
			return offset + height * 9 + (width >> 1) - 1;
		}
		
		public int getSize()
		{
			return elements.length;
		}

		public int getCount()
		{
			return count;
		}

		public int getPage()
		{
			return page;
		}

		public int prevPage()
		{
			return setPage(page - 1);
		}

		public int nextPage()
		{
			return setPage(page + 1);
		}

		public int firstPage()
		{
			return setPage(0);
		}

		public int lastPage()
		{
			return setPage(0);
		}

		public int refreshPage()
		{
			return setPage(page);
		}

		public int setPage(int page)
		{
			List<? extends E> displayables = getDisplayables();
			Object[] element = this.elements;
			int size = displayables.size();
			int pageLength = element.length;
			int totalPage = (size - 1) / pageLength;

			if (page < 0)
				page = 0;
			else if (page > totalPage)
				page = totalPage;

			int offset = page * pageLength;
			int limit = Math.min(offset + pageLength, size);
			int count = limit - offset;
			TapInventory inventory = getTapInventory();
			for (int i = 0; i < count; i++)
			{
				E o = displayables.get(offset + i);
				element[i] = o;
				inventory.setItem(toSlot(i), o == null ? null : getDisplayItemStack(o));
			}

			TapItemStack emptySlotItemStack = getEmptySlotItemStack();
			for (int i = count; i < pageLength; i++)
			{
				element[i] = null;
				inventory.setItem(toSlot(i), emptySlotItemStack);
			}

			this.count = count;
			this.page = page;
			this.totalPage = totalPage;
			this.pageItemStack.setDisplayName("§6" + (page + 1) + " §r/ §c" + (totalPage + 1));

			return page;
		}

		protected final int toSlot(int index)
		{
			int width = this.width;

			return this.offset + index % width + index / width * 9;
		}

		protected abstract List<? extends E> getDisplayables();

		protected TapItemStack getDisplayItemStack(E o)
		{
			return ((Displayable) o).getDisplayItemStack();
		}

		protected TapItemStack getEmptySlotItemStack()
		{
			return null;
		}

		@SuppressWarnings("unchecked")
		public E get(int index)
		{
			return (E) this.elements[index];
		}

		protected final int toIndex(int slot)
		{
			slot -= this.offset;

			return slot / 9 * this.width + slot % 9;
		}

		public void clear()
		{
			TapInventory inventory = getTapInventory();
			int count = this.count;

			for (int i = 0; i < count; i++)
			{
				inventory.setItem(toSlot(i), null);
			}

			Arrays.fill(this.elements, 0, count, null);
			this.page = 0;
			this.totalPage = 0;
			this.count = 0;
		}

		@SuppressWarnings("unchecked")
		void onClickDisplay(InventoryClickEvent event)
		{
			onClick(event, (E) this.elements[toIndex(event.getSlot())]);
		}

		public void onClick(InventoryClickEvent event, E o)
		{}

		public class ButtonPrev implements Button
		{
			@Override
			public void onClick(InventoryClickEvent event)
			{
				ClickType click = event.getClick();

				if (click == ClickType.LEFT)
					prevPage();
				else if (click == ClickType.RIGHT)
					setPage(page - 2);
				else if (click == ClickType.SHIFT_LEFT)
					setPage(page - Math.max(1, (page + 1) >> 1));
				else if (click == ClickType.SHIFT_RIGHT)
					firstPage();
			}
		}

		public class ButtonNext implements Button
		{
			@Override
			public void onClick(InventoryClickEvent event)
			{
				ClickType click = event.getClick();

				if (click == ClickType.LEFT)
					nextPage();
				else if (click == ClickType.RIGHT)
					setPage(page + 2);
				else if (click == ClickType.SHIFT_LEFT)
					setPage(page + Math.max(1, (totalPage - page + 1) >> 1));
				else if (click == ClickType.SHIFT_RIGHT)
					lastPage();
			}
		}

		public class ButtonPage implements Button
		{
			@Override
			public void onClick(InventoryClickEvent event)
			{
				if (event.getClick() != ClickType.DOUBLE_CLICK)
					refreshPage();
			}
		}
	}

	@Override
	public boolean canOpen(Player player)
	{
		return getInventory().getViewers().size() == 1;
	}

	@Override
	public void onOpen(Player player)
	{
		for (Viewer<?> viewer : this.viewers)
			viewer.firstPage();
	}

	@Override
	public void onClose(Player player)
	{
		for (Viewer<?> viewer : this.viewers)
			viewer.clear();
	}

	public void clear()
	{
		for (Viewer<?> viewer : this.viewers)
		{
			viewer.clear();
		}
	}

	static class ButtonDisplay implements Button
	{
		private final Viewer<?> viewer;

		ButtonDisplay(Viewer<?> viewer)
		{
			this.viewer = viewer;
		}

		@Override
		public void onClick(InventoryClickEvent event)
		{
			this.viewer.onClickDisplay(event);
		}
	}
}
