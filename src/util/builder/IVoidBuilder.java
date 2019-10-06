package util.builder;

/**
 * A builder that does not really return a result. It will e.g.
 * write its results to a stream, a file, ...
 * 
 * @author Peter Van Weert
 */
public interface IVoidBuilder {

    /**
     * Hier moeten eventuele benodigde bronnen worden 
     * gereserveerd, niet bij constructie...
     */
    public void init() throws BuilderException;

    /**
     * Het is steeds mogelijk dat een director besluit het
     * bouwen te onderbreken.
     */
    public void abort() throws BuilderException;

    /**
     * Hier moeten de gereserveerde bronnen worden vrijgegeven...
     */
    public void finish() throws BuilderException;

}
