package edu.wit.cs.comp3370;

public class Edge {

	public Vertex src;
	public Vertex dst;
	public double cost;
	
	// creates an edge between two vertices
	Edge(Vertex s, Vertex d, double c) {
		src = s;
		dst = d;
		cost = c;
	}
	
	public String toString() {
		return String.format("(%d, %d) c: %f", src.ID, dst.ID, cost);
	}
}
