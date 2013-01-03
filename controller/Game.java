package edu.uchicago.cs.java.finalproject.controller;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Control;

import edu.uchicago.cs.java.finalproject.game.model.*;
import edu.uchicago.cs.java.finalproject.game.view.*;
import edu.uchicago.cs.java.finalproject.sounds.Sound;

// ===============================================
// == This Game class is the CONTROLLER

// ===============================================

public class Game implements Runnable, KeyListener {

	// ===============================================
	// FIELDS
	// ===============================================

	private static final int NUM_ASTEROID = 5;
	public static final Dimension DIM = new Dimension(810, 700);
	private GamePanel gmpPanel;
	public static Random R = new Random();
	public final static int ANI_DELAY = 45; // milliseconds between screen
											// updates (animation)
	private Thread thrAnim;
	private int nLevel = 1;
	private int nTick = 0;
	
	private final int PAUSE = 80, // p key
			QUIT = 81, // q key
			LEFT = 37, // rotate left
			RIGHT = 39, // rotate right
			UP = 38, // thrust
			START = 83, // s key
			FIRE = 32, // space key
			BACKFIRE = 65, //a
			GRENADE = 79; //o key

	// for possible future use
	// HYPER = 68, 					// d key
	// SHIELD = 65, 				// a key arrow
	// NUM_ENTER = 10, 				// hyper speed
	// KILL = 75; 					// for testing explode and debugging

	private Clip clpThrust;
	private Clip clpMusicBackground;

	private static final int SPAWN_NEW_SHIP_FLOATER = 250;
	private static boolean isLooping;
	private static FloatControl thrustGain;

	// ===============================================
	// ==CONSTRUCTOR
	// ===============================================

	public Game() {
		CommandCenter.setFirstGame(true);
		gmpPanel = new GamePanel(DIM);
		gmpPanel.addKeyListener(this);

		clpThrust = Sound.clipForLoopFactory("THRUST.wav");
		
		//test to see what kind of controls clip contains
		for(Control cType : clpThrust.getControls()){
			System.out.println(cType.toString());
		}
		
		setThrustGain((FloatControl) clpThrust.getControls()[0]);
		
//		clpMusicBackground = Sound.clipForLoopFactory("music-background.wav");

	}

