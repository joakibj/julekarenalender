package no.jervell.view.animation.impl;

import no.jervell.view.animation.TimeFunction;

/**
 * @author Arne C. Jervell (arne@jervell.no)
*/
public class VelocityMover
{
  TimeFunction velocity;
  double[] dist;
  double frameDuration = 20;  // Frame duration in milliseconds. TODO: Configurable
  int index = 0;

  public VelocityMover( TimeFunction velocity )
  {
    setVelocityFunction( velocity );
  }

  public void setVelocityFunction( TimeFunction velocity )
  {
    init( velocity );
  }

  private double getVelocity( int t )
  {
    int    time  = t < 0 ? -t : t;
    double value = velocity.f( time );
    return t<0 ? -value : value;
  }

  public double getCurrentVelocity()
  {
    return getVelocity( index );
  }

  private double getDistance( int t )
  {
    int    time  = t < 0 ? -t : t;
    double value = time >= velocity.length() ? 0 : dist[ time ];
    return t<0 ? -value : value;
  }

  public double getCurrentDistance()
  {
    return getDistance( index );
  }

  public boolean isEndReached()
  {
    return Math.abs( index ) >= velocity.length();
  }

  private int getVelocityIndex( double v )
  {
    double value = v < 0 ? -v : v;
    int i = 0;
    while( i < velocity.length() )
    {
      if ( value >= velocity.f(i) )
      {
        return v < 0 ? Math.min(-i,-1) : i;
      }
      i++;
    }
    return v < 0 ? -velocity.length() : velocity.length();
  }

  public void reset( double initialVelocity )
  {
    index = getVelocityIndex( initialVelocity );
  }

  public void move()
  {
    if ( index < 0 )
    {
      index--;
    }
    else
    {
      index++;
    }
  }

  private void init( TimeFunction velocityFunction )
  {
    velocity = velocityFunction;
    dist     = ensureDistanceFunction(velocityFunction.length());
    computeDistanceFunction( velocityFunction, dist );
  }

  private void computeDistanceFunction( TimeFunction velocityFunction, double[] dist )
  {
    int len = velocityFunction.length();
    for ( int i = len-1; i >= 0; --i )
    {
      dist[i] = velocityFunction.f(i) * frameDuration / 1000.;
      if ( i < len-1 )
      {
        dist[i] += dist[i+1];
      }
    }
  }

  private double[] ensureDistanceFunction( int length )
  {
    if ( dist == null || dist.length < length )
    {
      dist = new double[ length ];
    }
    return dist;
  }
}
