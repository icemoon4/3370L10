package edu.wit.cs.comp3370;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/* Calculates the reconstruction matrix of the Floyd-Warshall algorithm for
 * all-pairs shortest paths 
 * 
 * Wentworth Institute of Technology
 * COMP 3370
 * Lab Assignment 10
 * Rachel Palmer
 */

public class LAB10 {
	
	/**
	 * FindAllPaths takes a given graph object and first converts it into an adjacency matrix. In 
	 * the same loop, it will then construct a PI matrix of connections to the shortest paths among
	 * vertices. The first PI generated just shows basic connections (vertices directly connected by
	 * one edge). Then, it uses Floyd-Warshall algorithm to dynamically construct the shortest paths
	 * from one vertex to any other vertex, based on edge costs, using matrix data that updates n
	 * times. 
	 * @param g		graph generated based on vertex and edge files
	 * @return		returns PI, the matrix of shortest paths
	 */
	public static Vertex[][] FindAllPaths(Graph g) {
		Vertex[] list = g.getVertices();
		Double[][] distances = new Double[list.length][list.length];
		Vertex[][] pi = new Vertex[list.length][list.length];
		//construct D
		for(int i = 0; i < list.length; i++){
			for(int j = 0; j < list.length; j++){
				if(distances[i][j] == null)
					distances[i][j] = Double.POSITIVE_INFINITY;
			}
			for(int j = 0; j < list[i].outEdges.size(); j++){
				distances[i][list[i].outEdges.get(j).dst.ID] = list[i].outEdges.get(j).cost;
			}
			distances[i][i] = 0.0;
			//Construct PI
			for(int j = 0; j < list.length; j++){
				if(distances[i][j] != 0 && distances[i][j] != Double.POSITIVE_INFINITY)
					pi[i][j] = list[j];
			}
		}
		
		//floyd-warshall
		for(int k = 0; k < list.length; k++){
			//update new distances matrix
			for(int i = 0; i < list.length; i++){
				for(int j = 0; j < list.length; j++){
					if(distances[i][j] > (distances[i][k] + distances[k][j])){ 
						distances[i][j] = distances[i][k] + distances[k][j];
						pi[i][j] = pi[i][k];
					}
				}
			}
		}
		return pi;
	}
	
	/********************************************
	 * 
	 * You shouldn't modify anything past here
	 * 
	 ********************************************/
	

	// reads in an undirected graph from a specific file formatted with one
	// x/y node coordinate per line:
	private static Graph InputGraph(String vFile, String eFile) {
		
		Graph g = new Graph();
		// vFile is list of (x coord, y coord) tuples
		try (Scanner f = new Scanner(new File(vFile))) {
			while(f.hasNextDouble())
				g.addVertex(f.nextDouble(), f.nextDouble());
		} catch (IOException e) {
			System.err.println("Cannot open file " + vFile + ". Exiting.");
			System.exit(0);
		}
		
		// eFile is list of (src ID, dst ID, cost) tuples
		try (Scanner f = new Scanner(new File(eFile))) {
			while(f.hasNextInt())
				g.addEdge(f.nextInt(), f.nextInt(), f.nextDouble());
		} catch (IOException e) {
			System.err.println("Cannot open file " + eFile + ". Exiting.");
			System.exit(0);
		}
		
		return g;
	}
	
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		String vFile, eFile;
		
		System.out.printf("Enter <vertices file> <edges file>\n");
		System.out.printf("(e.g: verts/small1 edges/small1)\n");
		vFile = s.next();
		eFile = s.next();

		// read in vertices
		Graph g = InputGraph(vFile, eFile);
		
		Vertex paths[][] = FindAllPaths(g);
		
		System.out.println("next array:");
		for (int i = 0; i < paths.length; i++) {
			for (int j = 0; j < paths.length; j++) {
				if (paths[i][j] == null)
					System.out.printf("%3s","x");
				else
					System.out.printf("%3d",paths[i][j].ID);
			}
			System.out.println();
		}
		s.close();

	}

}
