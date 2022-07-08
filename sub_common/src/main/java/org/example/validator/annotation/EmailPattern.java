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
@Constraint(validatedBy = { EmailPattern.EmailValidator.class })
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailPattern {

    String message() default "invalid email format.";
    // RFC822 완전히 준수. IP 주소 및 서버 이름을 허용합니다 (인트라넷 용도)
    String pattern() default "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class EmailValidator implements ConstraintValidator<EmailPattern, String> {
        private String pattern;

        @Override
        public void initialize(EmailPattern constratintAnnotation) {
            this.pattern = constratintAnnotation.pattern();
        }

        @Override
        public boolean isValid(String data, ConstraintValidatorContext context) {
            if(data == null) return true;
            Pattern EMAIL_REGEX = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            return EMAIL_REGEX.matcher(data).matches();
        }
    }
}
