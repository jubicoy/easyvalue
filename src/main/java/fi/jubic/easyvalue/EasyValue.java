package fi.jubic.easyvalue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface EasyValue {
    /**
     * Set to true to produce JSON (de)serialization code.
     */
    boolean excludeJson() default false;
}
