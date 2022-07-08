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
import static java.lang.annotation.ElementType.TYPE_USE;

@Documented
@Constraint(validatedBy = { NamePattern.NameValidator.class })
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NamePattern {

    String message() default "invalid name format. (kor, eng, number, -, _, .)";
    String pattern() default "^[ㄱ-ㅎ|가-힣|a-z|A-Z|0-9|_|\\-|\\.| ]+$";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class NameValidator implements ConstraintValidator<NamePattern, String> {
        private String pattern;

        @Override
        public void initialize(NamePattern constratintAnnotation) {
            this.pattern = constratintAnnotation.pattern();
        }

        @Override
        public boolean isValid(String data, ConstraintValidatorContext context) {
            if(data == null) return true;
            return Pattern.matches(pattern, data);
        }
    }
}
