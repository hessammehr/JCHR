package compiler.codeGeneration;

import static compiler.CHRIntermediateForm.constraints.ud.Occurrence.getOnlyPartner;
import static compiler.codeGeneration.ConstraintCodeGenerator.getOccurrenceName;
import static compiler.codeGeneration.TupleCodeGenerator.getTupleFQN;

import java.util.Iterator;
import java.util.Set;

import runtime.history.IdentifierPropagationHistory;
import runtime.history.TuplePropagationHistory;
import util.iterator.Filtered.Filter;

import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.rulez.Head;
import compiler.CHRIntermediateForm.rulez.Rule;

public class HistoryCodeGenerator extends JavaCodeGenerator {

    private Set<Integer> usedTupleArities;
    
    private boolean insertInLastTest = true;
    
    public HistoryCodeGenerator(ConstraintCodeGenerator codeGenerator) {
        this(codeGenerator, codeGenerator.getUsedTupleArities());
    }
    public HistoryCodeGenerator(
        CodeGenerator codeGenerator,
        Set<Integer> usedTupleArities
    ) {
        super(codeGenerator);
        setUsedTupleArities(usedTupleArities);
    }
    
    public void setUsedTupleArities(Set<Integer> usedTupleArities) {
        this.usedTupleArities = usedTupleArities;
    }
    protected void addUsedTupleArity(int arity) {
        getUsedTupleArities().add(arity);
    }
    public Set<Integer> getUsedTupleArities() {
        return usedTupleArities;
    }
    
    @Override
    protected void doGenerate() throws GenerationException {
        throw new GenerationException("unsupported operation");
    }
    
    public void generateMembers(UserDefinedConstraint constraint, Iterable<Rule> rules) throws GenerationException {
    	if (needsSeparateHistoryId(constraint)) {
    		nl();
    		tprintln("protected final int historyId = IDcounter++;");
    	}
    	for (Rule rule : rules) {
	        if (needsHistoryMembers(constraint, rule)) {
	            nl();
	            
	            String name = getHistoryName(rule);
	            int n = rule.getPositiveHead().getNbOccurrences();
	
	            if (n == 1) {
	                tprint("protected boolean "); print(name); println(";");
	            } else {
	                String type = (n == 2)
	                	? IdentifierPropagationHistory.class.getCanonicalName()
	                    : TuplePropagationHistory.class.getCanonicalName();
	
	                tprint("protected "); print(type); print(' '); print(name);
	                    print(" = new "); print(type); println("();");
	            }
	        }
    	}
    }
    
    public void generateTerminationCode(UserDefinedConstraint constraint, Rule rule) throws GenerationException {
    	if (needsHistoryMembers(constraint, rule) && rule.getPositiveHead().getNbOccurrences() > 1) {
			tprint(getHistoryName(rule)); println(" = null;");
    	}
    }
    
    protected static boolean needsHistoryMembers(UserDefinedConstraint constraint, Rule rule) {
    	if (rule.needsHistory() && !simpleHistorySuffices(rule)) 
    		for (Occurrence occurrence : rule.getPositiveHead())
    			if (occurrence.isActive() && occurrence.getConstraint().equals(constraint))
    				return true;
        return false;
    }
    
    protected void printDotId(Occurrence occurrence, boolean sameAsActive) throws GenerationException {
    	print(needsSeparateHistoryId(occurrence.getConstraint())
    				? ".historyId"
					: sameAsActive? ".ID" : ".getConstraintId()"
		);
    }
    protected void printThisDotId(Occurrence active) throws GenerationException {
    	print("this"); printDotId(active, true);
    }
    protected void printOccurrenceDotId(Occurrence active, Occurrence occurrence) throws GenerationException {
    	print(getOccurrenceName(occurrence));
    	printDotId(occurrence, occurrence.getConstraint() == active.getConstraint());
    }
    
    protected final static boolean oneConstraintCanMatchBoth(Rule rule) {
    	Head head = rule.getPositiveHead();
    	assert head.getNbOccurrences() == 2;
    	Occurrence one = head.getOccurrenceAt(0),
    			 other = head.getOccurrenceAt(1);
    	return one.isActive()
    		&& other.isActive()
    		&& one.getConstraint().equals(other.getConstraint());
    }
    
    public boolean generateNotInHistoryTest(Occurrence active) throws GenerationException {
        if (! hasToDoHistoryTest(active)) return false;
        String name = getHistoryName(active.getRule());
        
        if (simpleHistorySuffices(active)) {
        	tprint("if (!stored || (");
        	boolean first = true;
        	for (Occurrence occurrence : active.getPartners()) {
        		if (first) first = false;
        		else print(" && ");
        		
        		printThisDotId(active);
        		print(" > ");
        		printOccurrenceDotId(active, occurrence);
        	}
        	print("))");
        } else switch (active.getHead().getNbOccurrences()) {
            case 1:
               tprint("if (!"); print(name); print(')');
            break;
            
            case 2:
                Occurrence partner = getOnlyPartner(active);

                boolean negate = false;
                
                tprint("if (!stored || !(");
                if (! partner.isPassive()) {
                    print(getOccurrenceName(partner)); print('.'); print(name);
                    print(".contains(");
                    	if (oneConstraintCanMatchBoth(active.getRule())) {
                    		if (active.getOccurrenceIndex() == 1)
                    			print('-');
                			else
                				negate = true;
                    	}
                    	printThisDotId(active); 
                    println(')');
                    ttprint("|| ");
                }

                if (insertInLastTest()) print('!');
                print(name); 
                print(insertInLastTest()? ".insert(" : ".contains(");
                	if (negate) print('-'); 
                	printOccurrenceDotId(active, partner); 
            	print(')');
                
                print("))");
            break;
            
            default:
                Iterator<Occurrence> activePartners = active.getPartners(ACTIVE_PARTNERS).iterator();
                if (activePartners.hasNext()) {
                    printTupleDeclaration(active, false);
                    tprintln("if (!stored || !(");
                    tprintTabs();
                    
                    boolean first = true;
                    do {
                        print(getOccurrenceName(activePartners.next()));
                        	print('.'); print(name); print(".contains($$tuple");
                        	if (first) {
                        		first = false;
                        		print(" = "); printNewTuple(active, false);
                        	}
                        	println(')');
                    	ttprint("|| "); 
                    } while (activePartners.hasNext());

                    if (insertInLastTest()) print('!');
                    print(name); 
                    print(insertInLastTest()? ".insert" : ".contains"); println("($$tuple)");
                    tprint("))");
                    
                } else {    // no active partners
                    printTupleDeclaration(active, true);
                    tprint("if (!stored || ");
                    if (!insertInLastTest()) print('!');
                    print(name);
                    print(insertInLastTest()? ".insert" : ".contains"); 
                    print("($$tuple = "); printNewTuple(active, true); 
                    print("))");
                }
            break;
        }
        
        return true;
    }
    
