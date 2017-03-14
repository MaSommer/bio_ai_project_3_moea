package moea;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.util.ArrayList;

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
		int c = 0;
		if (hasAlphaChannel) {
			final int pixelLength = 4;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
				int alpha = (((int) pixels[pixel] & 0xff) << 24); // alpha
				int blue = ((int) pixels[pixel] & 0xff); // blue
				int green = (((int) pixels[pixel + 1] & 0xff) << 8); // green
				int red = (((int) pixels[pixel + 2] & 0xff) << 16); // red
				Pixel p = new Pixel(red, green, blue, alpha, true, c);
				result[row][col] = p;
				col++;
				if (col == width) {
					col = 0;
					row++;
				}
				c++;
			}
		} else {
			final int pixelLength = 3;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
				int alpha = -16777216; // 255 alpha
				int red = (((int) pixels[pixel + 2])  & 0xff); // red
				int green = (((int) pixels[pixel + 1])  & 0xff); // green
				int blue = ((int) pixels[pixel] & 0xff); // blue
//				System.out.println("b: "+ blue);
//				System.out.println("g: "+ green);
//				System.out.println("r: "+ red);
				Pixel p = new Pixel(red, green, blue, alpha, false, c);
				result[row][col] = p;
				col++;
				if (col == width) {
					col = 0;
					row++;
				}
				c++;
			}
			System.out.println(result.length);
			System.out.println(result[0].length);
		}
		return createPixelNeighbours(result);
	}
	
	public static Pixel[][] createPixelNeighbours(Pixel[][] pixels){
		int height = pixels.length;
		int width = pixels[0].length;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				ArrayList<Pixel> neighbours = pixels[i][j].getNeighbours();
				//insert west neighbour
				if (j != 0){
					neighbours.add(pixels[i][j-1]);
				}
				//insert north neighbour
				if (i != 0){
					neighbours.add(pixels[i-1][j]);
				}
				//insert east neighbour
				if (j != width-1){
					neighbours.add(pixels[i][j+1]);
				}
				//insert south neighbour
				if (i != height-1){
					neighbours.add(pixels[i+1][j]);
				}
			}
		}
		return pixels;
	}

	public static void drawImage(Pixel[][] pixels){
		JFrame frame = new DrawImage(pixels);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo( null );
        frame.setVisible( true );
	}

}
