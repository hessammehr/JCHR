package compiler.analysis;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import util.Resettable;
import annotations.JCHR_Declare;
import annotations.JCHR_Free;
import annotations.JCHR_Init;

import compiler.CHRIntermediateForm.arg.argument.IImplicitArgument;
import compiler.CHRIntermediateForm.arg.argument.FormalArgument.OneDummy;
import compiler.CHRIntermediateForm.arg.argument.FormalArgument.OtherDummy;
import compiler.CHRIntermediateForm.arg.argumented.IBasicArgumented;
import compiler.CHRIntermediateForm.arg.visitor.NOPArgumentVisitor;
import compiler.CHRIntermediateForm.conjuncts.Conjunction;
import compiler.CHRIntermediateForm.conjuncts.IConjunct;
import compiler.CHRIntermediateForm.conjuncts.IConjunctVisitor;
import compiler.CHRIntermediateForm.constraints.bi.Failure;
import compiler.CHRIntermediateForm.constraints.bi.IBuiltInConjunct;
import compiler.CHRIntermediateForm.constraints.bi.SolverBuiltInConstraintInvocation;
import compiler.CHRIntermediateForm.constraints.java.AssignmentConjunct;
import compiler.CHRIntermediateForm.constraints.java.NoSolverConjunct;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.OccurrenceType;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConjunct;
import compiler.CHRIntermediateForm.init.InitialisatorMethodInvocation;
import compiler.CHRIntermediateForm.matching.CoerceMethod;
import compiler.CHRIntermediateForm.members.AbstractMethodInvocation;
import compiler.CHRIntermediateForm.members.ConstructorInvocation;
import compiler.CHRIntermediateForm.members.FieldAccess;
import compiler.CHRIntermediateForm.members.MethodInvocation;
import compiler.CHRIntermediateForm.rulez.Body;
import compiler.CHRIntermediateForm.rulez.Guard;
import compiler.CHRIntermediateForm.types.Reflection;
import compiler.CHRIntermediateForm.variables.Variable;

/**
 * A conservative (be it somewhat ad-hoc) 
 * analysor for determining whether given conjunctions are JCHR free. 
 * The only two non-conservative choices the analysor currently makes 
 * are that:
 * <ol>
 *  <li>Calls on <code>System.out</code> and <code>System.err</code>
 *  	are JCHR free (these fields could in principle be altered to
 *  	do call backs on a JCHR handler).</li>
 *  <li>{@link Object#toString()} is JCHR free (partly because
 *  	this is implicitly required by the previous item).
 *  </li>
 * </ol>
 * 
 * @author Peter Van Weert
 */
public final class JCHRFreeAnalysor {
	
	private JCHRFreeAnalysor() { /* not instantiatable */ }
	
	final static MasterJCHRFreeAnalysor ANALYSORS = new MasterJCHRFreeAnalysor();
	public static void registerJCHRFreeAnalysor(IJCHRFreeAnalysor analysor) {
		ANALYSORS.ANALYSORS.add(analysor);
	}
	
	public final static MemberSet JCHR_FREE_MEMBERS = new MemberSet();
	public final static Set<Class<?>> JCHR_FREE_CLASSES = new HashSet<Class<?>>(32);
	public final static Set<Package> JCHR_FREE_PACKAGES = new HashSet<Package>();
	
	public static class MemberSet {
		private final Set<Member> MEMBERS = new HashSet<Member>();

		public void clear() {
			MEMBERS.clear();
		}
		
		public boolean contains(Member member) {
			if (member instanceof Method)
				return contains((Method)member);
			return MEMBERS.contains(member);
		}
		public boolean contains(Method method) {
			if (MEMBERS.contains(method)) return true;
			for (Method zuper : Reflection.getSuperMethods(method))
				if (MEMBERS.contains(zuper)) return true;
			return false;
		}
		
		public boolean isEmpty() {
			return MEMBERS.isEmpty();
		}

		public void add(Member member) {
			MEMBERS.add(member);
		}
	}
	
	public static class ClassSet {
		private final Set<Class<?>> CLASSES = new HashSet<Class<?>>();
		
		public void clear() {
			CLASSES.clear();
		}
		
		public boolean contains(Class<?> clazz) {
			return containsRec(clazz);
		}
		private boolean containsRec(Class<?> clazz) {
			if (clazz == null) return false;
			if (CLASSES.contains(clazz)) return true;
			if (containsRec(clazz.getSuperclass())) return true;
			for (Class<?> interfoce : clazz.getInterfaces())
				if (containsRec(interfoce)) return true;
			return false;
		}

