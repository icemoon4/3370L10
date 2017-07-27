package edu.wit.cs.comp3370.tests;

import java.io.File;
import java.io.IOException;
import java.security.Permission;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import edu.wit.cs.comp3370.Graph;
import edu.wit.cs.comp3370.LAB10;
import edu.wit.cs.comp3370.Vertex;

import static org.junit.Assert.*;


public class LAB10TestCase{

	@Rule
	public Timeout globalTimeout = Timeout.seconds(15);
	
	@SuppressWarnings("serial")
	private static class ExitException extends SecurityException {}
	
	private static class NoExitSecurityManager extends SecurityManager 
    {
        @Override
        public void checkPermission(Permission perm) {}
        
        @Override
        public void checkPermission(Permission perm, Object context) {}
        
        @Override
        public void checkExit(int status) { super.checkExit(status); throw new ExitException(); }
    }
	
	@Before
    public void setUp() throws Exception 
    {
        System.setSecurityManager(new NoExitSecurityManager());
    }
	
	@After
    public void tearDown() throws Exception 
    {
        System.setSecurityManager(null);
    }

	private void _testPaths(Graph g, String expectedSerializedMatrix) {
		
		Vertex[][] matrix = null;
		try {
			matrix = LAB10.FindAllPaths(g);
		} catch (ExitException e) {}
		
		assertNotNull("reconstruction matrix is null", matrix);
		String actualSerializedMatrix = "";
		
		for (int i = 0; i < g.size(); i++) {
			for (int j = 0; j < g.size(); j++) {
				if (matrix[i][j] == null)
					actualSerializedMatrix += "x ";
				else
					actualSerializedMatrix += Integer.toString(matrix[i][j].ID) + " ";
			}
			actualSerializedMatrix += "/";
		}
		
		assertEquals("Reconstruction matrix does not match", expectedSerializedMatrix, actualSerializedMatrix);
	}

	private void _testFilePaths(String vFile, String eFile, String expectedSerializedMatrix) {
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
		
		_testPaths(g, expectedSerializedMatrix);
	}
	
	
	@Test
	public void testTiny() {
		_testFilePaths("verts/tiny", "edges/tiny", "x 1 1 /2 x 2 /0 0 x /");
	}
	
	@Test
	public void testSmall() {
		_testFilePaths("verts/small1", "edges/small1", "x 1 1 1 /2 x 2 2 /0 0 x 3 /0 0 0 x /");
		_testFilePaths("verts/small2", "edges/small2", "x 5 5 3 5 5 /0 x 2 2 2 5 /4 4 x 4 4 4 /0 5 5 x 5 5 /3 1 1 3 x 3 /4 1 1 4 4 x /");
	}
	
