package no.jervell.view.animation;

public interface TimeFunction
{
  /** Get function value at given time */
  public double f( int t );
  /** Get time range */
  public int length();
}