	// ===============================================
	// ==METHODS
	// ===============================================

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() { // uses the Event dispatch thread from Java 5 (refactored)
					public void run() {
						try {
							Game game = new Game(); // construct itself
							game.fireUpAnimThread();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void fireUpAnimThread() { // called initially
		if (thrAnim == null) {
			thrAnim = new Thread(this); // pass the thread a runnable object (this)
			thrAnim.start();
		}
	}

	// implements runnable - must have run method
	public void run() {

		// lower this thread's priority; let the "main" aka 'Event Dispatch'
		// thread do what it needs to do first
		thrAnim.setPriority(Thread.MIN_PRIORITY);

		// and get the current time
		long lStartTime = System.currentTimeMillis();

		// this thread animates the scene
		while (Thread.currentThread() == thrAnim) {
			tick();
			spawnNewShipFloater();
			gmpPanel.update(gmpPanel.getGraphics()); // update takes the graphics context we must 
														// surround the sleep() in a try/catch block
														// this simply controls delay time between 
														// the frames of the animation

			//this might be a good place to check for collisions
			checkCollisions();
			

			//this might be a good place to check if the level is clear (no more foes)
			//if the level is clear then spawn some big asteroids -- the number of asteroids 
			//should increase with the level.
			if(CommandCenter.movFoes.size() == 0){
				if(CommandCenter.getLevel()==4){
					littleAsteriods(50);
					CommandCenter.setLevel(5);
				}
				
				else if(CommandCenter.getLevel()==5){
					CommandCenter.setWin(true);
					CommandCenter.setPlaying(false);
					
				}
				
				else{
					spawnAsteriods(NUM_ASTEROID+CommandCenter.getLevel());
					CommandCenter.setLevel(CommandCenter.getLevel()+1);
				}
			}
			

			try {
				// The total amount of time is guaranteed to be at least ANI_DELAY long.  If processing (update) 
				// between frames takes longer than ANI_DELAY, then the difference between lStartTime - 
				// System.currentTimeMillis() will be negative, then zero will be the sleep time
				lStartTime += ANI_DELAY;
				Thread.sleep(Math.max(0,
						lStartTime - System.currentTimeMillis()));
			} catch (InterruptedException e) {
				// just skip this frame -- no big deal
				continue;
			}
		} // end while
	} // end run
	
	

	private void checkCollisions() {

		//@formatter:off
		//for each friend in movFriends
			//for each foe in movFoes
				//if the distance between the two centers is less than the sum of their radii
					//remove it.
		//@formatter:on

		Point pntFriendCenter, pntFoeCenter;
		int nFriendRadiux, nFoeRadiux;

		for (Movable movFriend : CommandCenter.movFriends) {
			for (Movable movFoe : CommandCenter.movFoes) {

				pntFriendCenter = movFriend.getCenter();
				pntFoeCenter = movFoe.getCenter();
				nFriendRadiux = movFriend.getRadius();
				nFoeRadiux = movFoe.getRadius();

				//detect collision 
				if (pntFriendCenter.distance(pntFoeCenter) < (nFriendRadiux + nFoeRadiux)) {

					//explode/remove friend (for testing: Falcon not yet implemented)
					if (!(movFriend instanceof Falcon)){
						CommandCenter.movFriends.remove(movFriend);
						Debris db = new Debris((Sprite)movFoe);
						CommandCenter.movDebris.add(db);
						
						if(((Asteroid) movFoe).getSize()==0){
							Sound.playSound("grenadesound.wav");
						}
						else{
							Sound.playSound("smallExplosion.wav");
						}

						//explode/remove foe/get points
						CommandCenter.setScore(CommandCenter.getScore()+movFoe.points());
						CommandCenter.movFoes.remove(movFoe);
						if(CommandCenter.getLevel()==5 && CommandCenter.movFoes.size()==0){
							CommandCenter.setScore(CommandCenter.getScore()+(long)CommandCenter.getNumFalcons()*100);
							Sound.playSound("FemaleOrgasmGame.wav");
						}
					}
					
					if(movFriend instanceof Falcon){
						if(!((Falcon)movFriend).getProtected()){
				
						Debris db2 = new Debris((Sprite)movFriend);
						CommandCenter.movDebris.add(db2);
						
						CommandCenter.movFriends.remove(movFriend);
						clpThrust.stop();
						CommandCenter.spawnFalcon(false);
						Sound.playSound("fart.wav");
						}
					}
					
				}//end if 
			}//end inner for
		}//end outer for


		//check for collisions between falcon and floaters
		if (CommandCenter.getFalcon() != null){
			Point pntFalCenter = CommandCenter.getFalcon().getCenter();
			int nFalRadiux = CommandCenter.getFalcon().getRadius();
			Point pntFloaterCenter;
			int nFloaterRadiux;
			
			for (Movable movFloater : CommandCenter.movFloaters) {
				pntFloaterCenter = movFloater.getCenter();
				nFloaterRadiux = movFloater.getRadius();
	
				//detect collision
				if (pntFalCenter.distance(pntFloaterCenter) < (nFalRadiux + nFloaterRadiux)) {
					
					CommandCenter.movFloaters.remove(movFloater);
					CommandCenter.setNumFalcons(CommandCenter.getNumFalcons()+1);
					//get 10 points for a floater
					CommandCenter.setScore(CommandCenter.getScore()+10);
					Sound.playSound("pacman_eatghost.wav");
	
				}//end if 
			}//end inner for
		}//end if not null
	}//end meth

	//some methods for timing events in the game,
	//such as the appearance of UFOs, floaters (power-ups), etc. 
	public void tick() {
		if (nTick == Integer.MAX_VALUE)
			nTick = 0;
		else
			nTick++;
	}


	public int getTick() {
		return nTick;
	}

	private void spawnNewShipFloater() {
		//make the appearance of power-up dependent upon ticks and levels
		//the higher the level the more frequent the appearance
		if (nTick % (SPAWN_NEW_SHIP_FLOATER - nLevel * 7) == 0 && CommandCenter.isPlaying()) {
			CommandCenter.movFloaters.add(new NewShipFloater());
		}
	}

	// Called when user presses 's'
	private void startGame() {
		CommandCenter.clearAll();
		CommandCenter.initGame();
		
		if(CommandCenter.getNewHighScore() != 0){
			CommandCenter.setHighScore(CommandCenter.getNewHighScore());
		}

		// Add 5 random asteroids to the game.  You will want to vary the number of asteroids after each level
		// clear to get progressively harder.  I've started here with 5, but you can start with more or less
		spawnAsteriods(NUM_ASTEROID);

		CommandCenter.setPlaying(true);
		CommandCenter.setWin(false);
		CommandCenter.setPaused(false);
		CommandCenter.setFart(true);
		CommandCenter.setFirstGame(false);
		isLooping = true;
		//clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
	}

	public void spawnAsteriods(int nNum) {
		for (int nC = 0; nC < nNum; nC++) {
			CommandCenter.movFoes.add(new Asteroid(randBetween(0,2)));
		}
	}
	public void littleAsteriods(int nNum) {
		for (int nC = 0; nC < nNum; nC++) {
			CommandCenter.movFoes.add(new Asteroid(2));
		}
	}

	// Varargs for looping music clips
	private static void stopLoopingSounds(Clip... clpClips) {
		for (Clip clp : clpClips) {
			clp.stop();
		}
	}

	// ===============================================
	// KEYLISTENER METHODS
	// ===============================================

	@Override
	public void keyPressed(KeyEvent e) {
		Falcon fal = CommandCenter.getFalcon();
		int nKey = e.getKeyCode();
//		 System.out.println(nKey);
		
		if (nKey == START && !CommandCenter.isPlaying())
			startGame();
		
		if (nKey == QUIT && !CommandCenter.isPlaying())
			System.exit(0);
		
		if (fal != null) {

			switch (nKey) {
			case PAUSE:
				CommandCenter.setPaused(!CommandCenter.isPaused());
				if (CommandCenter.isPaused())
					stopLoopingSounds(clpMusicBackground, clpThrust);
				else
					clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
				break;
			case QUIT:
				System.exit(0);
				break;
			case UP:
				fal.thrustOn();
				if (CommandCenter.isPlaying() && isLooping){
					clpThrust.setFramePosition(0);
					thrustGain.setValue(0.0F);
					clpThrust.loop(Clip.LOOP_CONTINUOUSLY);
//					fadeOut(0.0F);
					isLooping = false;
				}
				break;
			case LEFT:
				fal.rotateLeft();
				break;
			case RIGHT:
				fal.rotateRight();
				break;
			case GRENADE:
				Sound.playSound("testaud.wav");
				break;
			// possible future use
			// case KILL:
			// case SHIELD:
			// case NUM_ENTER:

			default:
				break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		Falcon fal = CommandCenter.getFalcon();
		int nKey = e.getKeyCode();
		// System.out.println(nKey);

		if (fal != null) {
			switch (nKey) {
			case FIRE:
				CommandCenter.movFriends.add(new Bullet(fal));
				Sound.playSound("laser.wav");
				break;
			case BACKFIRE:
				CommandCenter.movFriends.add(new BackBullet(fal));
				Sound.playSound("laser2.wav");
				break;
			case LEFT:
				fal.stopRotating();
				break;
			case RIGHT:
				fal.stopRotating();
				break;
			case UP:
				fal.thrustOff();
				fadeOut(-80.0F);
				isLooping = true;
				break; 
			default:
				break;
			}
		}
	}

	@Override
	// Just need it b/c of KeyListener implementation
	public void keyTyped(KeyEvent e) {
	}
	
    public static int randBetween(int start, int end) {
    	return start + (int)Math.round(Math.random() * (end - start));
	}
    
    //Method that's meant to fade the thrust sample's gain gradually. Doesn't seem to work in practice...
    public void fadeOut(float targetDB){
    	
    	
    	float fadePerStep = .1F;   // .1 works for applets, 1 is okay for apps
		float currDB = getThrustGain().getValue();
		
		
		if (currDB > targetDB) {
		
	        while (currDB > targetDB) {
	            currDB -= fadePerStep;
	            tick2();
	            getThrustGain().setValue(currDB);
	        }
		}
		
		else if (currDB < targetDB) {
			
			 while (currDB < targetDB) {
		            currDB += fadePerStep;
		            tick2();
		            getThrustGain().setValue(currDB);
			 }
		}
	    
	}
    
	public void tick2(){
		
		int n = -100;
		int t = 1;
		
		while( (t-n) < 10000){
			t++;
		}
	}
	
	public float doubleToDB(double in){
	double value = (in<=0.0)? 0.0001 : ((in>1.0)? 1.0 : in);
	float dB = (float)(Math.log(value)/Math.log(10.0)*20.0);
	System.out.println(dB);
	return dB;
	}

	public static FloatControl getThrustGain() {
		return thrustGain;
	}

	@SuppressWarnings("static-access")
	public void setThrustGain(FloatControl thrustGain) {
		this.thrustGain = thrustGain;
	}
}
