package compiler.analysis.joinordering;

import util.iterator.FilteredIterable;

import compiler.CHRIntermediateForm.constraints.ud.schedule.ISelector;

/**
 * A simple helper class: iterables that filter out all trivial selectors.
 * 
 * @author Peter Van Weert
 */
public class FilteredSelectors<T extends ISelector> extends FilteredIterable<T> {
    
    public final static Filter<ISelector> ONLY_NON_TRIVIAL = 
        new Filter<ISelector>() {
            @Override
            public boolean exclude(ISelector selector) {
                return selector.succeeds();
            }
        };
    
    public FilteredSelectors(Iterable<T> selectors) {
        super(selectors, ONLY_NON_TRIVIAL);
    }
}
