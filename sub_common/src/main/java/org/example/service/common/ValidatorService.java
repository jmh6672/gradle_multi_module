package org.example.service.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.validation.*;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import javax.validation.ConstraintViolation;
import javax.validation.metadata.ConstraintDescriptor;
import java.io.Serializable;
import java.util.*;

@Log
@Service
@RequiredArgsConstructor
public class ValidatorService {
    private final SpringValidatorAdapter validatorAdapter;

	private static final String COLLMOD = "collMod";
	private static final String LIST_COLLECTIONS = "listCollections";
	private static final String CURSOR = "cursor";
	private static final String FIRST_BATCH = "firstBatch";
	private static final String VALIDATOR = "validator";

	private static final String JSON_SCHEMA = "$jsonSchema";
	private static final String BSON_TYPE = "bsonType";
	private static final String REQUIRED = "required";
	private static final String PROPERTIES = "properties";
	private static final String ITEMS = "items";


//	public Map getValidator(){
//		HashMap validatorMap = new HashMap();
//
//		List<Class> entities = getEntities();
//		entities.stream()
//				.map(entity -> getValidator(entity))
//				.forEach(entity -> validatorMap.putAll(entity));
//
//		return validatorMap;
//	}
//
//	@SneakyThrows
//	public Map getValidator(Class entity) {
//		HashMap validatorMap = new HashMap();
//
//		Object object = entity.getConstructor().newInstance();
//		Set<ConstraintViolation<Object>> violations = validatorAdapter.validate(object);
//
//		Arrays.stream(entity.getDeclaredFields())
////				.filter(field -> field.getDeclaredAnnotation(Constraint.class) != null)
//				.forEach(field -> {
//					Class fieldType = field.getType();
//					if(fieldType.getPackageName().startsWith(ENTITY_PACKAGE)){
//					}else if(fieldType.isAssignableFrom(List.class)){
//					}else{
//					}
//				});
//
//		return validatorMap;
//	}
//
//	public Map makeValidatorSchema(Field field){
//		Map fieldValidatorMap = new HashMap();
//
//		Arrays.stream(field.getDeclaredAnnotations())
//				.filter(annotation -> annotation.annotationType().isAnnotationPresent(Constraint.class))
//				.forEach(annotation -> {
//					Class annotationType = annotation.annotationType();
////					if(annotationType.isAssignableFrom(Size.class) || annotationType.isAssignableFrom(Length.class)){
////					}else if(annotationType.isAssignableFrom())
//				});
//
//		return fieldValidatorMap;
//	}


	/** validate 체크 */
    public BindingResult validate(Object target) {
        BindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
		String _targetClassName = target.getClass().getName();
//		log.info("[validate] target type name: " + target.getClass().getName());

        if(Collection.class.isAssignableFrom(target.getClass())){
            Object[] targetList = ((Collection<?>) target).toArray();
            for (int i=0; i<targetList.length; i++) {
                Object targetItem = targetList[i];
                processViolations(validatorAdapter.validate(targetItem),bindingResult,i);
            }
        } else {
			processViolations(validatorAdapter.validate(target),bindingResult,null);
        }

        return bindingResult;
    }

	/** violation 을 시스템에 맞게 재정렬 체크 */
    protected void processViolations(Set<ConstraintViolation<Object>> violations, BindingResult bindingResult, Integer index) {
		//필드 에러가 있을경우 사용됨
		List<FieldError> fieldErrors = new ArrayList<>();

        for (ConstraintViolation<Object> violation : violations) {
            String field = violation.getPropertyPath().toString();
            try {
                ConstraintDescriptor<?> cd = violation.getConstraintDescriptor();
                String errorCode = determineErrorCode(cd);
                Object[] errorArgs = getArgumentsForConstraint(bindingResult.getObjectName(), field, cd);

                String nestedField = bindingResult.getNestedPath() + field;
                if (nestedField.isEmpty()) {
                    String[] errorCodes = bindingResult.resolveMessageCodes(errorCode);
                    ObjectError error = new ViolationObjectError(
                            bindingResult.getObjectName(), errorCodes, errorArgs, violation, validatorAdapter);
                    bindingResult.addError(error);
                }
                else {
					String fieldName = index==null ? nestedField : "["+index+"]." + nestedField;
					Object rejectedValue = getRejectedValue(field, violation, bindingResult);
					String[] errorCodes = bindingResult.resolveMessageCodes(errorCode, field);
					String message = violation.getMessage();
					boolean duplicatedField = false;
					for(int i=0; i<fieldErrors.size(); i++){
						FieldError fieldError = fieldErrors.get(i);
						if(fieldError.getField().equals(fieldName)){
							duplicatedField = true;
							errorCodes = ArrayUtils.addAll(fieldError.getCodes(),errorCodes);
							message = fieldError.getDefaultMessage() + "," +message;
							fieldErrors.set(i,new FieldError(
									bindingResult.getObjectName(), fieldName,
									rejectedValue, false, errorCodes, errorArgs,
									message));
						}
					}
					if(!duplicatedField) {
						fieldErrors.add(new FieldError(
									bindingResult.getObjectName(), fieldName,
									rejectedValue, false, errorCodes, errorArgs,
									message));
					}
                }
            }
            catch (NotReadablePropertyException ex) {
                throw new IllegalStateException("JSR-303 validated property '" + field +
                        "' does not have a corresponding accessor for Spring data binding - " +
                        "check your DataBinder's configuration (bean property versus direct field access)", ex);
            }
        }
		if(fieldErrors.size() > 0){
			fieldErrors.forEach(fieldError -> bindingResult.addError(fieldError));
		}
	}

