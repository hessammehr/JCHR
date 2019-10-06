package compiler.analysis.join;

import java.util.SortedMap;
import java.util.TreeMap;

import compiler.CHRIntermediateForm.variables.Variable;
import compiler.analysis.join.JoinGraph.Edge;

import util.Resettable;
import util.builder.AbstractBuilder;
import util.builder.BuilderException;
import util.comparing.LexComparator;

public class JoinGraphBuilder extends AbstractBuilder<JoinGraph> implements Resettable {
	
	private int edgeCounter;
	private final SortedMap<Variable[],Integer> edgeMap = 
		new TreeMap<Variable[], Integer>(LexComparator.<Variable>getInstance());
	
	protected Edge createEdge(Variable[] vars) {
		Integer edgeId = edgeMap.get(vars);
		if (edgeId != null) return new Edge(edgeId, vars);
		edgeMap.put(vars, edgeCounter++);
		return new Edge(edgeCounter, vars);
	}
	
	public void buildNbNodes(int nbNodes) throws BuilderException {
		setResult(new JoinGraph(nbNodes));
	}
	
	public void buildEdge(int from, int to, Variable[] vars) throws BuilderException {
		try {
			getResult().addEdge(from, to, createEdge(vars));
		} catch (NullPointerException npe) {
			throw new BuilderException("Number of nodes not built");
		} catch (IndexOutOfBoundsException iobe) {
			throw new BuilderException(iobe);
		}
	}
	
	public void init() throws BuilderException {
		// NOP
	}
	
	public void abort() throws BuilderException {
		reset();
	}
	
	public void reset() {
		edgeCounter = 0;
		edgeMap.clear();
		setResult(null);
	}

	public void finish() throws BuilderException {
		// NOP
	}
}