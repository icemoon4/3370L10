package edu.wit.cs.comp3370;

import java.util.ArrayList;

// represents a graph as a modified adjacency list: each vertex has a list of outgoing edges
public class Graph {

	private ArrayList<Vertex> vs;
	private int nextID = 0;
	
	public Graph() {
		vs = new ArrayList<Vertex>();
	}
	
	public void addVertex(double x, double y) {
		Vertex v = new Vertex(x, y, nextID++);
		vs.add(v);
	}
	
	public void addEdge(int srcID, int dstID, double cost) {
			vs.get(srcID).outEdges.add(new Edge(vs.get(srcID), vs.get(dstID), cost));
	}
	
	public int size() {
		return vs.size();
	}
	
	public Vertex[] getVertices() {
		return vs.toArray(new Vertex[vs.size()]);
	}
	
	public Vertex getVertex(int ID) {
		return vs.get(ID);
	}
	
	public Edge[] getEdges() {
		ArrayList<Edge> edges = new ArrayList<Edge>();
		for (Vertex v: vs) {
			for (Edge e: v.outEdges)
				edges.add(e);
		}
		return edges.toArray(new Edge[edges.size()]);
	}
	
	// sums up the costs of all edges in the graph
	public double getTotalEdgeWeight() {
		double ret = 0;
		for (Vertex v: vs) {
			for (Edge e: v.outEdges)
				ret += e.cost;
		}
		return ret;
	}
}
