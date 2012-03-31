package org.gamesbyangelina.advoo;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.gamesbyangelina.advoo.search.SearchPoint;
import org.gamesbyangelina.advoo.search.SearchPointComparator;

public class AMap {
	
	//Feel free to play with these.
	public static final int MAP_WIDTH = 10;
	public static final int MAP_HEIGHT = 10;
	
	public boolean[][] map;
	
	//These can also be fun to play with. Try moving the end co-ordinates into 
	//the middle of the map (or even next to the start!)
	public int start_x = 0;
	public int start_y = 0;
	public int end_x = MAP_WIDTH-1;
	public int end_y = MAP_HEIGHT-1;
	
	public int length = -1;
	public LinkedList<SearchPoint> path = new LinkedList<SearchPoint>();
	
	/*
	 * You often want a few ways of constructing solutions for an evolutionary program.
	 * I use a default one (which generates a random map) and a parameterised one
	 * which is used during crossover, to directly inherit from a parent.
	 */
	public AMap(){
		map = newRandomMap(MAP_WIDTH, MAP_HEIGHT);
		length = calculateLength();
	}
	
	public AMap(AMap master){
		map = new boolean[master.map.length][master.map[0].length];
		for(int i=0; i<map.length; i++){
			for(int j=0; j<map[0].length; j++){
				map[i][j] = master.map[i][j];
			}
		}
		//length = master.length;
	}
	
	/*
	 * Just a loop helper that appeals to newRandomMapTile().
	 */
	private static boolean[][] newRandomMap(int MAP_WIDTH, int MAP_HEIGHT){
		boolean[][] map = new boolean[MAP_WIDTH][MAP_HEIGHT];
		for(int i=0; i<map.length; i++){
			for(int j=0; j<map[0].length; j++){
				map[i][j] = newRandomMapTile();
			}
		}
		return map;
	}

	/*
	 * A note on random map generation:
	 * Initially, I thought that we'd have a 50/50 odds of making a tile blocked or free. However,
	 * like many evolutionary algorithms, the first guess is not the best one. Instead, we make random
	 * maps more empty than full, by weighting against adding in blocked tiles. The reason we do this is
	 * to avoid having lots of random maps which are completely impassable, with no route at all to the end.
	 */
	private static boolean newRandomMapTile(){
		if(Math.random() > 0.75){
			return false;
		}
		return true;
	}
	
	//CROSSOVER METHODS OF VARIOUS KINDS
	
	/*
	 * Point Swap creates a child based on the map contained in <code>this</code>.
	 * It then uses a fixed probability, P, to decide whether a tile in the child should 
	 * instead be inherited from <code>otherParent</code>.
	 */
	public AMap crossoverViaPointSwap(AMap otherParent){
		//Create a copy of ourselves
		AMap child = new AMap(this);
		double swapchance = 0.1;
		
		for(int i=0; i<map.length; i++){
			for(int j=0; j<map[0].length; j++){
				if(Math.random() < swapchance)
					child.map[i][j] = otherParent.map[i][j]; 
			}
		}
		
		child.length = child.calculateLength();
		
		return child;
	}
	
	/*
	 * One-Point crossover chooses an arbitrary point in the map. For all
	 * elements occuring before this point (reading left-right, top-down),
	 * take the map entry from <code>this</code> and add to child. For all
	 * elements after this point, take the map entry from <code>otherParent</code>. 
	 */
	public AMap crossoverOnePoint(AMap otherParent){
		//Create a copy of ourselves
		AMap child = new AMap(this);
		
		int swap = (int) Math.random() * (MAP_HEIGHT * MAP_WIDTH);
		for(int i=0; i<map.length; i++){
			for(int j=0; j<map[0].length; j++){
				if(swap > 0)
					swap--;
				else{
					child.map[i][j] = otherParent.map[i][j];
				}
			}
		}
		child.length = child.calculateLength();
		
		return child;
	}
	
	/*
	 * Row-column crossover, something I've used myself in the past but not recommended
	 * in general. 0.3 chance of taking a column or row from <code>otherParent</code>
	 * rather than <code>this</code>.
	 */
	public AMap crossoverViaRowColumnSwap(AMap otherParent){
		//Create a copy of ourselves
		AMap child = new AMap(this);
		
		if(Math.random() < 0.5){
			for(int i=0; i<map.length; i++){
				//0.3 chance of taking a column from the other parent
				if(Math.random() < 0.3){
					for(int j=0; j<map[0].length; j++){
						child.map[i][j] = otherParent.map[i][j]; 
					}
				}
			}
		}
		else{
			for(int i=0; i<map[0].length; i++){
				//0.3 chance of taking a row from the other parent
				if(Math.random() < 0.3){
					for(int j=0; j<map.length; j++){
						child.map[j][i] = otherParent.map[j][i]; 
					}
				}
			}
		}
		
		child.length = child.calculateLength();
		
		return child;
	}
	
