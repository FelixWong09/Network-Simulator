package mainfunction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import Gui.Simulator;
import Routing.Dijkstra;
import Routing.Edge;
import Routing.Graph;
import Routing.Vertex;

public class Network {
	
	static List<Vertex> nodes;
	static List<Edge> edges;
	
	public static void main(String args[]) {

		int[] labelRed = new int[11];
		int[] labelBlue = new int[11];
		Random rand = new Random(System.currentTimeMillis());
		for(int i = 0; i < 11; i ++) {
			int d = rand.nextInt(7) + 1;
			labelRed[i] = d;
		}
		for(int i = 0; i < 11; i ++) {
			int d = rand.nextInt(7) + 1;
			labelBlue[i] = d;
		}
		
		nodes = new ArrayList<Vertex>();
		edges = new ArrayList<Edge>();
		for (int i = 0; i < 8; i++) {
			Vertex location = new Vertex("" + i);
			nodes.add(location);
		}
		
		int[][] routerIndex = new int[][]{{1,2},{1,7},{2,5},
			{5,7},{2,3},{5,6},{7,8},{3,6},{6,8},{3,4},{4,8}};
		for(int i = 0; i < 11; i ++) {
			//System.out.println(routerIndex[i][0] + "\t" + routerIndex[i][1] + "\t" + labelRed[i]);
			addEdge("Edge_"+i, routerIndex[i][0]-1, routerIndex[i][1]-1, labelRed[i]);
			addEdge("Edge_"+i, routerIndex[i][1]-1, routerIndex[i][0]-1, labelRed[i]);
		}
		
		Graph graph = new Graph(nodes, edges);
		Dijkstra shortestPath = new Dijkstra(graph);
		//start from root node
		shortestPath.execute(nodes.get(0));
		//contains four routers in all.
		LinkedList<Vertex> path = shortestPath.getPath(nodes.get(3));
		
		int[] pathRed = new int[path.size()];
		for(int i = 0; i < path.size(); i ++) {
			pathRed[i] = Integer.valueOf(path.get(i).getName());
		}

		//set the weight of each edge, including the link between host and router.It can produce
		//speed bubbles are supposed to move on certain link.
		int[] delayRed = new int[pathRed.length + 3];
		delayRed[0] = delayRed[1] = delayRed[pathRed.length + 1] = delayRed[pathRed.length + 2] = 2;
		for (int i = 0; i < pathRed.length - 1; i ++) {
			int delaylast = getEdgeWeight(pathRed[i], pathRed[i+1]);
			delayRed[i + 2] = delaylast;
		}
		
		edges = new ArrayList<Edge>();
		for(int i = 0; i < 11; i ++) {
			addEdge("Edge_"+i, routerIndex[i][0]-1, routerIndex[i][1]-1, labelBlue[i]);
			addEdge("Edge_"+i, routerIndex[i][1]-1, routerIndex[i][0]-1, labelBlue[i]);
		}
		
		graph = new Graph(nodes, edges);
		shortestPath = new Dijkstra(graph);
		shortestPath.execute(nodes.get(0));
		path = shortestPath.getPath(nodes.get(3));
		
		int[] pathBlue = new int[path.size()];
		for(int i = 0; i < path.size(); i ++) {
			pathBlue[i] = Integer.valueOf(path.get(i).getName());
			//System.out.println(pathBlue[i]);
		}
		//set the weight of each edge, including the link between host and router.It can produce
		//speed bubbles are supposed to move on certain link.
		int[] delayBlue = new int[pathBlue.length + 3];
		delayBlue[0] = delayBlue[1] = delayBlue[pathBlue.length + 1] = delayBlue[pathBlue.length + 2] = 2;
		for (int i = 0; i < pathBlue.length - 1; i ++) {
			int delaylast = getEdgeWeight(pathBlue[i], pathBlue[i+1]);
			delayBlue[i + 2] = delaylast;
		}
		Simulator network = new Simulator(labelRed, labelBlue, pathRed, pathBlue, delayRed, delayBlue);
		JFrame frame = new JFrame();
		frame.add(network);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setTitle("Network Simulator");
		frame.setVisible(true);
	}
	private static void addEdge(String Id, int sourceNo, int destinationNo, int delay) {
		Edge newEdge = new Edge(Id, nodes.get(sourceNo), nodes.get(destinationNo), delay);
		edges.add(newEdge);
	}
	
	private static int getEdgeWeight(int m, int n) {
		for(Edge e : edges) {
			if (Integer.valueOf(e.getSource().getName()) == m && 
					Integer.valueOf(e.getDestination().getName()) == n)
				return e.getWeight();
		}
		
		return -1;
	}
}
