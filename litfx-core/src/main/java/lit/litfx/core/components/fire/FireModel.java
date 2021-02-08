package lit.litfx.core.components.fire;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;

import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Random;

public class FireModel {
    Random rand = new Random();

    public int screenWidth = 800, screenHeight = 600; // The canvas dimensions

    public int[] fire;         //this buffer will contain the fire
    public int[] fireBuf;      //double buffer
    public int[] bottomRow;    //seeds of flames randomly generated
    public int[] paletteAsInts; //this will contain a 32 bit (integer) array of colors for the palette

    public boolean classic = true;
    public int shift1, shift2, shift3;  //cheesy way to use bit shifting to make waves
    WritableImage writableImage;

    public FireModel(){
        this(800, 600);
    }

    public FireModel(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.paletteAsInts = generateArgbPalette(256);
        writableImage = new WritableImage(screenWidth, screenHeight);
        reallocate();
    }

    private void reallocate() {
        fire = new int[screenHeight * screenWidth + 1];  //this buffer will contain the fire
        fireBuf = new int[screenHeight * screenWidth + 1];
        bottomRow = new int[screenWidth];
    }

    public void genRandomFireRowWidth() {
        Arrays.setAll(bottomRow, (int operand) -> Math.abs(32768 + rand.nextInt(65536)) % 256);
    }

    private int[] generateArgbPalette(int max ) {
        int [] pal = new int[max];
        //generate the palette
        for (int x = 0; x < max; x++) {
            //HSLtoRGB is used to generate colors:
            //Hue goes from 0 to 85: red to yellow
            //Saturation is always the maximum: 255
            //Lightness is 0..255 for x=0..128, and 255 for x=128..255
            //color = HSLtoRGB(ColorHSL(x / 3, 255, std::min(255, x * 2)));
            //set the palette to the calculated RGB value
            //palette[x] = RGBtoINT();
            double brightness = Math.min(255, x*2) / 255.0;
            Color color = Color.hsb(x / 3.0, 1.0, brightness , 1);
            pal[x] = rgbToIntArgb(color);
        }
        return pal;
    }

    private static int rgbToIntArgb(Color colorRGB) {
        return (int)(colorRGB.getOpacity()*255) << 24 |
                (int)(colorRGB.getRed()    *255) << 16 |
                (int)(colorRGB.getGreen()  *255) <<  8 |
                (int)(colorRGB.getBlue()   *255);
    }

    public void copyFireRowBottom() {
        int fireStartHeight = (screenHeight - 1) * screenWidth;
        System.arraycopy(bottomRow, 0, fire, fireStartHeight, screenWidth);
    }

    public void convolution() {
        int a, b;
        int row, pixel;
        for (int y = 0; y < screenHeight - 1; y++) {
            a = (y + 1) % screenHeight * screenWidth;
            row = y * screenWidth;
            for (int x = 0; x < screenWidth; x++) {
                b = x % screenWidth;
                int index = row + x;
                pixel = fire[index]
                        = ((fire[a + ((x - 1 + screenWidth) % screenWidth)]
                        + fire[((y + 2) % screenHeight) * screenWidth + b]
                        + fire[a + ((x + 1) % screenHeight)]
                        + fire[((y + 3) % screenHeight * screenWidth) + b])
                        * 128) / 513;
                fireBuf[index] = getPaletteValue(pixel);
            }
        }
    }

    private int getPaletteValue(int pixelIndex) {
        if(classic)
            return paletteAsInts[pixelIndex];
        int value = 0;
        if(shift1 > -1)
            value |= paletteAsInts[pixelIndex] << shift1;
        if(shift2 > -1)
            value |= paletteAsInts[pixelIndex] << shift2;
        if(shift3 > -1)
            value |= paletteAsInts[pixelIndex] << shift3;
        return value;
    }

    public WritableImage copyWritable(WritablePixelFormat<IntBuffer> pixelFormat) {
        PixelWriter pwBuffer = writableImage.getPixelWriter();
        try {
            pwBuffer.setPixels(0, 0, screenWidth, screenHeight, pixelFormat, fireBuf, 0, screenWidth);
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return writableImage;
    }

    public void cleanup() {
        writableImage.cancel();
        writableImage = null;

        fire = null;         //this buffer will contain the fire
        fireBuf = null;      //double buffer
        bottomRow = null;    //seeds of flames randomly generated
        paletteAsInts = null;
    }
}