		public boolean isEmpty() {
			return CLASSES.isEmpty();
		}

		public void add(Class<?> clazz) {
			CLASSES.add(clazz);
		}
	}
	
	static { reset(); }
	
	private static boolean guard;
	static boolean analysingGuard() {
		return guard;
	}
	static void setGuard(boolean guard) {
		JCHRFreeAnalysor.guard = guard;
	}
	
	public static void reset() {
		JCHR_FREE_MEMBERS.clear();
		JCHR_FREE_CLASSES.clear();
		JCHR_FREE_PACKAGES.clear();
		
		JCHR_FREE_CLASSES.add(BigInteger.class);
		JCHR_FREE_CLASSES.add(BigDecimal.class);
		JCHR_FREE_CLASSES.add(Boolean.class);
		JCHR_FREE_CLASSES.add(Byte.class);
		JCHR_FREE_CLASSES.add(Short.class);
		JCHR_FREE_CLASSES.add(Integer.class);
		JCHR_FREE_CLASSES.add(Long.class);
		JCHR_FREE_CLASSES.add(Float.class);
		JCHR_FREE_CLASSES.add(Double.class);
		JCHR_FREE_CLASSES.add(Character.class);
		JCHR_FREE_CLASSES.add(String.class);
		
		JCHR_FREE_CLASSES.add(java.awt.Color.class);
		JCHR_FREE_CLASSES.add(java.awt.AWTKeyStroke.class);
		JCHR_FREE_CLASSES.add(javax.swing.KeyStroke.class);
		JCHR_FREE_CLASSES.add(java.util.Locale.class);
		JCHR_FREE_CLASSES.add(java.net.URI.class);
		JCHR_FREE_CLASSES.add(java.net.URL.class);
		JCHR_FREE_CLASSES.add(java.lang.Class.class);
		JCHR_FREE_CLASSES.add(java.lang.Package.class);
		JCHR_FREE_CLASSES.add(java.awt.Color.class);
		JCHR_FREE_CLASSES.add(java.math.MathContext.class);
		JCHR_FREE_CLASSES.add(java.util.Currency.class);
		JCHR_FREE_CLASSES.add(java.util.Formatter.class);
		
		JCHR_FREE_CLASSES.add(Math.class);
		
		try {
			JCHR_FREE_MEMBERS.add(Object.class.getConstructor());
			JCHR_FREE_MEMBERS.add(Object.class.getMethod("toString"));
		} catch (SecurityException x) {
			x.printStackTrace();
		} catch (NoSuchMethodException x) {
			throw new InternalError();
		}
		
		ANALYSORS.ANALYSORS.clear();

		registerJCHRFreeAnalysor(new AbstractJCHRFreeAnalysor() {
			@Override
			public boolean analyse(MethodInvocation<?> conjunctOrArgument) {
				return (
					   CoerceMethod.isCoerceMethodInvocation(conjunctOrArgument)
					|| (
							conjunctOrArgument instanceof SolverBuiltInConstraintInvocation
						&& analysingGuard()
					)
				) && analyseArguments(conjunctOrArgument);
			}
		
			@Override
			public boolean analyse(ConstructorInvocation conjunctOrArgument) {
				Constructor<?> constructor = conjunctOrArgument.getConstructor();
				return (
					   constructor.isAnnotationPresent(JCHR_Declare.class)
					|| constructor.isAnnotationPresent(JCHR_Init.class)
				) && analyseArguments(conjunctOrArgument);
			}
		});
		
		registerJCHRFreeAnalysor(new BasicJCHRFreeAnalysor() {
			@Override
			protected boolean isJCHRFree(Method method) {
				return isJCHRFree2(method); 
			}
			@Override
			protected boolean isJCHRFree(Constructor<?> constructor) {
				return isJCHRFree2(constructor);
			}
			
			private <T extends AnnotatedElement & Member> boolean isJCHRFree2(T member) {
				if (member.isAnnotationPresent(JCHR_Free.class)
						 || JCHR_FREE_MEMBERS.contains(member)) return true;
				Class<?> clazz = member.getDeclaringClass();
				return clazz.isAnnotationPresent(JCHR_Free.class)
					|| JCHR_FREE_CLASSES.contains(clazz)
					|| clazz.getPackage().isAnnotationPresent(JCHR_Free.class)
					|| JCHR_FREE_PACKAGES.contains(clazz.getPackage());
			}
		});
		
		try {
			registerJCHRFreeAnalysor(new AbstractJCHRFreeAnalysor(true) {
				final Field OUT = System.class.getDeclaredField("out"),
					ERR = System.class.getDeclaredField("err");
				@Override
				public boolean analyse(MethodInvocation<?> conjunct) {
					IImplicitArgument implArg = conjunct.getImplicitArgument();
					if (!(implArg instanceof FieldAccess)) return false;
					Field field = ((FieldAccess)implArg).getArgumentable().getField();
					return (field.equals(OUT) || field.equals(ERR))
						&& analyseArguments(conjunct);
				}
			});
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			throw new InternalError();
		}
		
		registerJCHRFreeAnalysor(new AbstractJCHRFreeAnalysor() {
			@Override
			public boolean analyse(MethodInvocation<?> conjunctOrArgument) {
				Method method = conjunctOrArgument.getMethod();
				return (Modifier.isFinal(method.getModifiers())
						&& method.getDeclaringClass().equals(Enum.class))
						&& analyseArguments(conjunctOrArgument);
			}
		});
	}
	