	//A* SEARCH CODE AND HELPER METHODS
	
	/*
	 * Calculates the length from start_x/y to end_x/y.
	 * Uses A* search, a common but complex search algorithm
	 * Feel free to treat as a black box, but I've liberally commented it.
	 */
	private int calculateLength(){
		if(!map[start_x][start_y] || !map[end_x][end_y])
			return Integer.MIN_VALUE;
		
		//We're working a bit dirtily here, I shouldn't be building another map just to search it
		//But it keeps the top-level representation as just a boolean[][], which is easier to talk about.
		SearchPoint[][] abstractMap = new SearchPoint[map.length][map[0].length];
		for(int i=0; i<abstractMap.length; i++){
			for(int j=0; j<abstractMap[0].length; j++){
				abstractMap[i][j] = new SearchPoint(i,j, end_x, end_y);
			}
		}
		
		//A* Search
		//Great tutorial on this here: http://www.policyalmanac.org/games/aStarTutorial.htm
		LinkedList<SearchPoint> openList = new LinkedList<SearchPoint>();
		LinkedList<SearchPoint> closedList = new LinkedList<SearchPoint>();
		SearchPointComparator sorter = new SearchPointComparator();
		
		//Add the legal neighbours of the starting point to the openlist.
		SearchPoint start = abstractMap[start_x][start_y];
		//The g-value is the cost-to-start, which is obviously zero here.
		start.gvalue = 0;
		
		List<SearchPoint> initialPoints = getLegalAdjacents(start, abstractMap);
		
		//Each point has a 'parent', representing the quickest next step back to the start.
		for(SearchPoint ip : initialPoints){
			ip.gvalue = 1;
			ip.parent = start;
		}
		
		openList.addAll(initialPoints);
		closedList.add(start);
		
		//While there are points left to check
		while(openList.size() > 0){
			//Sort the list so we get the lowest F-Value (lowest estimated cost to goal)
			Collections.sort(openList, sorter);
			//Drop it from the openlist to the closedlist
			SearchPoint bestNext = openList.remove(0);
			closedList.add(bestNext);
			
			//TERMINATION CONDITION - if we just added the target square to the closedlist, we're done.
			if(bestNext.equals(abstractMap[end_x][end_y])){
				int res = bestNext.gvalue;
				path = new LinkedList<SearchPoint>();
				do{
					path.add(new SearchPoint(bestNext.x, bestNext.y, -1, -1));
					bestNext = bestNext.parent;
				}
				while(bestNext.parent != null);
				//The length of the map is just the gvalue of this tile.
				return path.size();
			}
			
			//Get all legal neighbours
			List<SearchPoint> adjacents = getLegalAdjacents(bestNext, abstractMap);
			for(SearchPoint sp : adjacents){
				//Ignore anything we've already closed off
				if(closedList.contains(sp))
					continue;
				//If it's not on the openlist, add it and make us its parent.
				if(!openList.contains(sp)){
					openList.add(sp);
					sp.parent = bestNext;
					sp.gvalue = bestNext.gvalue + 1;
				}
				//Do we offer a better way of reaching this tile than it currently has?
				else if(bestNext.gvalue + 1 < sp.gvalue){
					sp.parent = bestNext;
					sp.gvalue = bestNext.gvalue + 1;
				}
			}
		}
		
		//If we're here, the openList was empty, so the target is unreachable.
		return Integer.MIN_VALUE;
	}

	public List<SearchPoint> getLegalAdjacents(SearchPoint p, SearchPoint[][] smap){
		LinkedList<SearchPoint> nbs = new LinkedList<SearchPoint>();
		if(p.x > 0)
			addSearchPointIfLegal(nbs, smap[p.x-1][p.y]);
		if(p.x < map.length-1)
			addSearchPointIfLegal(nbs, smap[p.x+1][p.y]);
		if(p.y > 0)
			addSearchPointIfLegal(nbs, smap[p.x][p.y-1]);
		if(p.y < map[0].length-1)
			addSearchPointIfLegal(nbs, smap[p.x][p.y+1]);
		
		return nbs;
	}

	private void addSearchPointIfLegal(LinkedList<SearchPoint> nbs, SearchPoint point){
		if(map[point.x][point.y])
			nbs.add(point);
	}
	
}