    public final static Filter<Occurrence> 
        ACTIVE_PARTNERS = new Filter<Occurrence>() {
            @Override
            public boolean include(Occurrence occurrence) {
                return occurrence.isActive();
            }
        };
         
    
    protected void printTupleDeclaration(Occurrence active, boolean excludeActive) throws GenerationException {
        tprint(getTupleFQN(active.getHead().getNbOccurrences() - (excludeActive? 1 : 0))); 
        print(" $$tuple = null;");
    }
    
    protected void printNewTuple(Occurrence active, boolean excludeActive)
    throws GenerationException {
        Head head = active.getHead();
        final int nb = head.getNbOccurrences(), arity = nb - (excludeActive? 1 : 0);
        print("new "); print(getTupleFQN(arity)); print('(');
        
        for (int i = 0; i < nb; i++) {
            Occurrence partner = head.getOccurrenceAt(i);
            if (partner == active) {
                if (!excludeActive) {
                    if (i != 0) print(", ");
                    printThisDotId(active);
                }
            } else {
                if (i != 0) print(", ");
                printOccurrenceDotId(active, partner);
            }
        }
        
        print(')');
        
        addUsedTupleArity(arity);
    }
    
    public static boolean hasToDoHistoryTest(Occurrence occurrence) {
        return occurrence.getRule().needsHistory() 
        	&& (occurrence.checksHistoryOnActivate() || occurrence.isReactive());
    }
    
    public static boolean needsSeparateHistoryId(UserDefinedConstraint constraint) {
    	for (Occurrence occurrence : constraint.getPositiveOccurrences()) {
    		if (occurrence.isActive()
    				&& !occurrence.isStored()	// XXX if ALWAYS stored, store at beginning?
    											// XXX als niet nodig dat gesorteerd: niet moeite doen
    				&& occurrence.getRule().needsHistory())
				return true;
    	}
    	return false;
    }
    
    public static String getHistoryName(Rule rule) {
        return "$$" + rule.getIdentifier() + "_history";
    }
    
    public void generateAddToHistory(Occurrence active) throws GenerationException {
    	generateAddToHistory(active, false);
    }
    public void generateAddToHistoryOnStore(Occurrence active) throws GenerationException {
    	generateAddToHistory(active, true);
    }
    	
    protected static boolean simpleHistorySuffices(Occurrence active) {
    	return simpleHistorySuffices(active.getRule());
    }
    
    protected static boolean simpleHistorySuffices(Rule rule)  {
    	return false; // XXX will be released in version 1.6.1
//    	for (Occurrence occurrence : rule.getPositiveHead())
//    		if (occurrence.isReactive()) return false;
//    	for (NegativeHead head : rule.getNegativeHeads())
//    		if (head.isActive()) return false;
//    	return true;
    }
    
	protected void generateAddToHistory(Occurrence active, boolean onStore) throws GenerationException {
		if (!simpleHistorySuffices(active)) switch (active.getHead().getNbOccurrences()) {
            case 1:
            	tprint(getHistoryName(active.getRule()));
                println(" = true;");
            break;
            
            case 2:
            	// do not add again if inserted as last test
            	// do not add again if stored before outside "onStore" part
            	if (!(hasToDoHistoryTest(active) && insertInLastTest()) ^ !onStore) {
        			tprint(getHistoryName(active.getRule()));
        			print(".add(");
        			if (oneConstraintCanMatchBoth(active.getRule())
        					&& active.getOccurrenceIndex() == 0) print('-');
        			printOccurrenceDotId(active, getOnlyPartner(active));
        			println(");");
            	}
        	break;
            
            default:
        		if (hasToDoHistoryTest(active)) {
        			if (!insertInLastTest() || onStore) {
        				tprint(getHistoryName(active.getRule()));
        				print(".add(");
        				if (onStore)
        					printNewTuple(active, !active.hasPartners(ACTIVE_PARTNERS));
        				else
        					print("$$tuple");
        				println(");");
        			}
                } else {
                	tprint(getHistoryName(active.getRule()));
                    print(".add(");
                    printNewTuple(active, !active.hasPartners(ACTIVE_PARTNERS));
                    println(");");
                }
            break;
        }
    }
    
    public void setInsertInLastTest(boolean insertInLastTest) {
		this.insertInLastTest = insertInLastTest;
	}
    public boolean insertInLastTest() {
		return insertInLastTest;
	}
}