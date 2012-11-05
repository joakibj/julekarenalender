package no.jervell.animation;

/**
 * @author Arne C. Jervell (arne@jervell.no)
 */
public interface Animation
{
  public void init( Timer timer );
  public void move( Timer timer );
}
