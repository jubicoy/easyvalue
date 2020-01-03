package fi.jubic.easyvalue.legacy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface EasyProperty {
    /**
     * Serialized name of the property. Used for JSON mapping. Defaults to default behavior
     * of serialization framework.
     */
    String value() default "";
}
