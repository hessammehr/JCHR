package compiler.analysis.join;

import static util.Arrays.intersectSorted;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import util.builder.BuilderException;
import util.builder.IDirector;
import util.iterator.ChainingIterator;
import util.iterator.IteratorUtilities;

import compiler.CHRIntermediateForm.ICHRIntermediateForm;
import compiler.CHRIntermediateForm.constraints.ud.schedule.ISelector;
import compiler.CHRIntermediateForm.rulez.Head;
import compiler.CHRIntermediateForm.rulez.Rule;
import compiler.CHRIntermediateForm.variables.Variable;
import compiler.analysis.AnalysisException;
import compiler.analysis.CifAnalysor;
import compiler.options.Options;

public class JoinAnalysor extends CifAnalysor implements IDirector<JoinGraphBuilder> {

	public JoinAnalysor(ICHRIntermediateForm cif, Options options) {
		super(cif, options);
		setBuilder(new JoinGraphBuilder());
	}
	
	private JoinGraphBuilder builder;
	
	protected void setBuilder(JoinGraphBuilder builder) {
		this.builder = builder;
	}
	public JoinGraphBuilder getBuilder() {
		return builder;
	}
	
	public void construct() throws BuilderException {
		throw new BuilderException();
	}
	
	@Override
	public boolean doAnalysis() throws AnalysisException {
		analyseRules();
		return true;
	}
	
	@Override
	protected void analyse(Rule rule) throws AnalysisException {
		if (!rule.isTerminated()) try {
			Head head = rule.getPositiveHead();
			
			getBuilder().reset();
			getBuilder().buildNbNodes(head.getNbOccurrences());
			int n = head.getNbOccurrences();
			for (int i = 0; i < n; i++) for (int j = i+1; j < n; j++)
				getBuilder().buildEdge(i, j, getVariables(head, i, j));
			getBuilder().finish();
		
		} catch (BuilderException be) {
			throw new AnalysisException(be);
		}
	}
	
	protected static class VariableSet extends TreeSet<Variable> {
		private static final long serialVersionUID = 1L;
		public void addAll(Variable[] vars) {
			for (Variable var : vars) add(var);
		}
	}
	
	protected static Variable[] getVariables(Head head, int i, int j) {
		Variable[] oneVars = head.getOccurrenceAt(i).getVariableArray();
		Variable[] otherVars = head.getOccurrenceAt(j).getVariableArray();
		VariableSet result = new VariableSet();
		result.addAll(intersectSorted(oneVars, otherVars));
		
		for (ISelector selector : getSelectors(head)) {
			Set<Variable> selectorVarSet = selector.getJoinOrderPrecondition();
			Variable[] selectorVars = new Variable[selectorVarSet.size()]; 
			selectorVarSet.toArray(selectorVars);
			
			Variable[] s1 = intersectSorted(oneVars, selectorVars);
			if (s1.length != 0) {
				Variable[] s2 = intersectSorted(otherVars, selectorVars);
				if (s2.length != 0) {
					result.addAll(s1);
					result.addAll(s2);
				}
			}
		}
		
		return result.toArray(new Variable[result.size()]);
	}
	
	protected static Iterable<ISelector> getSelectors(Head head) {
		return getSelectors(head.getRule());
	}
	protected static Iterable<ISelector> getSelectors(final Rule rule) {
		return new Iterable<ISelector>() {
            @SuppressWarnings("unchecked")
            public Iterator<ISelector> iterator() {
                return new ChainingIterator<ISelector>(
                    rule.getPositiveGuard().iterator(),
                    rule.getNegativeHeads().iterator()
                );
            }
            
            @Override
            public String toString() {
                return IteratorUtilities.deepToString(this);
            }
        };
	}
}
