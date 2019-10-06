package compiler.CHRIntermediateForm.constraints.ud.schedule;

import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.lookup.Lookup;
import compiler.CHRIntermediateForm.rulez.NegativeHead;
import compiler.CHRIntermediateForm.variables.IActualVariable;
import compiler.CHRIntermediateForm.variables.NamelessVariable;

public class DefaultVariableInfoQueue
    extends AbstractVariableInfoQueue {
    
    private static final long serialVersionUID = 1L;
    
    public static IVariableInfoQueue createPositiveInstance(final Occurrence occurrence) {
        return new DefaultVariableInfoQueue(occurrence) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void offerInitialVariableInfos() {
                IActualVariable[] implicitVars = 
                    occurrence.getScheduleElements().getActiveImplicitVariables();
                
                assert implicitVars.length == occurrence.getArity();
                
                for (int i = 0; i < implicitVars.length; i++) {
                    if (implicitVars[i] != NamelessVariable.getInstance()) {
                        assert implicitVars[i].isImplicit() 
                            || implicitVars[i] == occurrence.getArgumentAt(i); 
                        
                        offer(implicitVars[i],
                            occurrence,
                            occurrence.getFormalVariableAt(i),
                            0
                        );
                    }
                }
            }
        };
    }
    
    public static IVariableInfoQueue createNegativeInstance(NegativeHead head) {
        return new DefaultVariableInfoQueue(head);
    }

    DefaultVariableInfoQueue(IScheduled scheduled) {
        try {
            offerInitialVariableInfos();
            
            scheduled.accept(new AbstractScheduleVisitor() {
                private int index;
                
                @Override
                protected void visit(IScheduleElement element) throws Exception {
                    index++;
                }
                @Override
                public void visit(Lookup lookup) {
                    // FIRST increment index: we are adding declarations AFTER the lookup
                    index++;
                    
                    Occurrence occurrence = lookup.getOccurrence();
                    IActualVariable[] variables = lookup.getVariables();
                    
                    for (int i = 0; i < variables.length; i++) {
                        if (variables[i] != NamelessVariable.getInstance()) {
                            offer(variables[i],
                                occurrence,
                                occurrence.getFormalVariableAt(i),
                                index
                            );
                        }
                    }
                }
            });
        } catch (Exception x) {
            // CANNOT happen
            throw new RuntimeException(x);
        }
    }
    
    protected void offerInitialVariableInfos() {
        // default: do nothing
    }
}
