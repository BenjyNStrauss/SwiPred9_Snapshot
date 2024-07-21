package utilities;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
//import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

public @interface Unfinished {

}
