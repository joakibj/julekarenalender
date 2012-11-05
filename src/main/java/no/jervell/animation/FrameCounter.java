package no.jervell.animation;

public class FrameCounter implements Animation
{
  private String name;

  private int   segmentSize = 100;
  private int   counter   = 0;
  private long  startTime;

  private long  lastSegmentDuration = 0;

  public FrameCounter()
  {
    this.name = getClass().getSimpleName();
  }

  public FrameCounter( String name )
  {
    this.name = name;
  }

  public void init(Timer timer)
  {
    init();
  }

  private void init()
  {
    startTime = System.currentTimeMillis();
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public long getSegmentDuration()
  {
    return lastSegmentDuration;
  }

  public double getFPS()
  {
    return (segmentSize*1000.)/lastSegmentDuration;
  }

  public void move(Timer timer)
  {
    frame();
  }

  public boolean frame()
  {
    if ( counter == 0 && startTime == 0 )
    {
      init();
    }
    counter++;
    if ( counter >= segmentSize )
    {
      long now = System.currentTimeMillis();
      lastSegmentDuration = now - startTime;
      startTime = now;
      counter = 0;
      return true;
    }
    else
    {
      return false;
    }
  }

  @Override
  public String toString()
  {
    return name + ": " + segmentSize + " frames in " + lastSegmentDuration + " ms ==> " + (int)Math.round(getFPS()) + " FPS";
  }
}
