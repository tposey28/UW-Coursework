import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;

/**
 * @author Derek Rhodehamel and Taylor Posey Extra Credit Options Implemented,
 *         if any: A5E1 (note: must change Class Constant NUMBER_OF_PEGS)
 *
 *         Solution to Assignment 5 in CSE 373, Autumn 2014 University of
 *         Washington. This assignment requires Java 8 JDK
 *
 *         Starter code provided by Steve Tanimoto and Si J. Liu, Nov. 21, 2014.
 *
 */

public class ExploredGraph {
	public final int NUMBER_OF_PEGS = 3; // number of pegs in this game
	private Set<Vertex> Ve; // collection of explored vertices
	private Set<Edge> Ee; // collection of explored edges
	private int VeSize; // size of collection of explored vertices
	private int EeSize; // size of collection of explored edges
	private List<Operator> operators; // collection of operators (6 in this
										// game)
	private HashMap<Vertex, LinkedList<Edge>> map; // map of successor vertex
													// with its edges

	public ExploredGraph() {
		initialize();
	}

	public void initialize() {
		Ve = new LinkedHashSet<Vertex>();
		Ee = new LinkedHashSet<Edge>();
		map = new HashMap<Vertex, LinkedList<Edge>>();
		VeSize = 0;
		EeSize = 0;
		setOperators();
	}

	private void clearExplored() {
		Ve.clear();
		Ee.clear();
		map.clear();
		VeSize = 0;
		EeSize = 0;
	}

	private void setOperators() {
		operators = new ArrayList<Operator>();
		for (int i = 0; i < NUMBER_OF_PEGS; i++) {
			for (int j = 0; j < NUMBER_OF_PEGS; j++) {
				if (i != j) {
					operators.add(new Operator(i, j));
				}
			}
		}
	}

	public int nvertices() {
		VeSize = Ve.size();
		return VeSize;
	}

	public int nedges() {
		EeSize = Ee.size();
		return EeSize;
	}

	@SuppressWarnings("unchecked")
	public void dfs(Vertex vi, Vertex vj) {
		clearExplored();
		Stack<Vertex> path = new Stack<Vertex>();
		Ve.add(vi);
		path.push(vi);
		boolean isRunning = true;
		// Changed to iterative solution because recursive ends up too deep
		while (isRunning) {
			Vertex current = path.peek();
			if (current.equals(vj)) {
				isRunning = false;
			} else {
				boolean hasNext = false;
				// Cycles through operators backwards so operators are explored
				// in
				// proper priority, Stack prevents us from starting at 0.
				for (int i = operators.size() - 1; i >= 0; i--) {
					Operator currOperator = operators.get(i);
					if ((boolean) currOperator.getPrecondition().apply(current)) {
						Vertex toVert = (Vertex) currOperator.getTransition()
								.apply(current);
						if (!Ve.contains(toVert)) {
							// Adds new found Vertex to explored
							Edge tweenEdge = new Edge(current, toVert);
							Ve.add(toVert);
							Ee.add(tweenEdge);
							if (!map.containsKey(toVert)) {
								map.put(toVert, new LinkedList<Edge>());
							}
							map.get(toVert).add(tweenEdge);
							path.push(toVert);
							hasNext = true;
						}
					}
				}
				if (!hasNext) {
					path.pop();
				}
			}
		}

	}

	public void bfs(Vertex vi, Vertex vj) {
		clearExplored();
		Queue<Vertex> process = new LinkedList<Vertex>();
		process.add(vi);
		boolean found = false;
		while (!found && !process.isEmpty()) {
			found = bfs(process, vj);
		}
	}

	@SuppressWarnings("unchecked")
	private boolean bfs(Queue<Vertex> process, Vertex goal) {
		Vertex current = process.poll();
		Ve.add(current);
		if (current.equals(goal)) {
			return true;
		} else {
			for (int i = 0; i < operators.size(); i++) {
				Operator op = operators.get(i);
				Function<Vertex, Boolean> precondition = op.getPrecondition();
				if (precondition.apply(current)) {
					Function<Vertex, Vertex> transition = op.getTransition();
					Vertex newVertex = transition.apply(current);
					Edge newEdge = new Edge(current, newVertex);
					Ee.add(newEdge);
					if (!Ve.contains(newVertex)) {
						Ve.add(newVertex);
						process.add(newVertex);
						map.put(newVertex, new LinkedList<Edge>());
						map.get(newVertex).add(newEdge);
					}
				}
			}
			return false;
		}
	}