	@Test
	public void testCapitals() {
		_testFilePaths("verts/state_capitals", "edges/state_capitals", "x 46 46 46 46 46 6 46 46 46 46 46 46 46 46 6 46 46 46 46 46 46 46 46 46 46 46 46 46 6 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 46 /44 x 44 32 44 44 44 44 44 44 44 44 44 44 44 15 44 44 44 44 44 44 32 44 44 44 44 44 44 44 44 31 32 44 44 44 44 44 44 44 32 44 44 44 44 32 44 44 /21 10 x 10 10 10 10 10 10 10 10 10 10 10 10 39 10 10 10 10 10 21 10 10 10 21 10 10 10 10 10 10 10 10 10 10 10 10 10 39 10 10 10 10 10 10 10 10 /42 42 42 x 42 42 42 42 42 42 42 42 42 42 42 42 42 42 42 42 42 42 42 42 42 42 42 27 42 42 42 42 42 42 42 42 42 42 42 42 42 42 42 42 42 42 42 42 /7 7 7 7 x 7 34 7 7 34 7 7 7 7 7 34 34 7 34 34 34 7 34 7 7 34 34 7 28 34 7 7 34 34 34 7 34 7 34 34 34 7 34 7 7 34 34 34 /7 7 7 7 7 x 7 7 7 13 7 7 7 13 13 7 13 36 13 13 7 7 36 7 7 7 36 7 7 7 7 31 36 7 13 7 36 7 7 7 36 7 7 7 7 7 36 36 /20 20 20 29 20 20 x 20 20 20 20 20 20 20 20 29 20 46 29 29 20 20 46 20 20 29 46 20 20 29 46 20 46 20 29 46 46 20 20 20 40 20 20 20 20 46 46 20 /0 1 2 23 23 23 23 x 1 13 2 23 23 13 13 23 13 17 13 23 23 23 23 23 23 30 23 23 23 23 30 1 23 23 13 30 23 23 23 30 23 23 23 23 1 23 23 23 /47 47 47 41 18 47 18 47 x 47 47 47 47 47 47 47 41 47 18 18 18 47 18 47 47 47 47 47 18 18 47 41 47 47 18 47 47 47 47 47 40 41 18 47 47 47 47 47 /25 1 15 25 25 25 25 25 25 x 15 25 25 25 25 15 15 25 15 15 25 25 25 25 1 25 25 25 15 25 25 25 25 25 15 25 25 25 25 25 25 25 25 25 1 25 25 25 /42 42 42 42 42 42 42 42 42 42 x 42 42 42 42 42 42 42 42 42 42 42 42 42 42 25 42 42 42 42 42 42 42 42 42 35 42 42 42 42 42 42 42 42 42 42 42 42 /12 43 12 43 43 12 6 12 12 12 12 x 12 12 12 6 43 12 43 43 6 12 12 12 6 12 12 43 43 6 12 43 12 12 43 43 12 12 12 12 6 43 42 43 6 43 12 12 /37 5 5 37 46 5 37 5 37 37 5 37 x 5 5 5 37 46 37 37 37 37 46 5 37 46 46 37 45 37 46 46 46 37 46 37 46 37 37 39 46 37 37 37 37 45 46 37 /19 7 34 19 34 14 19 7 14 14 34 14 14 x 14 34 34 14 34 19 19 14 19 7 14 14 14 14 34 19 14 14 19 19 34 14 14 14 19 14 19 14 19 14 19 19 14 14 /26 26 34 26 24 24 26 24 36 9 34 26 24 26 x 9 16 36 34 34 26 26 36 24 24 9 26 26 34 26 26 36 36 26 34 35 36 26 26 26 36 26 35 26 26 36 36 36 /34 34 34 34 34 11 34 34 34 34 34 11 11 28 28 x 34 27 34 34 34 34 34 34 34 34 34 27 28 34 34 34 34 34 34 35 34 34 34 34 34 11 34 11 34 27 34 34 /4 4 4 4 4 4 4 4 8 8 4 4 4 4 4 4 x 4 4 4 4 4 4 4 4 8 4 4 4 4 4 4 4 4 4 4 4 4 8 4 4 4 4 4 4 4 4 8 /41 41 41 41 41 41 41 41 41 41 41 41 41 41 41 39 41 x 41 41 41 41 41 41 41 41 41 27 41 41 41 41 41 41 41 41 41 41 41 39 41 41 41 41 41 27 41 41 /19 4 4 19 4 19 19 4 19 19 4 19 19 19 19 19 4 19 x 19 19 19 19 4 19 19 19 19 28 19 19 19 19 19 4 19 19 19 19 19 19 19 19 19 19 19 19 19 /6 37 37 6 6 37 6 37 6 6 37 37 37 6 6 6 37 6 37 x 6 37 22 37 6 6 6 37 6 6 6 6 42 6 6 6 6 37 6 6 6 37 42 37 6 42 6 6 /38 44 44 44 44 44 44 44 38 9 44 44 44 44 44 39 44 44 44 44 x 44 44 44 44 9 44 44 44 44 44 31 44 33 44 44 44 44 38 39 44 44 44 44 44 44 44 38 /0 25 25 0 0 0 0 0 25 0 25 0 0 0 0 0 0 0 0 0 0 x 0 0 0 25 0 0 0 0 0 0 0 25 0 0 0 0 0 0 0 0 25 0 0 0 0 0 /21 21 14 3 3 14 3 3 3 14 14 14 14 3 14 3 14 3 28 28 3 21 x 3 14 21 14 14 28 3 14 3 3 21 14 14 3 14 3 14 3 3 3 14 3 3 3 3 /24 24 24 3 24 24 24 24 24 24 24 24 24 24 24 15 24 24 24 19 24 24 24 x 24 24 24 24 24 24 24 24 24 24 24 24 24 24 24 24 24 24 3 24 24 24 24 24 /12 12 12 12 4 12 12 12 12 12 12 12 12 12 12 12 12 12 4 4 12 12 12 12 x 12 12 12 4 12 12 12 12 12 4 12 12 12 12 12 12 12 12 12 12 12 12 12 /37 1 2 37 42 37 42 37 33 33 2 37 37 37 37 42 37 42 37 37 42 37 42 37 37 x 37 37 42 42 30 37 42 33 42 30 37 37 37 30 42 37 42 37 37 42 37 33 /37 37 37 37 30 37 37 37 37 20 37 37 37 37 37 30 37 37 37 37 20 37 37 37 20 30 x 37 30 37 30 20 37 37 30 30 37 37 37 30 37 37 37 37 20 37 37 37 /45 4 4 45 4 4 4 4 4 45 4 4 4 45 45 4 4 45 4 4 4 45 4 4 4 45 45 x 4 4 4 45 45 4 4 45 45 4 4 45 4 45 45 4 4 45 45 4 /18 18 18 18 18 11 18 18 18 13 18 11 11 13 13 18 13 18 18 18 18 18 18 18 18 18 18 11 x 18 30 18 18 18 13 30 18 18 18 18 18 11 18 11 18 18 18 18 /21 25 15 3 40 40 15 40 40 40 15 40 40 40 40 15 15 40 15 15 15 21 15 40 40 25 40 15 15 x 40 40 3 25 15 15 40 40 40 40 40 40 3 40 40 40 40 40 /37 37 4 37 4 37 37 4 37 9 4 37 37 35 35 39 4 35 4 4 37 37 39 4 24 25 37 37 4 37 x 37 39 37 4 35 35 37 37 39 37 37 35 37 37 37 37 37 /5 1 2 5 5 5 5 5 1 5 2 5 5 5 5 5 5 17 5 5 5 2 5 5 5 5 5 5 5 5 5 x 5 5 5 5 5 5 5 2 5 17 2 5 1 5 5 5 /45 4 4 3 4 40 4 4 40 14 4 40 40 45 14 4 45 45 4 4 4 45 22 4 40 45 14 43 45 4 14 45 x 4 4 14 14 40 40 45 40 45 45 43 40 45 45 40 /8 8 8 8 8 8 8 8 8 8 8 8 8 35 35 8 16 35 8 8 8 8 8 8 8 8 8 8 8 8 8 35 8 x 8 35 35 8 8 8 8 8 35 8 8 8 8 8 /18 18 2 18 18 18 18 18 18 18 2 18 18 18 18 15 16 18 18 18 18 18 18 18 18 18 18 18 18 18 18 18 18 18 x 18 18 18 18 18 18 18 18 18 18 18 18 18 /38 36 13 17 42 36 42 36 36 36 13 36 36 13 13 42 17 17 17 13 42 36 42 36 42 36 36 36 42 42 36 36 42 36 13 x 36 36 38 36 42 17 42 36 42 42 36 36 /47 46 47 46 46 46 46 46 47 47 47 46 46 13 13 46 17 17 17 17 46 46 46 46 46 46 46 46 46 46 46 31 46 46 46 46 x 46 47 46 46 17 46 46 46 46 46 47 /38 11 11 11 11 11 11 11 38 38 11 11 11 13 13 11 11 11 11 11 11 21 11 11 38 21 11 11 11 11 11 11 11 33 11 11 11 x 38 11 11 11 11 11 38 11 11 38 /0 44 47 41 44 44 44 44 47 47 47 44 44 44 44 47 41 0 47 47 0 44 0 44 44 47 0 44 44 44 0 0 0 44 44 0 0 44 x 44 0 41 44 44 44 44 0 47 /32 5 15 32 32 5 15 5 32 32 15 15 5 32 32 15 15 32 15 15 15 32 32 5 5 32 15 15 15 15 5 5 32 15 15 15 5 15 15 x 32 32 32 32 32 32 15 15 /38 44 44 44 44 44 44 44 44 44 44 44 44 44 44 44 44 44 44 44 44 44 44 44 44 30 44 44 44 44 30 31 44 44 44 30 44 44 38 44 x 44 44 44 44 44 44 44 /18 18 31 3 18 31 18 18 16 18 31 18 18 18 18 18 16 18 18 18 18 18 18 18 18 18 18 18 18 18 18 31 18 18 18 18 18 18 18 18 18 x 18 18 18 18 18 18 /6 32 32 32 32 6 6 32 6 6 32 46 6 32 32 6 32 32 32 32 6 32 32 32 6 6 46 46 32 6 46 46 32 6 32 46 46 46 6 6 6 32 x 46 6 32 46 6 /0 1 27 41 27 46 41 27 33 46 27 46 46 27 27 41 41 27 41 41 46 27 46 27 1 46 46 27 41 41 46 41 46 33 27 35 46 46 46 46 46 41 41 x 1 27 46 46 /24 24 24 24 24 24 24 24 8 8 24 24 24 13 13 24 24 24 24 24 24 24 24 24 24 24 24 24 24 24 24 24 24 24 24 24 24 24 24 39 24 24 24 24 x 24 24 8 /21 13 13 17 17 24 42 13 17 13 13 24 24 13 13 15 17 17 17 17 42 21 42 13 24 21 13 17 28 42 13 17 42 42 13 13 42 24 42 17 42 17 42 24 42 x 42 42 /26 26 5 32 32 5 26 5 36 9 5 26 26 32 32 26 36 36 36 36 26 26 32 5 26 26 26 26 32 26 26 36 32 26 32 26 36 26 26 26 32 36 26 26 26 32 x 36 /38 38 2 41 38 38 8 38 8 9 2 38 38 38 38 9 41 38 8 8 8 38 38 38 38 9 38 38 8 8 38 41 38 9 38 38 38 38 38 38 8 41 8 38 38 38 38 x /");
	}
	
	@Test
	public void testDisconnected() {
		_testFilePaths("verts/disconnected", "edges/disconnected", "x 1 x x /x x x x /x x x 3 /x x x x /");
	}
		
}
