package no.jervell.animation;

import no.jervell.util.SimpleLogger;

/**
 * @author Arne C. Jervell (arne@jervell.no)
*/
public class AnimationLoop extends Thread
{
  private volatile boolean keepRunning;
  private Timer timer;
  private Animation[] animations;

  public AnimationLoop()
  {
    timer = new Timer( 50 );
    keepRunning = true;
    setDaemon( true );
  }

  public void setAnimations( Animation... animations )
  {
    this.animations = animations;
  }

  public void end()
  {
    keepRunning = false;
  }

  @Override
  public void run()
  {
    try
    {
      initAnimations();
      while( keepRunning )
      {
        timer.waitForNextFrame();
        moveAnimations();
      }
    }
    catch ( InterruptedException e )
    {
      SimpleLogger.getInstance().info( "Breaking out of loop. Reason: " + e );
    }
    SimpleLogger.getInstance().debug( "Loop stopped." );
  }

  private void initAnimations()
  {
    for ( Animation animation : animations )
    {
      animation.init( timer );
    }
  }

  private void moveAnimations()
  {
    for ( Animation animation : animations )
    {
      animation.move( timer );
    }
  }
}
