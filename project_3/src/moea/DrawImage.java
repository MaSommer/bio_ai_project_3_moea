package moea;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class DrawImage extends JFrame{
	
	private JPanel panel;
	private ArrayList<ArrayList<Pixel>> segments;
	private long duration;

	public DrawImage(ArrayList<ArrayList<Pixel>> pixels, ArrayList<ArrayList<Pixel>> segments, long duration){
		panel = new JPanel();
		this.segments = segments;
		this.duration = duration;
		getContentPane().add( panel );
		int height = pixels.size();
		int width = pixels.get(0).size();
		int[] rgbValues = new int[height*width];
		int c = 0;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int alpha = pixels.get(i).get(j).getAlpha();
				int red = pixels.get(i).get(j).getRed();
				int blue = pixels.get(i).get(j).getBlue();
				int green = pixels.get(i).get(j).getGreen();
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
		setLayout(new FlowLayout());
//	    panel.setLayout(new FlowLayout()); 
//	    panel.add(label = new JLabel("add JLabel"));
//	    add(panel);
		String text = "<html>" 				
				+ "Segments: " + segments.size() 
				+ "<br><br>PopSize: " + Variables.pSize 
				+ "<br>InitialiseringsKutt: "+ Variables.numberOfFirstInitializingCut + "-"+ (int)(Variables.numberOfFirstInitializingCut+Variables.pSize)
				+ "<br>Gen: " + Variables.numberOfGenerations 
				+ "<br>Opt.Nr.Sol: " + Variables.optimalNumberOfSegments 
				+ "<br><br>MutMergeSmallesRate: " + (double) (1-Variables.mutationMergeAllCombinationsRate) 
				+ "<br>MutMergeBestComb: " + Variables.mutationMergeAllCombinationsRate 
				+ "<br>MutSplit: " + Variables.mutationSplitRate 
				+ "<br>CrossoverRate" + Variables.crossoverRate
				+ "<br><br>Dev: " + Variables.activeObjectives[0] 
				+ "<br>Edge: " + Variables.activeObjectives[1] 
				+ "<br>Con: " + Variables.activeObjectives[2] 
				+ "<br><br>MinSegmentSize: " + Variables.minimumSegmentSize  
				+ "<br><br>Total duration: " + duration + " sec"  
				+ "</html>";
		label.setText(text);
		label.setVerticalTextPosition(SwingConstants.TOP);
//		label.setToolTipText(text);
		add(label);
		return label;
	}

}
