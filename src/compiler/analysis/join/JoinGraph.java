package compiler.analysis.join;

import static java.util.Arrays.deepToString;
import static java.util.Arrays.fill;
import static util.Arrays.append;
import static util.Arrays.binaryInsert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import util.collections.Singleton;

import compiler.CHRIntermediateForm.variables.Variable;

public class JoinGraph {

	private Edge[][] graph;
	
	public JoinGraph(int nbNodes) {
		graph = new Edge[nbNodes][];
		for (int i = 0; i < nbNodes; i++)
			graph[i] = new Edge[i];
	}
	
	protected static class Edge implements Comparable<Edge> {
		public final int ID;
		public final Variable[] variables;
		public Edge(int ID, Variable... variables) {
			this.ID = ID;
			this.variables = variables;
		}
		public int compareTo(Edge other) {
			return this.ID - other.ID;
		}
		@Override
		public String toString() {
			return String.valueOf(ID);
		}
	}
	
	public int getNbNodes() {
		return graph.length;
	}
	public Edge getEdge(int from, int to) {
		return (from == to)? null : (to < from)? graph[from][to] : graph[to][from];
	}
	public boolean isConnected(int from, int to) {
		return getEdge(from, to) != null;
	}
	
	protected void addEdge(int from, int to, Edge edge) {
		if (from == to)
			throw new IllegalArgumentException();
		if (isConnected(from, to)) {
			if (getEdge(from, to) != edge)
				throw new IllegalStateException();
			return;
		}
		
		setEdge(from, to, edge);
		addToEdgeList(edge);
	}
	
	protected void setEdge(int from, int to, Edge edge) {
		if (to < from)
			graph[from][to] = edge;
		else
			graph[to][from] = edge;
	}
	
	private Edge[] edgeList = new Edge[0];
	
	protected Edge[] getEdgeList() {
		return edgeList;
	}
	
	protected void addToEdgeList(Edge edge) {
		edgeList = binaryInsert(edgeList, edge);
	}
	
	protected static char toChar(Edge edge) {
		if (edge == null) 
			return '_';
		else
			return (char)('a' + edge.ID); 
	}
	
	public boolean isAcyclic() {
		int n = getNbNodes();
		return n <= 2 || isAcyclic(new boolean[n], 0, -1);
	}
	private boolean isAcyclic(boolean[] reached, int node, int previous) {
		reached[node] = true;
		for (int i = 0; i < reached.length; i++)
			if (i != previous && isConnected(node, i) &&
				(reached[i] || !isAcyclic(reached, i, node))) 
					return false;
		return true;
	}
	
	public boolean isConnected() {
		int n = getNbNodes();
		return n <= 1 || numConnected(new boolean[n], 0, 0) == getNbNodes();
	}
	private int numConnected(boolean[] reached, int num, int node) {
		reached[node] = true;
		if (++num == getNbNodes()) return num;

		for (int i = 0; i < reached.length; i++) {
			if (!reached[i] && isConnected(node, i)) 
				if ((num = numConnected(reached, num, i)) == getNbNodes()) 
					return num;
		}
		
		return num;
	}
	
	public Collection<JoinGraph> getConnectedComponents() {
		int n = getNbNodes();
		if (n <= 1) return new Singleton<JoinGraph>(this);
			
		List<JoinGraph> components = new ArrayList<JoinGraph>(2);
		boolean[] reached = new boolean[n];
		int node = 0;
		int[] nodeMapping = new int[n]; 
		
		outer: while (true) {
			boolean[] nowReached = new boolean[n];
			int num = numConnected(nowReached, 0, node);
			if (num == n) return new Singleton<JoinGraph>(this);
			
			JoinGraph component = new JoinGraph(num);
			Edge edge;
			
			for (int i = 0, j  = 0; i < n; i++)
				if (nowReached[i]) nodeMapping[i] = j++;
			for (int i = 0; i < n; i++)
				if (nowReached[i]) {
					for (int j = i+1; j < n; j++)
						if ((edge = getEdge(i, j)) != null)
							component.addEdge(nodeMapping[i], nodeMapping[j], edge);
					reached[i] = true;
				}
			components.add(component);
			
			do {
				if (++node == n) break outer;
			} while (reached[node]);
		}
		
		return components;
	}
	
	
	private int[][][] cliques;
	
