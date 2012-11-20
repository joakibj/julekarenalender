package no.jervell.jul;

import no.jervell.animation.Animation;
import no.jervell.animation.Timer;
import no.jervell.swing.WheelView;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;

/**
 * INIT:
 * x set first date
 *
 * LOOP:
 * x set person wheel target index
 * x set bonus wheel target index (bonus if next date is same as current, no bonus otherwise)
 * x enable person wheel
 *
 * WAIT_FOR_PERSON:
 * x ...wait for person wheel to stop
 *
 * WINNER:
 * x init text animation
 * x enable bonus wheel
 * - ...wait for person wheel OR bonus wheel to start
 * x if person wheel spinning, discard winner and select a different one
 * x if person wheel spinning, disable bonus wheel
 * x if bonus wheel spinning, disable person wheel
 * x ...wait for bonus wheel OR person wheel to start
 * x stop text animation
 * x if person wheel started, go to WAIT_FOR_PERSON
 *
 * WAIT_FOR_BONUS:
 * x ...wait for bonus wheel to stop
 * x disable bonus wheel
 * x if new date: repeat to LOOP
 *
 * FINISHED:
 *
 *
 * @author Arne C. Jervell (arne@jervell.no)
 */
public class GameLogic implements Animation, WheelAnimation.Listener, WheelSpinner.Target
{
  private enum State { INIT, LOOP, WAIT_FOR_PERSON, WINNER, WAIT_FOR_BONUS, FINISHED }

  private PersonDAO personDAO;
  private Script script;
  private State state;

  private Random rnd = new Random();
  private WheelView date;
  private WheelView person;
  private WheelView bonus;
  private WheelRowAnimator blink;

  public GameLogic( int[] days, Julekarenalender julekarenalender )
  {
    this.personDAO     = julekarenalender.personDAO;
    this.date          = julekarenalender.dateWheel;
    this.person        = julekarenalender.personWheel;
    this.bonus         = julekarenalender.bonusWheel;
    this.blink         = new WheelRowAnimator( person );
    this.script        = new Script( days );
    populatePerson( julekarenalender );
    setState( State.INIT );
  }

  private void populatePerson( Julekarenalender julekarenalender )
  {
    List<WheelView.Row> rows = new ArrayList<WheelView.Row>( person.getRows() );
    for ( Person p : script.getPersonList() )
    {
      rows.add( new WheelView.Row( p, julekarenalender.createPersonWheelRow(p) ) );
    }
    person.setRows( rows );
  }

  public void init( Timer timer )
  {
    date.setEnabled( false );
    person.setEnabled( false );
    bonus.setEnabled( false );
    if ( script.hasCurrent() )
    {
      date.setYOffset( script.getDay() );
    }
    setState( State.LOOP );
  }

  public void move( Timer timer )
  {
    rotateDateWheel();
    switch ( state )
    {
      case LOOP:
        if ( script.hasCurrent() )
        {
          person.setEnabled( true );
          setState( State.WAIT_FOR_PERSON );
        }
        else
        {
          setState( State.FINISHED );
        }
        break;

      case WINNER:
        blink.move( timer );
        break;
    }
  }

  private void rotateDateWheel()
  {
    if ( script.hasCurrent() )
    {
      double dateY   = date.getYOffset();
      double diff    = script.getDay() - dateY;
      double absDiff = Math.abs( diff );
      if ( absDiff > .001 )
      {
        double move = Math.min( .02, absDiff );
        date.setYOffset( dateY + move*Math.signum(diff) );
        date.repaint();
      }
    }
  }

  public Integer getTargetIndex( WheelView view )
  {
    switch ( state )
    {
      case WAIT_FOR_PERSON:
        if ( view == person )
        {
          return script.getPerson();
        }
        break;

      case WINNER:
        if ( view == person )
        {
          return script.replacePerson();
        }
        else if ( view == bonus )
        {
          boolean repeatingDate = script.hasNext() && script.getDay() == script.getNextDay();
          return bonus.getIndex( repeatingDate ? 1 : -1 );  // TODO: Constants?
        }
        break;
    }
    return null;
  }