    protected String determineErrorCode(ConstraintDescriptor<?> descriptor) {
   		return descriptor.getAnnotation().annotationType().getSimpleName();
   	}

    protected Object[] getArgumentsForConstraint(String objectName, String field, ConstraintDescriptor<?> descriptor) {
        List<Object> arguments = new ArrayList<>();
        arguments.add(getResolvableField(objectName, field));
        // Using a TreeMap for alphabetical ordering of attribute names
        Map<String, Object> attributesToExpose = new TreeMap<>();
        descriptor.getAttributes().forEach((attributeName, attributeValue) -> {
            if (attributeValue instanceof String) {
                attributeValue = new ResolvableAttribute(attributeValue.toString());
            }
            attributesToExpose.put(attributeName, attributeValue);
        });
        arguments.addAll(attributesToExpose.values());
        return arguments.toArray();
    }

    protected MessageSourceResolvable getResolvableField(String objectName, String field) {
   		String[] codes = new String[] {objectName + Errors.NESTED_PATH_SEPARATOR + field, field};
   		return new DefaultMessageSourceResolvable(codes, field);
   	}

    @Nullable
   	protected Object getRejectedValue(String field, ConstraintViolation<Object> violation, BindingResult bindingResult) {
   		Object invalidValue = violation.getInvalidValue();
   		if (!field.isEmpty() && !field.contains("[]") &&
				(invalidValue == violation.getLeafBean() || field.contains("[") || field.contains("."))) {
   			// Possibly a bean constraint with property path: retrieve the actual property value.
   			// However, explicitly avoid this for "address[]" style paths that we can't handle.
   			invalidValue = bindingResult.getRawFieldValue(field);
   		}
   		return invalidValue;
   	}



    protected static boolean requiresMessageFormat(ConstraintViolation<?> violation) {
   		return containsSpringStylePlaceholder(violation.getMessage());
   	}

    private static boolean containsSpringStylePlaceholder(@Nullable String message) {
   		return (message != null && message.contains("{0}"));
   	}




   	private static class ResolvableAttribute implements MessageSourceResolvable, Serializable {

   		private final String resolvableString;

   		public ResolvableAttribute(String resolvableString) {
   			this.resolvableString = resolvableString;
   		}

   		@Override
   		public String[] getCodes() {
   			return new String[] {this.resolvableString};
   		}

   		@Override
   		@Nullable
   		public Object[] getArguments() {
   			return null;
   		}

   		@Override
   		public String getDefaultMessage() {
   			return this.resolvableString;
   		}

   		@Override
   		public String toString() {
   			return this.resolvableString;
   		}
   	}


    private static class ViolationObjectError extends ObjectError implements Serializable {

   		@Nullable
   		private transient SpringValidatorAdapter adapter;

   		@Nullable
   		private transient ConstraintViolation<?> violation;

   		public ViolationObjectError(String objectName, String[] codes, Object[] arguments,
   				ConstraintViolation<?> violation, SpringValidatorAdapter adapter) {

   			super(objectName, codes, arguments, violation.getMessage());
   			this.adapter = adapter;
   			this.violation = violation;
   			wrap(violation);
   		}

   		@Override
   		public boolean shouldRenderDefaultMessage() {
   			return (this.adapter != null && this.violation != null ?
   					requiresMessageFormat(this.violation) :
   					containsSpringStylePlaceholder(getDefaultMessage()));
   		}
   	}
}
