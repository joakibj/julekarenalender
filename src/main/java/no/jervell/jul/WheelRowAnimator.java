package no.jervell.jul;

import no.jervell.awt.*;
import no.jervell.awt.Label;
import no.jervell.animation.Animation;
import no.jervell.animation.Timer;
import no.jervell.swing.WheelView;

import java.awt.*;

public class WheelRowAnimator implements Animation
{
  private WheelView wheel;
  private int blinkIndex; 

  private Color textPaint = Color.black;
  private Color highlightedTextPaint = Color.red;
  private Paint[] textPaintAnimated;
  private int frame = -1;

  public WheelRowAnimator( WheelView wheel )
  {
    this.wheel = wheel;

    textPaintAnimated = createColorAnimation( 25, textPaint, highlightedTextPaint );
  }

  private Paint[] createColorAnimation( int nFrames, Color textPaint, Color highlighted )
  {
    Paint[] textPaintAnimated = new Paint[ nFrames ];
    for ( int i = 0; i < nFrames; ++i )
    {
      int beta = 128 + (int)(128*Math.sin( -Math.PI/2 + (double)i*Math.PI*2/nFrames ));
      int r = (highlighted.getRed()  *beta + textPaint.getRed()  *(256-beta))>>8;
      int g = (highlighted.getGreen()*beta + textPaint.getGreen()*(256-beta))>>8;
      int b = (highlighted.getBlue() *beta + textPaint.getBlue() *(256-beta))>>8;
      textPaintAnimated[i] = new Color( r, g, b );
    }
    return textPaintAnimated;
  }

  public void init(Timer timer)
  {
  }

  public void start( int blinkIndex )
  {
    frame = 0;
    this.blinkIndex = blinkIndex;
  }

  public void stop()
  {
    setPaint(textPaint);
    frame = -1;
  }

  public boolean isActive()
  {
    return frame >= 0;
  }

  public void move( Timer timer )
  {
    if ( isActive() &&
         blinkIndex >= 0 && blinkIndex < wheel.getRowCount() )
    {
      Paint paint = textPaintAnimated[(frame++) % textPaintAnimated.length];
      setPaint(paint);
    }
  }

  private void setPaint(Paint color)
  {
    Paintable paintable = wheel.getRow( blinkIndex ).getPaintable();
    Label label = (paintable instanceof Label) ? (Label)paintable : null;
    if ( paintable instanceof ImageLabel)
    {
      label = ((ImageLabel)paintable).getLabel();
    }
    if ( label != null )
    {
      label.setPaint(color);
      wheel.repaint();
    }
  }
}
