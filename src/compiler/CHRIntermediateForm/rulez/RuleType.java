package compiler.CHRIntermediateForm.rulez;

import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;

/*
 * version 1.4.0    Peter Van Weert
 *  Added the newInstance method
 * version 1.1.1    Peter Van Weert
 *  Turned it into an enumeration type  
 */

/**
 * @author Peter Van Weert
 */
public enum RuleType {
    PROPAGATION {
        @Override
        public Rule newInstance(String id, int nbr) throws IllegalIdentifierException  {
            return new PropagationRule(id, nbr);
        }
        
        @Override
        public String getOperator() {
            return "==>";
        }
    },
    SIMPLIFICATION {
        @Override
        public Rule newInstance(String id, int nbr) throws IllegalIdentifierException {
            return new SimplificationRule(id, nbr);
        }
        
        @Override
        public String getOperator() {
            return "<=>";
        }
    },
    SIMPAGATION {
        @Override
        public Rule newInstance(String id, int nbr) throws IllegalIdentifierException {
            return new SimpagationRule(id, nbr);
        }
        
        @Override
        public String getOperator() {
            return "<=>";
        }
    };
    
    public abstract Rule newInstance(String id, int nbr) throws IllegalIdentifierException ;
    
    public abstract String getOperator();
}