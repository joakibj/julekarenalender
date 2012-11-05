package no.jervell.jul;

import no.jervell.animation.*;
import no.jervell.swing.WheelView;
import no.jervell.gfx.VerticalBlur;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Arne C. Jervell (arne@jervell.no)
 */
public class WheelAnimation implements Animation
{
  private enum State { IDLE, INTERACTION, START, SPINNING, STOP }

  private static final TimeFunction[] GRADIENTS =
  {
    new TimeFunctionSequence( new VelocityDecay( 50, .994, .002, .05 ), new OscilateToRest(  .5, 2.0, 3, 70 ) ),
    new TimeFunctionSequence( new VelocityDecay( 50, .994, .002, .05 ), new OscilateToRest( -.5, 2.0, 3, 70 ) ),
    new TimeFunctionSequence( new VelocityDecay( 50, .994, .002, .20 ), new OscilateToRest(   0, 0.3, 2, 50 ) )
  };

  private WheelView wheelView;
  private State state;
  private VelocityMover mover;
  private VerticalBlur blurFilter;

  private List<Listener> listeners = new ArrayList<Listener>();

  private double extraDistance;

  public WheelAnimation( WheelView wheelView, double maxVelocity )
  {
    this.wheelView = wheelView;
    this.mover     = new VelocityMover( new VelocityDecay( maxVelocity ) );
    this.state     = State.IDLE;
    setBlur( true );
  }

  public void setBlur( boolean on )
  {
    if ( on && blurFilter == null )
    {
      blurFilter = new VerticalBlur();
      wheelView.setFilter( blurFilter );
    }
    else if ( !on )
    {
      blurFilter = null;
      wheelView.setFilter( null );
    }
  }

  public void addListener( Listener listener )
  {
    if ( !listeners.contains( listener ) )
    {
      listeners.add( listener );
    }
  }

  public void removeListener( Listener listener )
  {
    listeners.remove( listener );
  }

  private void notifySpinStarted( double velocity )
  {
    for ( Listener listener : listeners )
    {
      listener.spinStarted( wheelView, velocity );
    }
  }

  private void notifySpinStopped()
  {
    for ( Listener listener : listeners )
    {
      listener.spinStopped( wheelView );
    }
  }

  public WheelView getWheelView()
  {
    return wheelView;
  }

  public boolean isBusy()
  {
    return state == State.SPINNING || !wheelView.isEnabled();
  }

  public synchronized void spin( double velocity )
  {
    spin( velocity, null );
  }

  public synchronized void spin( double velocity, Integer targetY )
  {
    mover.setVelocityFunction( selectGradient() );
    mover.reset( velocity );
    if ( targetY == null )
    {
      extraDistance = 0;
    }
    else
    {
      double landingZone = wheelView.getYOffset() + mover.getCurrentDistance();
      extraDistance = getTargetDistance( velocity, landingZone, targetY );
    }
    state = State.START;
  }

  private TimeFunction selectGradient()
  {
    int index = (int)(Math.abs(Math.random())*GRADIENTS.length);
    return GRADIENTS[ Math.min(GRADIENTS.length,index) ];
  }

  private double getTargetDistance( double velocity, double sourceY, double targetY )
  {
    sourceY = wheelView.toYRange( sourceY );
    targetY = wheelView.toYRange( targetY );
    double distance = targetY - sourceY;
    if ( velocity > 0 && targetY < sourceY )
    {
      distance += wheelView.getRowCount();
    }
    else if ( velocity < 0 && targetY > sourceY )
    {
      distance -= wheelView.getRowCount();
    }
    return distance;
  }

  public synchronized void halt()
  {
    state = State.STOP;
  }

  public void init( Timer timer )
  {
  }

  public synchronized void move( Timer timer )
  {
    switch ( state )
    {
      case START:
        timer.reset();
        state = State.SPINNING;
        notifySpinStarted( mover.getCurrentVelocity() );
        break;

      case STOP:
        timer.reset();
        blur( 0 );
        state = State.IDLE;
        break;

      case SPINNING:
        double velocity = mover.getCurrentVelocity();
        double distance = velocity * timer.getFrameDuration() / 1000.;
        wheelView.setYOffset( wheelView.getRawYOffset() + distance );
        if ( Math.abs( extraDistance ) > .01 && Math.abs( extraDistance ) > Math.abs( distance ) )
        {
          extraDistance -= distance;
        }
        else
        {
          mover.move();
        }
        if ( mover.isEndReached() )
        {
          notifySpinStopped();
          state = State.IDLE;
        }
        blur( wheelView.getHeight() * Math.pow( Math.abs(velocity), 1.5 ) / 1000 );
        wheelView.repaint();
        break;

      default:
        blur( 0 );
        break;
    }
  }

  private void blur( double radius )
  {
    if ( blurFilter != null )
    {
      blurFilter.setRadius( radius );
    }
  }


  public interface Listener
  {
    public void spinStarted( WheelView view, double velocity );
    public void spinStopped( WheelView view );
  }
}
