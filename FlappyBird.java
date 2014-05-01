import java.awt.*;
import java.util.*;
import java.io.*;
import java.lang.*;

class FlappyBird
{
  static Bird bird;
  
  static int numPipes=3;
  static Pipe[]pipes;
  
  static int score;
  static Parallax background;
  static Parallax city;
  static Parallax trees;
  static Parallax front;
  
  public static void main(String[]args)
  {
    setScale();
    StdAudio.loop("back2.wav");
    
    try
    {
      Font gameFont=Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("gamefont.ttf"));
      gameFont=gameFont.deriveFont(25f);
      StdDraw.setFont(gameFont);
    }
    catch (Exception e)
    {
      System.out.println(e);
    }
    
    do
    { 
//StdAudio.loop("back2.wav");   
      setUp();
        
      showTitle();
      boolean collision=false;
      
      do 
      {
        drawScreen(collision);
    
        collision=getCollision();
      }while(!collision);
      
      gameOver();
//StdAudio.close();
    }while(true);
  }
  
  public static void setUp()//reset all ( & create new objects)
  {     
    //StdAudio.loop("back.wav");
//    StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 25));

    score=0;
    
    bird=new Bird();
    pipes=new Pipe[numPipes];
    
    for(int i=0; i<numPipes; i++)
      pipes[i]=new Pipe(i+1);
      
   // background=new Parallax("bglong.png", 2/504.0*900, 2, 0, -0.01);
    city=new Parallax("city.png", 1, 1, -0.5, -0.01);
    trees=new Parallax("trees.png", 0.5, 0.8, -0.6, -0.015);
    front=new Parallax("ground.png", 0.7, 0.1, -0.95, -0.025);
  }
  
  public static void setScale()
  {
    StdDraw.setXscale(-1*10/11.0, 1*10/11.0);//stdDraw automatically adds 10% border
    StdDraw.setYscale(-1*10/11.0, 1*10/11.0);
  }
  
  public static void showTitle()//title screen until space
  {
    char keyTyped='.';

    do
    {
      StdDraw.setPenColor(StdDraw.WHITE);
      StdDraw.filledSquare(0,0,1);
      
      StdDraw.setPenColor(StdDraw.BLACK);
      StdDraw.text(0,0,"press space to start");
      StdDraw.show(20);
      
      if(StdDraw.hasNextKeyTyped()){
        keyTyped=StdDraw.nextKeyTyped();
        
        if(keyTyped=='q'||keyTyped=='Q')
          System.exit(0);
      }
        
    }while(keyTyped!=' ');
  }
  
  public static void gameOver()//end screen until space
  {  
    StdDraw.setPenColor();
    StdDraw.picture(0,0.5,"gameover.png");
    
    String highscore=highscore();
    StdDraw.picture(0,0,"gameoverbg2.png");
     
  //  StdDraw.text(0,0.5,"Highscore: "+highscore);
      
 //   StdDraw.text(0,0,"GAME OVER  you scored "+score+" press space");
      
    double medalX=-0.25;
    double medalY=-0.03;
      
    //calculate medal
    if(score>=40)       StdDraw.picture(medalX, medalY,"platinum.png");
    else if (score>=30) StdDraw.picture(medalX, medalY,"gold.png");
    else if (score>=20) StdDraw.picture(medalX, medalY,"silver.png");
    else if (score>=10) StdDraw.picture(medalX, medalY,"bronze.png");
    
    StdDraw.textRight(0.36,0.04,""+score);
    StdDraw.textRight(0.36,-0.1,""+highscore);
    
    StdDraw.show();

    try
    {
      Thread.currentThread().sleep(1000);
    }
    catch(InterruptedException ie){}
   //  System.wait(1000);
    
    char keyTyped='.';

    do
    { 
      if(StdDraw.hasNextKeyTyped())
      {
        keyTyped=StdDraw.nextKeyTyped();
        
        if(keyTyped=='q'||keyTyped=='Q')
          System.exit(0);
      }      
    }while(keyTyped!=' ');
  }
  
  public static String highscore()//gets highscore or saves current score as highscore
  {
    String highscore;
    try
    {
      Scanner sc = new Scanner(new File("score.txt"));//get highscore from file
      highscore=sc.next();
      sc.close();
    }
    catch(FileNotFoundException e)//first time playing
    {
      highscore="0";
    }
    
    if(score>Integer.parseInt(highscore))//if better score, write new score to file
    {
      highscore=""+score;
      
      try
      {
        PrintWriter pr=new PrintWriter(new File("score.txt"));
        pr.print(highscore);
        pr.close();
      }
      catch (FileNotFoundException e)
      {
        System.out.println(e);
      }
    }
    
    return highscore;
  }
  
  public static void drawScreen(boolean hit)//draw all elements on screen
  {
    checkKeyboard(hit);
      
    drawBackground();
    drawPipes();
    drawFront();
    drawBird(hit);   
    
    if(!hit)
    {
      checkPoints();
      showScore();
    }

    StdDraw.show(20);
  }
  
  public static void drawBackground()//moves and draws background objects
  {  
    StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE ); 
    StdDraw.filledSquare(0, 0, 1); 
 //   background.move();
 //   background.draw();
    
    city.move();
    city.draw();
    
    trees.move();
    trees.draw(); 
  }
  
  public static void drawFront()//moves and draws foregound objects
  { 
    front.move();
    front.draw(); 
  }
  
  public static void checkKeyboard(boolean hit)//check what the user typed
  {
    if(StdDraw.hasNextKeyTyped()&&!hit)
    {
      char keyTyped=StdDraw.nextKeyTyped();
      
      if(keyTyped==' ')
        bird.flap();
      else if(keyTyped=='q'||keyTyped=='Q')
        System.exit(0);
    }
  }
  
  public static void drawPipes()//moves and draws each pipe
  {
    for(int i=0; i<numPipes; i++)
    {
      pipes[i].movePipe();
      pipes[i].draw();
    }
  }
  
  public static void checkPoints()//check if user scored
  {
    for(int i=0; i<numPipes; i++)
    {
      if(pipes[i].getPoint(bird.getPos()))
      {
        score++;
        StdAudio.play("point.wav");
      }
    }
  }
  
  public static void drawBird(boolean hit)//moves and draws bird
  {
    bird.moveBird();
    bird.drawBird(hit);
  }
  
  public static void showScore()//displays score
  {
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.text(0,0.8,""+score);
  }

  public static boolean getCollision()//check if collision has happened
  {
    boolean collision=false;
    
    if( bird.getBottomCollision())//bird hit bottom
    {
      bird.reverse();
      collision = true;
      drawScreen(collision);
      
      StdAudio.play("die.wav");
    }  
    
    for(int i=0; i<numPipes; i++)//if bird hit pipe, then fall to bottom
    {
      if(bird.getPipeCollison(pipes[i].getGap()))
      {
        //stop all objects
        bird.stop();
     //   background.stop();
        trees.stop();
        city.stop();
        front.stop();
        
        for(int j=0; j<numPipes; j++)
          pipes[j].stop();
          
        if(!collision)
        {
          collision = true;
          StdAudio.play("die.wav");
        }
          
        while(!bird.getBottomCollision())  //continue until it has hit the bottom
          drawScreen(collision);
      }
    }
    return collision;
  }
}