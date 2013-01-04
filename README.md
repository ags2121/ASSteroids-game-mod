ASSteroids-game-mod
===================

Asteroids game mod written in Java.

This is a simple game mod I developed for a final project from an existing code base. It's based on the classic Asteroids game where the objective is to destroy as many asteroids without losing all your ships.

I implemented: 

-Score
-5 levels, where each level's difficulty is determined by an increasing amount of asteroids. A game is won if all 5 levels are cleared. 
-Sound effects
  *Distinct sounds for: losing a ship; gaining a ship; winning; losing; exploding asteroids; engaging ship thrusters
-Debris animation
-Collision detection 
-Backwards shooting bullets 
-High score
	*Every time the application is fired up, high score is reset to 0. Your high score is only maintained for a single game-playing session.

Game instructions are explained on the welcome screen. 

*Something I'm having trouble with: I have a global private variable in the Game class, thrustGain, which is initialized to
be the gain control for the Thrust audio clip (initialized in line 67). I wanted control over the gain because I wanted
to create a smooth fade out whenever the user ceases to engage the ship's thrusters. So, in line 419, I have a method that
gradually fades down thrustGain whenever the up arrow is released. It doesn't seem to work as I can still hear the annoying 
clipping that happens when the sample stops abruptly. Any help would be really appreciated!

