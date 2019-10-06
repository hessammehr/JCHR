package runtime.debug;

public class SysoutTracer extends OutputStreamTracer {
    private SysoutTracer() { /* SINGLETON */
        super(System.out);
    }
    
    private static SysoutTracer instance;
    public static SysoutTracer getInstance() {
        if (instance == null)
            instance = new SysoutTracer();
        return instance;
    }
}
