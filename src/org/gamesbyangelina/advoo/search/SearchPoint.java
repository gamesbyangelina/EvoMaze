package org.gamesbyangelina.advoo.search;

import java.awt.Point;

/*
 * Little helper class used for search. Extends the basic point from Java AWT (!) but adds in
 * a few convenience fields to make A* search a little cleaner in the main code.
 */
public class SearchPoint extends Point implements Comparable<SearchPoint> {

	public SearchPoint parent;
	
	public int hvalue = Integer.MAX_VALUE/2;
	public int gvalue = Integer.MAX_VALUE/2;
	
	public SearchPoint(int x, int y, int tx, int ty){
		super(x,y);
		hvalue = Math.abs(tx - x) + Math.abs(ty - y);
	}

	@Override
	public int compareTo(SearchPoint p){
		if(this.x == p.x && this.y == p.y)
			return 0;
		return -1;
	}
	
	
}
