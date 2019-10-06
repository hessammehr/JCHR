package compiler.CHRIntermediateForm.matching;

import static util.comparing.Comparison.AMBIGUOUS;
import static util.comparing.Comparison.EQUAL;

import java.util.ArrayList;
import java.util.List;

import util.collections.Empty;
import util.comparing.Comparison;

import compiler.CHRIntermediateForm.init.IInitialisator;
import compiler.CHRIntermediateForm.init.Initialisator;

/**
 * @author Peter Van Weert
 */
public class MatchingInfo extends AbstractMatchingInfo<MatchingInfo> {
    private byte info;
    
    private List<CoerceMethod> coerceMethods;
    
    private IInitialisator<?> initialisator;
 	
    private final static class ImmutableMatchingInfo extends MatchingInfo {
        ImmutableMatchingInfo(byte info) {
            super((Object)null);
            super.setInfo(info);
        }
        
        @Override
        protected void addCoerceMethod(CoerceMethod coerceMethod) {
            throw new UnsupportedOperationException();
        }
        @Override
        protected void clearCoerceMethods() {
            throw new UnsupportedOperationException();
        }
        @Override
        public boolean initCoerceMethods(MatchingInfo previous, CoerceMethod newone) {
            throw new UnsupportedOperationException();
        }
        @Override
        public void initInitialisator(IInitialisator<?> initialisator) {
            throw new UnsupportedOperationException();
        }
        @Override
        public void setAmbiguous() {
            throw new UnsupportedOperationException();
        }
        @Override
        protected void setCoerceMethod(CoerceMethod coerceMethod) {
            throw new UnsupportedOperationException();
        }
        @Override
        protected void setCoerceMethods(List<CoerceMethod> coerceMethods) {
            throw new UnsupportedOperationException();
        }
        @Override
        protected void setCoerceMethods(List<CoerceMethod> coerceMethods, CoerceMethod coerceMethod) {
            throw new UnsupportedOperationException();
        }
        @Override
        public List<CoerceMethod> getCoerceMethods() {
            return Empty.getInstance();
        }
        @Override
        protected void setCoerceMethods(MatchingInfo previous, CoerceMethod newone) {
            throw new UnsupportedOperationException();
        }
        @Override
        protected void addCoerceMethods(List<CoerceMethod> coerceMethods) {
            throw new UnsupportedOperationException();
        }
        @Override
        protected void clearInitialisator() {
            throw new UnsupportedOperationException();
        }
        @Override
        protected void setInfo(byte info) {
            throw new UnsupportedOperationException();
        }
        @Override
        protected void setInitialisator(IInitialisator<?> initialisator) {
            throw new UnsupportedOperationException();
        }
    }
    public final static MatchingInfo
        EXACT_MATCH = new ImmutableMatchingInfo(EXACT_MATCH_INFO),
    	DIRECT_MATCH = new ImmutableMatchingInfo(DIRECT_MATCH_INFO),
        AMBIGUOUS_MATCH = new ImmutableMatchingInfo(AMBIGUOUS_INFO),
    	NO_MATCH = new ImmutableMatchingInfo(NO_MATCH_INFO);
    
    public static MatchingInfo valueOf(boolean match) {
        return match? DIRECT_MATCH : NO_MATCH;
    }
    
    public MatchingInfo(CoerceMethod coerceMethod) {
        this(COERCED_MATCH_INFO);
        setCoerceMethod(coerceMethod);
    }
    
    public MatchingInfo() {
        this(NO_MATCH_INFO);
    }
    
    private MatchingInfo(byte info) {
        setInfo(info);
        setCoerceMethods(new ArrayList<CoerceMethod>(1));
    }
    
    MatchingInfo(Object dummy) {
        // NOP
    }
    
    public Comparison compareWith(MatchingInfo other) {
        final int comparison = this.getMatchClass() - other.getMatchClass();
        
        if (comparison == 0) {
            if (this.isAmbiguous() || other.isAmbiguous())
                return AMBIGUOUS; // als 1 van de 2 ambigu is is geen vergelijking mogelijk
            
            Comparison result = EQUAL;
            if (isInitMatch())
                result = Initialisator.compare(this.getInitialisator(), other.getInitialisator());

            if (result == EQUAL && isCoercedMatch())
                return CoerceMethod.compare(this.getCoerceMethods(), other.getCoerceMethods());
            else
                return result;
        }
        else
            return Comparison.get(comparison);
    }
    
    public List<CoerceMethod> getCoerceMethods() {
        return coerceMethods;
    }
    public int getNbCoerceMethods() {
        return getCoerceMethods().size();
    }
    
    protected void setCoerceMethods(List<CoerceMethod> coerceMethods) {        
        this.coerceMethods = coerceMethods;
    }
    protected void clearCoerceMethods() {
        getCoerceMethods().clear();
    }
    protected void setCoerceMethods(MatchingInfo previous, CoerceMethod newone) {
        setCoerceMethods(previous.getCoerceMethods(), newone);
    }
    protected void setCoerceMethods(List<CoerceMethod> coerceMethods, CoerceMethod coerceMethod) {
        clearCoerceMethods();        
        addCoerceMethod(coerceMethod);
        addCoerceMethods(coerceMethods);
    }
    protected void setCoerceMethod(CoerceMethod coerceMethod) {
        clearCoerceMethods();
        addCoerceMethod(coerceMethod);
    }
    protected void addCoerceMethods(List<CoerceMethod> coerceMethods) {
        getCoerceMethods().addAll(coerceMethods);        
    }
    protected void addCoerceMethod(CoerceMethod coerceMethod) {
        getCoerceMethods().add(coerceMethod);
    }
    
