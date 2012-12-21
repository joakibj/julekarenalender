package no.jervell.view.animation;

public interface Timer {
    public void reset();

    public void setFrameRate(double fps);

    public double getFrameRate();

    public int getDurationInFrames(double seconds);

    public int getFrameCounter();

    public long getFrameDuration();

    public void waitForNextFrame() throws InterruptedException;
}