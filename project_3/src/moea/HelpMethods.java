package moea;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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
		}
		return createPixelNeighbours(result);
	}
	
	public static ArrayList<Pixel> generatePixelList(Pixel[][] pixels){
		int height = pixels.length;
		int width = pixels[0].length;
		ArrayList<Pixel> pixelList = new ArrayList<Pixel>();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				pixelList.add(pixels[i][j]);
			}
		}
		return pixelList;
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
	
	public static ArrayList<ArrayList<Pixel>> generateImage(Pixel[][] pixels){
		ArrayList<ArrayList<Pixel>> image = new ArrayList<ArrayList<Pixel>>();
		for(int i = 0 ; i < pixels.length ; i++){
			image.add(new ArrayList<Pixel>());
			for(int j = 0 ; j < pixels[0].length ; j++){
				image.get(i).add(pixels[i][j]);
			}
		}
		return image;
	}

	public static void drawImage(ArrayList<ArrayList<Pixel>> pixels){
		JFrame frame = new DrawImage(pixels);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo( null );
        frame.setVisible( true );
	}
	
	public static void mergeArrayList(ArrayList<Pixel> a1, ArrayList<Pixel> a2){
		for(Pixel p:a2){
			a1.add(p);
		}
	}
	
	public static ArrayList<ArrayList<Pixel>> decodeChromosome(ArrayList<Pixel> chromosome, ArrayList<Pixel> pixels){
		ArrayList<ArrayList<Pixel>> decodedChromosome = new ArrayList<ArrayList<Pixel>>();
		ArrayList<Boolean> visited = new ArrayList<Boolean>();
		for(int i = 0 ; i < chromosome.size(); i++){
			visited.add(false);
		}
		int index = -1;
		Pixel newPixel=null;



		while(visited.contains(false)){
			ArrayList<Pixel> chain = new ArrayList<Pixel>(); //Ny kjede som foelges til en ende
			for(int i = 0 ; i < visited.size(); i++){    //finner foerste ledige sted aa starte fra
				if(!visited.get(i)){
					index = i;
					chain.add(pixels.get(index));
					break;
				}
			}
			Boolean visitedFound = false;

			while(!visitedFound){
				Boolean tempFix = false;
				if(visited.get(index)){   //Sjekker at vi ikke har vaert innom foer. Hvis vi har det, saa skal vi avslutte kjedesoeket.
					if(!tempFix)
						newPixel = chromosome.get(index);
					for(ArrayList<Pixel> segment:decodedChromosome){
						if(segment.contains(newPixel)){								//Finner hvilken gruppe som inneholder den noden kjeden er knyttet til.
							HelpMethods.mergeArrayList(segment, chain);			//Legger inn kjeden til ritig segment i dekodet kromosom.
							visitedFound = true;
							break;
						}
					}
					if(visitedFound){
						break;
					}
					decodedChromosome.add(chain);
					break;
				}

				visited.set(index, true);
				newPixel = chromosome.get(index);									//Hvis vi ikke har besoekt den nye kjeden, saa finner vi hvilket pixel som ligger paa den nye indexen.

				index = newPixel.getId();
				if(!visited.get(index)){
					chain.add(newPixel);
					tempFix=true;												//Legger til ubesoekt node i segmentet. 
				}
			}
		}
		System.out.println(visited);
		return decodedChromosome;
	}
	


}
