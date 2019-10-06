package compiler.CHRIntermediateForm.matching;

import util.comparing.Comparable;



/**
 * @author Peter Van Weert
 */
public interface IMatchingInfo<T extends IMatchingInfo<?>> extends Comparable<T> {

    public boolean isMatch();
    
    public boolean isNonAmbiguousMatch();
    
    public boolean isAmbiguous();
    
    public boolean isDirectMatch();
    
    public boolean isNonDirectMatch();
       
    public boolean isCoercedMatch();
    
    public boolean isInitMatch();
    
    public boolean isNonInitMatch();
}