package compiler.CHRIntermediateForm.constraints.ud.lookup.category;


public class NeverStoredLookupCategories extends SingletonLookupCategories {
    
    private NeverStoredLookupCategories() {/* SINGLETON */}
    private static NeverStoredLookupCategories instance;
    public static NeverStoredLookupCategories getInstance() {
        if (instance == null)
            instance = new NeverStoredLookupCategories();

        return instance;
    }
    
    @Override
    protected SingletonLookupCategory getSingletonInstance() {
        return NeverStoredLookupCategory.getInstance();
    }
}
