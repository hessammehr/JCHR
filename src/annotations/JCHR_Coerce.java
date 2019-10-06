package annotations;

import java.lang.annotation.*;

/**
 * @author Peter Van Weert
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JCHR_Coerce {
    // This is just an empty annotation
}
