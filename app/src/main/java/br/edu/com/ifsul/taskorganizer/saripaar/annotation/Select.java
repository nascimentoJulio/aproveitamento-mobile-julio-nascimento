package br.edu.com.ifsul.taskorganizer.saripaar.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Select {
    int defaultSelection() default 0;
    String message() default "Please select a valid option";
}
