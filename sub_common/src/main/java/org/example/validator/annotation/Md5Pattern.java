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
@Constraint(validatedBy = { Md5Pattern.NameValidator.class })
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Md5Pattern {

    String message() default "invalid name format. (eng, number, _, -, @)";
    String pattern() default "^[a-z|A-Z|0-9|_|\\-|\\@]+$";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class NameValidator implements ConstraintValidator<Md5Pattern, String> {
        private String pattern;

        @Override
        public void initialize(Md5Pattern constratintAnnotation) {
            this.pattern = constratintAnnotation.pattern();
        }

        @Override
        public boolean isValid(String data, ConstraintValidatorContext context) {
            if(data == null) return true;
            return Pattern.matches(pattern, data);
        }
    }
}
