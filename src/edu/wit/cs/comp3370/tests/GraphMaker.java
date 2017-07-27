package edu.wit.cs.comp3370.tests;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JFrame;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import edu.wit.cs.comp3370.LAB10;
import edu.wit.cs.comp3370.Vertex;
import edu.wit.cs.comp3370.Edge;
import edu.wit.cs.comp3370.Graph;

public class GraphMaker extends JFrame
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2707712944901661771L;

	private mxCell endpoints[];
	private ArrayList<mxCell> pathEdges;
	private Vertex paths[][];
	private Graph g;
	private Object[] verts;
	
	public GraphMaker()
	{
		super("Shortest distances");

		mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();
		try
		{
			
			g = InputGraph("verts/small1", "edges/small1");
			paths = LAB10.FindAllPaths(g);
			verts = new Object[g.size()];
			endpoints = new mxCell[2];
			pathEdges = new ArrayList<mxCell>();
			
			for (Vertex v: g.getVertices()) {	//insert all vertices
				verts[v.ID] = graph.insertVertex(parent, null, "",  v.x*570+10, v.y*570+10, 13,
					13, "strokeColor=black;fillColor=black;shape=ellipse");
				
			}

			for (Vertex v: g.getVertices()) {	//insert all edges
				for (Edge e: v.outEdges)
					graph.insertEdge(parent, null, e.cost, verts[e.src.ID], verts[e.dst.ID], "noLabel=1");
			}
			
			graph.cellsOrdered(verts, false);
			
			Map<String, Object> edgeStyle = new HashMap<String, Object>();	//make edges prettier
			edgeStyle.put(mxConstants.STYLE_SHAPE,    mxConstants.SHAPE_CONNECTOR);
			edgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
			edgeStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
			edgeStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
			edgeStyle.put(mxConstants.STYLE_LABEL_BACKGROUNDCOLOR, "#ffffff");

			mxStylesheet stylesheet = new mxStylesheet();
			stylesheet.setDefaultEdgeStyle(edgeStyle);
			graph.setStylesheet(stylesheet);
		}
		finally
		{
			graph.getModel().endUpdate();
		}

		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		
	    // Handle only mouse click events
	    graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
	        @Override
	        public void mousePressed(MouseEvent e) {
	            mxCell cell = (mxCell) graphComponent.getCellAt(e.getX(), e.getY());
	            handleVertexClick(graphComponent, cell);
	        }
	    });
		getContentPane().add(graphComponent);
	}

	private void handleVertexClick(mxGraphComponent graphComponent, mxCell cell) {
		if (cell == null)
			return;
		
		if (cell.isEdge() && cell.getStyle() == "noLabel=1")	//turn on edge label
			cell.setStyle("noLabel=0");
		else if (cell.isEdge() && cell.getStyle() == "noLabel=0") 	//turn off edge label
			cell.setStyle("noLabel=1");
		else if (cell.isEdge())	//path edge - ignore click
			return;
		else if (endpoints[0] == null) {	//set source
			endpoints[0] = cell;
			endpoints[0].setStyle("fillColor=green");
		}
		else if (endpoints[1] == null && cell != endpoints[0]) {	//set destination
			endpoints[1] = cell;
			endpoints[1].setStyle("fillColor=red");
			
			int startID = Integer.parseInt(endpoints[0].getId()) - 2;
			int tmpID = startID;
			int endID = Integer.parseInt(endpoints[1].getId()) - 2;
			mxGraph graph = graphComponent.getGraph();
			
			while (paths[tmpID][endID] != null) {
				Vertex src = g.getVertex(tmpID);
				Vertex dst = paths[tmpID][endID];
				double cost = -1;
				
				for (Edge e: src.outEdges) {	// find edge cost
					if (e.dst.equals(dst)) {
						cost = e.cost;
						break;
					}
				}
				
				Object o = graph.insertEdge(graph.getDefaultParent(), null, Double.toString(cost), verts[tmpID], verts[paths[tmpID][endID].ID], "endArrow=open;strokeWidth=2;strokeColor=red");
				
				pathEdges.add((mxCell) o);
				tmpID = paths[tmpID][endID].ID;
				if (tmpID == startID)	//untested, should handle path loops
					break;
			}
		}
		else if (endpoints[1] != null) {	//reset and set source
			resetGraphStyle(graphComponent);
			endpoints[0] = cell;
			endpoints[0].setStyle("fillColor=green");
		}
		
		graphComponent.refresh();
		
	}
	
	private void resetGraphStyle(mxGraphComponent graphComponent) {
		
		if (endpoints[0] != null)
			endpoints[0].setStyle("fillColor=black;shape=ellipse");
		
		if (endpoints[1] != null)
			endpoints[1].setStyle("fillColor=black;shape=ellipse");
		
		for (mxCell c: pathEdges) {
			graphComponent.getGraph().getModel().remove(c);
		}
		//strokeWidth=4;strokeColor=red
		pathEdges.clear();
		
		endpoints[0] = null;
		endpoints[1] = null;
		
	}
	
	public static void main(String[] args)
	{
		GraphMaker frame = new GraphMaker();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(630, 630);
		frame.setVisible(true);
	}
	

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
			System.err.println("Cannot open file " + vFile + ". Exiting.");
			System.exit(0);
		}
		
		return g;
	}
	

}