	public ArrayList<Vertex> retrievePath(Vertex vj) {
		ArrayList<Vertex> path = new ArrayList<Vertex>();
		// Initialize path
		path.add(vj);
		Vertex current = vj;

		// Traverses back from given Vertex to start
		while (map.containsKey(current)) {
			Vertex temp = map.get(current).getFirst().vi;
			path.add(temp);
			current = temp;
		}
		// Reverses so path is in correct order
		Collections.reverse(path);
		return path;
	}

	public ArrayList<Vertex> shortestPath(Vertex vi, Vertex vj) {
		bfs(vi, vj);
		ArrayList<Vertex> path = retrievePath(vj);
		return path;
	}

	public Set<Vertex> getVertices() {
		return Ve;
	}

	public Set<Edge> getEdges() {
		return Ee;
	}

	public static void main(String[] args) {
		ExploredGraph eg = new ExploredGraph();
		// Test the vertex constructor:
		Vertex v0 = eg.new Vertex("[[4,3,2,1],[],[]]");
		Vertex v1 = eg.new Vertex("[[],[],[4,3,2,1]]");
		eg.dfs(v0, v1);
		System.out.println(eg.retrievePath(v1));
		System.out.println(eg.retrievePath(v1).size());
		eg.bfs(v0, v1);
		System.out.println(eg.retrievePath(v1));
		System.out.println(eg.retrievePath(v1).size());
		// These end up as the same path I don't think it is a problem,
		// But might be?

	}

	private class Vertex {
		private Stack<Integer>[] pegs; // Each vertex will hold a
										// Towers-of-Hanoi state.

		// Constructor that takes a string such as "[[4,3,2,1],[],[]]":
		@SuppressWarnings("unchecked")
		public Vertex(String vString) {
			String[] parts = vString.split("\\],\\[");
			pegs = new Stack[NUMBER_OF_PEGS];
			for (int i = 0; i < NUMBER_OF_PEGS; i++) {
				pegs[i] = new Stack<Integer>();
				try {
					parts[i] = parts[i].replaceAll("\\[", "");
					parts[i] = parts[i].replaceAll("\\]", "");
					ArrayList<String> al = new ArrayList<String>(
							Arrays.asList(parts[i].split(",")));
					// System.out.println("ArrayList al is: " + al);
					Iterator<String> it = al.iterator();
					while (it.hasNext()) {
						Object item = it.next();
						// System.out.println("item is: " + item);
						Integer diskInteger = new Integer((String) item);
						pegs[i].push(diskInteger);
					}
				} catch (Exception e) {
				}
			}
		}

		public String toString() {
			String ans = "[";
			for (int i = 0; i < NUMBER_OF_PEGS; i++) {
				ans += pegs[i].toString().replace(" ", "");
				if (i < NUMBER_OF_PEGS - 1) {
					ans += ",";
				}
			}
			ans += "]";
			return ans;
		}

		public boolean equals(Object v) {
			return this.hashCode() == v.hashCode();
		}

		@Override
		public int hashCode() {
			return toString().hashCode();
		}

		public Stack<Integer>[] getPegs() {
			return pegs;
		}

	}

	class Edge {
		public Vertex vi;
		public Vertex vj;

		public Edge(Vertex vi, Vertex vj) {
			this.vi = vi;
			this.vj = vj;
		}

		public String toString() {
			return vi.toString() + " --> " + vj.toString();
		}

		public boolean equals(Object e) {
			return this.hashCode() == e.hashCode();
		}

		@Override
		public int hashCode() {
			return (vi.toString() + vj.toString()).hashCode();
		}
	}

	class Operator {
		private int i, j;

		public Operator(int i, int j) {
			this.i = i;
			this.j = j;
		}

		// Additional explanation of what to do here will be given in GoPost or
		// as extra text in the spec.
		@SuppressWarnings("rawtypes")
		Function getPrecondition() {
			return new Function() {
				@Override
				public Boolean apply(Object vertex) {
					if (vertex instanceof Vertex) {
						Stack<Integer> fromPeg = ((Vertex) vertex).pegs[i];
						Stack<Integer> toPeg = ((Vertex) vertex).pegs[j];
						return !fromPeg.isEmpty()
								&& (toPeg.isEmpty() || toPeg.peek() > fromPeg
										.peek());
					} else {
						return false;
					}
				}

			};
		}

		@SuppressWarnings("rawtypes")
		Function getTransition() {
			return new Function() {
				@Override
				public Vertex apply(Object vertex) {
					if (vertex instanceof Vertex) {
						Vertex v = (Vertex) vertex;
						v.pegs[j].push(v.pegs[i].pop());
						Vertex next = new Vertex(v.toString());
						v.pegs[i].push(v.pegs[j].pop());
						return next;
					} else {
						return null;
					}
				}
			};
		}

		public String toString() {
			return "Operation (" + i + ", " + j + ")";
		}
	}

}