package compiler.CHRIntermediateForm.matching;

import static util.comparing.Comparison.AMBIGUOUS;
import static util.comparing.Comparison.BETTER;
import static util.comparing.Comparison.EQUAL;
import static util.comparing.Comparison.WORSE;

import java.util.Arrays;

import util.comparing.Comparison;


/**
 * @author Peter Van Weert
 */
public class MatchingInfos extends AbstractMatchingInfo<MatchingInfos> {

    private MatchingInfo[] assignmentInfos;
    
    private boolean ignoreImplicitArgument;
    
    public final static MatchingInfos 
    	NO_MATCH = new MatchingInfos() {
            @Override
	        protected byte getInfo() {
	            return NO_MATCH_INFO;
	        }

            @Override
	        public boolean isAmbiguous() {
	            return false;
	        }

            @Override
	        public boolean isCoercedMatch() {
	            return false;
	        }

            @Override
	        public boolean isDirectMatch() {
	            return false;
	        }

            @Override
	        public boolean isMatch() {
	            return false;
	        }

            @Override
            public boolean isInitMatch() {
                return false;
            }

            @Override
            public boolean isNonAmbiguousMatch() {
                return false;
            }

            @Override
            public boolean isNonDirectMatch() {
                return false;
            }

            @Override
            public String toString() {
                return "NO MATCH";
            }

            @Override
            public Comparison compareWith(MatchingInfos other) {
                return other.isMatch()? WORSE : EQUAL;
            }
	    }, 
        EXACT_MATCH = new MatchingInfos() {
            @Override
            public MatchingInfo getAssignmentInfoAt(int index) {
                return MatchingInfo.EXACT_MATCH;
            }
            
            @Override
            protected byte getInfo() {
                return EXACT_MATCH_INFO;
            }

            @Override
            public boolean isAmbiguous() {
                return false;
            }

            @Override
            public boolean isCoercedMatch() {
                return false;
            }

            @Override
            public boolean isDirectMatch() {
                return true;
            }

            @Override
            public boolean isMatch() {
                return true;
            }

            @Override
            public boolean isInitMatch() {
                return false;
            }

            @Override
            public boolean isNonAmbiguousMatch() {
                return true;
            }

            @Override
            public boolean isNonDirectMatch() {
                return false;
            }

            @Override
            public String toString() {
                return "EXACT MATCH";
            }

            @Override
            public Comparison compareWith(MatchingInfos other) {
                return other.isExactMatch()? EQUAL : BETTER;
            }
        },
        DIRECT_MATCH = new MatchingInfos() {
            @Override
            public MatchingInfo getAssignmentInfoAt(int index) {
                return MatchingInfo.DIRECT_MATCH;
            }
            
            @Override
            protected byte getInfo() {
                return DIRECT_MATCH_INFO;
            }

            @Override
            public boolean isAmbiguous() {
                return false;
            }

            @Override
            public boolean isCoercedMatch() {
                return false;
            }

            @Override
            public boolean isDirectMatch() {
                return true;
            }

            @Override
            public boolean isMatch() {
                return true;
            }

            @Override
            public boolean isInitMatch() {
                return false;
            }

            @Override
            public boolean isNonAmbiguousMatch() {
                return true;
            }

            @Override
            public boolean isNonDirectMatch() {
                return false;
            }

            @Override
            public String toString() {
                return "DIRECT MATCH";
            }

            @Override
            public Comparison compareWith(MatchingInfos other) {
                return other.isDirectMatch()? EQUAL : BETTER;
            }
        },
	    AMBIGUOUS_NO_INIT = new MatchingInfos() {
	        @Override
            protected byte getInfo() {
	            return AMBIGUOUS_INFO;
	        }

            @Override
	        public boolean isAmbiguous() {
	            return true;
	        }

            @Override
	        public boolean isCoercedMatch() {
	            return true;
	        }

            @Override
	        public boolean isDirectMatch() {
	            return false;
	        }
            
            @Override
	        public boolean isMatch() {
	            return true;
	        }
            
            @Override
            public boolean isInitMatch() {
                return false;
            }

            @Override
            public boolean isNonAmbiguousMatch() {
                return false;
            }

            @Override
            public boolean isNonDirectMatch() {
                return true;
            }

            @Override
            public String toString() {
                return "AMBIGUOUS MATCH";
            }
            
            @Override
            public Comparison compareWith(MatchingInfos other) {
                if (other.isInitMatch()) return BETTER;
                else if (other.isCoercedMatch()) return AMBIGUOUS;
                else if (other.isDirectMatch()) return WORSE;
                else return BETTER;
            }
	    },
        AMBIGUOUS_INIT = new MatchingInfos() {
            @Override
            protected byte getInfo() {
                return AMBIGUOUS_INFO;
            }

            @Override
            public boolean isAmbiguous() {
                return true;
            }

            @Override
            public boolean isCoercedMatch() {
                return true;    /* hoeft niet echt, maar all? */
            }

            @Override
            public boolean isDirectMatch() {
                return false;
            }
            
            @Override
            public boolean isMatch() {
                return true;
            }
            
            @Override
            public boolean isInitMatch() {
                return true;
            }

            @Override
            public boolean isNonAmbiguousMatch() {
                return false;
            }

            @Override
            public boolean isNonDirectMatch() {
                return true;
            }

            @Override
            public String toString() {
                return "AMBIGUOUS MATCH";
            }
            
            @Override
            public Comparison compareWith(MatchingInfos other) {
                if (other.isInitMatch()) return AMBIGUOUS;
                else if (other.isCoercedMatch() || other.isDirectMatch()) return WORSE;
                else return BETTER;
            }
        };
    
