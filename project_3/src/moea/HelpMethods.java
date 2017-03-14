package moea;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;

import javax.imageio.ImageIO;

public class HelpMethods {
	
	
	public static Pixel[][] createImagePixelByPixel(String imagePath) throws IOException{
		BufferedImage image = ImageIO.read(ReadPicture.class.getResource(imagePath));
		final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		final int width = image.getWidth();
		final int height = image.getHeight();
		Pixel[][] result = new Pixel[height][width];
		final int pixelLength = 3;
		for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
			int blue = ((int) pixels[pixel] & 0xff); // blue
			int green = (((int) pixels[pixel + 1] & 0xff) << 8); // green
			int red = (((int) pixels[pixel + 2] & 0xff) << 16); // red
			Pixel p = new Pixel(red, green, blue);
			result[row][col] = p;
			col++;
			if (col == width) {
				col = 0;
				row++;
			}
		}
		return result;
	}

}
