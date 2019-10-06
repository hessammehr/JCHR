package compiler.CHRIntermediateForm.builder;

import compiler.CHRIntermediateForm.rulez.RuleType;

import util.builder.BuilderException;
import util.builder.IBuilder;


/**
 * Mogelijke producten:
 * 		- CHRIntermediateForm
 * 		- normaal bestand
 * 		- XML bestand (uiteraard bestaat er een XSLT voor ;-) )
 * 		- (semantische) verificatie
 * 		- echo
 * Mogelijke directors:
 * 		- manueel (vanuit java-code)
 * 			OPM: hierbij mogelijk maken dat bijvoorbeeld
 * 				een regel van de parser wordt gebruikt om
 * 				enkel een regel parsen? Is mogelijk!!
 * 		- normaal bestand
 * 		- XML bestand
 * 
 * @author Peter Van Weert
 */
public interface ICHRIntermediateFormBuilder<Result> extends IBuilder<Result> {
    public void buildPackageDeclaration(String packageName) throws BuilderException;
    
    public void beginImports() throws BuilderException;
    
    	public void importSingleType(String id) throws BuilderException;
        
        public void importOnDemand(String id) throws BuilderException;
        
        public void importSingleStatic(String id) throws BuilderException;
        
        public void importStaticOnDemand(String id) throws BuilderException;
    	
    public void endImports() throws BuilderException;
    
    public void beginHandler(String name, String accessModifier) throws BuilderException;
    
    	public void beginTypeParameters() throws BuilderException;
    	
    		public void beginTypeParameter(String name) throws BuilderException;
    			
    			public void beginUpperBound(String name) throws BuilderException;
    			
    			public void endUpperBound() throws BuilderException;
    		
    		public void endTypeParameter() throws BuilderException;
    	
    	public void endTypeParameters() throws BuilderException;
	    
	    public void beginDeclarations() throws BuilderException;
	    
    	    public void beginSolverDeclaration(String solverInterface, String accessModifier) 
            throws BuilderException;
            
                public void beginSolverDefault() throws BuilderException;
                
                    // argument
                
                public void endSolverDefault() throws BuilderException;
    	    	
    	    	public void beginTypeArguments() throws BuilderException; 
    	    
    	    		public void beginTypeArgument(String type) throws BuilderException;
    	    		
    	    		public void endTypeArgument() throws BuilderException;
    	    		
    	    	public void endTypeArguments() throws BuilderException;
    	    	
    	    	public void buildSolverName(String name) throws BuilderException;
    	    	
    	    public void endSolverDeclaration() throws BuilderException;

            
	    	public void beginConstraintDeclaration(String id, String accessModifier) throws BuilderException;
	    	
                public void buildInfixIdentifier(String infix) throws BuilderException;
            
	    		public void beginConstraintArgument() throws BuilderException;
                
                    public void beginConstraintArgumentType(String type, boolean fixed) throws BuilderException;

                        /* typeParameters */
                    
                    public void endConstraintArgumentType() throws BuilderException;
                    
	    			public void buildConstraintArgumentName(String name) throws BuilderException;
	    		
	    		public void endConstraintArgument() throws BuilderException;
	    	
	    	public void endConstraintDeclaration();
	    
	    public void endDeclarations() throws BuilderException;
	    
	    public void beginRules() throws BuilderException;
	    
	    	public void beginLocalVariableDeclarations() throws BuilderException;	    	
            
                public void beginVariableType(String type, boolean fixed) throws BuilderException;
	    		    
                    /* typeParameters */
                
                public void endVariableType() throws BuilderException;
	    
	    		public void declareLocalVariable(String id) throws BuilderException;
	    	
	    	public void endLocalVariableDeclarations() throws BuilderException;
            
            
            // XXX variable declarations are just a temporary fix (cf manual)!!
            public void beginVariableDeclarations() throws BuilderException;
            
                /* variableType */
            
                public void declareVariable(String id) throws BuilderException;
    
            public void endVariableDeclarations() throws BuilderException;
            

            public void beginRule() throws BuilderException;
            
    	    	public void beginRuleDefinition(RuleType type) throws BuilderException;
    	    	
    	    	public void beginRuleDefinition(String id, RuleType type) throws BuilderException ;
    	    		// begin van een strategie...
    	    		public void beginPositiveHead() throws BuilderException;
    	    			public void beginKeptOccurrences() throws BuilderException;
    	    			public void beginRemovedOccurrences() throws BuilderException;
    	    				
    	    					// etc etc
                        
                            public void setPassive() throws BuilderException;
                            public void buildOccurrenceId(String id) throws BuilderException;
        			
        				public void endKeptOccurrences() throws BuilderException;
        		    	public void endRemovedOccurrences() throws BuilderException;
    		    	public void endPositiveHead() throws BuilderException;
                    
                    public void beginNegativeHeads() throws BuilderException;
                    
