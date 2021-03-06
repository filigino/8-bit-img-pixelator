/* 
 * Implements color quantization using the median cut algorithm.
 */

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.Math;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class Pixelator {
    // New color palette must be a power of 2 since using median cut
    public static void pixelate(BufferedImage image, int numberOfColors) {
        if (numberOfColors > 0 && ((numberOfColors & (numberOfColors - 1)) != 0)) {
            System.out.println("Not a power of 2");
            return;
        }

        int depth = (int) (Math.log(numberOfColors) / Math.log(2));
        int width = image.getWidth();
        int height = image.getHeight();

        // Store RGB values and location of each pixel in array
        int[][] pixelArr = new int[width * height][5];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int pixelIndex = i + j * width;
                int RGBInt = image.getRGB(i, j);

                pixelArr[pixelIndex][0] = (RGBInt >> 16) & 0xFF;
                pixelArr[pixelIndex][1] = (RGBInt >> 8) & 0xFF;
                pixelArr[pixelIndex][2] = RGBInt & 0xFF;
                pixelArr[pixelIndex][3] = i;
                pixelArr[pixelIndex][4] = j;
            }
        }

        // Recursively call median cut algorithm for color quantization
        splitIntoBuckets(pixelArr, depth);

        // Recolor original picture w new color palette
        for (int i = 0; i < pixelArr.length; i++) {
            image.setRGB(pixelArr[i][3], pixelArr[i][4],
                new Color(pixelArr[i][0], pixelArr[i][1],
                    pixelArr[i][2]).getRGB());
        }

        // Recolor larger pixels with dominant color from each
        int pixelWidth = 8;
        for (int i = 0; i < width; i = i + pixelWidth) {
            for (int j = 0; j < height; j = j + pixelWidth) {
                HashMap<Integer, Integer> colorFrequency = new HashMap<Integer, Integer>();
                for (int k = 0; k < pixelWidth; k++) {
                    for (int l = 0; l < pixelWidth; l++) {
                        int color = image.getRGB(i + k, j + l);
                        int count = colorFrequency.getOrDefault(color, 0);
                        colorFrequency.put(color, count + 1);
                    }
                }

                int highestFrequency = 0;
                int dominantColor = 0;
                for (int k : colorFrequency.keySet()) {
                    if (colorFrequency.get(k) > highestFrequency) {
                        highestFrequency = colorFrequency.get(k);
                        dominantColor = k;
                    }
                }
                
                for (int k = 0; k < pixelWidth; k++) {
                    for (int l = 0; l < pixelWidth; l++) {
                        image.setRGB(i + k, j + l, dominantColor);
                    }
                }
            }
        }
    }

    private static void splitIntoBuckets(int[][] pixelArr, int depth) {
        if (depth == 0) {
            colorQuantize(pixelArr);
            return;
        }

        // Find color channel w greatest range
        int redMin = 255;
        int redMax = 0;
        int greenMin = 255;
        int greenMax = 0;
        int blueMin = 255;
        int blueMax = 0;
        for (int i = 0; i < pixelArr.length; i++) {
            if (pixelArr[i][0] < redMin) {
                redMin = pixelArr[i][0];
            }
            if (pixelArr[i][0] > redMax) {
                redMax = pixelArr[i][0];
            }
            if (pixelArr[i][1] < greenMin) {
                greenMin = pixelArr[i][1];
            }
            if (pixelArr[i][1] > greenMax) {
                greenMax = pixelArr[i][1];
            }
            if (pixelArr[i][2] < blueMin) {
                blueMin = pixelArr[i][2];
            }
            if (pixelArr[i][2] > blueMax) {
                blueMax = pixelArr[i][2];
            }
        }

        int redRange = redMax - redMin;
        int greenRange = greenMax - greenMin;
        int blueRange = blueMax - blueMin;
        int greatestRange = 0;
        if (redRange >= greenRange && redRange >= blueRange) {
            greatestRange = 0;
        } else if (greenRange >= redRange && greenRange >= blueRange) {
            greatestRange = 1;
        } else if (blueRange >= redRange && blueRange >= greenRange) {
            greatestRange = 2;
        }
        final int finalGreatestRange = greatestRange;

        // Sort by values in color channel w greatest range
        Arrays.sort(pixelArr,
            Comparator.comparingInt(a -> a[finalGreatestRange]));

        splitIntoBuckets(
            Arrays.copyOfRange(pixelArr, 0, pixelArr.length / 2),
            depth - 1
        );
        splitIntoBuckets(
            Arrays.copyOfRange(pixelArr, pixelArr.length / 2, pixelArr.length),
            depth - 1
        );
    }

    // Average RGB values of each pixel in bucket and overwrite them w average
    // values
    private static void colorQuantize(int[][] pixelArr) {
        int redAvg = 0;
        int greenAvg = 0;
        int blueAvg = 0;
        for (int i = 0; i < pixelArr.length; i++) {
            redAvg += pixelArr[i][0];
            greenAvg += pixelArr[i][1];
            blueAvg += pixelArr[i][2];
        }
        redAvg = (int) Math.round(redAvg * 1.0 / pixelArr.length);
        greenAvg = (int) Math.round(greenAvg * 1.0 / pixelArr.length);
        blueAvg = (int) Math.round(blueAvg * 1.0 / pixelArr.length);

        for (int i = 0; i < pixelArr.length; i++) {
            pixelArr[i][0] = redAvg;
            pixelArr[i][1] = greenAvg;
            pixelArr[i][2] = blueAvg;
        }
    }

    public static void main(String[] args) {
    }
}