    public void setAmbiguous() {
        setInfo((byte)(getInfo() | AMBIGUOUS_INFO));
    }
    
    /* als een van de 2 ambigu is valt er niet te vergelijken... */
    protected boolean testAmbiguity(MatchingInfo previous) {
        if (previous.isAmbiguous()) setAmbiguous();
        return !isAmbiguous();
    }
    
    /**
     * @pre ! isExactMatch()
     * @pre previous.isMatch() 
     */
    public boolean initCoerceMethods(MatchingInfo previous, CoerceMethod newone) {
        boolean equalInitialisors = false;
        
        /* ===INIT============================================================================= */
        if (isInitMatch()) {
            if (! previous.isInitMatch()) { /* ==> previous.isExactMatch() || previous.isCoerceMatch() */
                if (previous.isDirectMatch())
                    setCoerceMethod(newone);
                else /* ==> previous.isCoerceMatch() */
                    setCoerceMethods(previous, newone);
                
                setInfo(COERCED_MATCH_INFO);
                clearInitialisator();
                return true;
            }
            else {  /* ==> isInitMatch() && previous.isInitMatch() */
                if (! testAmbiguity(previous)) return false;
                
                final IInitialisator<?> otherInitialisator = previous.getInitialisator();
                                
                switch (this.getInitialisator().compareWith(otherInitialisator)) {
                    case BETTER:
                        setCoerceMethods(previous, newone);
                        setInitialisator(otherInitialisator);
                        setInfo(COERCED_INIT_MATCH_INFO);
                    case WORSE:
                    return true;
                    
                    case AMBIGUOUS:
                        setAmbiguous();
                    return false;
                    
                    case EQUAL:
                        equalInitialisors = true;
                    break;
                }
            }
        }
        /* ===COERCE============================================================================= */
        if (isCoercedMatch()) { /* && !isInitMatch() || (equalInitialisators && no ambiguity) */ 
            if (! equalInitialisors) {
                if (previous.isInitMatch())
                    return true;
                /* ==> geen van 2 init, of alletwee equal init */
                if (! testAmbiguity(previous))
                    return false;
            }
            /* ==> geen van 2 ambigu of init (tenzij alletwee equal init) */
            if (previous.isCoercedMatch()) {
                final List<CoerceMethod> previousCoerceMethods = previous.getCoerceMethods();

                switch (CoerceMethod.compare(this.getCoerceMethods(), previousCoerceMethods.size(), newone)) {
                    case BETTER:
                        setCoerceMethods(previousCoerceMethods, newone);
                        if (equalInitialisors) setInitialisator(previous.getInitialisator());
                    case WORSE:
                    return true;
                    
                    case EQUAL:
                    case AMBIGUOUS:
                        setAmbiguous();
                    return false;
                }
            }
            
            if (previous.isDirectMatch() || equalInitialisors) {                
                switch (CoerceMethod.compare(this.getCoerceMethods(), newone)) {
                    case WORSE:
                        setCoerceMethod(newone);
                        if (equalInitialisors) setInitialisator(previous.getInitialisator());
                    case BETTER:
                    return true;
                    
                    case EQUAL:
                    case AMBIGUOUS:
                        setAmbiguous();
                    return false;
                }
            }
            
            throw new RuntimeException();
        }
        /* ===NOMATCH============================================================================= */
        if (! isMatch()) {
            if (previous.isDirectMatch())
                setCoerceMethod(newone);
            else /* ==> previous.isCoerceMatch() */
                setCoerceMethods(previous, newone);
            
            if (previous.isAmbiguous()) setAmbiguous();
            
            if (previous.isInitMatch())  {
                setInitialisator(previous.getInitialisator());
                setInfo(COERCED_INIT_MATCH_INFO);
            }
            else
                setInfo(COERCED_MATCH_INFO);
            
            return true;
        }

        throw new RuntimeException();
    }
    
    @Override
    protected byte getInfo() {
        return info;
    }
    protected void setInfo(byte info) {
        this.info = info;
    }
    
    @Override
    public String toString() {
        switch (getInfo()) {
            case EXACT_MATCH_INFO: return "exact match";
        	case DIRECT_MATCH_INFO: return "direct match";
        	case NO_MATCH_INFO:    return "no match";
        	case AMBIGUOUS_INFO:   return "ambiguous";
            case INIT_MATCH_INFO:  return "init match: " + getInitialisator();
        	case COERCED_MATCH_INFO: return "coerced match: " + getCoerceMethods();
        	default: 
                return "?match? " + getCoerceMethods() + " => " + getInitialisator(); 
        }
    }

    public IInitialisator<?> getInitialisator() {
        return initialisator;
    }    
    public void initInitialisator(IInitialisator<?> initialisator) {
        if (initialisator == null) return;
        if (isMatch() && ! isInitMatch()) return;
        if (isInitMatch()) {
            switch (initialisator.compareWith(this.getInitialisator())) {
                case EQUAL:
                    if (! isCoercedMatch()) {
                        setAmbiguous();
                        return;
                    }
                    /* else ... */
                case BETTER:
                    clearCoerceMethods();
                    setInitialisator(initialisator);
                case WORSE:
                return;

                case AMBIGUOUS:
                    setAmbiguous();
                return;
            }
        }
        else {  /* ==> ! isMatch() !!! */
            setInfo(INIT_MATCH_INFO);
            setInitialisator(initialisator);
        }        
    }
    protected void setInitialisator(IInitialisator<?> initialisator) {
        this.initialisator = initialisator;
    }
    protected void clearInitialisator() {
        setInitialisator(null);
    }
    
}