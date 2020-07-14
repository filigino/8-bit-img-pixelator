/* 
 * Implement color quantization using the median cut algorithm.
 * 
 * Uses Princeton's Picture library:
 * https://introcs.cs.princeton.edu/java/stdlib/javadoc/Picture.html
 */


import java.awt.Color;
import java.io.File;
import java.lang.Math;
import java.util.Arrays;
import java.util.Comparator;

public class Pixelator {
    private void pixelate(Picture picture, int numberOfColors) {
        if (numberOfColors > 0 && ((numberOfColors & (numberOfColors - 1)) != 0)) {
            System.out.println("Not a power of 2");
            return;
        }
        int depth = (int) (Math.log(numberOfColors) / Math.log(2));

        // Store RGB values and location of each pixel in array
        int[][] pixelArr = new int[picture.width() * picture.height()][5];
        for (int i = 0; i < picture.width(); i++) {
            for (int j = 0; j < picture.height(); j++) {
                int pixelIndex = i + j * picture.height();
                int RGBInt = picture.getRGB(i, j);
                Color pixelColor = new Color(RGBInt);

                pixelArr[pixelIndex][0] = pixelColor.getRed();
                pixelArr[pixelIndex][1] = pixelColor.getGreen();
                pixelArr[pixelIndex][2] = pixelColor.getBlue();
                pixelArr[pixelIndex][3] = i;
                pixelArr[pixelIndex][4] = j;
            }
        }

        // Recursively call median cut algorithm for color quantization
        this.splitIntoBuckets(pixelArr, depth);

        // Recolor original picture w new color palette
        for (int i = 0; i < pixelArr.length; i++) {
            picture.set(pixelArr[i][3], pixelArr[i][4],
                new Color(pixelArr[i][0], pixelArr[i][1], pixelArr[i][2]));
        }
    }

    private void splitIntoBuckets(int[][] pixelArr, int depth) {
        if (depth == 0) {
            this.colorQuantize(pixelArr);
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

        this.splitIntoBuckets(
            Arrays.copyOfRange(pixelArr, 0, pixelArr.length / 2),
            depth - 1
        );
        this.splitIntoBuckets(
            Arrays.copyOfRange(pixelArr, pixelArr.length / 2, pixelArr.length),
            depth - 1
        );
    }

    private void colorQuantize(int[][] pixelArr) {
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
        Pixelator pixelator = new Pixelator();
        Picture picture = new Picture(new File("ATRZ5207.jpg"));
        picture.show();
        pixelator.pixelate(picture, 16);
        picture.show();
    }
}
