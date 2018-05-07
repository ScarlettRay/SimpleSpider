package xyz.iamray.bilianimespider;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Cell {
   String value() default "";
}
