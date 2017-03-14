package moea;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DrawImage extends JFrame{

	public DrawImage(Pixel[][] pixels){
		JPanel panel = new JPanel();
		getContentPane().add( panel );

		int width = pixels[0].length;
		int height = pixels.length;
		int[] rgbValues = new int[width*height];
		int c = 0;
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[0].length; j++) {
				int alpha = pixels[i][j].getAlpha();
				int red = pixels[i][j].getRed();
				int blue = pixels[i][j].getBlue();
				int green = pixels[i][j].getGreen();
				rgbValues[c] = ((alpha&0x0ff)<<24)|((red&0x0ff)<<16)|((green&0x0ff)<<8)|(blue&0x0ff);
				c++;
			}
		}

		panel.add( createImageLabel(rgbValues, width, height) );
	}

	private JLabel createImageLabel(int[] pixels, int width, int height){
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		WritableRaster raster = image.getRaster();
		raster.setDataElements(0, 0, width, height, pixels);
		JLabel label = new JLabel( new ImageIcon(image) );
		return label;
	}

}
