package compiler.CHRIntermediateForm.constraints.java;

import java.lang.reflect.TypeVariable;
import java.util.Comparator;

import compiler.CHRIntermediateForm.arg.argument.constant.IntArgument;
import compiler.CHRIntermediateForm.arg.arguments.Arguments;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.members.Method;
import compiler.CHRIntermediateForm.solver.Solver;
import compiler.CHRIntermediateForm.types.GenericType;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.types.Reflection;
import compiler.CHRIntermediateForm.types.TypeParameter;

/*
 * Version 1.3.1    (Peter Van Weert)
 *  - Since solver "cannot be coerced" in the private getInstances method
 *      if (type.isAssignableTo(comparator).isMatch())
 *    was changed to
 *      if (type.isDirectlyAssignableTo(comparator))
 *  - Related: the following test was superfluous since solvers cannot be
 *    immutable Java wrappers!
 *      if (comparator.isImmutableJavaWrapper()) return new Comparison[0];
 *  - String, Boolean, BigInteger and BigDecimal are to be compared
 *      using the Comparable interface!
 *  - equals-methodes zijn er!
 */
public abstract class Comparison extends NoSolverConstraint {
    
    private final static Comparison[] NO_COMPARISONS = new Comparison[0];

    protected Comparison(IType argType, String infix, String actual) {
        super(argType, infix, actual, true);
    }
    
    /**
     * @pre ``solver.getType() extends java.util.Comparator''
     */
    public static Comparison[] getInstances(Solver solver) {
        return getInstances(solver, solver.getType());
    }
    /*
     * Allow recursion in case the solver is a TypeParameter
     * (we still need the original solver object) 
     */
    private static Comparison[] getInstances(Solver solver, IType comp) {
        if (comp instanceof TypeParameter) {
            final GenericType comparator = GenericType.getInstance(Comparator.class);
            for (IType type : ((TypeParameter)comp).getUpperBounds())
                // TODO: this test is wrong!
                if (type.isDirectlyAssignableTo(comparator))
                    return getInstances(solver, type);
            throw new IllegalArgumentException("This solver isn't a comparator!");
        }
        else {  // comp instanceof GenericType
            final GenericType comparator = (GenericType)comp;

            final IType argType = Reflection.reflect(comparator, ComparatorComparison.TYPE_VARIABLE);
            return new Comparison[] {
                new ComparatorComparison(argType, LTi,  solver),
                new ComparatorComparison(argType, GTi,  solver),
                new ComparatorComparison(argType, LEQi, solver),
                new ComparatorComparison(argType, LEQi2, LEQi, solver),
                new ComparatorComparison(argType, GEQi, solver)
            };
        }
    }
    
    /**
     * @pre ``comp extends java.lang.Comparable''
     */
    public static Comparison[] getInstances(IType comp) {
        if (comp instanceof TypeParameter) {
            for (IType type : ((TypeParameter)comp).getUpperBounds())
                // TODO: this test is wrong!
                if (type.isDirectlyAssignableTo(GenericType.getInstance(Comparable.class)))
                    return getInstances(type);
            throw new IllegalArgumentException("This type isn't comparable!");
        }
        else {  // comparable instanceof GenericType
            final GenericType comparable = (GenericType)comp;
            
            if (comparable.isComparablePrimitiveWrapper()) 
                return NO_COMPARISONS;
            
            final IType argType = Reflection.reflect(comparable, ComparableComparison.TYPE_VARIABLE);
            return new Comparison[] {
                new ComparableComparison(argType, LTi),
                new ComparableComparison(argType, GTi),
                new ComparableComparison(argType, LEQi),
                new ComparableComparison(argType, LEQi2, LEQi),
                new ComparableComparison(argType, GEQi)
            };
        }
    }

    protected Class<?> getErasedArgumentType() {
    	return getArgumentType().getErasure();
    }
    
    private static class ComparableComparison extends Comparison {
        protected final static TypeVariable<?> TYPE_VARIABLE = Comparable.class.getTypeParameters()[0];
        
        ComparableComparison(IType argType, String infix) {
            this(argType, infix, infix);
        }
        
        ComparableComparison(IType argType, String infix, String actual) {
            super(argType, infix, actual);
        }
        
        
        @Override
        public NoSolverConjunct createInstance(IArguments arguments) {
            try {
				return super.createInstance(
				    new Arguments(
						new Method(getErasedArgumentType().getMethod("compareTo", Object.class)).createInstance(arguments),
						IntArgument.ZERO
					)
				);
			} catch (NoSuchMethodException e) {
				throw new IllegalStateException();
			}
        }
        
        @Override
        public boolean equals(NoSolverConstraint other) {
            return (other instanceof ComparableComparison) 
                && super.equals(other);
        }
    }
    
    private static class ComparatorComparison extends Comparison {
        protected final static TypeVariable<?> TYPE_VARIABLE = Comparator.class.getTypeParameters()[0];
        
        private Solver implicitArgument;
        private Method compareToMethod;
        
        ComparatorComparison(IType argType, String infix, Solver solver) {
            this(argType, infix, infix, solver);
        }
        ComparatorComparison(IType argType, String infix, String actual, Solver solver) {
            super(argType, infix, actual);
            setImplicitArgument(solver);
        }
        
        @Override
        public NoSolverConjunct createInstance(IArguments arguments) {
            arguments.addImplicitArgument(getImplicitArgument());
            return super.createInstance(
                new Arguments(compareToMethod.createInstance(arguments), IntArgument.ZERO)
            );
        }

        protected Solver getImplicitArgument() {
            return implicitArgument;
        }
        public Method getCompareToMethod() {
			return compareToMethod;
		}
        protected void setImplicitArgument(Solver implicitArgument) {
            try {
            	this.implicitArgument = implicitArgument;
				java.lang.reflect.Method COMPARE_TO = Comparator.class.getMethod("compareTo", Object.class);
				for (Method method : implicitArgument.getMethods("compareTo")) 
					if (Reflection.overrides(method.getMethod(), COMPARE_TO)) {
						this.compareToMethod = method;
						return;
					}
			} catch (NoSuchMethodException e) {
				throw new InternalError();
			}
			throw new IllegalStateException();
        }
        
        @Override
        public boolean equals(NoSolverConstraint other) {
            return (other instanceof ComparatorComparison)
                && this.equals(other);
        }
        
        public boolean equals(ComparatorComparison other) {
            return super.equals(other)
                && this.getImplicitArgument().equals(other.getImplicitArgument());
        }
    }
}