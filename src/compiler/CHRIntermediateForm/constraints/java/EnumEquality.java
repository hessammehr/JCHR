package compiler.CHRIntermediateForm.constraints.java;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.matching.MatchingInfo;
import compiler.CHRIntermediateForm.matching.MatchingInfos;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.types.PrimitiveType;
import compiler.CHRIntermediateForm.types.TypeFactory;

public class EnumEquality extends NoSolverConstraint {

    public final static IType ENUM_TYPE = TypeFactory.getInstance(Enum.class);
    
    private EnumEquality() {
        this(EQi2);
    }
    
    private EnumEquality(String id) {
        super(IType.OBJECT, id, true);
    }
    
    private static EnumEquality instance;
    public static EnumEquality getInstance() {
        if (instance == null)
            instance = new EnumEquality();
        return instance;
    }
    
    private static EnumEquality negatedInstance;
    public static EnumEquality getNegatedInstance() {
        if (negatedInstance == null)
            negatedInstance = new EnumEquality(NEQi);
        return negatedInstance;
    }
    
    @Override
    public MatchingInfos canHaveAsArguments(IArguments args) {
        if (args.getArity() != 2) 
            return MatchingInfos.NO_MATCH;
        
        return (
               args.getArgumentAt(0).isDirectlyAssignableTo(ENUM_TYPE)
            || args.getArgumentAt(1).isDirectlyAssignableTo(ENUM_TYPE) 
        )
            ? MatchingInfos.EXACT_MATCH
            : MatchingInfos.NO_MATCH;
    }
    
    @Override
    public MatchingInfo canHaveAsArgumentAt(int index, IArgument argument) {
        return MatchingInfo.valueOf(PrimitiveType.isPrimitive(argument.getType()));
    }
    
    @Override
    protected IType getArgumentType() {
        return ENUM_TYPE;
    }
    
    @Override
    public boolean equals(Object other) {
        return this == other;
    }

    @Override
    public String getIdentifier() {
        return EQ;
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
    public boolean isSymmetric() {
        return true;
    }
    @Override
    public boolean isCoreflexive() {
        return this != negatedInstance;
    }
    @Override
    public boolean isEquality() {
        return this != negatedInstance;
    }
    @Override
    public boolean isIrreflexive() {
        return this == negatedInstance;
    }
    @Override
    public boolean isReflexive() {
        return this != negatedInstance;
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