package moea;

public class Edge {

	private Pixel from;
	private Pixel to;
	private double weight;
	
	public Edge(Pixel from, Pixel to, double weight){
		this.from = from;
		this.to = to;
		this.weight = weight;
	}
	
	public Pixel getFrom() {
		return from;
	}

	public Pixel getTo() {
		return to;
	}

	public double getWeight() {
		return weight;
	}
	
	
}
