package org.gamesbyangelina.advoo;

import java.util.Comparator;

/*
 * Comparator classes are fun! Implement the interface for a given type, and then Override
 * the compare() method. This lets you use Collections.sort(List, Comparator) to sort a list
 * using your defined way of comparing. Very useful for fitness functions.
 * 
 * In ANGELINA, I sometimes use multiple different fitness functions, so comparators let me
 * swap them in and out as I wish.
 */
public class MapComparator implements Comparator<AMap> {

	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 * 
	 * Make SURE you read the description of this method. Every time I implement this
	 * I forget which way around I want it. Returning -1 means map1 should appear
	 * earlier in the list than map2. Returning 1 means the opposite.
	 */
	@Override
	public int compare(AMap map1, AMap map2){
		if(map1.length < map2.length)
			return 1;
		else if(map1.length == map2.length)
			return 0;
		else
			return -1;
		
	}

}
