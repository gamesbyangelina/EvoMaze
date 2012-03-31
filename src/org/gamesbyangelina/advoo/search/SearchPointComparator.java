package org.gamesbyangelina.advoo.search;

import java.util.Comparator;

/*
 * Comparator classes are fun! Implement the interface for a given type, and then Override
 * the compare() method. This lets you use Collections.sort(List, Comparator) to sort a list
 * using your defined way of comparing. Very useful for fitness functions.
 * 
 * In ANGELINA, I sometimes use multiple different fitness functions, so comparators let me
 * swap them in and out as I wish.
 */
public class SearchPointComparator implements Comparator<SearchPoint> {

	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 * 
	 * Make SURE you read the description of this method. Every time I implement this
	 * I forget which way around I want it. Returning -1 means p1 should appear
	 * earlier in the list than p2. Returning 1 means the opposite.
	 */
	@Override
	public int compare(SearchPoint p1, SearchPoint p2){
		if(p1.gvalue + p1.hvalue > p2.gvalue + p2.hvalue)
			return 1;
		else if(p1.gvalue + p1.hvalue == p2.gvalue + p2.hvalue)
			return 0;
		else
			return -1;
				
	}

}
