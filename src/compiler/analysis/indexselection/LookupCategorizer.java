package compiler.analysis.indexselection;

import java.util.Comparator;

import util.Arrays;

import compiler.CHRIntermediateForm.CHRIntermediateForm;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.constraints.ud.lookup.Lookup;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.ILookupCategory;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.DefaultLookupType;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.ILookupType;
import compiler.CHRIntermediateForm.constraints.ud.schedule.AbstractScheduleVisitor;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IScheduleVisitable;
import compiler.CHRIntermediateForm.rulez.NegativeHead;
import compiler.CHRIntermediateForm.rulez.Rule;
import compiler.analysis.AnalysisException;
import compiler.analysis.CifAnalysor;
import compiler.options.Options;

public class LookupCategorizer extends CifAnalysor {

    public LookupCategorizer(CHRIntermediateForm cif, Options options) {
        super(cif, options);
    }
    
    @Override
    public boolean doAnalysis() throws AnalysisException {
        analyseRules();
        analyseConstraints();
        return true;
    }

    @Override
    protected void analyse(Rule rule) throws AnalysisException {
        for (Occurrence occurrence : rule.getPositiveHead())
            if (!occurrence.isPassive()) categorizeLookups(occurrence);
        for (NegativeHead negativeHead : rule.getNegativeHeads())
            if (negativeHead.isSelective()) categorizeLookups(negativeHead);
        // if a negative head always succeeds, we do not have to test it
        //  - if a positive occurrence is active
        //  - or (in theory) if a negative one would be active
    }
    
    protected void categorizeLookups(IScheduleVisitable scheduleVisitable) {
        try {
            scheduleVisitable.accept(new AbstractScheduleVisitor() {
                @Override
                public void visit(Lookup lookup) {
                    ILookupType type = lookup.getLookupType();
                    if (type == DefaultLookupType.getInstance())
                        lookup.getConstraint().ensureDefaultLookupCategory();
                    else {
                        ILookupCategory category = 
                            lookup.getConstraint().ensureLookupCategory(type);
                        category.addLookupType(type);
                        lookup.setLookupCategory(category);
                    }
                }
            });
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }
    
    @Override
    protected void analyse(UserDefinedConstraint constraint) throws AnalysisException {
        // If it has a master category, this is the default category
        // (implemented with a single linked list): you can't do
        // any better then that! Another possibility is that
        // the masterlookup analysis is called more then once...
        if (constraint.hasMasterLookupCategory()) return;

        // Otherwise we look for the best lookup category:
        Arrays.max(
            LookupCategoryComparator.getInstance(),
            constraint.getLookupCategories().toArray()
        ).setMasterCategory();
    }
    
    private static class LookupCategoryComparator implements Comparator<ILookupCategory> {
        private LookupCategoryComparator() {/* NOP */}
        
        private static LookupCategoryComparator instance;
        public static LookupCategoryComparator getInstance() {
            if (instance == null)
                instance = new LookupCategoryComparator();
            return instance;
        }
        
        public int compare(ILookupCategory one, ILookupCategory other) {
            // For now: we do no reasoning at all here...
            return 0;
        }
    }
}