                        public void beginNegativeHead() throws BuilderException;
                            public void beginNegativeOccurrences() throws BuilderException;                        
                                // etc
                            public void endNegativeOccurrences() throws BuilderException;
                            
                            public void beginNegativeGuard() throws BuilderException;
                                // etc
                            public void endNegativeGuard() throws BuilderException;
                        
                        public void endNegativeHead() throws BuilderException;
                        
                    public void endNegativeHeads() throws BuilderException;
    
    	    		public void beginGuard() throws BuilderException;
    	    		public void beginBody() throws BuilderException;
                    
                        public void addFieldAccessConjunct(String id) throws BuilderException;
                        
                        public void addSimpleIdConjunct(String id) throws BuilderException;
                        public void addFlagConjunct(String id) throws BuilderException;
                        public void addVariableConjunct(String id) throws BuilderException;
                        
                        public void addFailureConjunct() throws BuilderException;
                        public void addFailureConjunct(String message) throws BuilderException;
    	    			
    	    			public void beginArgumentedConjunct(String id) throws BuilderException;
    
                        public void beginComposedIdConjunct(String id) throws BuilderException;	    		
    		    		public void beginMarkedBuiltInConstraint(String id) throws BuilderException;
    		    		public void beginMethodInvocationConjunct(String id) throws BuilderException;
                        public void beginConstructorInvocationConjunct(String id) throws BuilderException;
    
                        public void beginSimpleIdConjunct(String id) throws BuilderException;
                        public void beginBuiltInConstraint(String id) throws BuilderException;
                        public void beginUserDefinedConstraint(String id) throws BuilderException;
                        
    		    			public void beginArguments() throws BuilderException;
    		    			
    		    				public void addIdentifiedArgument(String id) throws BuilderException;
    		    				
    		    				public void addPrimitiveArgument(boolean value) throws BuilderException;
    		    				public void addPrimitiveArgument(byte value) throws BuilderException;
    		    				public void addPrimitiveArgument(short value) throws BuilderException;
    		    				public void addPrimitiveArgument(int value) throws BuilderException;
    		    				public void addPrimitiveArgument(char value) throws BuilderException;
    		    				public void addPrimitiveArgument(long value) throws BuilderException;
    		    				public void addPrimitiveArgument(float value) throws BuilderException;
    		    				public void addPrimitiveArgument(double value) throws BuilderException;
    		    				public void addStringLiteralArgument(String value) throws BuilderException;
                                public void addCharLiteralArgument(String value) throws BuilderException;
                                
    		    				public void addNullArgument() throws BuilderException;
    		    				
                                public void addFieldAccessArgument(String id) throws BuilderException; 
                                
    		    				public void beginMethodInvocationArgument(String id) throws BuilderException;
                                public void beginConstructorInvocationArgument(String id) throws BuilderException;
    		    				
    		    					// beginArguments etc etc
    		    				
    		    				public void endMethodInvocationArgument() throws BuilderException;
                                public void endConstructorInvocationArgument() throws BuilderException;
    		    			
    		    			public void endArguments() throws BuilderException;
    		    		
    		    			public void endArgumentedConjunct() throws BuilderException;
    		    			
    		    			public void endComposedIdConjunct() throws BuilderException;	    		
    			    		public void endMarkedBuiltInConstraint() throws BuilderException;
    			    		public void endMethodInvocationConjunct() throws BuilderException;
                            public void endConstructorInvocationConjunct() throws BuilderException;
    			    		
    			    		public void endSimpleIdConjunct() throws BuilderException;
    			    		public void endBuiltInConstraint() throws BuilderException;
    			    		public void endUserDefinedConstraint() throws BuilderException;
                            
                            public void beginInfixConstraint() throws BuilderException;
                                
                                // add argument 1
                            
                                public void buildInfix(String infix) throws BuilderException;
                                public void buildMarkedInfix(String infix) throws BuilderException;
                                public void buildBuiltInInfix(String infix) throws BuilderException;
                                public void buildUserDefinedInfix(String infix) throws BuilderException;
                            
                                // add argument 2
                                
                            public void endInfixConstraint() throws BuilderException;
                            
                            public void beginDeclarationConjunct() throws BuilderException;
                            	public void buildDeclaredVariable(String id) throws BuilderException;
                            public void endDeclarationConjunct() throws BuilderException;
                            
    		    	public void endGuard() throws BuilderException;
    		    	public void endBody() throws BuilderException;
	    		
    		    public void endRuleDefinition() throws BuilderException;
                
                public void beginPragmas() throws BuilderException;
                
                    public void addPassivePragma(String id) throws BuilderException;
                    
                    public void addNoHistoryPragma() throws BuilderException;
                    
                    public void addDebugPragma() throws BuilderException;
                
                public void endPragmas() throws BuilderException;
                    
		    public void endRule() throws BuilderException;
	    
	    public void endRules() throws BuilderException;
    
    public void endHandler() throws BuilderException;
}