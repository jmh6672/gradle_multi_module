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
@Constraint(validatedBy = { PhoneDigitPattern.PhoneDigitValidator.class })
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneDigitPattern {

    String message() default "invalid phone digit format. (0-9, *, #)";
    String pattern() default "^[0-9|\\*|\\#]+$";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class PhoneDigitValidator implements ConstraintValidator<PhoneDigitPattern, String> {
        private String pattern;

        @Override
        public void initialize(PhoneDigitPattern constratintAnnotation) {
            this.pattern = constratintAnnotation.pattern();
        }

        @Override
        public boolean isValid(String data, ConstraintValidatorContext context) {
            if(data == null) return true;
            return Pattern.matches(pattern, data);
        }
    }
}
