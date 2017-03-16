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
import java.util.Random;

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
//				if ((pixels[i][j].getRed() != 0 || pixels[i][j].getGreen() != 0|| pixels[i][j].getBlue() != 0) &&( pixels[i][j].getRed() != 255 || pixels[i][j].getGreen() != 255 || pixels[i][j].getBlue() != 255)){
//					System.out.println(pixels[i][j].getRed() +  " " +pixels[i][j].getGreen() + " " +pixels[i][j].getBlue());
//				}
				ArrayList<Pixel> neighbours = pixels[i][j].getNeighbours();
				//insert west neighbour
				if (j != 0){
					pixels[i][j].addNeighbour((pixels[i][j-1]));
				}
				//insert north neighbour
				if (i != 0){
					pixels[i][j].addNeighbour(pixels[i-1][j]);
				}
				//insert east neighbour
				if (j != width-1){
					pixels[i][j].addNeighbour(pixels[i][j+1]);
				}
				//insert south neighbour
				if (i != height-1){
					pixels[i][j].addNeighbour(pixels[i+1][j]);
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
	
	public static ArrayList<Pixel> minimumSpanningTree2(ArrayList<Pixel> pixels){
		ArrayList<Pixel> pixelsMST = new ArrayList<Pixel>();
		int pixelsRemaining = pixels.size()-1;
		Comparator<Edge> edgeComparator = new Comparator<Edge>() {
			public int compare(Edge e1, Edge e2){
				if (e1.getWeight() > e2.getWeight()){
					return 1;
				}
				else if (e1.getWeight() < e2.getWeight()){
					return -1;
				}
				else {
					return 0;
				}
			}
		};
		PriorityQueue<Edge> edges = new PriorityQueue<Edge>(edgeComparator);
		for (int i = 0; i < pixels.size(); i++) {
			pixelsMST.add(null);
		}
		int randomPixel = (int)(Math.random()*pixels.size());
		Pixel bestPixel = pixels.get(0);
		pixelsMST.set(bestPixel.getId(), bestPixel);
		long startTime = System.nanoTime();
		while (pixelsRemaining > 0){
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
			pixelsRemaining--;
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
//				for (int i = 0; i < 100; i++) {
//					System.out.println("round: " + count + " This is the wight!: "+edges.poll().getWeight());						
//					
//				}
//				for (Edge edge : edges) {
//				}
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
	
	public static ArrayList<Chromosome> createPopulation(ArrayList<Pixel> pixelsMST, int populationSize, ArrayList<Pixel> pixels, HashMap<Pixel, ArrayList<Integer>> mapPixelToIndex){
		ArrayList<Chromosome> population = new ArrayList<Chromosome>();
		ArrayList<Edge> edges = generateEdges(pixelsMST, pixels, mapPixelToIndex);
		for (int i = 0; i < populationSize; i++) {
			long startTime = System.nanoTime();
			ArrayList<Pixel> cuttedChromosome = cutIntoSegments(i+1, pixelsMST, (ArrayList<Edge>) edges.clone(), pixels);
			population.add(new Chromosome(cuttedChromosome, pixels, i+1));
			if(population.size() >0){
				ArrayList<ArrayList<Pixel>> segments = population.get(0).getSegments();
				double red = 0 ;
				double green = 0;
				double blue = 0;
				int counter = 0;
				for(ArrayList<Pixel> segment : segments){
					for(Pixel p:segment){
						red+=p.getRed();
						green+=p.getGreen();
						blue+=p.getBlue();
					}
					counter++;
					red = red/segment.size();
					green = green/segment.size();
					blue = blue/segment.size();
					System.out.println("Segment "+counter+ "\tsize: " +segment.size() + "\tAvg red: " +red + "\tAvg green: "+green+ "\tAvg blue: "+blue );
				}
			}
			long endTime = System.nanoTime();
			long duration = endTime - startTime;
			System.out.println("Total duration: " + duration/Math.pow(10, 9) + " sec");
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
			Pixel pixel = maxEdge.getFrom();
//			System.out.println("MAXEDGE CUT SHI: " +maxEdge.getWeight());
			cuttedPixels.set(maxEdge.getFrom().getId(), pixels.get(maxEdge.getFrom().getId()));
			edges.remove(maxEdge);
			edges.add(new Edge(pixel, pixel, 0));
		}
		return cuttedPixels;
	}
	
	public static ArrayList<Edge> generateEdges(ArrayList<Pixel> pixelsMST, ArrayList<Pixel> pixels, HashMap<Pixel, ArrayList<Integer>> mapPixelToIndex){
		ArrayList<Edge> edgeList = new ArrayList<Edge>();
		for (int i = 0; i < pixelsMST.size(); i++) {
			Pixel p = pixels.get(i);
			double cost = p.getDistance(pixelsMST.get(i));
			double minCost = cost;
			for (Integer index : mapPixelToIndex.get(p)) {
				double weight = pixels.get(index).getDistance(p);
				if (weight < minCost){
					minCost += weight;
				}				
			}
			//dividing on minCost for normalizing to avoid choosing edge which gives a segment with few pixels and another with a lot pixels. 
			
			Edge edge = new Edge(pixels.get(i), pixelsMST.get(i), cost/(minCost+0.00000000001));
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
		ArrayList<ArrayList<Pixel>> decodedChromosome = new ArrayList<ArrayList<Pixel>>();
		ArrayList<Boolean> visited = new ArrayList<Boolean>();
		ArrayList<Integer> pixelToSegment = new ArrayList<Integer>();
		int remainingPixels = pixels.size();
		for(int i = 0 ; i < chromosome.size(); i++){
			visited.add(false);
			pixelToSegment.add(null);
		}
		int index = -1;
		int lastStartIndex = 0;
		Pixel newPixel=null;
		
		long startTime3 = System.nanoTime();
		int chainNr = 0;
		while(remainingPixels > 0){
			ArrayList<Pixel> chain = new ArrayList<Pixel>(); //Ny kjede som foelges til en ende
			for(int i = lastStartIndex ; i < visited.size(); i++){    //finner foerste ledige sted aa starte fra
				if(!visited.get(i)){
					index = i;
					lastStartIndex = i;
					chain.add(pixels.get(index));
					break;
				}
			}

			while(true){
				Boolean tempFix = false;
				if(visited.get(index)){   //Sjekker at vi ikke har vaert innom foer. Hvis vi har det, saa skal vi avslutte kjedesoeket.
					if(!tempFix){
						newPixel = chromosome.get(index);						
					}
					if (pixelToSegment.get(index) != null){
						HelpMethods.mergeArrayList(decodedChromosome.get(pixelToSegment.get(index)), chain);
						for (int i = 0; i < chain.size(); i++) {
							int ind = chain.get(i).getId();
							pixelToSegment.set(ind, pixelToSegment.get(index));
						}
						remainingPixels -= chain.size();
					}
					else{
						for (int i = 0; i < chain.size(); i++) {
							int ind = chain.get(i).getId();
							pixelToSegment.set(ind, chainNr);
						}
						decodedChromosome.add(chain);
						chainNr++;
						remainingPixels -= chain.size();		
					}
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
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("decodeChromosome: " + duration/Math.pow(10, 9));
		return decodedChromosome;
	}
	
	public static void paintEdgesGreen(Chromosome chromosome){
		for (ArrayList<Pixel> segmentEdges : chromosome.getSegmentEdges()) {
			for (Pixel pixel : segmentEdges) {
				pixel.paintGreen();
			}
		}
	}
	
	public static ArrayList<ArrayList<Pixel>> generateSegmentEdges(ArrayList<ArrayList<Pixel>> segments, ArrayList<Integer> pixelToSegment){
		ArrayList<ArrayList<Pixel>> segmentEdges = new ArrayList<ArrayList<Pixel>>();
		for (int i = 0; i < segments.size(); i++) {
			segmentEdges.add(Functions.getEdge(segments.get(i), i, pixelToSegment));
		}
		return segmentEdges;
	}
	
	public static HashMap<Pixel, ArrayList<Integer>> createMapPixelToIndex(ArrayList<Pixel> representation){
		HashMap<Pixel, ArrayList<Integer>> mapPixelToIndex = new HashMap<Pixel, ArrayList<Integer>>();
		for (int i = 0; i < representation.size(); i++) {
			Pixel pixel = representation.get(i);
			if (mapPixelToIndex.get(pixel) == null){
				ArrayList<Integer> indexes = new ArrayList<Integer>();
				indexes.add(i);
				mapPixelToIndex.put(pixel, indexes);
			}
			else{
				mapPixelToIndex.get(pixel).add(i);
			}
		}
		return mapPixelToIndex;
	}
	
	public static void selection(ArrayList<Chromosome> population){
		long seed = System.nanoTime();
		ArrayList<Chromosome> selectedPopulation = new ArrayList<Chromosome>();
		Collections.shuffle(population, new Random(seed));	
		
		for (int i = 0; i < population.size()-1; i+=2) {
			Chromosome chr1 = population.get(i);
			Chromosome chr2 = population.get(i+1);
			Chromosome fittest = chr2;
			if (chr1.getFintessValue() < chr2.getFintessValue()){
				fittest = chr1;
			}
			int random = (int)(Math.random()*2);
			if (random < 1000*Variables.selectBestChromosomeRate){
				selectedPopulation.add(fittest);
			}
			else{
				random = (int) (Math.random()*2);
				if (random == 0){
					selectedPopulation.add(chr1);
				}
				else{
					selectedPopulation.add(chr2);					
				}
			}
		}
	}
	
	public static void crossover(ArrayList<Chromosome> selectedPopulation){
		
	}
	
	public static Chromosome[] generateOffsprings(Chromosome chr1, Chromosome chr2){
		Chromosome[] offsprings = new Chromosome[2];
		
		
		return offsprings;
	}
	
	public static void mutation(Chromosome mutation){
		
	}
	
//	public static void main(String[] args) {
//		ArrayList<Pixel> pixels = new ArrayList<Pixel>();
//		ArrayList<Pixel> chromosome = new ArrayList<Pixel>();
//		for(int i = 0 ; i < 16 ; i++){
//			pixels.add(new Pixel(0, 0, 0, 0, true, i));
//		}
//		chromosome.add(pixels.get(1));
//		chromosome.add(pixels.get(5));
//		chromosome.add(pixels.get(1));
//		chromosome.add(pixels.get(3));
//		chromosome.add(pixels.get(0));
//		chromosome.add(pixels.get(4));
//		chromosome.add(pixels.get(2));
//		chromosome.add(pixels.get(3));
//		chromosome.add(pixels.get(9));
//		chromosome.add(pixels.get(10));
//		chromosome.add(pixels.get(14));
//		chromosome.add(pixels.get(7));
//		chromosome.add(pixels.get(8));
//		chromosome.add(pixels.get(12));
//		chromosome.add(pixels.get(15));
//		chromosome.add(pixels.get(15));
//		
//		System.out.println(HelpMethods.decodeChromosome(chromosome, pixels));
//		
//		
//	}
	
//	public static void main(String[] args) {
//		Pixel p1 = new Pixel(0, 0, 0, 0, false, 0);
//		Pixel p2 = new Pixel(255, 255, 255, 0, false, 1);
//		System.out.println(Functions.pixelToPixelDeviation(p1, p2));
//	}

}
