package org.gamesbyangelina.advoo.vis;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JApplet;
import javax.swing.JFrame;

import org.gamesbyangelina.advoo.AMap;
import org.gamesbyangelina.advoo.MapEvolution;

/*
 * I know as much about java applets as a seal does about international politics,
 * but this should work if you run it as a Java application through Eclipse. 
 */
public class MapVis extends JApplet {

	private static final long serialVersionUID = 3198514399246596255L;
	private static MapEvolution mapEvo = null;
	
	public static final int GRID_SIZE = 32;
	
	/*
	 * Running this class as a Java application will show a neat little evolution
	 * in the visualiser!
	 */
    public static void main(String[] args) {
        JFrame f = new JFrame("Advanced Java - Map Evolution");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        /*
         * Construct + init the Map Evolution
         */
        mapEvo = new MapEvolution();
        mapEvo.initPopulation();
        
        JApplet ap = new MapVis();
        ap.init();
        ap.start();
        f.setMinimumSize(new Dimension((AMap.MAP_WIDTH+2)*GRID_SIZE, (AMap.MAP_HEIGHT+2)*GRID_SIZE));
        f.add("Center", ap);
        f.pack();
        f.setVisible(true);
        
        /*
         * While it's not finished, tick and repaint the canvas.
         */
        while(!mapEvo.evolutionFinished()){
        	for(int i=0; i<10; i++){
        		mapEvo.tickGeneration();
        		ap.repaint();
        	}
        }
        
    }
    
    /*
     * (non-Javadoc)
     * @see java.awt.Container#paint(java.awt.Graphics)
     * 
     * This is where the applet gets the graphics splashed onto the screen.
     * The call to repaint() requests that this method is recalled (among
     * other things).
     */
    public void paint(Graphics _g) {
    	if(mapEvo == null)
    		return;
    	
        Graphics2D g = (Graphics2D) _g;
        boolean[][] map;
        AMap m;
        
        /*
         * synchronized()  is a special keyword in Java that's used for securing concurrent
         * code. This is a little complicated and not needed for now, but here's a rough
         * explanation. We want to get an object out of the MapEvolution object, but we need
         * to make sure no-one tries to change that object when we do it. We declare the code
         * to be synchronized on mapEvo - what that means is that when Java runs the code, it
         * can't try and do anything that might change mapEvo in another thread.
         */
        synchronized (mapEvo) {
        	m = mapEvo.getBestMapSoFar();
        	map = m.map;
		}
        
        //Print out some nice borders, and then the map itself.
        //This is quite messy code, I hacked it together rather quickly for the lecture.
        for(int i=0; i<map.length+2; i++){
			for(int j=0; j<map[0].length+2; j++){
				//Borders
				if(i == 0 || j == 0 || i == map.length+1 || j == map[0].length+1){
					g.setColor(Color.GRAY);
					g.fillRect(i*GRID_SIZE, j*GRID_SIZE, GRID_SIZE, GRID_SIZE);
				}
				else{
					if(i-1 == m.start_x && j-1 == m.start_y)
						g.setColor(Color.GREEN);
					else if(i-1 == m.end_x && j-1 == m.end_y)
						g.setColor(Color.RED);
					else if(map[i-1][j-1])
						g.setColor(Color.WHITE);
					else
						g.setColor(Color.BLACK);
					g.fillRect(i*GRID_SIZE, j*GRID_SIZE, GRID_SIZE, GRID_SIZE);
				}
			}
		}
    }
	
}
