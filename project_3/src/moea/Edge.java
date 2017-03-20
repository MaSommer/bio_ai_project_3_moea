package moea;

public class Edge {

	private Pixel from;
	private Pixel to;
	private double weight;
	private int segmentSizeFromNode;
	private int segmentSizeToNode;
	private double fakeWeight;

	public Edge(Pixel from, Pixel to, double weight){
		this.from = from;
		this.to = to;
		this.weight = weight;
	}
	
	public double getFakeWeight() {
		return fakeWeight;
	}

	public void setFakeWeight(double fakeWeight) {
		this.fakeWeight = fakeWeight;
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
	
	public String toString(){
		return "From: " +from.toString() + " to " + to.toString();
	}
	
	public void setWeight(double weight){
		this.weight = weight;
	}
	
	public int getSegmentSizeFromNode() {
		return segmentSizeFromNode;
	}

	public void setSegmentSizeFromNode(int segmentSizeFromNode) {
		this.segmentSizeFromNode = segmentSizeFromNode;
	}

	public int getSegmentSizeToNode() {
		return segmentSizeToNode;
	}

	public void setSegmentSizeToNode(int segmentSizeToNode) {
		this.segmentSizeToNode = segmentSizeToNode;
	}
	
	
	
}