	public static boolean hasJCHRFreeArguments(IBuiltInConjunct<?> builtInConjunct) {
		return !(builtInConjunct instanceof IBasicArgumented)
			|| hasJCHRFreeArguments((IBasicArgumented)builtInConjunct);
	}
	public static boolean hasJCHRFreeArguments(IBasicArgumented argumented) {
		return ANALYSORS.analyseArguments(argumented);
	}
	
	public static boolean isJCHRFree(IConjunct conjunct, boolean guard) {
		try {
			ANALYSORS.reset();
			setGuard(guard);
			conjunct.accept(ANALYSORS);
			return ANALYSORS.getResult();
		} catch (Exception x) {
			x.printStackTrace();
			return false;
		}
	}
	
	public static boolean isJCHRFree(Guard guard) {
		return isJCHRFree(guard, true);
	}
	public static boolean isJCHRFree(Body body) {
		return isJCHRFree(body, false);
	}
	static boolean isJCHRFree(Conjunction<?> conjunction, boolean guard) {
		try {
			ANALYSORS.reset();
			setGuard(guard);
			conjunction.accept(ANALYSORS);
			return ANALYSORS.getResult();
		} catch (Exception x) {
			x.printStackTrace();
			return false;
		}
	}
	
	static class JCHRFreeArgumentVisitor extends NOPArgumentVisitor {
		public JCHRFreeArgumentVisitor(boolean explicitOnly) { super(explicitOnly); }
		public JCHRFreeArgumentVisitor() { this(false); }
		
		private boolean result = true;
		public boolean getResult() { return result; }

		@Override
		public void visit(AbstractMethodInvocation<?> arg) throws Exception {
			if (!(arg instanceof InitialisatorMethodInvocation) 
					&& !ANALYSORS.analyse((MethodInvocation<?>)arg)) result = false;
		}
		@Override
		public void visit(ConstructorInvocation arg) throws Exception {
			if (!ANALYSORS.analyse(arg)) result = false;
		}
		
		@Override
		public void visit(OneDummy arg) throws Exception {
			throw new IllegalStateException();
		}
		@Override
		public void visit(OtherDummy arg) throws Exception {
			throw new IllegalStateException();
		}
		
		@Override
		public boolean isVisiting() {
			return super.isVisiting() && getResult();
		}
		@Override
		public void reset() throws Exception {
			super.reset();
			result = true;
		}
	}
	
	public static interface IJCHRFreeAnalysor {
		public boolean analyseArguments(IBasicArgumented argumented);
		
		public boolean analyse(ConstructorInvocation conjunctOrArgument);
	    
	    public boolean analyse(FieldAccess conjunctOrArgument);
	    
	    public boolean analyse(MethodInvocation<?> conjunctOrArgument);
	    
	    public boolean analyse(NoSolverConjunct conjunct);
	    
	    public boolean analyse(AssignmentConjunct conjunct);
	}
	
	public static abstract class AbstractJCHRFreeAnalysor implements IJCHRFreeAnalysor {
		private boolean explicitOnly;
		
		public AbstractJCHRFreeAnalysor() {
			this(false);
		}
		public AbstractJCHRFreeAnalysor(boolean explicitOnly) {
			setExplicitOnly(explicitOnly);
		}
		
		protected void setExplicitOnly(boolean explicitOnly) {
			this.explicitOnly = explicitOnly;
		}
		