	public void findAllCliques() {
		if (cliques != null) throw new IllegalStateException();
		cliques = new int[getNbNodes()][][];
		fill(cliques, new int[][] {});
		new BronKerboschAlgorithm().findAllCliques();
	}
	
	protected void addClique(int[] clique) {
		for (int i = 0; i < clique.length; i++) 
			append(cliques[clique[i]], clique);
	}
	
	protected class BronKerboschAlgorithm {
		private int[] compsub;
		private int c;
		private int edgeID;
		
		public void findAllCliques() {
			final int N = getNbNodes();
			compsub = new int[N];
			int[] ALL = new int[N];
			for (int i = 0; i < N; i++) ALL[i] = i;
			Edge[] edges = getEdgeList();
			for (int i = 0; i < edges.length; i++) {
				edgeID = edges[i].ID;
				extend(ALL, 0, N);
			}
		}
		
		protected boolean isConnected(int from, int to) {
			Edge edge = getEdge(from, to);
			return edge != null && edge.ID == edgeID;
		}
		
		protected void extend(int[] old, int ne, int ce) {
			int[] neu = new int[ce];
			int minnod = ce;
			int nod = 0;
			int fixp = -1234, s = -1234;
			
			// determine each counter value and look for minimum:
			for (int i = 0; i < ce && minnod != 0; i++) {
				int p = old[i];
				int count = 0;
				int pos = -1234;
				
				// count disconnections:
				for (int j = ne; j < ce && count < minnod; j++)
					if (j != i && !isConnected(p, old[j])) {
						count++;
						pos = j;	// save position of potential candidate
					}
				
				// test new minimum:
				if (count < minnod) {
					fixp = p;	
					minnod = count;
					if (i < ne) { 
						s = pos;
					} else {
						s = i; nod = 1;	// preincrement number of disconnections
					}
				}
			}

			// backtrackcycle:
			for (nod = minnod + nod; nod >= 1; nod--) {
				// interchange:
				int sel = old[s]; 
				old[s] = old[ne];
				old[ne] = sel;
				
				// add to compsub:
				compsub[c++] = sel;
				
				// fill new set not:
				int newne = 0;
				for (int i = 0; i < ne; i++) {
					if (isConnected(sel, old[i])) {
						neu[newne++] = old[i];
					}
				}
				// fill new set cand:
				int newce = newne;
				for (int i = ne; i < ce; i++) {
					if (isConnected(sel, old[i])) {
						neu[newce++] = old[i];
					}
				}
				
				if (newce == 0) {
					int[] clique = new int[c];
					System.arraycopy(compsub, 0, clique, 0, c);
					addClique(clique);
				} else if (newne < newce) {
					extend(neu, newne, newce);
				}

				// remove from compsub:
				c--;

				// add to not:
				ne++;

				if (nod > 1) {	// select a candidate disconnected to the fixed point:
					s = ne;
					while (isConnected(fixp, old[s])) s++;
				}
			}
		}
	}
	
	@Override
	public String toString() {
		return deepToString(graph);
	}
	
	public static void main(String[] args) {
		JoinGraph test = new JoinGraph(5);
		Edge e1 = new Edge(0);
		Edge e2 = new Edge(1);
//		Edge e3 = new Edge(2);
		test.addEdge(0, 1, e2);
		test.addEdge(2, 1, e1);
		test.addEdge(0, 2, e1);
		test.findAllCliques();
		System.out.println(test.isAcyclic());
		System.out.println(test.isConnected());
		System.out.println(test.getConnectedComponents());
	}
}