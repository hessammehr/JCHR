package compiler.analysis.indexselection;

import static compiler.CHRIntermediateForm.constraints.ud.lookup.type.IndexType.FD_SS_HASH_MAP;
import static compiler.CHRIntermediateForm.constraints.ud.lookup.type.IndexType.HASH_MAP;
import static compiler.CHRIntermediateForm.constraints.ud.lookup.type.IndexType.SS_HASH_MAP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import util.Arrays;
import util.Resettable;
import util.builder.BuilderException;
import util.builder.Current;
import util.builder.IBuilder;
import util.builder.IDirector;

import compiler.CHRIntermediateForm.ICHRIntermediateForm;
import compiler.CHRIntermediateForm.arg.argument.FormalArgument;
import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argument.ILeafArgument;
import compiler.CHRIntermediateForm.arg.argumentable.IArgumentable;
import compiler.CHRIntermediateForm.arg.argumented.IArgumented;
import compiler.CHRIntermediateForm.arg.arguments.Arguments;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.arg.visitor.AbstractVariableScanner;
import compiler.CHRIntermediateForm.arg.visitor.UpCastingArgumentVisitor;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunct;
import compiler.CHRIntermediateForm.conjuncts.ImplicitGuardConjunct;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.constraints.ud.lookup.BasicLookup;
import compiler.CHRIntermediateForm.constraints.ud.lookup.Lookup;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.BinaryGuardedLookupType;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.DefaultLookupType;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.ILookupType;
import compiler.CHRIntermediateForm.constraints.ud.schedule.AbstractScheduleVisitor;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IScheduleElement;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IScheduled;
import compiler.CHRIntermediateForm.constraints.ud.schedule.ISelector;
import compiler.CHRIntermediateForm.constraints.ud.schedule.ScheduleElements;
import compiler.CHRIntermediateForm.rulez.NegativeHead;
import compiler.CHRIntermediateForm.rulez.Rule;
import compiler.CHRIntermediateForm.variables.NamelessVariable;
import compiler.CHRIntermediateForm.variables.Variable;
import compiler.analysis.AnalysisException;
import compiler.analysis.CifAnalysor;
import compiler.options.Options;

public class GreedyHashLookupInsertor extends CifAnalysor {

    public GreedyHashLookupInsertor(ICHRIntermediateForm intermediateForm, Options options) {
        super(intermediateForm, options);
    }

    @Override
    public boolean doAnalysis() throws AnalysisException {
        if (getOptions().hashIndexing()) analyseRules();
        return true;
    }
    
    @Override
    protected void analyse(Rule rule) throws AnalysisException {
        for (Occurrence occurrence : rule.getPositiveHead())
            if (!occurrence.isPassive()) analyse(occurrence);
        for (NegativeHead negativeHead : rule.getNegativeHeads())
            if (negativeHead.isSelective()) analyse(negativeHead);
    }
    
    protected void analyse(IScheduled scheduled) throws AnalysisException {
        try {
            new ScheduleConstructor(scheduled).construct();
        } catch (BuilderException be) {
            throw new AnalysisException(be);
        }
    }
    
    protected static class ScheduleConstructor 
        extends AbstractScheduleVisitor
        implements IDirector<HashLookupBuilder> {
        
        private IScheduled scheduled;
        
        private ScheduleElements scheduleElements; 

        private HashLookupBuilder builder;
        
        public ScheduleConstructor(IScheduled scheduled) {
            setBuilder(new HashLookupBuilder());
            setScheduled(scheduled);
            setScheduleElements(new ScheduleElements(
                scheduled.getScheduleElements()
            ));
        }
        
        public void construct() throws BuilderException {
            try {
                getScheduled().accept(this);
                constructIt();  // one final time for the last lookup!
                getScheduled().changeScheduleElements(getScheduleElements());
                
            } catch (BuilderException be) {
                throw be;
            } catch (Exception e) {
                throw new BuilderException(e);
            }
        }
        
        protected void constructIt() throws BuilderException {
            if (isBuilding()) {
                getBuilder().finish();
                add(getBuilder().getResult());
                addAll(getBuilder().getRemainingSelectors());
                getBuilder().reset();
            }
        }
        
        public HashLookupBuilder getBuilder() {
            return builder;
        }
        protected void setBuilder(HashLookupBuilder builder) {
            this.builder = builder;
        }
        protected boolean isBuilding() {
            return getBuilder().isBuilding();
        }
        
        public ScheduleElements getScheduleElements() {
            return scheduleElements;
        }
        protected void setScheduleElements(ScheduleElements scheduleElements) {
            this.scheduleElements = scheduleElements;
        }
        
        protected void setScheduled(IScheduled scheduled) {
            this.scheduled = scheduled;
        }
        public IScheduled getScheduled() {
            return scheduled;
        }
        
        protected boolean add(IScheduleElement element) {
            return getScheduleElements().add(element);
        }
        protected boolean addAll(Collection<? extends IScheduleElement> elements) {
            return getScheduleElements().addAll(elements);
        }

        @Override
        public void visit(NegativeHead negativeHead) throws Exception {
            if (isBuilding())
                getBuilder().buildNegativeHead(negativeHead);
            else
                add(negativeHead);
        }
        @Override
        public void visit(IGuardConjunct explicitGuard) throws Exception {
            if (isBuilding())
                getBuilder().buildExplicitGuard(explicitGuard);
            else
                add(explicitGuard);
        }
        @Override
        public void visit(ImplicitGuardConjunct implicitGuard) throws Exception {
            if (isBuilding())
                getBuilder().buildImplicitGuard(implicitGuard);
            else
                add(implicitGuard);
        }
        @Override
        public void visit(Lookup lookup) throws Exception {
            constructIt();
            
            if (lookup.getLookupType() != DefaultLookupType.getInstance())
                add(lookup);
            else
                getBuilder().buildLookup(lookup);
        }
    }
    
