public class Parallax
{
  private String pic;
  private double picWidth, picHeight, posY, vel;
  private int noPics;
  private double [] posX;
    
  public Parallax(String pic, double picWidth, double picHeight, double posY, double vel)
  {
    this.pic=pic;//url of pic
    this.picWidth=picWidth;//width of pic
    this.picHeight=picHeight;//height of pic
    this.posY=posY;//y pos of middle
    this.vel=vel;//speed of pic
    
    noPics=(int)Math.round(Math.ceil(2.0/picWidth))+1;//no of pics needed to cover screen
    
    posX=new double [noPics];
    
    for(int i=0; i<noPics; i++)
      posX[i]=-1+picWidth/2.0+i*picWidth;//position of each pic
  }
  
  public void move()
  {
    for(int i=0; i<noPics; i++)
    {
      posX[i]+=vel;
      
      if(posX[i]+picWidth/2.0<-1)//off the screen
        posX[i]+=picWidth*noPics;
    }
  }
  
  public void draw()
  {
    for(int i=0; i<noPics; i++)
    {    
      StdDraw.picture(posX[i],posY,pic, picWidth*1.005, picHeight);//overlap pics slightly to avoid flicker
    }
  }
  
  public void stop()
  {
    vel=0;
  }
}