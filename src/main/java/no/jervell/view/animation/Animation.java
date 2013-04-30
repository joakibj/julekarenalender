package no.jervell.view.animation;

import no.jervell.view.animation.impl.DefaultTimer;

/**
 * @author Arne C. Jervell (arne@jervell.no)
 */
public interface Animation {
    public void init(DefaultTimer timer);

    public void move(DefaultTimer timer);
}