    protected static class HashLookupBuilder implements IBuilder<Lookup>, Resettable {
        
        private Current<Lookup> currentLookup = new Current<Lookup>();
        
        private Lookup result; 
        
        private GuardIncorporationInfo[] guardIncorporationInfos;
        private List<ISelector> remainingSelectors = new ArrayList<ISelector>();
        
        public boolean isBuilding() {
            return currentLookup.isSet();
        }
        
        public void buildLookup(Lookup lookup) throws BuilderException {
            try {
                currentLookup.set(lookup);
                guardIncorporationInfos = 
                    new GuardIncorporationInfo[lookup.getOccurrence().getArity()];
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }
        
        public void buildNegativeHead(NegativeHead head) throws BuilderException {
            if (!isBuilding()) throw new BuilderException();
            remainingSelectors.add(head);
        }
        
        
        public void buildExplicitGuard(IGuardConjunct guard) throws BuilderException {
            if (!isBuilding()) throw new BuilderException();
            if (guard.isEquality() && !guard.isNegated()) {
                try {
                    GUARD_ANALYSOR.reset();
                    guard.accept(GUARD_ANALYSOR);
                    
                    if (GUARD_ANALYSOR.canIncorporate()) {
                        incorporateGuard(guard, false,
                            GUARD_ANALYSOR.getVariableIndex(),
                            GUARD_ANALYSOR.getOther(),
                            GUARD_ANALYSOR.getOtherIndex()
                        );
                    } else {
                        remainingSelectors.add(guard);
                    }
                    
                } catch (Exception x) {
                    System.err.println("Recovered from unexpected exception: " + x);
                    remainingSelectors.add(guard);
                }
            } else {
                remainingSelectors.add(guard);
            }
        }
        
        public void buildImplicitGuard(ImplicitGuardConjunct guard) throws BuilderException {
            if (!isBuilding()) throw new BuilderException();
            try {
                IArgument other = guard.getOtherArgument();
            
                if (new AbstractVariableScanner() {
                    @Override
                    protected boolean scanVariable(Variable variable) {
                        return getVariableIndexOf(variable) >= 0;
                    }
                }.scan(other)) {       // other argument contains another variable
                    remainingSelectors.add(guard);
                    return;
                } 
                
                incorporateGuard(guard, true,
                    getVariableIndexOf(guard.getImplicitVariable()),
                    guard.getOtherArgument(),
                    ImplicitGuardConjunct.OTHER_ARGUMENT_INDEX
                );
                
            } catch (Exception x) {
                System.err.print("Recovered from unexpected exception: " + x);
                remainingSelectors.add(guard);
            }
        }
        
        protected int getVariableIndexOf(Variable variable) {
            return currentLookup.get().getVariableIndexOf(variable);
        }
        
        protected void incorporateGuard(IGuardConjunct guard, boolean implicitGuard, int variableIndex, IArgument other, int otherIndex) {
            GuardIncorporationInfo previous = guardIncorporationInfos[variableIndex];
            
            if (previous != null) {
                if ((other.isConstant() && !previous.other.isConstant())
                    || (implicitGuard && !previous.implicitGuard)
                ) {
                    remainingSelectors.add(0, previous.guard);                    
                } else { // XXX no reason (??) to prefer this one over the previous one
                    remainingSelectors.add(0, guard);
                	return;
                }
            }

            guardIncorporationInfos[variableIndex] = 
                new GuardIncorporationInfo(guard, implicitGuard, other, otherIndex);
        }
        
        public List<ISelector> getRemainingSelectors() {
            return Collections.unmodifiableList(remainingSelectors);
        }
        
        protected void setResult(Lookup result) {
            this.result = result;
        }
        public Lookup getResult() {
            return result;
        }

        public void abort() throws BuilderException {
            reset();
        }

        public void finish() throws BuilderException {
            BinaryGuardedLookupType type = 
                new BinaryGuardedLookupType(HASH_MAP);
            
            GuardIncorporationInfo[] infos = this.guardIncorporationInfos;
            
            // TODO: this does not yet work:
            int[] superfluous = currentLookup.get().getConstraint()
            		.getFunctionalDependencies()
            		.getSuperfluousIndices(Arrays.getNonNullIndices(infos));
            for (int i = 0; i < superfluous.length; i++) {
            	remainingSelectors.add(0, infos[superfluous[i]].guard);
            	infos[superfluous[i]] = null;
            }
            
            GuardIncorporationInfo info;
            IArguments arguments = new Arguments();
            
            for (int i = 0; i < infos.length; i++) {
                info = infos[i];
                if (info != null) {
                	IGuardConjunct guard = info.guard;
                	if (info.implicitGuard)
                		guard = ((ImplicitGuardConjunct)guard).getDecorated();
                    type.addGuard(i, guard, info.otherIndex);
                    arguments.addArgument(info.other);
                }
            }
            
            if (type.getNbGuards() == 0) {
                setResult(currentLookup.get());
            } else {
                UserDefinedConstraint constraint =
                    currentLookup.get().getOccurrence().getConstraint();
                if (constraint.getMultisetInfo().isSet()) {
                    	// only one guard per argument is possible!
                    if (type.getNbGuards() == constraint.getExplicitArity())
                    	type.setIndexType(SS_HASH_MAP);
                    else if (functionalDependenciesImplySetSemantics(constraint, type))
                    	type.setIndexType(FD_SS_HASH_MAP);
                }
                    
                setResult(new BasicLookup(
                    currentLookup.get(), type, arguments
                ));
            }
        }
        
        protected static boolean functionalDependenciesImplySetSemantics(UserDefinedConstraint constraint, ILookupType type) {
        	int[] indices = type.getVariableIndices();
        	int[] impliedIndices = 
        		constraint.getFunctionalDependencies().getDependents(indices);
        	return indices.length + impliedIndices.length == constraint.getArity();
        }
        
        public void init() throws BuilderException {
            // NOP
        }

        public void reset() {
            currentLookup.reset();
            remainingSelectors.clear();
            setResult(null);
        }
        
        protected static class GuardIncorporationInfo {
            public final IGuardConjunct guard;
            public final boolean implicitGuard;
            public final IArgument other;
            public final int otherIndex;

            public GuardIncorporationInfo(
                    IGuardConjunct guard, boolean implicitGuard, 
                    IArgument other, int otherIndex) 
            {
                this.guard = guard;
                this.implicitGuard = implicitGuard;
                this.other = other;
                this.otherIndex = otherIndex;
            }
        }
        
        protected final GuardAnalysor GUARD_ANALYSOR = new GuardAnalysor();
        protected class GuardAnalysor extends UpCastingArgumentVisitor {
            private int depth;
            
            private int variableIndex;
            private int otherIndex;
            private IArgument other;
            
            private boolean impossible;
            
            public GuardAnalysor() {
                super(true);
            }
            
            @Override
            public boolean recurse() {
                return !impossible;
            }
            
            @Override
            public void visit(FormalArgument arg) throws IllegalStateException {
                throw new IllegalStateException();
            }
            @Override
            public void visit(NamelessVariable arg) throws IllegalStateException {
                throw new IllegalStateException();
            }
            
            @Override
            public void visit(Variable arg) {
                if (impossible) return;
                
                int index = getVariableIndexOf(arg);
                
                if (depth == 0) {
                    if (index < 0) {
                        setOther(arg);
                    } else if (variableIndex < 0) {
                        variableIndex = index;
                    } else {    // equality between two variables
                        impossible = true;
                    }
                } else if (index >= 0) {    // equality with non-top-level variable
                    impossible = true;
                }
            }
        
            @Override
            public void visit(ILeafArgument arg) {
                if (depth == 0) setOther(arg);
            }
        
            @Override
            public void visit(IArgument arg) {
                if (depth++ == 0) setOther(arg);
            }
            
            @Override
            public <T extends IArgumented<? extends IArgumentable<?>> & IArgument> void leaveArgumented(T arg) {
                depth--;
            }
            
            @Override
            public void reset() {
                try {
                    super.reset();
                } catch (Exception x) {
                    // CANNOT HAPPEN
                }
                impossible = false;
                variableIndex = -1;
                otherIndex = -1;
            }
            
            public boolean canIncorporate() {
                return !impossible && (variableIndex >= 0) && (otherIndex >= 0);
            }
            
            public int getVariableIndex() {
                return variableIndex;
            }
            public int getOtherIndex() {
                return otherIndex;
            }
            public IArgument getOther() {
                return other;
            }
            
            public void setOther(IArgument other) {
                this.otherIndex = (variableIndex >= 0)? 1 : 0;
                this.other = other;
            }
        }
    }
}