  public void spinStarted( WheelView view, double velocity )
  {
    System.out.println("--- spin started");
    switch ( state )
    {
      case WINNER:
        blink.stop();
        if ( view == person )
        {
          bonus.setEnabled( false );
          setState( State.WAIT_FOR_PERSON );
        }
        else if ( view == bonus )
        {
          person.setEnabled( false );
          setState( State.WAIT_FOR_BONUS );
        }
        break;
    }
  }

  public void spinStopped( WheelView view )
  {
    System.out.println("--- spin stopped");
    switch ( state )
    {
      case WAIT_FOR_PERSON:
        blink.start( script.getPerson() );
        bonus.setEnabled( true );
        setState( State.WINNER );
        break;

      case WAIT_FOR_BONUS:
        bonus.setEnabled( false );
        if ( script.move() )
        {
          setState( State.LOOP );
        }
        else
        {
          setState( State.FINISHED );
        }
        break;
    }
  }

  private void setState( State state )
  {
    System.out.println( ">>> " + state );
    if ( state == State.FINISHED ||
         state == State.WINNER )
    {
      try
      {
        personDAO.persist();
      }
      catch ( Exception e )
      {
        System.err.println( "Unable to persist data. Reason: " + e );
      }
    }
    this.state = state;
  }

  private class Script
  {
    private int pos;
    private int[] days;
    private Person[] winners;
    private List<Person> queue;
    private List<Person> personList;

    public Script( int[] days )
    {
      this.days = days;
      this.winners = new Person[ days.length ];
      this.pos = 0;
      init();
    }

    private void init()
    {
      personList = getFilteredList( personDAO.getPersonList(), getFirstDay() );
      queue = new ArrayList<Person>( personList );
      for ( int i = 0; i < days.length; ++i )
      {
        winners[ i ] = pickWinner( days[i], queue );
      }
    }

    public List<Person> getPersonList()
    {
      return personList;
    }

    private List<Person> getFilteredList( List<Person> list, int minDay )
    {
      List<Person> result = new ArrayList<Person>();
      for ( Person p : list )
      {
        if ( p.getDay() == 0 || p.getDay() >= minDay )
        {
          result.add( p );
        }
      }
      return result;
    }

    private int getFirstDay()
    {
      if ( days == null || days.length == 0 )
      {
        return 0;
      }
      int min = days[0];
      for ( int val : days )
      {
        min = Math.min( min, val );
      }
      return min;
    }

    private Person pickWinner( int day, List<Person> queue )
    {
      Person winner = extractRiggedWinner( day, queue );
      if ( winner == null )
      {
        winner = extractRandomWinner( day, queue );
      }
      if ( winner == null )
      {
        winner = new Person();
        winner.setDay( day );
        winner.setName( "NOBODY" );
      }
      return winner;
    }

    private Person extractRiggedWinner(int day, List<Person> queue )
    {
      for ( Person p : queue )
      {
        if ( p.getDay() == day )
        {
          queue.remove( p );
          return p;
        }
      }
      return null;
    }

    private Person extractRandomWinner( int day, List<Person> queue )
    {
      if ( queue.size() > 0 )
      {
        Person p = queue.remove( rnd.nextInt( queue.size() ) );
        p.setDay( day );
        return p;
      }
      return null;
    }

    public int getDay()
    {
      return date.getIndex( winners[ pos ].getDay() );
    }

    public int getNextDay()
    {
      return date.getIndex( winners[ pos+1 ].getDay() );
    }

    public int getPerson()
    {
      return person.getIndex( winners[ pos ] );
    }

    public int replacePerson()
    {
      Person oldPerson = winners[ pos ];
      winners[ pos ] = extractRandomWinner( oldPerson.getDay(), queue );
      oldPerson.setDay( 0 );
      return getPerson();
    }

    public boolean hasCurrent()
    {
      return pos < days.length;
    }

    public boolean hasNext()
    {
      return pos+1 < days.length;
    }

    public boolean move()
    {
      if ( hasCurrent() )
      {
        pos++;
      }
      return hasCurrent();
    }
  }

}
