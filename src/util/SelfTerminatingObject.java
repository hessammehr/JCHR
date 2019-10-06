package util;

/**
 * An <code>SelfTerminatingObject</code> is an object that terminates itself
 * (if needed) if the garbage collector removes it from the heap. I have
 * never used this class, but I like the idea (of course it is not at all
 * efficient, but it could help with building robust software).
 *  
 * @author Peter Van Weert
 */
public abstract class SelfTerminatingObject implements Terminatable {

    @Override
    protected void finalize() throws Throwable {
        if (! isTerminated()) terminate();
    }
}
