package org.example.validator.annotation;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import static java.lang.annotation.ElementType.*;

@Documented
@Constraint(validatedBy = { Base64Pattern.NameValidator.class })
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Base64Pattern {

    String message() default "invalid base64 format.";
    String pattern() default "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class NameValidator implements ConstraintValidator<Base64Pattern, String> {
        private String pattern;

        @Override
        public void initialize(Base64Pattern constratintAnnotation) {
            this.pattern = constratintAnnotation.pattern();
        }

        @Override
        public boolean isValid(String data, ConstraintValidatorContext context) {
            if(data == null) return true;
            return Pattern.matches(pattern, data);
        }
    }
}
