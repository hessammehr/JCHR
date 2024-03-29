package util.builder;

/**
 * Iedere buildermethode kan per definitie een BuilderException
 * gooien. Dit is een generische exceptie: het is duidelijk niet
 * geweten welke uitzonderingen er in concrete builders kunnen
 * optreden.
 * 
 * @author Peter Van Weert
 */
public interface IBuilder<Result> extends IVoidBuilder {

    /**
     * Geeft het resultaat van het constructieproces.
     * Deze methode hoeft niet ge�mplementeerd te zijn...
     * 
     * @return Het resultaat van het constructieproces.
     * 
     * @exception UnsupportedOperationException
     * 	Deze operatie hoeft niet ge�mplementeerd te zijn...
     */
    public Result getResult() 
    throws UnsupportedOperationException, BuilderException;

}