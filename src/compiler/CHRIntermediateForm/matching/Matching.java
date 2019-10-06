package compiler.CHRIntermediateForm.matching;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import util.comparing.Comparison;

import compiler.CHRIntermediateForm.arg.argumentable.IArgumentable;
import compiler.CHRIntermediateForm.arg.argumented.IArgumented;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.exceptions.AmbiguousArgumentsException;
import compiler.CHRIntermediateForm.exceptions.IllegalArgumentsException;

public abstract class Matching {

    private Matching() {/* not instantiatable */}

    public static <T extends IArgumentable<?>> IArgumented<?> 
    getBestMatch(Set<T> argumentables, IArguments arguments)
        throws AmbiguousArgumentsException, IllegalArgumentsException {
    	
    	int n = argumentables.size();
    	List<T> list = new ArrayList<T>(n);
    	list.addAll(argumentables);
    	List<MatchingInfos> matchings = new ArrayList<MatchingInfos>(n);
    	for (int i = 0; i < n; i++) {
    		MatchingInfos matching = list.get(i).canHaveAsArguments(arguments);
    		if (matching.isMatch())
    			matchings.add(matching);
    		else {
    			list.remove(i--);
    			n--;
    		}
    	}
    	
    	outer: for (int i = 0; i < n; i++) {
    		inner: for (int j = 0; j < n; j++) {
    			if (i == j) continue;
    			Comparison c = matchings.get(i).compareWith(matchings.get(j));
    			boolean retry = false;
    			compare: while (true) switch (c) {
    				case BETTER:
    					list.remove(j);
    					matchings.remove(j);
    					n--;
    					j--;
    				continue inner;
    				
    				case WORSE:
    					list.remove(i);
    					matchings.remove(i);
    					n--;
    					i--;
    				continue outer;
    				
    				case EQUAL:
    					if (!retry && matchings.get(i).isNonAmbiguousMatch() 
    							&& matchings.get(j).isNonAmbiguousMatch()) {
							retry = true;
    						c = list.get(i).compareWith(list.get(j));
    						continue compare;
    					}
					
    				case AMBIGUOUS: continue inner;
    			}
    		}
    	}
    	
    	for (int i = 0; i < n; i++)
    		if (matchings.get(i).isAmbiguous())
    			throw new AmbiguousArgumentsException(argumentables, arguments);
    	
    	switch (n) {
    		case 0:
    			throw new IllegalArgumentsException(argumentables, arguments); 
    		case 1:
    			return list.get(0).createInstance(matchings.get(0), arguments);
			default:
				throw new AmbiguousArgumentsException(argumentables, arguments);
    	}
    }
    
//    public static <T extends IArgumentable<?>> IArgumented<?> 
//        getBestMatch(Set<T> argumentables, IArguments arguments)
//            throws AmbiguousArgumentsException, IllegalArgumentsException {
//        
//        MatchingInfos matchingInfos, bestInfos = MatchingInfos.NO_MATCH;
//        T best = null;
//        boolean ambiguous = false;
//
//        for (T argumentable : argumentables) {
//            matchingInfos = argumentable.canHaveAsArguments(arguments);
//
//            switch (matchingInfos.compareWith(bestInfos)) {
//                case BETTER:
//                    best = argumentable;
//                    bestInfos = matchingInfos;
//                    // the last one is unambiguously better then the previous one?
//                    // TODO: not true: has to be compared to all "best" ones?
//                    ambiguous = false;
//                break;
//
//                case AMBIGUOUS:
//                    ambiguous = true;
//                break;
//
//                case EQUAL:
//                    if (bestInfos.isNonAmbiguousMatch())
//                        switch (argumentable.compareWith(best)) {
//                        case BETTER:
//                            best = argumentable;
//                            bestInfos = matchingInfos;
//                            break;
//    
//                        case AMBIGUOUS:
//                        case EQUAL:
//                            if (bestInfos.isDirectMatch())
//                                throw new AmbiguousArgumentsException(argumentables, arguments);
//                            else
//                                ambiguous = true;
//                            break;
//                        }
//                break;
//            }
//        }
//
//        if ((ambiguous && !bestInfos.isDirectMatch()) || bestInfos.isAmbiguous())
//            throw new AmbiguousArgumentsException(argumentables, arguments);
//        if (best == null || !bestInfos.isNonAmbiguousMatch())
//            throw new IllegalArgumentsException(argumentables, arguments);
//
//        IArgumented<?> result = best.createInstance(bestInfos, arguments);
//        return result;
//    }

}
