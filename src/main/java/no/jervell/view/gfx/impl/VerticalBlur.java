package no.jervell.view.gfx.impl;

import no.jervell.view.gfx.ImageFilter;

/**
 * Vertical blur filter. Coded for speed, not beauty.
 */
public class VerticalBlur implements ImageFilter
{
  private double maxRadius = 200.0;
  private int radius = 0;

  private int[] bufferR;
  private int[] bufferG;
  private int[] bufferB;

  public VerticalBlur()
  {
    setRadius( 0 );
  }

  public VerticalBlur( double radius )
  {
    setRadius( radius );
  }

  public void setRadius( double radius )
  {
    this.radius = (int)Math.round( Math.max( 0.0, Math.min( maxRadius, Math.abs( radius ) ) ) );
    int kernelLength = this.radius*2+1;
    if ( bufferR == null || bufferR.length < kernelLength )
    {
      bufferR = new int[ kernelLength ];
      bufferG = new int[ kernelLength ];
      bufferB = new int[ kernelLength ];
    }
  }

  public void setMaxRadius( double maxRadius )
  {
    this.maxRadius = Math.abs( maxRadius );
    setRadius( radius );
  }


  public synchronized void apply( int[] pix, int w, int h )
  {
    int rad = radius;
    if ( rad > h/2 )
    {
      rad = h/2;
    }
    if ( rad >= 1 )
    {
      applyBlur( pix, w, h, rad );
    }
  }

  private void applyBlur( int[] pix, int w, int h, int rad )
  {
    int kernelLength = rad*2+1;
    for ( int x = 0; x < w; ++x )
    {
      int bufferWriteIndex = 0;
      int bufferRemoveIndex = 0;
      int bufferSize = 0;

      int R;
      int G;
      int B;
      int accR = 0;
      int accG = 0;
      int accB = 0;

      int src = x;
      int dst = x;

      // Accumulate [rad] pixels
      for ( int n = 0; n < rad; ++n )
      {
        int rgb = pix[ src ];
        R = ((rgb&0xff0000)>>16);
        G = ((rgb&0x00ff00)>> 8);
        B = ((rgb&0x0000ff)    );
        bufferR[bufferWriteIndex] = R;
        bufferG[bufferWriteIndex] = G;
        bufferB[bufferWriteIndex] = B;
        accR += R;
        accG += G;
        accB += B;
        src += w;
        bufferSize++;
        bufferWriteIndex++;
      }

      // Blur [h-rad] pixels
      int h_rad = h-rad;
      for ( int y = 0; y < h_rad; ++y )
      {
        pix[ dst ] = ((accR/bufferSize)<<16) |
                     ((accG/bufferSize)<< 8) |
                     ((accB/bufferSize)    );
        dst += w;

        if ( bufferSize == kernelLength )
        {
          accR -= bufferR[bufferRemoveIndex];
          accG -= bufferG[bufferRemoveIndex];
          accB -= bufferB[bufferRemoveIndex];
          bufferSize--;
          bufferRemoveIndex++;
          if ( bufferRemoveIndex == kernelLength )
          {
            bufferRemoveIndex = 0;
          }
        }

        int rgb = pix[ src ];
        R = ((rgb&0xff0000)>>16);
        G = ((rgb&0x00ff00)>> 8);
        B = ((rgb&0x0000ff)    );
        bufferR[bufferWriteIndex] = R;
        bufferG[bufferWriteIndex] = G;
        bufferB[bufferWriteIndex] = B;
        accR += R;
        accG += G;
        accB += B;
        src += w;
        bufferSize++;
        bufferWriteIndex++;
        if ( bufferWriteIndex == kernelLength )
        {
          bufferWriteIndex = 0;
        }
      }

      // Blur final [rad] pixels
      for ( int y = h-rad; y < h; ++y )
      {
        pix[ dst ] = ((accR/bufferSize)<<16) |
                     ((accG/bufferSize)<< 8) |
                     ((accB/bufferSize)    );
        dst += w;

        accR -= bufferR[bufferRemoveIndex];
        accG -= bufferG[bufferRemoveIndex];
        accB -= bufferB[bufferRemoveIndex];
        bufferSize--;
        bufferRemoveIndex++;
        if ( bufferRemoveIndex == kernelLength )
        {
          bufferRemoveIndex = 0;
        }
      }
    }
  }

}
