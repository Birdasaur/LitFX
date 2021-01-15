package lit.litfx.core.components.fire;

/**
 * Object dedicated to computing the fire convolution for a row of pixels
 * Does not convert pixel to RGB palette value, only provides final convolved 
 * pixel array via int [] pixelValues.
 * @author Birdasaur
 */
public class FireConvolution {
    int y;
    int canvasHeight;
    int canvasWidth;
    public int [] pixelValues; //the results from the convolution
    
    public FireConvolution(int canvasHeight, int canvasWidth, int y) {
        this.canvasHeight = canvasHeight;
        this.canvasWidth = canvasWidth;
        this.y = y;
        pixelValues = new int[canvasWidth];
    }
    
    public void convolve(int [] fire) {
        int a, b, shiftedValue, row;
        row = y * canvasWidth;
        int fireIndex1, fireIndex2; //column oriented values computed in outer loop
        fireIndex1 = ((y + 2) % canvasHeight) * canvasWidth;
        fireIndex2 = ((y + 3) % canvasHeight * canvasWidth);
        for (int x = 0; x < canvasWidth; x++) {
            a = (y + 1) % canvasHeight * canvasWidth;
            b = x % canvasWidth;
            shiftedValue = (
                (fire[a + ((x - 1 + canvasWidth) % canvasWidth)] //fireIndex0
                + fire[fireIndex1 + b] //fireIndex1
                + fire[a + ((x + 1) % canvasWidth)] //fireIndex2
                + fire[fireIndex2 + b]) //fireIndex3
                << 7); //multiply by constant 128
            // divide by constant 513
            fire[row + x] = pixelValues[x] = ((shiftedValue << 9) - shiftedValue) >> 18;
        }       
    }   
}