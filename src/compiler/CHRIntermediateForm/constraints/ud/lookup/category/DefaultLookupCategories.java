package compiler.CHRIntermediateForm.constraints.ud.lookup.category;


public class DefaultLookupCategories extends SingletonLookupCategories {
    
    private DefaultLookupCategories() {/* SINGLETON */}
    private static DefaultLookupCategories instance;
    public static DefaultLookupCategories getInstance() {
        if (instance == null)
            instance = new DefaultLookupCategories();

        return instance;
    }
    
    @Override
    protected SingletonLookupCategory getSingletonInstance() {
        return DefaultLookupCategory.getInstance();
    }
}