    protected MatchingInfos() {
        // NOP (just declaring this has to be a protected constructor)
    }
        
    public MatchingInfos(int arity, boolean ignoreImplicitArgument) {
        this(ignoreImplicitArgument, new MatchingInfo[arity]);
    }
    
    public MatchingInfos(boolean ignoreImplicitArgument, CoerceMethod... coercions) {
        MatchingInfo[] infos = new MatchingInfo[coercions.length];
        for (int i = 0; i < infos.length; i++)
            infos[i] = new MatchingInfo(coercions[i]);
        setAssignmentInfos(infos);
        setIgnoreImplicitArgument(ignoreImplicitArgument);
    }
    
    public MatchingInfos(boolean ignoreImplicitArgument, MatchingInfo... assignmentInfos) {
        setAssignmentInfos(assignmentInfos);
        setIgnoreImplicitArgument(ignoreImplicitArgument);
    }
    
    public MatchingInfo[] getAssignmentInfos() {
        return assignmentInfos;
    }
    public int getArity() {
        return getAssignmentInfos().length;        
    }
    protected void setAssignmentInfos(MatchingInfo[] assignmentInfos) {
        this.assignmentInfos = assignmentInfos;
    }
    
    public MatchingInfo getAssignmentInfoAt(int index) {
        return getAssignmentInfos()[index];
    }
    public void setAssignmentInfoAt(MatchingInfo info, int index) {
        getAssignmentInfos()[index] = info;
    }
    
    // TODO re-implement this class: the original idea was nice, 
    //  but this is getting ridiculous!!!
    @Override
    protected byte getInfo() {
        byte direct = DIRECT_MATCH_INFO;
        byte exact = EXACT_MATCH_INFO;
        byte result = 0;
        
        int i = -1;
        while (++i < assignmentInfos.length && assignmentInfos[i] != null) {
            byte info = assignmentInfos[i].getInfo(); 
            result |= info;
            direct &= ( info | info >> 1 ); // or exact!!
            exact &= info;
        }

        // all, but also at least one
        direct &= result;   
        exact &= result;
        
        if (exact == 0) 
            result &= ~EXACT_MATCH_INFO; 
        if (direct == 0)
            result &= ~DIRECT_MATCH_INFO; 

//        printInfo(result);
        
        return result;
    }
    
//    private void printInfo(byte b) {
//        int i = 0;
//        while (i < assignmentInfos.length && assignmentInfos[i] != null)
//            print(assignmentInfos[i++].getInfo());
//        System.out.println("--------");
//        print(b);
//        System.out.println();
//    }
//    private static void print(byte b) {
//        StringBuilder result = new StringBuilder(8);
//        for (int i = 6; i >= 0; i--)
//            result.append(((b & (1 << i)) == 0)? 0:1 );
//        System.out.println(result);
//    }
    
    public Comparison compareWith(MatchingInfos other) {
        if ((other == NO_MATCH) || (other == AMBIGUOUS_INIT) || (other == AMBIGUOUS_NO_INIT))
            return Comparison.flip(other.compareWith(this));
        
        Comparison comparison = 
            Comparison.get(this.getMatchClass() - other.getMatchClass());
        
        if (comparison == EQUAL && isNonDirectMatch()) {
            // als beiden coerces zijn, en minstens 1 ervan ambigu, 
            // is vergelijking niet mogelijk 
            if (this.isAmbiguous() || other.isAmbiguous())
                return AMBIGUOUS;
            
            final int arity = this.getArity();
            MatchingInfo m1, m2;
            Comparison temp;
            boolean way = false, bitbetter = false, bitworse = false;
            
            for (int i = this.getStartIndex(), j = other.getStartIndex(); i < arity; i++, j++) {
                temp = (m1 = this.getAssignmentInfoAt(i)).compareWith(m2 = other.getAssignmentInfoAt(j));
                switch (temp) {
                	// als er minstens 1 vergelijking ambigu is, is vergelijking niet mogelijk
                 	case AMBIGUOUS:
                 	    return AMBIGUOUS;
             	    /*break;*/
                 	    
             	    // als er een verschil is moet...
             	    case BETTER:
             	        if (m1.isExactMatch() && m2.isDirectMatch()) 
                            bitbetter = true;
         	        case WORSE:
                        if (temp == WORSE && m1.isDirectMatch() && m2.isExactMatch()) 
                            bitworse = true;
                        
         	            if (comparison == temp) way = true;
                        
         	            if (comparison == EQUAL) // ... na de eerste keer ...
         	                comparison = temp;
         	            else if (comparison != temp) { // ... de vergelijking steeds hetzelfde zijn
                           if (way) return AMBIGUOUS;
                           
                           // only one way exception: if not yet way better or way worse,
                           // this can still be reversed:
                           if (bitbetter) comparison = BETTER;
                           if (bitworse) comparison = WORSE;
                           way = true;  // after this, all is way beyound saving!
                        }
                }
            }
        }
        
        return comparison;
    }
    
    @Override
    public String toString() {
        return Arrays.deepToString(getAssignmentInfos()) + " (" + getInfo() + ")";
    }

    public boolean haveToIgnoreImplicitArgument() {
        return ignoreImplicitArgument;
    }
    protected int getStartIndex() {
        return haveToIgnoreImplicitArgument()? 1 : 0;
    }
    protected void setIgnoreImplicitArgument(boolean ignoreImplicitArgument) {
        this.ignoreImplicitArgument = ignoreImplicitArgument;
    }
    
    public static MatchingInfos fromBoolean(boolean bool) {
        return bool? DIRECT_MATCH : NO_MATCH;
    }
}
