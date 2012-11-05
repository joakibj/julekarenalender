package no.jervell.animation;

/**
 * @author Arne C. Jervell (arne@jervell.no)
 */
public class Timer
{
  private double fps;
  private long lastFrame;
  private int frameCounter;

  public Timer( double fps )
  {
    setFrameRate( fps );
    reset();
  }

  public void reset()
  {
    lastFrame = 0;
    frameCounter = 0;
  }

  private void setFrameRate( double fps )
  {
    if ( fps > 100 )
    {
      throw new IllegalArgumentException( "FPS must be <= 100" );
    }
    this.fps = fps;
  }

  public double getFrameRate()
  {
    return fps;
  }

  public int getDurationInFrames( double seconds )
  {
    return (int)(seconds * fps);
  }

  public int getFrameCounter()
  {
    return frameCounter;
  }

  /**
   * @return frame duration in milliseconds
   */
  public long getFrameDuration()
  {
    return (long)(1000./fps);
  }

  public void waitForNextFrame() throws InterruptedException
  {
    long now = System.currentTimeMillis();
    try
    {
      long maxSleepTime = getFrameDuration();
      long timePassed   = now - lastFrame;
      long timeToSleep = maxSleepTime - timePassed;
      if ( timeToSleep <= 0 )
      {
        Thread.yield();
      }
      else
      {
        Thread.sleep( timeToSleep );
      }
    }
    finally
    {
      lastFrame = System.currentTimeMillis();
      frameCounter++;
    }
  }
}
