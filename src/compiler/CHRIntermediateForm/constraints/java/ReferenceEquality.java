package compiler.CHRIntermediateForm.constraints.java;

import java.util.List;

import util.comparing.Comparison;
import static util.comparing.Comparison.*;
import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.matching.CoerceMethod;
import compiler.CHRIntermediateForm.matching.MatchingInfo;
import compiler.CHRIntermediateForm.matching.MatchingInfos;
import compiler.CHRIntermediateForm.types.GenericType;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.types.PrimitiveType;

public class ReferenceEquality extends NoSolverConstraint {

    private ReferenceEquality() {
        this(EQi2);
    }
    
    private ReferenceEquality(String id) {
        super(IType.OBJECT, id, true);
    }
    
    private static ReferenceEquality instance;
    public static ReferenceEquality getInstance() {
        if (instance == null)
            instance = new ReferenceEquality();
        return instance;
    }
    
    private static ReferenceEquality forcedInstance;
    public static ReferenceEquality getForcedInstance() {
        if (forcedInstance == null)
            forcedInstance = new ReferenceEquality();
        return forcedInstance;
    }
    
    private static ReferenceEquality negatedInstance;
    public static ReferenceEquality getNegatedInstance() {
        if (negatedInstance == null)
            negatedInstance = new ReferenceEquality(NEQi);
        return negatedInstance;
    }
    
    @Override
    public MatchingInfos canHaveAsArguments(IArguments args) {
        // excluding the obvious:
        IType type0, type1;
        if (args.getArity() != 2
            || PrimitiveType.isPrimitive(type0 = args.getTypeAt(0))
            || PrimitiveType.isPrimitive(type1 = args.getTypeAt(1))
        ) return MatchingInfos.NO_MATCH;
        
        if (this != forcedInstance 
                && GenericType.isPrimitiveWrapper(type0) 
                && GenericType.isPrimitiveWrapper(type1))
            return MatchingInfos.NO_MATCH;
        
        // both types should always be compatible:
        if (type0.isCompatibleWith(type1))
            return MatchingInfos.DIRECT_MATCH;
        
        // if not, we can still try coercion...
        //  (initialisation ~ new object ==> '==' cannot succeed...)
        
        // first try to coerce one to the other:
        MatchingInfo
            info01 = type0.isAssignableTo(type1),
            info10 = type1.isAssignableTo(type0);
        
        if (info01.isNonInitCoercedMatch())
            if (info10.isNonInitCoercedMatch())
                return MatchingInfos.AMBIGUOUS_NO_INIT;
            else
                return new MatchingInfos(false, info01, MatchingInfo.EXACT_MATCH);
        else
            if (info10.isNonInitCoercedMatch())
                return new MatchingInfos(false, MatchingInfo.EXACT_MATCH, info10);
        
        // last resort: coerce both to compatible two types
        //  (note: we do not know where we are going, we don't want to do
        //   cycle detection, so only coerce-chains of length 1)
        // ps: we hope this will not occur to often...
        List<CoerceMethod> coerces1 = type1.getCoerceMethods();
        CoerceMethod[] best = null;
        
        for (CoerceMethod coerce0 : type0.getCoerceMethods()) {
            for (CoerceMethod coerce1 : coerces1) {
                if (coerce0.getReturnType().isCompatibleWith(coerce1.getReturnType())) {
                    if (best == null) { 
                        best = new CoerceMethod[] { coerce0, coerce1 };
                        continue;
                    } else {
                        Comparison 
                            comp0 = best[0].compareTo(coerce0),
                            comp1 = best[1].compareTo(coerce1);
                        
                        if (comp1 == AMBIGUOUS) 
                            return MatchingInfos.AMBIGUOUS_NO_INIT; 
                        switch (comp0) {
                            case AMBIGUOUS:
                                return MatchingInfos.AMBIGUOUS_NO_INIT;
                            case EQUAL:
                                switch (comp1) {
                                    case EQUAL:
                                        return MatchingInfos.AMBIGUOUS_NO_INIT;
                                    case BETTER:
                                        best[0] = coerce0;
                                        best[1] = coerce1;
                                    continue;
                                }
                            
                            case BETTER:
                                if (comp1 == WORSE)
                                    return MatchingInfos.AMBIGUOUS_NO_INIT;
                                best[0] = coerce0;
                                best[1] = coerce1;
                            continue;
                            
                            case WORSE:
                                if (comp1 == BETTER)
                                    return MatchingInfos.AMBIGUOUS_NO_INIT;
                        }
                    }
                }
            }
        }
        
        if (best == null) 
            return MatchingInfos.NO_MATCH;
        else
            return new MatchingInfos(false, best);
    }
    
    @Override
    public MatchingInfo canHaveAsArgumentAt(int index, IArgument argument) {
        return MatchingInfo.valueOf(!PrimitiveType.isPrimitive(argument.getType()));
    }
    
    @Override
    public boolean equals(Object other) {
        return this == other;
    }

    @Override
    public String getIdentifier() {
        return REF_EQ;
    }

    @Override
    public boolean isAntisymmetric() {
        return this != negatedInstance;
    }
    @Override
    public boolean isAskConstraint() {
        return true;
    }
    @Override
    public boolean isAsymmetric() {
        return false;
    }
    @Override
    public boolean isCoreflexive() {
        return this != negatedInstance;
    }
    @Override
    public boolean isEquality() {
        return false;   // !!!
    }
    @Override
    public boolean isIrreflexive() {
        return this == negatedInstance;
    }
    @Override
    public boolean isReflexive() {
        return false;   // !!!
    }
    @Override
    public boolean isSymmetric() {
        return true;
    }
    @Override
    public boolean isTotal() {
        return false;
    }
    @Override
    public boolean isTransitive() {
        return this != negatedInstance;
    }
    @Override
    public boolean isTrichotomous() {
        return false;
    }
}