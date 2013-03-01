package org.gamesbyangelina.advoo;

import java.util.Collections;
import java.util.LinkedList;

import org.gamesbyangelina.advoo.search.SearchPoint;

/*
 * This is the main class where we do all the high-level stuff. You should be able to see some parallels
 * between this and the cake example. I've added a few bells and whistles that I didn't really discuss
 * during the lecture, email me if you want to know more.
 */
public class MapEvolution {
	
	public LinkedList<AMap> population = new LinkedList<AMap>();
	
	/*
	 * Bigger populations take longer, but allow more diversity in the gene pool.
	 */
	public static final int POPULATION_SIZE = 100;
	
	/*
	 * STEADY_STATE = true -> means that we keep the parents for the next generation. This is
	 * useful if you are churning a lot and your crossover methods are unreliable, because it means
	 * you never throw away good solutions.
	 */
	public static final boolean STEADY_STATE = false;
	
	/*
	 * NOVELTY_INJECTION = true -> means that we add new maps to the population at each new generation.
	 * This is useful, particularly in systems that don't use mutation (like this one) to keep the gene
	 * pool fresh and to try and avoid it getting stuck with lots of variations on the same one map.
	 */
	public static final boolean NOVELTY_INJECTION = true;
	
	public static final int MAX_GENERATIONS = 1000;
	public static int GENERATIONS_PASSED = 0;
	
	public static void main(String[] args){
		//Hooray for objects!
		new MapEvolution().evolveMap();
	}

	public void initPopulation(){
		//Step 1 - initialise a population with random solutions
		for(int i=0; i<POPULATION_SIZE; i++){
			population.add(new AMap());
		}
	}
	
	public boolean tickGeneration(){
		return tickGeneration(true);
	}
	
	public boolean tickGeneration(boolean verbose){
		//Step 2 - sort the population based on fitness
		Collections.sort(population, new MapComparator());

		/*
		 * This is debug code, really, but watching how fitness changes over time
		 * can give you vital info on your evolutionary process. You might find that
		 * nothing happens after 500 generations, in which case you're either wasting 
		 * time running your program for longer, or there's a problem with your setup.
		 */
		if(verbose && GENERATIONS_PASSED % 100 == 0){
			System.out.println(population.get(0).length);
			printMap(population.get(0).map);
			printPath(population.get(0));
		}
		
		LinkedList<AMap> nextGeneration = new LinkedList<AMap>();
		
		while(nextGeneration.size() < POPULATION_SIZE){
			AMap parent1 = population.removeFirst();
			AMap parent2 = population.removeFirst();
			
			if(STEADY_STATE){
				nextGeneration.add(parent1);
				nextGeneration.add(parent2);
			}
			
			/*
			 * Mix and match crossover styles as you wish. I tend to blend a few together.
			 */
			nextGeneration.add(parent1.crossoverOnePoint(parent2));
			nextGeneration.add(parent2.crossoverOnePoint(parent1));
			nextGeneration.add(parent1.crossoverViaPointSwap(parent2));
			nextGeneration.add(parent2.crossoverViaPointSwap(parent1));
			nextGeneration.add(parent1.mutate());
			nextGeneration.add(parent2.mutate());
			
			if(NOVELTY_INJECTION){
				//Add in a purely random map, to discourage early convergence.
				nextGeneration.add(new AMap());
				nextGeneration.add(new AMap());
			}
		}
		
		population = nextGeneration;
		GENERATIONS_PASSED++;
		
		return GENERATIONS_PASSED >= MAX_GENERATIONS;
	}
	
	public AMap evolveMap(){
		initPopulation();
		
		for(int i=0; i<MAX_GENERATIONS; i++){
			tickGeneration();
		}
		
		Collections.sort(population, new MapComparator());
		
		System.out.println(population.get(0).length);
		System.out.println(population.get(population.size()-1).length);
		
		this.printMap(population.get(0).map);
		this.printPath(population.get(0));
		
		return population.get(0);
	}
	
	public AMap evolveMap(boolean verbose){
		initPopulation();
		
		for(int i=0; i<MAX_GENERATIONS; i++){
			tickGeneration(verbose);
		}
		
		Collections.sort(population, new MapComparator());
		
		if(verbose){
			System.out.println(population.get(0).length);
			System.out.println(population.get(population.size()-1).length);
			
			this.printMap(population.get(0).map);
			this.printPath(population.get(0));
		}
		
		return population.get(0);
	}
	
	private void printPath(AMap aMap){
		System.out.println("---");
		for(int i=0; i<aMap.map.length; i++){
			for(int j=0; j<aMap.map[0].length; j++){
				if(aMap.path.contains(new SearchPoint(i, j, -1, -1))){
					System.out.print("? ");
				}
				else
					System.out.print(aMap.map[i][j] ? "O " : "X ");
			}
			System.out.println();
		}
		System.out.println("---");
	}

	private void printMap(boolean[][] map){
		System.out.println("---");
		for(int i=0; i<map.length; i++){
			for(int j=0; j<map[0].length; j++){
				System.out.print(map[i][j] ? "O " : "X ");
			}
			System.out.println();
		}
		System.out.println("---");
	}

	public AMap getBestMapSoFar() {
		return population.get(0);
	}

	public boolean evolutionFinished() {
		return MAX_GENERATIONS <= GENERATIONS_PASSED;
	}

}
