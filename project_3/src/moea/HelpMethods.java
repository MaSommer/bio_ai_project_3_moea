package moea;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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

	public static void drawImage(Chromosome chr, long duration){
		JFrame frame = new DrawImage(chr, duration);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo( null );
		frame.setVisible( true );
	}
	
	public static ArrayList<Pixel> minimumSpanningTree2(ArrayList<Pixel> pixels){
		boolean[] isAdded = new boolean[pixels.size()];
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
		PriorityQueue<Edge> edges = new PriorityQueue<Edge>(10, edgeComparator);
		for (int i = 0; i < pixels.size(); i++) {
			pixelsMST.add(null);
			isAdded[i] = false;
		}
		Pixel bestPixel = pixels.get(0);
		pixelsMST.set(bestPixel.getId(), bestPixel);
		isAdded[bestPixel.getId()] = true;
		long startTime = System.nanoTime();
		while (pixelsRemaining > 0){
			int i = 0;
			for (Pixel neighbourPixel : bestPixel.getNeighbours()) {
				if (pixelsMST.get(neighbourPixel.getId()) == null){
					Edge edge = new Edge(neighbourPixel, bestPixel, neighbourPixel.getDistance(bestPixel));
//					Edge edge = new Edge(neighbourPixel, bestPixel, neighbourPixel.getNeighbourDistances().get(i));
					edges.add(edge);	
					i++;
				}
			}
			while (true){
				if (pixelsMST.get(edges.peek().getFrom().getId()) != null){
					edges.poll();
				}
				else{
					Edge bestEdge = edges.poll();
					bestPixel = bestEdge.getFrom();
					Pixel bestRetPixel = bestEdge.getTo();
					pixelsMST.set(bestPixel.getId(), bestRetPixel);
					pixelsRemaining--;
//					isAdded[edges.peek().getFrom().getId()] = true;
					break;
				}
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
				System.out.println(count + " edges is initialized. Duration: " + duration/Math.pow(10, 9) + " sec");	
			}	
		}
		return pixelsMST;
	}
	
	public static ArrayList<Chromosome> createPopulation(ArrayList<Pixel> pixelsMST, ArrayList<Pixel> pixels, ArrayList<ArrayList<Pixel>> image, ArrayList<double[]> distances){
		ArrayList<Chromosome> population = new ArrayList<Chromosome>();
		ArrayList<Pixel> cuttedChromosome = (ArrayList<Pixel>) pixelsMST.clone();
		HashMap<Pixel, ArrayList<Integer>> mapPixelsThatPointsOnPixel = createMapPixelToIndex((ArrayList<Pixel>) pixelsMST.clone());
		int pixelIdToCut = cutIntoTwoSegments(pixelsMST, pixels, mapPixelsThatPointsOnPixel, pixels, Variables.minimumSegmentSize);
		for (int i = 0; i < Variables.pSize+Variables.numberOfFirstInitializingCut; i++) {
			long startTime = System.nanoTime();
			
			cuttedChromosome.set(pixelIdToCut, pixels.get(pixelIdToCut));
			Chromosome chr = new Chromosome((ArrayList<Pixel>) cuttedChromosome.clone(), pixels, i+1, distances, image);

			//finds the biggest segment after the cut
			ArrayList<ArrayList<Pixel>> segments = chr.getSegments();
			int maxSegmentIndex = -1;
			int maxSegmentSize = Integer.MIN_VALUE;
			for (int j = 0; j < segments.size(); j++) {
				if (segments.get(j).size() > maxSegmentSize){
					maxSegmentSize = segments.get(j).size();
					maxSegmentIndex = j;
				}
			}
			ArrayList<Pixel> segment = segments.get(maxSegmentIndex);
			
			if (i > Variables.numberOfFirstInitializingCut){
				population.add(chr);				
			}
			mapPixelsThatPointsOnPixel = createMapPixelToIndex((ArrayList<Pixel>) cuttedChromosome.clone());
			
			//cut the next biggest segment
			pixelIdToCut = cutIntoTwoSegments((ArrayList<Pixel>) cuttedChromosome.clone(), pixels, mapPixelsThatPointsOnPixel, segment, Variables.minimumSegmentSize);

			long endTime = System.nanoTime();
			long duration = endTime - startTime;
			System.out.println("Total duration: " + duration/Math.pow(10, 9) + " sec");
		}
		return population;
	}
	
	//Returns the pixel id that should point to itself instead. This will be the cut.
	public static int cutIntoTwoSegments(ArrayList<Pixel> representation, ArrayList<Pixel> pixels, HashMap<Pixel, ArrayList<Integer>> mapPixelsThatPointsOnPixel, ArrayList<Pixel> segment, int minSegmentSize){
		ArrayList<Edge> edges = generateEdgesWithMap(representation, segment, pixels, mapPixelsThatPointsOnPixel);
		int originalSegmentSize = segment.size();
//		for (Edge edge : edges) {
//			edge.setFakeWeight(edge.getWeight());			
//		}
		int[] segmentSizes;
		Collections.sort(edges, new Comparator<Edge>() {
			public int compare(Edge e1, Edge e2) {
				if (e1.getWeight() < e2.getWeight())
					return 1;
				else if (e1.getWeight() > e2.getWeight())
					return -1;
				return 0;
			}
		});
		int index = 0;
		Edge maxEdge = null;
		while (true){
			if (index == edges.size()){
				return -1;
			}
			maxEdge = edges.get(index);
			segmentSizes = setSegmentSizeForEachPixel(maxEdge, mapPixelsThatPointsOnPixel, pixels, originalSegmentSize);
			if (segmentSizes[0] < minSegmentSize || segmentSizes[1] < minSegmentSize){
				index++;
				continue;
			}
			System.out.println("Seg sizez: " + Arrays.toString(segmentSizes));
			break;
		}
		return maxEdge.getFrom().getId();
	}
	
	public static ArrayList<Edge> generateEdgesWithMap(ArrayList<Pixel> pixelsMST, ArrayList<Pixel> segment, ArrayList<Pixel> pixels, HashMap<Pixel, ArrayList<Integer>> mapPixelToIndex){
		ArrayList<Edge> edgeList = new ArrayList<Edge>();
		for (int i = 0; i < segment.size(); i++) {
			Pixel p = segment.get(i);
			double cost = p.getDistance(pixelsMST.get(p.getId()));
			double minCost = cost;
			if (mapPixelToIndex.get(p) != null){
				for (Integer index : mapPixelToIndex.get(p)) {
					double weight = pixels.get(index).getDistance(p);
					if (weight < minCost){
						minCost += weight;
					}				
				}				
			}
			//dividing on minCost for normalizing to avoid choosing edge which gives a segment with few pixels and another with a lot pixels. 
			
			Edge edge = new Edge(segment.get(i), pixelsMST.get(i), cost/(minCost+0.00000000001));
			edgeList.add(edge);
		}
		
		return edgeList;
	}
	
	public static int[] setSegmentSizeForEachPixel(Edge edge, HashMap<Pixel, ArrayList<Integer>> mapPixelsThatPointsOnPixel, ArrayList<Pixel> pixels, int originalSegmentSize){
		int segmentSizeFrom = 0;
		int segmentSizeTo = 0;
		int fromPixelId = edge.getFrom().getId();
		ArrayList<Integer> pointsToFromPixel = mapPixelsThatPointsOnPixel.get(pixels.get(fromPixelId));
		
		segmentSizeFrom = recursiveSegmentSizeCounter(fromPixelId, pointsToFromPixel, segmentSizeFrom, mapPixelsThatPointsOnPixel, pixels);
		segmentSizeTo = originalSegmentSize - segmentSizeFrom;
		
		int[] ret = {segmentSizeFrom, segmentSizeTo};
		return ret;
	}
	
	public static int recursiveSegmentSizeCounter(int pixelId, ArrayList<Integer> pixelsList, int counter, HashMap<Pixel, ArrayList<Integer>> mapPixelsThatPointsOnPixel, ArrayList<Pixel> pixels){
		counter++;
		if (pixelsList == null){
			return counter;
		}
		for (Integer integer : pixelsList) {
			if (pixelId == integer){
				continue;
			}
			ArrayList<Integer> newPixelsList = mapPixelsThatPointsOnPixel.get(pixels.get(integer));
			counter = recursiveSegmentSizeCounter(integer, newPixelsList, counter, mapPixelsThatPointsOnPixel, pixels);
		}
		return counter;
	}
	
	public static void mergeArrayList(ArrayList<Pixel> a1, ArrayList<Pixel> a2){
		for(Pixel p:a2){
			a1.add(p);
		}
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
	
	//Key: Pixel, Value: A list of the pixels ids that points to the key in the MST
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
	
	
//	public static ArrayList<Chromosome> selection(ArrayList<Chromosome> population, ArrayList<Pixel> pixels){
//		long seed = System.nanoTime();
//		ArrayList<Chromosome> selectedPopulation = new ArrayList<Chromosome>();
//		selectedPopulation.add(new Chromosome(HelpMethods.findBestChromosome(population)));
//		Collections.shuffle(population, new Random(seed));	
//		
//		for (int i = 0; i < population.size()-1; i+=2) {
//			Chromosome chr1 = population.get(i);
//			Chromosome chr2 = population.get(i+1);
//			Chromosome fittest = chr2;
//			if (chr1.getFitnessValue() < chr2.getFitnessValue()){
//				fittest = chr1;
//			}
//			int random = (int)(Math.random()*2);
//			if (random < 1000*Variables.selectBestChromosomeRate){
//				selectedPopulation.add(fittest);
//			}
//			else{
//				random = (int) (Math.random()*2);
//				if (random == 0){
//					selectedPopulation.add(chr1);
//				}
//				else{
//					selectedPopulation.add(chr2);					
//				}
//			}
//		}
////		population = selectedPopulation;
//		return selectedPopulation;
//	}
	
	public static ArrayList<Chromosome> crossover(ArrayList<Chromosome> selectedPopulation, ArrayList<Pixel> pixels){
		ArrayList<Chromosome> population = new ArrayList<Chromosome>();
		while(population.size() < Variables.pSize){
			for(int i = 0 ; i < selectedPopulation.size()-1; i+=2){
				Chromosome chr1 = selectedPopulation.get(i);
				Chromosome chr2 = selectedPopulation.get(i+1);
				ArrayList<Chromosome> children = HelpMethods.generateOffsprings(chr1, chr2, Variables.mixingRate, pixels);
				population.addAll(children);
				
			}
			Collections.shuffle(selectedPopulation);
		}
//		selectedPopulation = population;
		return population;
	}
	
	private static ArrayList<Chromosome> generateOffsprings(Chromosome chr1, Chromosome chr2, double mixingRate, ArrayList<Pixel> pixels){
		ArrayList<Chromosome> offSprings = new ArrayList<Chromosome>();
		ArrayList<Pixel> representation1 = chr1.getRepresentation();
		ArrayList<Pixel> representation2 = chr2.getRepresentation();
		for(int i = 0 ; i < representation1.size() ; i++){
			if(Math.random() < mixingRate){
//				Pixel swap1 = representation1.get(i);
//				Pixel swap2 = representation2.get(i);
//				representation2.set(i, swap1);
//				representation1.set(i, swap2);
				
				int swap1index = representation1.get(i).getId();
				int swap2index = representation2.get(i).getId();
				
				representation2.set(i, pixels.get(swap1index));
				representation1.set(i, pixels.get(swap2index));
				
//				System.out.println("s1: " + swap1index);
//				System.out.println("s2: " + swap2index);
			}
			
		}
		chr1.updateChromosome();
		chr2.updateChromosome();
		offSprings.add(chr1);
		offSprings.add(chr2);
		
		return offSprings;
	}
	
	public static Chromosome findBestChromosome(ArrayList<Chromosome> population){
		double bestDev = Double.MAX_VALUE;
		double bestEdge = Double.MAX_VALUE;
		double bestConnectivity = Double.MAX_VALUE;
		for (Chromosome chromosome : population) {
			if (chromosome.getDeviationFitness() < bestDev){
				bestDev = chromosome.getDeviationFitness();
			}
			if (chromosome.getEdgeFitness() < bestEdge){
				bestEdge = chromosome.getEdgeFitness();
			}
			if (chromosome.getConnectivityFitness() < bestConnectivity){
				bestConnectivity = chromosome.getConnectivityFitness();
			}
		}
		Chromosome bestChr = population.get(0);
		double bestFitness = Double.MAX_VALUE;
		for (Chromosome chromosome : population) {
			double dev = 0;
			double edge = 0;
			double con = 0;
			if (Variables.activeObjectives[0]){
				dev = chromosome.getDeviationFitness();
			}
			if (Variables.activeObjectives[1]){
				edge = chromosome.getEdgeFitness();
			}
			if (Variables.activeObjectives[2]){
				con = chromosome.getConnectivityFitness();
			}
			double fitnessEvaluation = dev/bestDev + edge/bestEdge + con/bestConnectivity;
			if (fitnessEvaluation < bestFitness){
				bestChr = chromosome;
				bestFitness = fitnessEvaluation;
			}
		}
		return bestChr;
	}
	
	public static void mutation(ArrayList<Chromosome> population){
		for (Chromosome chromosome : population) {
			if (Math.random() < Variables.mutationRate){
				chromosome.mutate();
				chromosome.updateChromosome();
			}
		}
	}
	
	public static Chromosome initializeChromosome(int numberOfSegments, ArrayList<Pixel> pixelsMST, ArrayList<Pixel> pixels, ArrayList<ArrayList<Pixel>> image, ArrayList<double[]> distances){
		ArrayList<Pixel> cuttedChromosome = (ArrayList<Pixel>) pixelsMST.clone();
		HashMap<Pixel, ArrayList<Integer>> mapPixelsThatPointsOnPixel = createMapPixelToIndex((ArrayList<Pixel>) pixelsMST.clone());
		int pixelIdToCut = cutIntoTwoSegments(pixelsMST, pixels, mapPixelsThatPointsOnPixel, pixels, Variables.minimumSegmentSize);
		for (int i = 0; i < numberOfSegments; i++) {
			cuttedChromosome.set(pixelIdToCut, pixels.get(pixelIdToCut));
			
			Chromosome chr = new Chromosome((ArrayList<Pixel>) cuttedChromosome.clone(), pixels, 0, distances, image);
			//finds the biggest segment after the cut
			ArrayList<ArrayList<Pixel>> segments = chr.getSegments();
			int maxSegmentIndex = -1;
			int maxSegmentSize = Integer.MIN_VALUE;
			for (int j = 0; j < segments.size(); j++) {
				if (segments.get(j).size() > maxSegmentSize){
					maxSegmentSize = segments.get(j).size();
					maxSegmentIndex = j;
				}
			}
			ArrayList<Pixel> segment = segments.get(maxSegmentIndex);

			mapPixelsThatPointsOnPixel = createMapPixelToIndex((ArrayList<Pixel>) cuttedChromosome.clone());
			
			//cut the next biggest segment
			pixelIdToCut = cutIntoTwoSegments((ArrayList<Pixel>) cuttedChromosome.clone(), pixels, mapPixelsThatPointsOnPixel, segment, Variables.minimumSegmentSize);


		}
		return new Chromosome((ArrayList<Pixel>) cuttedChromosome.clone(), pixels, 0, distances, image);
	}
	
	
	public static void nonDominatedSort(ArrayList<Chromosome> population){
//		ArrayList<Chromosome> 
		for (Chromosome chromosome : population) {
			
		}
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
