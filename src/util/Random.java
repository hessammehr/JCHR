package util;

public class Random extends java.util.Random {
    private static final long serialVersionUID = 1L;
    
    public Random() {
        super();
    }

    public Random(long seed) {
        super(seed);
    }

    /**
     * Returns a pseudorandom, uniformly distributed int value 
     * between the specified values <code>from</code> (inclusive) 
     * and <code>to</code> (exclusive), drawn from this random 
     * number generator's sequence. The general contract of 
     * <code>nextInt</code> is that one <code>int</code> value in 
     * the specified range is pseudorandomly generated and returned. 
     * All <code>to-from</code> possible <code>int</code> values are 
     * produced with (approximately) equal probability. 
     * The method <code>nextInt(int from, int to)</code> 
     * is implemented by class <code>Random</code> as follows: 
     * <pre>
     * return from + nextInt(to-from);
     * </pre>
     * Note that this method also works for negative numbers. 
     * Some examples to make this clear:
     * <pre>
     *  (-5) + nextInt((-2)-(-5)) --> (-5) + nextInt(3)
     *  (-2) + nextInt(2-(-2))    --> (-2) + nextInt(4)
     * </pre>
     * We refer to {@link java.util.Random#nextInt(int)} more details.
     * 
     * @param from
     *  The lower bound on the random number to be returned (<b>inclusive</b>)
     * @param to
     *  The upper bound on the random number to be returned (<b>exclusive</b>)
     *  
     * @return A pseudorandom, uniformly distributed int value 
     *  between the specified values <code>from</code> (inclusive) 
     *  and <code>to</code> (exclusive).
     * 
     * @throws IllegalArgumentException
     *  If <code>to</code> is not larger then <code>from</code>.
     */
    public int nextInt(int from, int to) {
        try {
            return from + nextInt(to-from);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("to must be larger then from");
        }
    }
    
    /**
     * Returns a pseudorandom, uniformly distributed int value between 
     * 0 (inclusive) and the specified value (<b>inclusive</b>), 
     * drawn from this random number generator's sequence. 
     * The general contract of <code>nextInt</code> is that 
     * one <code>int</code> value in the specified range is 
     * pseudorandomly generated and returned. 
     * All <code>n+1</code> possible <code>int</code> values are 
     * produced with (approximately) equal probability.
     * The method <code>nextInt(int from, int to)</code> 
     * is implemented by class <code>Random</code> as follows: 
     * <pre>
     * return nextInt(n+1);
     * </pre>
     * We refer to {@link java.util.Random#nextInt(int)} for
     * more details.
     * 
     * @param n
     *  The bound on the random number to be returned (<b>inclusive</b>).
     * 
     * @return A pseudorandom, uniformly distributed int value between 
     *  0 (inclusive) and the specified value (<b>inclusive</b>), 
     *  drawn from this random number generator's sequence.
     *  
     * @throws IllegalArgumentException
     *  If <code>n</code> is negative (<code>n < 0</code>).
     */
    public int nextIntInclusive(int n) throws IllegalArgumentException {
        return nextInt(n+1);
    }
    
    /**
     * Returns a pseudorandom, uniformly distributed int value 
     * between the specified values <code>from</code> (inclusive) 
     * and <code>to</code> (<b>inclusive</b>), drawn from this random 
     * number generator's sequence. The general contract of 
     * <code>nextInt</code> is that one <code>int</code> value in 
     * the specified range is pseudorandomly generated and returned. 
     * All <code>to-from+1</code> possible <code>int</code> values are 
     * produced with (approximately) equal probability. 
     * The method <code>nextInt(int from, int to)</code> 
     * is implemented by class <code>Random</code> as follows: 
     * <pre>
     * return from + nextInt(to-from+1);
     * </pre>
     * Note that this method also works for negative numbers. Some
     * examples make this clear:
     * <pre>
     *  (-5) + nextInt((-2)-(-5)+1) --> (-5) + nextInt(4)
     *  (-2) + nextInt(2-(-2)+1)    --> (-2) + nextInt(5)
     * </pre> 
     * We refer to {@link java.util.Random#nextInt(int)} for
     * more details.
     * 
     * @param from
     *  The lower bound on the random number to be returned (<b>inclusive</b>)
     * @param to
     *  The upper bound on the random number to be returned (<b>inclusive</b>)
     *  
     * @return A pseudorandom, uniformly distributed int value 
     *  between the specified values <code>from</code> (inclusive) 
     *  and <code>to</code> (exclusive).
     * 
     * @throws IllegalArgumentException
     *  If <code>to</code> is not larger then or equal to 
     *  <code>from</code>.
     */
    public int nextIntInclusive(int from, int to) {
        try {
            return from + nextInt(to-from+1);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("to must be larger then from");
        }
    }
}
