package no.jervell.awt;

/**
 * @author Arne C. Jervell (arne@jervell.no)
*/
public enum Scaling
{
  /** Do not scale */
  NONE,
  /** Scale uniformly up/down */
  UNIFORM,
  /** Scale uniformly down (but never up) */
  DOWN,
  /** Scale uniformly up (but never down) */
  UP,
  /** Scale non-uniformly up/down */
  STRETCH;

  public Dim2D getScaleFactors( double targetWidth, double targetHeight, double preferredWidth, double preferredHeight )
  {
    double scaleW   = preferredWidth  == 0 ? 1 : targetWidth  / preferredWidth;
    double scaleH   = preferredHeight == 0 ? 1 : targetHeight / preferredHeight;
    double minScale = Math.min( scaleW, scaleH );

    switch( this )
    {
      case STRETCH:     return new Dim2D( scaleW, scaleH );
      case UNIFORM:     return new Dim2D( minScale, minScale );
      case DOWN:        return minScale < 1 ? new Dim2D( minScale, minScale ) : new Dim2D( 1, 1 );
      case UP:          return minScale > 1 ? new Dim2D( minScale, minScale ) : new Dim2D( 1, 1 );
      default:          return new Dim2D( 1, 1 );
    }
  }

}
