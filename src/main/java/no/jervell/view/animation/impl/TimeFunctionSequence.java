package no.jervell.view.animation.impl;

import no.jervell.view.animation.TimeFunction;

public class TimeFunctionSequence extends AbstractTimeFunction
{
  public TimeFunctionSequence()
  {
  }

  public TimeFunctionSequence( TimeFunction... functions )
  {
    setFunctions( functions );
  }

  public void setFunctions( TimeFunction... functions )
  {
    double[] values = new double[ getCombinedLength( functions ) ];
    int dst = 0;
    for ( TimeFunction fun : functions )
    {
      for ( int t = 0; t < fun.length(); ++t )
      {
        values[ dst++ ] = fun.f( t );
      }
    }
    super.setValues( values );
  }

  static int getCombinedLength( TimeFunction[] functions )
  {
    int length = 0;
    for ( TimeFunction f : functions )
    {
      length += f.length();
    }
    return length;
  }

}
