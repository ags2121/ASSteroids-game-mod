package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import edu.uchicago.cs.java.finalproject.controller.Game;

public class Debris extends Sprite {
	
	private static final int MAX_EXP = 20;
	private ArrayList<DebrisChunk> debChunks;
	
	public Debris(Sprite source){
		super();
		//change to double?
		double dLength = 15.0;
		int nChunkNumber = source.getLengths().length+20;
		
		setColor(Color.WHITE);
		
		double start = 360.0/(double)nChunkNumber;
		double dSpeed = 10.0;
		
		debChunks = new ArrayList<DebrisChunk>();
		
		setCenter(source.getCenter());
		setDeltaX(source.getDeltaX());
		setDeltaY(source.getDeltaY());
		
		for (int i = 0; i < nChunkNumber; i++) {
			
			double nDeltaX = (dSpeed*Math.cos(Math.toRadians(start*i)));
			double nDeltaY = (dSpeed*Math.sin(Math.toRadians(start*i)));
			
			DebrisChunk dc = new DebrisChunk(nDeltaX, nDeltaY, Game.R.nextInt(360), 1, dLength); 
			debChunks.add(dc);
		}
		
		setExpire(MAX_EXP);
		
	}
	

	
	
	public void move(){
		super.move();
		for (DebrisChunk dc : debChunks) {
			dc.move();
		}
		
	}
	
	@Override
	public void fadeInOut() {
	
		Color col = getColor();
		int nR = col.getRed();
		int nG = col.getGreen();
		int nB = col.getBlue();
		
		if(nR - 255/MAX_EXP >= 0){
		setColor(new Color(nR - 255/MAX_EXP, nG - 255/MAX_EXP, nB -255/MAX_EXP));
		}
		
	}


	public void draw(Graphics g){
		g.setColor(getColor());
		for (DebrisChunk dc : debChunks) {
			dc.draw(g);
		}
		
	}
	
	//inner class DebrisChunk
	public class DebrisChunk {
		
		private Point center;
		private double vX;
		private double vY;
		private int orientation;
		private int spin;
		private double length;
		
		public DebrisChunk(double nDeltaX, double nDeltaY, int nOrient, int nSpin, double length){
			center = new Point(getCenter().x, getCenter().y);
			vX = nDeltaX;
			vY = nDeltaY;
			this.spin = nSpin; 
			this.orientation = nOrient;  
			this.length = length;  	
//			System.out.printf("DC %g, %g, %d, %d, %d\n", vX, vY, spin, orientation, length);
		}
		
		public void move(){
			center.x += vX;
			center.y += vY;
			orientation = (orientation + spin) % 360;
	 	}
		
		public void draw(Graphics g){
			Point p1 = new Point();
			Point p2 = new Point();
			
			p1.x = (int) (center.x+(length/2)*Math.cos(Math.toRadians(1.0*orientation)));
			p1.y = (int) (center.y+(length/2)*Math.sin(Math.toRadians(1.0*orientation)));
			p2.x = (int) (center.x-(length/2)*Math.cos(Math.toRadians(1.0*orientation)));
			p2.y = (int) (center.y-(length/2)*Math.sin(Math.toRadians(1.0*orientation)));
			
			Graphics2D graphics2d = (Graphics2D)g;
			
			graphics2d.drawLine(p1.x, p1.y, p2.x, p2.y);
		}
		
	}
	

}
