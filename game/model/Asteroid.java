package edu.uchicago.cs.java.finalproject.game.model;


import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;

import edu.uchicago.cs.java.finalproject.controller.Game;

public class Asteroid extends Sprite {

	
	private int nSpin;
	private int nSize;
	
	//radius of a large asteroid
	private final int RAD = 100;
	
	//nSize determines if the Asteroid is Large (0), Medium (1), or Small (2)
	//when you explode a Large asteroid, you should spawn 2 or 3 medium asteroids
	//same for medium asteroid, you should spawn small asteroids
	//small asteroids get blasted into debris
	public Asteroid(int nSize){
		
		//call Sprite constructor
		super();
		this.setSize(nSize);
		
		setColor(Color.GRAY);
		
		
		//the spin will be either plus or minus 0-9
		int nSpin = Game.R.nextInt(10);
		if(nSpin %2 ==0)
			nSpin = -nSpin;
		setSpin(nSpin);
			
		//random delta-x
		int nDX = Game.R.nextInt(10);
		if(nDX %2 ==0)
			nDX = -nDX;
		setDeltaX(nDX);
		
		//random delta-y
		int nDY = Game.R.nextInt(10);
		if(nDY %2 ==0)
			nDY = -nDY;
		setDeltaY(nDY);
			
		assignRandomShape();
		
		//an nSize of zero is a big asteroid
		//a nSize of 1 or 2 is med or small asteroid respectively
		if (nSize == 0)
			setRadius(RAD);
		else
			setRadius(RAD/(nSize * 2));
		

	}


	//overridden
	public void move(){
		super.move();
		
		//an asteroid spins, so you need to adjust the orientation at each move()
		setOrientation(getOrientation() + getSpin());
		
	}

	public int getSpin() {
		return this.nSpin;
	}
	

	public void setSpin(int nSpin) {
		this.nSpin = nSpin;
	}
	
	//this is for an asteroid only
	  public void assignRandomShape ()
	  {
	    int nSide = Game.R.nextInt( 7 ) + 7;
	    int nSidesTemp = nSide;

	    int[] nSides = new int[nSide];
	    for ( int nC = 0; nC < nSides.length; nC++ )
	    {
	      int n = nC * 48 / nSides.length - 4 + Game.R.nextInt( 8 );
	      if ( n >= 48 || n < 0 )
	      {
	        n = 0;
	        nSidesTemp--;
	      }
	      nSides[nC] = n;
	    }

	    Arrays.sort( nSides );

	    double[]  dDegrees = new double[nSidesTemp];
	    for ( int nC = 0; nC <dDegrees.length; nC++ )
	    {
	    	dDegrees[nC] = nSides[nC] * Math.PI / 24 + Math.PI / 2;
	    }
	   setDegrees( dDegrees);
	   
		double[] dLengths = new double[dDegrees.length];
			for (int nC = 0; nC < dDegrees.length; nC++) {
				if(nC %3 == 0)
				    dLengths[nC] = 1 - Game.R.nextInt(40)/100.0;
				else
					dLengths[nC] = 1;
			}
		setLengths(dLengths);

	  }
	  
	public int points(){
		switch (getSize()) {
		case 0:
			return 100;
			//break;
		case 1:
			return 50;
			//break;
		default:
			break;
		}
		return 10;
		
	}


	public int getSize() {
		return nSize;
	}


	public void setSize(int nSize) {
		this.nSize = nSize;
	}
	
	public void draw(Graphics g) {
		super.draw(g);
		g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
	}

}
