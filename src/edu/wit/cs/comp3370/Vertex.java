package edu.wit.cs.comp3370;

import java.util.ArrayList;

// represents a vertex in a graph, including a unique ID to keep track of vertex
public class Vertex {

	public double x;
	public double y;
	public int ID;
	public ArrayList<Edge> outEdges;
	
	public Vertex(double xCoord, double yCoord, int vertID) {
		outEdges = new ArrayList<Edge>();
		x = xCoord; y = yCoord; ID = vertID;
	}
	
	public String toString() {
		return Integer.toString(ID);
	}
}
