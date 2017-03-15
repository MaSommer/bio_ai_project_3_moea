package moea;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class HelpMethods {

	private static final double INF = Double.MAX_VALUE;


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
				int red = -1;
				if (pixel + 2 >= pixels.length){
					red = 0;
				}
				else{
					red = (((int) pixels[pixel + 2])  & 0xff); // red					
				}
				int green = -1;
				if (pixel + 1 >= pixels.length){
					green = 0;
				}
				else{
					green = (((int) pixels[pixel + 1])  & 0xff); // green					
				}
				int blue = ((int) pixels[pixel] & 0xff); // blue
				Pixel p = new Pixel(red, green, blue, alpha, false, c);
				result[row][col] = p;
				col++;
				if (col == width) {
					col = 0;
					row++;
				}
				c++;
				if (pixel+2 < pixels.length){
					
				}
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
				for(Pixel neighbour: neighbours){
					pixels[i][j].getNeighbourDistances().add(Functions.pixelToPixelDeviation(pixels[i][j], neighbour));
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
	public static ArrayList<Pixel> minimumSpanningTree(ArrayList<Pixel> pixels){
		ArrayList<Pixel> pixelsMST = new ArrayList<Pixel>();
		ArrayList<Edge> edges = new ArrayList<Edge>();
		for (int i = 0; i < pixels.size(); i++) {
			pixelsMST.add(null);
		}
		int randomPixel = (int)(Math.random()*pixels.size());
		Pixel bestPixel = pixels.get(randomPixel);
		pixelsMST.set(bestPixel.getId(), bestPixel);
		long startTime = System.nanoTime();
		while (pixelsMST.contains(null)){
			for (Pixel neighbourPixel : bestPixel.getNeighbours()) {
				if (pixelsMST.get(neighbourPixel.getId()) == null){
					Edge edge = new Edge(neighbourPixel, bestPixel, neighbourPixel.getDistance(bestPixel));
					edges.add(edge);					
				}
			}
			Collections.sort(edges, new Comparator<Edge>() {
				public int compare(Edge e1, Edge e2)
				{
					return Double.compare(e1.getWeight(), e2.getWeight());
				}
			});
			bestPixel = edges.get(0).getFrom();
			Pixel bestRetPixel = edges.get(0).getTo();
//			System.out.println("bestID: " + bestPixel.getId());
//			System.out.println("beestRetID: " + bestRetPixel.getId());
			edges.remove(0);
			pixelsMST.set(bestPixel.getId(), bestRetPixel);
			ArrayList<Edge> edgesToRemove = new ArrayList<Edge>();
			for (Edge edge : edges) {
				if (edge.getFrom().getId() == bestPixel.getId()){
					edgesToRemove.add(edge);
				}
			}
			for (Edge edge : edgesToRemove) {
				edges.remove(edge);
			}

			long endTime = System.nanoTime();
			long duration = (endTime - startTime);
			int count = 0;
			for (Pixel pixel2 : pixelsMST) {
				if (pixel2 != null){
					count++;
				}
			}
//			if (count % 2000 == 0){
//				System.out.println("Nr. " + count + " Duration: " + duration/Math.pow(10, 9) + "sec");				
//			}	
		}
		return pixelsMST;
	}
	
	public static HashMap<Integer, Pixel> generatePixelMap(ArrayList<Pixel> pixels){
		HashMap<Integer, Pixel> pixelMap = new HashMap<Integer, Pixel>();
		for (int i = 0; i < pixels.size(); i++) {
			pixelMap.put(i, pixels.get(i));
		}
		return pixelMap;
	}
	
	public static ArrayList<ArrayList<Pixel>> createPopulation(ArrayList<Pixel> pixelsMST, int populationSize, HashMap<Integer, Pixel> pixelsMap){
		ArrayList<ArrayList<Pixel>> population = new ArrayList<ArrayList<Pixel>>();
		ArrayList<Edge> edges = generateEdges(pixelsMST, pixelsMap);
		for (int i = 0; i < populationSize; i++) {
			ArrayList<Pixel> cuttedChromosome = cutIntoSegments(i+1, pixelsMST, (ArrayList<Edge>) edges.clone(), pixelsMap);
			population.add(cuttedChromosome);
		}
		return population;
	}
	
	public static ArrayList<Pixel> cutIntoSegments(int numberOfSegments, ArrayList<Pixel> pixels, ArrayList<Edge> edges, HashMap<Integer, Pixel> pixelsMap){
		ArrayList<Pixel> cuttedPixels = (ArrayList<Pixel>) pixels.clone();
		for (int i = 0; i < numberOfSegments; i++) {
			Edge maxEdge = Collections.max(edges, new Comparator<Edge>() {
				public int compare(Edge e1, Edge e2) {
					if (e1.getWeight() > e2.getWeight())
						return 1;
					else if (e1.getWeight() < e2.getWeight())
						return -1;
					return 0;
				}
			});
			cuttedPixels.set(maxEdge.getFrom().getId(), pixelsMap.get(maxEdge.getFrom().getId()));
			edges.remove(maxEdge);
		}
		return cuttedPixels;
	}
	
	public static ArrayList<Edge> generateEdges(ArrayList<Pixel> pixelsMST, HashMap<Integer, Pixel> pixelMap){
		ArrayList<Edge> edgeList = new ArrayList<Edge>();
		for (int i = 0; i < pixelsMST.size(); i++) {
			double cost = pixelMap.get(i).getDistance(pixelsMST.get(i));
//			double minCost = INF;
//			for (Pixel neighbourPixel : pixelMap.get(i).getNeighbours()) {
//				if (pixelMap.get(i).getDistance(neighbourPixel) < minCost){
//					minCost = pixelMap.get(i).getDistance(neighbourPixel);
//				}
//			}
			//dividing on minCost for normalizing to avoid choosing edge which gives a segment with few pixels and another with a lot pixels. 
			Edge edge = new Edge(pixelMap.get(i), pixelsMST.get(i), cost);
			edgeList.add(edge);
		}
		return edgeList;
	}

}
