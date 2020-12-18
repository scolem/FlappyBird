package model;

import sdk.StdAudio;
import sdk.StdDraw;

public class Bird
{
  // initial values
  private double rx = -0.4, ry = 0.5;     // position
  private double vy = 0.00;                 // velocity
  
  private final double RADIUSX = 0.071;              // radius width
  private final double RADIUSY = 0.05;              // radius height
  
  private final double GRAVITY = -0.005;              // acceleration down
  private final double JUMP_VEL =0.055;              // velocity after jump
  
  private final int FLAP_DELAY=3;              // flap animation delay
  private int animationCount=FLAP_DELAY;

  public Bird()
  {
    
  }
  
  public void drawBird(boolean hit)//draws the bird on the screen
  {  
  //  sdk.StdDraw.setPenColor(sdk.StdDraw.ORANGE);
  //  sdk.StdDraw.filledRectangle(rx, ry, RADIUSX, RADIUSY);
    StdDraw.picture(rx, ry, "clumsy" + getAnimation(hit) + ".png", RADIUSX * 2, RADIUSY * 2, getAngle(hit));
  }
  
  private int getAnimation (boolean hit)//get which frame of amimation to use
  {
    if(hit)return 2;

    animationCount++;
    if(animationCount==FLAP_DELAY*5)
      animationCount=FLAP_DELAY;

    int animation=animationCount/FLAP_DELAY;

    if(animation==4)animation=2;

    return animation;//returns 1, 2, 3, 2, 1, 2, 3, ... and changes every FLAPDELAY frames
  }
  
  private double getAngle(boolean hit)//get angle of bird
  {
    if (hit) return -90;//if bird had collision, face down
    else return (30.0/JUMP_VEL*vy);
  }
  
  public void moveBird()//change the possition of the bird
  {
    vy += GRAVITY;
    ry += vy;
  }
  
  public boolean getBottomCollision()//check if bird has hit the bottom
  {
    return (ry + vy < -0.95);
  }
  
  public boolean getPipeCollison(double [] gap)//check if bird hit pipe
  {
    boolean hit=false;
    
    if(rx+RADIUSX>gap[2]&&rx-RADIUSX<gap[3])//x touching pipe
    {
      if(ry+RADIUSY>gap[0]||ry-RADIUSY<gap[1])//y touching pipe
      {
        hit=true;
      }
    }
    
    return hit;
    //return false;
  }
  
  public void flap()//when space is pressed, set vy and play sound
  {
    vy=JUMP_VEL;
    StdAudio.play("resources/sounds/jump.wav");
  }
  
  public void stop()//bird stops when it hits the pipe
  {
    vy=0;
  }
  
  public double [] getPos()
  {
    return new double [] {rx, ry};
  }
  
  public void reverse()//at colition with ground run this, else bird too low
  {
    ry-=vy;
  }
}
