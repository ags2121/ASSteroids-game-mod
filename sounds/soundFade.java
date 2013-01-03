package edu.uchicago.cs.java.finalproject.sounds;

import edu.uchicago.cs.java.finalproject.controller.Game;
import edu.uchicago.cs.java.finalproject.game.model.CommandCenter;

public class soundFade implements Runnable{

	@Override
	public void run() {
		
		    CommandCenter.setFading(true);   // prevent running twice on same sound
		    
		    if (CommandCenter.getCurrDB() > CommandCenter.getTargetDB() ) {
		        while (CommandCenter.getCurrDB() > CommandCenter.getTargetDB() ) {
		        	
		            CommandCenter.setCurrDB( CommandCenter.getCurrDB() - .5F);
		            
		            Game.getThrustGain().setValue(CommandCenter.getCurrDB());
		            
		            try {Thread.sleep(10);} catch (Exception e) {}
		        }
		    }
		    else if (CommandCenter.getCurrDB() < CommandCenter.getTargetDB() ) {
		        while (CommandCenter.getCurrDB() < CommandCenter.getTargetDB() ) {
		        	
		        	 CommandCenter.setCurrDB( CommandCenter.getCurrDB() + .5F);
		        	 
			         Game.getThrustGain().setValue(CommandCenter.getCurrDB());
		            
		            try {Thread.sleep(10);} catch (Exception e) {}
		        }
		    }
		    
		    CommandCenter.setFading(false);
		    CommandCenter.setCurrDB( CommandCenter.getTargetDB() );  // now sound is at this volume level
		    
	}

}
