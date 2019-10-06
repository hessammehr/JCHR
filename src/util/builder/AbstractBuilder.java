package util.builder;

public abstract class AbstractBuilder<Result> implements IBuilder<Result> {

    /* 
     * Do not remove constructor, since a no-argument constructor
     * is needed!
     */ 
    public AbstractBuilder() {
        // NOP 
    }
    public AbstractBuilder(Result result) {
        setResult(result);
    }

    private Result result;

    public Result getResult() throws BuilderException {        
        return result;
    }
    protected void setResult(Result result) {
        this.result = result;
    }
}
