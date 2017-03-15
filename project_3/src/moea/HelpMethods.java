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
import java.util.Iterator;
import java.util.PriorityQueue;

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
			if (count % 2000 == 0){
				System.out.println("Nr. " + count + " Duration: " + duration/Math.pow(10, 9) + "sec");				
			}	
		}
		return pixelsMST;
	}
	public static ArrayList<Pixel> minimumSpanningTree2(ArrayList<Pixel> pixels){
		ArrayList<Pixel> pixelsMST = new ArrayList<Pixel>();
		Comparator<Edge> edgeComparator = new Comparator<Edge>() {
			public int compare(Edge e1, Edge e2)
			{
				return Double.compare(e1.getWeight(), e2.getWeight());
			}
		};
		PriorityQueue<Edge> edges = new PriorityQueue<Edge>(10, edgeComparator);
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
			Edge bestEdge = edges.poll();
			bestPixel = bestEdge.getFrom();
			Pixel bestRetPixel = bestEdge.getTo();
//			System.out.println("bestID: " + bestPixel.getId());
//			System.out.println("beestRetID: " + bestRetPixel.getId());
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
			if (count % 2000 == 0){
				System.out.println("Nr. " + count + " Duration: " + duration/Math.pow(10, 9) + "sec");				
			}	
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
	
	public static ArrayList<Chromosome> createPopulation(ArrayList<Pixel> pixelsMST, int populationSize, ArrayList<Pixel> pixels){
		ArrayList<Chromosome> population = new ArrayList<Chromosome>();
		ArrayList<Edge> edges = generateEdges(pixelsMST, pixels);
		for (int i = 0; i < populationSize; i++) {
			ArrayList<Pixel> cuttedChromosome = cutIntoSegments(i+1, pixelsMST, (ArrayList<Edge>) edges.clone(), pixels);
			population.add(new Chromosome(cuttedChromosome, pixels));
		}
		return population;
	}
	
	public static ArrayList<Pixel> cutIntoSegments(int numberOfSegments, ArrayList<Pixel> pixelsMST, ArrayList<Edge> edges, ArrayList<Pixel> pixels){
		ArrayList<Pixel> cuttedPixels = (ArrayList<Pixel>) pixelsMST.clone();
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
			cuttedPixels.set(maxEdge.getFrom().getId(), pixels.get(maxEdge.getFrom().getId()));
			edges.remove(maxEdge);
		}
		return cuttedPixels;
	}
	
	public static ArrayList<Edge> generateEdges(ArrayList<Pixel> pixelsMST, ArrayList<Pixel> pixels){
		ArrayList<Edge> edgeList = new ArrayList<Edge>();
		for (int i = 0; i < pixelsMST.size(); i++) {
			double cost = pixels.get(i).getDistance(pixelsMST.get(i));
//			double minCost = INF;
//			for (Pixel neighbourPixel : pixelMap.get(i).getNeighbours()) {
//				if (pixelMap.get(i).getDistance(neighbourPixel) < minCost){
//					minCost = pixelMap.get(i).getDistance(neighbourPixel);
//				}
//			}
			//dividing on minCost for normalizing to avoid choosing edge which gives a segment with few pixels and another with a lot pixels. 
			Edge edge = new Edge(pixels.get(i), pixelsMST.get(i), cost);
			edgeList.add(edge);
		}
		return edgeList;
	}
	
	public static void mergeArrayList(ArrayList<Pixel> a1, ArrayList<Pixel> a2){
		for(Pixel p:a2){
			a1.add(p);
		}
	}
	
	public static ArrayList<ArrayList<Pixel>> decodeChromosome(ArrayList<Pixel> chromosome, ArrayList<Pixel> pixels){
		long startTime = System.nanoTime();
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		ArrayList<ArrayList<Pixel>> decodedChromosome = new ArrayList<ArrayList<Pixel>>();
		ArrayList<Boolean> visited = new ArrayList<Boolean>();
		for(int i = 0 ; i < chromosome.size(); i++){
			visited.add(false);
		}
		int index = -1;
		int lastStartIndex = 0;
		Pixel newPixel=null;
		double count1 = 0;
		double count2 = 0;
		double count3 = 0;
		double count4 = 0;
		
		long startTime3 = System.nanoTime();
		while(visited.contains(false)){
			
			ArrayList<Pixel> chain = new ArrayList<Pixel>(); //Ny kjede som foelges til en ende
			for(int i = lastStartIndex ; i < visited.size(); i++){    //finner foerste ledige sted aa starte fra
				if(!visited.get(i)){
					index = i;
					lastStartIndex = i;
					chain.add(pixels.get(index));
					break;
				}
			}
			Boolean visitedFound = false;

			long startTime2 = System.nanoTime();
			while(!visitedFound){
				Boolean tempFix = false;
				if(visited.get(index)){   //Sjekker at vi ikke har vaert innom foer. Hvis vi har det, saa skal vi avslutte kjedesoeket.
					if(!tempFix)
						newPixel = chromosome.get(index);
					long startTime1 = System.nanoTime();
					for(ArrayList<Pixel> segment:decodedChromosome){
						if(segment.contains(newPixel)){								//Finner hvilken gruppe som inneholder den noden kjeden er knyttet til.
							HelpMethods.mergeArrayList(segment, chain);			//Legger inn kjeden til ritig segment i dekodet kromosom.
							visitedFound = true;
							break;
						}
					}
					long endTime1 = System.nanoTime();
					long duration1 = (endTime1 - startTime1);
					count1+= duration1;
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
			long endTime2 = System.nanoTime();
			long duration2 = (endTime2 - startTime2);
			count2 += duration2;
		}
		long endTime3 = System.nanoTime();
		long duration3 = (endTime3 - startTime3);
		count3 += duration3;
		count3 -= (count2+count1);
		count2 -= count1;
		endTime = System.nanoTime();
		duration = (endTime - startTime);
		System.out.println("decodeChromosome: " + duration/Math.pow(10, 9) + " sec, count1: " + count1/Math.pow(10, 9) + ", count2: "+count2/Math.pow(10, 9)+ ", count3: "+count3/Math.pow(10, 9));
		return decodedChromosome;
	}
	
	public static void paintEdgesGreen(ArrayList<ArrayList<Pixel>> segments){
		for (ArrayList<Pixel> segment : segments) {
			ArrayList<Pixel> edgePixels = Functions.getEdge(segment);
			for (Pixel pixel : edgePixels) {
				pixel.paintGreen();
			}
		}
	}
	
	public static ArrayList<ArrayList<Pixel>> generateSegmentEdges(ArrayList<ArrayList<Pixel>> segments){
		ArrayList<ArrayList<Pixel>> segmentEdges = new ArrayList<ArrayList<Pixel>>();
		for (ArrayList segment : segments) {
			segmentEdges.add(Functions.getEdge(segment));
		}
		return segmentEdges;
	}
	


}
