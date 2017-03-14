package moea;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class HelpMethods {


	public static Pixel[][] createImagePixelByPixel(String imagePath) throws IOException{
		BufferedImage image = ImageIO.read(HelpMethods.class.getResource(imagePath));
		final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		final int width = image.getWidth();
		final int height = image.getHeight();
		Pixel[][] result = new Pixel[height][width];
		final boolean hasAlphaChannel = image.getAlphaRaster() != null;
		if (hasAlphaChannel) {
			System.out.println("1");
			final int pixelLength = 4;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
				int alpha = (((int) pixels[pixel] & 0xff) << 24); // alpha
				int blue = ((int) pixels[pixel] & 0xff); // blue
				int green = (((int) pixels[pixel + 1] & 0xff) << 8); // green
				int red = (((int) pixels[pixel + 2] & 0xff) << 16); // red
				Pixel p = new Pixel(red, green, blue, alpha, true);
				result[row][col] = p;
				col++;
				if (col == width) {
					col = 0;
					row++;
				}
			}
		} else {
			System.out.println("2");
			final int pixelLength = 3;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
				int alpha = -16777216; // 255 alpha
				int blue = ((int) pixels[pixel] & 0xff); // blue
				int green = (((int) pixels[pixel + 1] & 0xff) << 8); // green
				int red = (((int) pixels[pixel + 2] & 0xff) << 16); // red
				Pixel p = new Pixel(red, green, blue, alpha, false);
				result[row][col] = p;
				col++;
				if (col == width) {
					col = 0;
					row++;
				}
			}
		}
		return result;
	}

	public static void drawImage(Pixel[][] pixels){
		JFrame frame = new DrawImage(pixels);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo( null );
        frame.setVisible( true );
	}

}