		public boolean analyseArguments(IBasicArgumented argumented) {
			try {
				JCHRFreeArgumentVisitor visitor = new JCHRFreeArgumentVisitor(explicitOnly);
				argumented.accept(visitor);
				return visitor.getResult();
			} catch (Exception x) {
				x.printStackTrace();
				return false;
			}
		}
		public boolean analyse(AssignmentConjunct conjunct) {
			return false;
		}
		public boolean analyse(ConstructorInvocation conjunctOrArgument) {
			return false;
		}
		public boolean analyse(FieldAccess conjunctOrArgument) {
			return false;
		}
		public boolean analyse(MethodInvocation<?> conjunctOrArgument) {
			return false;
		}
		public boolean analyse(NoSolverConjunct conjunct) {
			return false;
		}
	}
	
	public static abstract class BasicJCHRFreeAnalysor extends AbstractJCHRFreeAnalysor {
		
		public BasicJCHRFreeAnalysor() {
			super();
		}
		public BasicJCHRFreeAnalysor(boolean explicitOnly) {
			super(explicitOnly);
		}
		
		@Override
		public final boolean analyse(ConstructorInvocation conjunctOrArgument) {
			return isJCHRFree(conjunctOrArgument.getConstructor()) && analyseArguments(conjunctOrArgument);
		}
		protected abstract boolean isJCHRFree(Constructor<?> constructor);
	    
		@Override
	    public final boolean analyse(FieldAccess conjunctOrArgument) {
	    	return analyseArguments(conjunctOrArgument);
	    }
		
		@Override
	    public final boolean analyse(MethodInvocation<?> conjunctOrArgument) {
			return isJCHRFree(conjunctOrArgument.getMethod()) && analyseArguments(conjunctOrArgument);
		}
	    protected abstract boolean isJCHRFree(Method method);
	    
	    @Override
	    public final boolean analyse(NoSolverConjunct conjunct) {
	    	return analyseArguments(conjunct);
	    }

	    @Override
	    public final boolean analyse(AssignmentConjunct conjunct) {
	    	return analyseArguments(conjunct);
	    }
	}
	
	static class MasterJCHRFreeAnalysor 
		implements IJCHRFreeAnalysor, IConjunctVisitor, Resettable {

		final List<IJCHRFreeAnalysor> ANALYSORS = new ArrayList<IJCHRFreeAnalysor>();
		
		private boolean result = true;
		public boolean getResult() { return result; }
		public void reset() { result = true; }
		
		public boolean analyseArguments(IBasicArgumented argumented) {
			for (IJCHRFreeAnalysor analysor : ANALYSORS)
				if (analysor.analyseArguments(argumented))
					return true;
			return false;
		}
		public boolean analyse(AssignmentConjunct conjunct) {
			for (IJCHRFreeAnalysor analysor : ANALYSORS)
				if (analysor.analyse(conjunct))
					return true;
			return false;
		}
		public boolean analyse(FieldAccess conjunctOrArgument) {
			for (IJCHRFreeAnalysor analysor : ANALYSORS)
				if (analysor.analyse(conjunctOrArgument))
					return true;
			return false;
		}
		public boolean analyse(NoSolverConjunct conjunct) {
			for (IJCHRFreeAnalysor analysor : ANALYSORS)
				if (analysor.analyse(conjunct))
					return true;
			return false;
		}
		public boolean analyse(ConstructorInvocation conjunctOrArgument) {
			for (IJCHRFreeAnalysor analysor : ANALYSORS)
				if (analysor.analyse(conjunctOrArgument))
					return true;
			return false;
		}
		public boolean analyse(MethodInvocation<?> conjunctOrArgument) {
			for (IJCHRFreeAnalysor analysor : ANALYSORS)
				if (analysor.analyse(conjunctOrArgument))
					return true;
			return false;
		}
		
		public void visit(AssignmentConjunct conjunct) throws Exception {
			result = result && analyse(conjunct);
		}
		public void visit(ConstructorInvocation conjunct) throws Exception {
			result = result && analyse(conjunct);
		}
		public void visit(Failure conjunct) throws Exception {
			// NOP
		}
		public void visit(FieldAccess conjunct) throws Exception {
			result = result && analyse(conjunct);
		}
		public void visit(InitialisatorMethodInvocation conjunct) throws Exception {
			// NOP
		}
		public void visit(MethodInvocation<?> conjunct) throws Exception {
			result = result && analyse(conjunct);
		}
		public void visit(NoSolverConjunct conjunct) throws Exception {
			result = result && analyse(conjunct);
		}
		public void visit(Occurrence occurrence) throws Exception {
			throw new IllegalStateException();
		}
		public void visit(UserDefinedConjunct conjunct) throws Exception {
			result = false;
		}
		public void visit(Variable conjunct) throws Exception {
			// NOP
		}
		public boolean visits(OccurrenceType type) {
			throw new IllegalStateException();
		}
	}
}