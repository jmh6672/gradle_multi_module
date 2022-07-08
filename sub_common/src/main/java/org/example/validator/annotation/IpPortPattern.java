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

/**
 * Ip Port Pattern
 *  - IPv4:Port
 */
@Documented
@Constraint(validatedBy = { IpPortPattern.IpPortValidator.class })
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IpPortPattern {

    String message() default "invalid ip:port format.";
    String pattern() default "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(:[0-9]+)$";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class IpPortValidator implements ConstraintValidator<IpPortPattern, String> {
        private String pattern;

        @Override
        public void initialize(IpPortPattern constratintAnnotation) {
            this.pattern = constratintAnnotation.pattern();
        }

        @Override
        public boolean isValid(String data, ConstraintValidatorContext context) {
            if(data == null) return true;
            return Pattern.matches(pattern, data);
        }
    }
}
