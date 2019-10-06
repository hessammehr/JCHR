package compiler.CHRIntermediateForm.constraints.ud.schedule;

import static compiler.CHRIntermediateForm.conjuncts.ImplicitGuardFactory.createImplicitGuard;
import static compiler.CHRIntermediateForm.variables.VariableFactory.createImplicitVariable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import util.Arrays;

import compiler.CHRIntermediateForm.conjuncts.IGuardConjunct;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.lookup.DefaultLookup;
import compiler.CHRIntermediateForm.rulez.NegativeHead;
import compiler.CHRIntermediateForm.variables.IActualVariable;
import compiler.CHRIntermediateForm.variables.NamelessVariable;
import compiler.CHRIntermediateForm.variables.Variable;

public class DefaultScheduleElements
    extends ArrayList<IScheduleElement>
    implements IScheduleElements {
    
    private static final long serialVersionUID = 1L;

    public static IScheduleElements createPositiveInstance(final Occurrence occurrence) {
        return new DefaultScheduleElements(occurrence) {
            private static final long serialVersionUID = 1L;
            
            private IActualVariable[] implicit;
            
            @Override
            public void addInitialElements() {
                final int arity = occurrence.getArity();
                
                IActualVariable[] explicit = 
                    occurrence.getVariableList().toArray(new IActualVariable[arity]);
                implicit = new IActualVariable[arity];
                
                for (int i = 0; i < arity; i++) {
                    if (explicit[i] != NamelessVariable.getInstance()
                        && !explicit[i].isImplicit()
                        && (Arrays.identityFirstIndexOf(explicit, explicit[i]) != i)
                       ) {
                        implicit[i] = createImplicitVariable(explicit[i]); 
                        add(createImplicitGuard(
                            (Variable)implicit[i], explicit[i], true
                        ));
                    } else {
                        implicit[i] = explicit[i];
                    }
                }
            }
            
            @Override
            public IActualVariable[] getActiveImplicitVariables() {
                return implicit;
            }
        };
    }
    
    public static IScheduleElements createNegativeInstance(NegativeHead negativeHead) {
        return new DefaultScheduleElements(negativeHead);
    }

    DefaultScheduleElements(IScheduled scheduled) {
        try {
            final Set<Variable> initialKnown = scheduled.getInitiallyKnownVariables();
            addInitialElements();
            
            scheduled.accept(new AbstractJoinOrderVisitor() {
                private Set<IActualVariable> alsoKnown = new HashSet<IActualVariable>();
                
                @Override
                public void visit(NegativeHead negativeHead) throws Exception {
                    add(negativeHead);
                }
                @Override
                public void visit(IGuardConjunct explicitGuard) throws Exception {
                    add(explicitGuard);
                }
                @Override
                public void visit(Occurrence occurrence) throws Exception {
                    int arity = occurrence.getArity();

                    if (arity == 0) {
                        add(new DefaultLookup(occurrence));
                    } else {
                        List<IActualVariable> explicitVariables = occurrence.getVariableList();
                        IActualVariable[] implicitVariables = new IActualVariable[arity];
                        
                        add(new DefaultLookup(occurrence, implicitVariables));
                        
                        boolean positive = occurrence.isPositive();
                        
                        for (int i = 0; i < arity; i++) {
                            IActualVariable explicit = explicitVariables.get(i);
                            if (explicit == NamelessVariable.getInstance() || explicit.isImplicit())
                                implicitVariables[i] = explicit;
                            else if (initialKnown.contains(explicit) || !alsoKnown.add(explicit)) {
                                Variable implicit = createImplicitVariable(explicit);
                                implicitVariables[i] = implicit;
            
                                add(createImplicitGuard(implicit, explicit, positive));
                            }
                            else implicitVariables[i] = explicit;
                        }
                    }
                }
            });
        } catch (Exception x) {
            // CANNOT happen
            throw new RuntimeException(x);
        }
    }
    
    public IActualVariable[] getActiveImplicitVariables() {
        return null;
    }
    
    public void addInitialElements() {
        // by default there are no initial elements
    }
    
    public void accept(IScheduleVisitor visitor) throws Exception {
        visitor.isVisiting();
        for (IScheduleElement element : this) element.accept(visitor);
    }
    
    public int getLength() {
    	return size();
    }
    public IScheduleElement getElementAt(int index) {
    	return get(index);
    }
    public int getIndexOf(IScheduleElement element) {
    	for (int i = 0; i < getLength(); i++)
    		if (getElementAt(i) == element)
    			return i;
    	throw new NoSuchElementException();
    }
}
