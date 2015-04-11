import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.io.Opener;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Soeren Berken-Mersmann on 09.04.2015.
 * Alpha Blending Plugin for ImageJ
 * Implements A over B (Porter Duff Algorithm)
 */
public class Alpha_Blending implements PlugIn {

    private final static String OUTPUT_FILE  = "result.png";

    private ImagePlus image1;
    private ImagePlus image1Alpha;
    private ImagePlus image2;
    private ImagePlus image2Alpha;

    private ColorProcessor cp;
    private ByteProcessor bp;

    @Override
    public void run(String s) {
        int[] imageIDs = WindowManager.getIDList();
        if (imageIDs == null || imageIDs.length < 1)
        {
            IJ.log("Please open at least one image before activating this plugin");
            return;
        }

        java.util.List<ImagePlus> images = new ArrayList();
        for (int i = 0; i < imageIDs.length; i++) {
            images.add(WindowManager.getImage(imageIDs[i]));
        }

        PluginDialog dialog = new PluginDialog(images);
     //   Dialog d = new Dialog(dialog);
      //  d.setVisible(true);
    }

    public void execute(ImagePlus imageA, ImagePlus imageB) {
        loadImages();

        int width = Math.max(image1.getWidth(),image2.getWidth());
        int height = Math.max(image1.getHeight(), image2.getHeight());

        cp = new ColorProcessor(width, height);
        bp = new ByteProcessor(width, height);

        int[] rgb1 = buildPixelArray(image1, width, height);
        int[] rgb2 = buildPixelArray(image2, width, height);

        int[] alpha1src = buildPixelArray(image1Alpha, width, height);
        int[] alpha2src = buildPixelArray(image2Alpha, width, height);

        int[] alpha1 = calculateAlpha(alpha1src);
        int[] alpha2 = calculateAlpha(alpha2src);

        int index;

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                index = x + width * y;
                compose(rgb1[index], alpha1[index], rgb2[index], alpha2[index], x, y);
            }
        }

        BufferedImage resultImage = create(cp, bp);
        saveResult(resultImage);
    }

    /**
     * Returns an array of all pixels contained in the image. If the image is smaller then the target image, the array will be filled with zeros.
     * @param image to get pixels from
     * @param width of the target image
     * @param height of the target image
     * @return array[width*height] of all pixels
     */
    private int[] buildPixelArray(ImagePlus image, int width, int height) {
        int[] srcArray = (int[]) image.getProcessor().getPixels();

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        if(imageWidth == width && imageHeight == height) {
            return srcArray;
        }

        int[] resArray = new int[width*height];

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                if(y >= imageHeight || x >= imageWidth) {
                    resArray[x + width * y] = 0;
                } else {
                    resArray[x + width * y] = srcArray[x + imageWidth * y];
                }
            }
        }

        return resArray;
    }

    /**
     * Converts an int[] array of RGB integers to an int[] array of alpha values ranging from 0x00 to 0xff
     * @param alphaSrc array of all pixels, RGB as int
     * @return array of all pixels, alpha as int [0x00;0xFF]
     */
    private int[] calculateAlpha(final int[] alphaSrc) {
        int[] result = new int[alphaSrc.length];
        for (int i = 0; i < alphaSrc.length; i++) {
            result[i] =  calculateTranslucency(alphaSrc[i]);
        }
        return result;
    }

    /**
     * calculates the translucency of a given RGB as int value
     * @param rgb as int [black = transparent; white = opaque]
     * @return translucency [0 = transparent ; 255 = opaque]
     */
    private int calculateTranslucency(int rgb) {
        int[] rgbChannels = extractRGB(rgb);
        return (rgbChannels[0] + rgbChannels[1] + rgbChannels[2]) / 3;
    }

    /**
     * Save the buffered image directly as a png.
     * We cannot use ImageJ directly as it doesn't support alpha channels.
     * @param resultImage BufferedImage to save
     */
    private void saveResult(BufferedImage resultImage) {
        try {
            File outputFile = new File(OUTPUT_FILE);
            ImageIO.write(resultImage, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Alpha composes two pixels
     * @param rgb1int RGB as int of image A
     * @param alpha1 Alpha as int of image A
     * @param rgb2int RGB as int of image B
     * @param alpha2 Alpha as int of image B
     * @param x coordinate of the pixels
     * @param y coordinate of the pixels
     */
    private void compose(int rgb1int, int alpha1, int rgb2int, int alpha2, int x, int y) {
        // extract colors
        int[] rgb1 = extractRGB(rgb1int);
        int[] rgb2 = extractRGB(rgb2int);

        // simple A over B
        int[] rgb = new int[3];

        // normalize alpha values
        float alpha1Normalized = alpha1 / (float) 0xff;
        float alpha2Normalized = alpha2 / (float) 0xff;

        float alphaNorm = calculateAlpha(alpha1Normalized, alpha2Normalized);
        int alpha = (int) (alphaNorm * 0xff);

        // calculate color value
        for (int i = 0; i < 3; i++) {
            // for each color channel do
            // color_A * alpha_A + color_B * alpha_B * ( 1 - alpha_A)
            // (sum weighted by alpha values)
            rgb[i] = calculateColor(rgb1[i], rgb2[i], alpha1Normalized, alpha2Normalized, alphaNorm);
        }

        // calculate alpha value

        // set output color & alpha
        cp.set(x, y, combineRGB(rgb));
        bp.set(x, y, alpha);
    }

    protected float calculateAlpha(float alpha1Normalized, float alpha2Normalized) {
        return alpha1Normalized + alpha2Normalized * (1 - alpha1Normalized);
    }

    protected int calculateColor(int color1, int color2, float alpha1Normalized, float alpha2Normalized, float alphaNorm) {
        // since png doesn't use pre-multiplied color values (where the color would be (r/a,g/a,b/a) instead of (r,g,b))
        // we can use the simple Color_A * alpha_A + Color_B * alpha_B * (1 - alpha_A)
        return (int) (color1 * alpha1Normalized + color2  * alpha2Normalized * (1 - alpha1Normalized));
    }

    /**
     * Extracts the values of each color channel (RGB)
     * @param rgbSrc RGB as int
     * @return [0]R, [1]G, [2]B
     */
    private int[] extractRGB(int rgbSrc) {
        return new int[]{
                (rgbSrc & 0x00ff0000) >> 16,
                (rgbSrc & 0x0000ff00) >> 8,
                (rgbSrc & 0x000000ff)
        };
    }

    /**
     * Combines each color channel into a single int
     * @param rgb int array with size 3 ([0]R,[1]G,[2]B)
     * @return RGB as int
     */
    private int combineRGB(int[] rgb) {
        return ((rgb[0] & 0xff) << 16 | (rgb[1] & 0xff) << 8 | (rgb[2] & 0xff));
    }

    /**
     * loads all images
     */
    private void loadImages() {
        Opener opener = new Opener();
        String pathPrefix = "./";

        image1 = opener.openImage(pathPrefix + "1.png");
        image1Alpha = opener.openImage(pathPrefix + "1a.png");
        image2 = opener.openImage(pathPrefix + "2.png");
        image2Alpha = opener.openImage(pathPrefix + "2a.png");
    }


    /**
     * Creates a BufferedImage with alpha channel.
     *
     * Credits to Jarek Sacha
     * src: http://imagej.1557.x6.nabble.com/Manage-PNG-Transparency-with-ImageJ-td3685645.html
     *
     * @param src ColorProcessor with color information
     * @param alpha ByteProcessor with alpha information
     * @return BufferedImage with alpha channel
     */
    public BufferedImage create(final ColorProcessor src, final ByteProcessor alpha) {
        if (src == null || alpha== null || src.getWidth() !=
                alpha.getWidth() || src.getHeight() != alpha.getHeight()) {
            throw new IllegalArgumentException("Input parameters are not valid: src=" + src + ", alpha=" + alpha);
        }

        final ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        final int[] bits = {8, 8, 8, 8};
        final ColorModel cm = new ComponentColorModel(cs, bits, true,
                false, Transparency.BITMASK, DataBuffer.TYPE_BYTE);
        final WritableRaster raster =
                cm.createCompatibleWritableRaster(src.getWidth(), src.getHeight());
        final DataBufferByte dataBuffer = (DataBufferByte)
                raster.getDataBuffer();

        final byte[] data = dataBuffer.getData();
        final int n = ((int[]) src.getPixels()).length;
        final byte[] r = new byte[n];
        final byte[] g = new byte[n];
        final byte[] b = new byte[n];
        final byte[] a = (byte[]) alpha.getPixels();
        src.getRGB(r, g, b);
        for (int i = 0; i < n; ++i) {
            final int offset = i * 4;
            data[offset] = r[i];
            data[offset + 1] = g[i];
            data[offset + 2] = b[i];
            data[offset + 3] = a[i];
        }

        return new BufferedImage(cm, raster, false, null);
    }

}
