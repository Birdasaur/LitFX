package lit.litfx.core.components.fire;

import javafx.scene.paint.Color;

/**
 * Object that computes and stores palette values for convolution effects.
 * @author Birdasaur
 */
public class ConvolutionPalette {
    private int shift1 = 16;
    private int shift2 = 8;
    private int shift3 = 0;
    int[] paletteAsInts; //this will contain the color palette
    public double flameOpacity = 0.5; 
    public boolean classic = true;
        
    public ConvolutionPalette(int max) {
        paletteAsInts = generateArgbPalette(max);
    }
    
    public int getPaletteValue(int pixelIndex) {
        int value = 0;
        try {
            if (classic)
                value = paletteAsInts[pixelIndex];
            if (getShift1() > -1)
                value |= paletteAsInts[pixelIndex] << getShift1();
            if (getShift2() > -1)
                value |= paletteAsInts[pixelIndex] << getShift2();
            if (getShift3() > -1)
                value |= paletteAsInts[pixelIndex] << getShift3();
        } catch (Exception e) {
            System.out.println("pixelIndex: " + pixelIndex + "  value " + value + " " + e );
        }
        //Apply additional opacity values controlled externally
        value |= (int)(flameOpacity*255) << 24;
        return value;
    }    
    public int[] generateArgbPalette(int max ) {
        int [] pal = new int[max];
        //generate the palette
        for (int x = 0; x < max; x++) {
            //HSLtoRGB is used to generate colors:
            //Hue goes from 0 to 85: red to yellow
            //Saturation is always the maximum: 255
            //Brightness is 0..255 for x=0..128, and 255 for x=128..255
            //color = HSLtoRGB(ColorHSL(x / 3, 255, std::min(255, x * 2)));
            //set the palette to the calculated RGB value
            //palette[x] = RGBtoINT();
            double brightness = Math.min(255, x*2) / 255.0;
            Color color = Color.hsb(x / 3.0, 1.0, brightness , brightness);
            pal[x] = rgbToIntArgb(color);            
        }
        return pal;
    }

    public static int rgbToIntArgb(Color colorRGB) {
      return (int)(colorRGB.getOpacity()*255) << 24 |
             (int)(colorRGB.getRed()    *255) << 16 | 
             (int)(colorRGB.getGreen()  *255) <<  8 | 
             (int)(colorRGB.getBlue()   *255);
    }     

    /**
     * @return the shift1
     */
    public int getShift1() {
        return shift1;
    }

    /**
     * @param shift1 the shift1 to set
     */
    public void setShift1(int shift1) {
        this.shift1 = shift1;
    }

    /**
     * @return the shift2
     */
    public int getShift2() {
        return shift2;
    }

    /**
     * @param shift2 the shift2 to set
     */
    public void setShift2(int shift2) {
        this.shift2 = shift2;
    }

    /**
     * @return the shift3
     */
    public int getShift3() {
        return shift3;
    }

    /**
     * @param shift3 the shift3 to set
     */
    public void setShift3(int shift3) {
        this.shift3 = shift3;
    }    
    
}
