/*
 * Copyright (C) 2009-2013 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.fbreader.library;

import java.util.Collections;

import org.geometerplus.fbreader.book.*;

public final class SeriesTree extends LibraryTree {
	public final String Series;

	SeriesTree(IBookCollection collection, String series) {
		super(collection);
		Series = series;
	}

	SeriesTree(LibraryTree parent, String series, int position) {
		super(parent, position);
		Series = series;
	}

	@Override
	public String getName() {
		return Series;
	}

	@Override
	protected String getStringId() {
		return "@SeriesTree " + getName();
	}

	@Override
	public boolean containsBook(Book book) {
		if (book == null) {
			return false;
		}
		final SeriesInfo info = book.getSeriesInfo();
		return info != null && Series.equals(info.Title);
	}

	@Override
	protected String getSortKey() {
		return " Series:" + super.getSortKey();
	}

	@Override
	public Status getOpeningStatus() {
		return Status.ALWAYS_RELOAD_BEFORE_OPENING;
	}

	@Override
	public void waitForOpening() {
		clear();
		for (Book book : Collection.booksForSeries(Series)) {
			createBookInSeriesSubTree(book);
		}
	}

	@Override
	public boolean onBookEvent(BookEvent event, Book book) {
		switch (event) {
			case Added:
				return containsBook(book) && createBookInSeriesSubTree(book);
			case Removed:
				// TODO: implement
			case Updated:
				// TODO: implement
			default:
				return super.onBookEvent(event, book);
		}
	}

	boolean createBookInSeriesSubTree(Book book) {
		final BookInSeriesTree temp = new BookInSeriesTree(Collection, book);
		int position = Collections.binarySearch(subTrees(), temp);
		if (position >= 0) {
			return false;
		} else {
			new BookInSeriesTree(this, book, - position - 1);
			return true;
		}
	}
}
