package org.example.validator.annotation;

import lombok.SneakyThrows;
import org.example.validator.CommonScope;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Documented
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Constraint(validatedBy = {DataScope.EnumValidator.class})
public @interface DataScope {
    String message() default "invalid data scope.";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
    Class<? extends CommonScope> scopeClass() default CommonScope.class;
    String[] scope() default { };

    class EnumValidator implements ConstraintValidator<DataScope, Object> {
        private List<String> scopes;

        @SneakyThrows
        @Override
        public void initialize(DataScope constraintAnnotation) {
            if(constraintAnnotation.scopeClass().getEnumConstants().length > 0){
                scopes = Arrays.stream(constraintAnnotation.scopeClass().getEnumConstants())
                              .map(constant -> constant.getValue().toString())
                              .collect(Collectors.toList());
            }else{
                scopes = Arrays.asList(constraintAnnotation.scope());
            }
        }

        @Override
        public boolean isValid(Object value, ConstraintValidatorContext context) {
            if(value == null) return true;
            boolean result = false;
            if(value instanceof Iterable){
                for(Object item:(List)value){
                    result = scopes.contains(item);
                    if(!result) break;
                }
            }else {
                result = scopes.contains(value);
            }
            return result;
        }
    }
}
