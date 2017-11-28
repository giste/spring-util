package org.giste.spring.util.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Retention(RUNTIME)
@Target({ TYPE, ANNOTATION_TYPE })
@Constraint(validatedBy = IntComparationValidator.class)
@Documented
@Repeatable(IntComparations.class)
public @interface IntComparation {

	String message() default "{org.giste.spring.util.validation.Comparation.equal.message}";
	
	Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    
    String field();
    
    String reference();
    
    Relationship relationship() default Relationship.Equal;
    
    /**
     * Defines several <code>@Comparation</code> annotations on the same element
     *
     * @see Comparation
     */
//    @Target({TYPE, ANNOTATION_TYPE})
//    @Retention(RUNTIME)
//    public @interface List {
//    	Comparation[] value();
//    }
    
}
