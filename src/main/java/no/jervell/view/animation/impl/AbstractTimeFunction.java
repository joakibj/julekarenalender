package no.jervell.view.animation.impl;

import no.jervell.view.animation.TimeFunction;

import java.util.List;

public class AbstractTimeFunction implements TimeFunction
{
  private Double negInf = null;
  private Double posInf = 0.;
  private double[] values;

  protected AbstractTimeFunction()
  {
  }

  public AbstractTimeFunction( double[] values )
  {
    this.values = values;
  }

  public void setValues( double[] values )
  {
    this.values = values;
  }

  public void setPositiveInfinity( Double posInf )
  {
    this.posInf = posInf;
  }

  public void setNegativeInfinity( Double negInf )
  {
    this.negInf = negInf;
  }

  public double f( int t )
  {
    if ( t < 0 )
    {
      return negInf == null ? values[0] : negInf;
    }
    else if ( t < length() )
    {
      return values[t];
    }
    else
    {
      return posInf == null ? values[ length()-1 ] : posInf;
    }
  }

  public int length()
  {
    return values.length;
  }

  protected static double[] copyToArray( List<Double> list )
  {
    double[] array = new double[ list.size() ];
    for ( int i = array.length-1; i >= 0; --i )
    {
      array[i] = list.get( i );
    }
    return array;
  }
}
