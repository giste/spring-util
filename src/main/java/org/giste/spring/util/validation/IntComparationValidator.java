package org.giste.spring.util.validation;

import java.lang.reflect.Field;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IntComparationValidator implements ConstraintValidator<IntComparation, Object> {

	private String field;
	private String reference;
	private Relationship relationship;

	@Override
	public void initialize(final IntComparation annotation) {
		this.field = annotation.field();
		this.reference = annotation.reference();
		this.relationship = annotation.relationship();
	}

	@Override
	public boolean isValid(final Object object, final ConstraintValidatorContext context) {
		try {
			int fieldValue = (int) getFieldValue(object, field);
			int referenceValue = (int) getFieldValue(object, reference);

			boolean valid = false;

			switch (this.relationship) {
			case Less:
				valid = fieldValue < referenceValue;
				break;
			case LessOrEqual:
				valid = fieldValue <= referenceValue;
				break;
			case Equal:
				valid = fieldValue == referenceValue;
				break;
			case Greater:
				valid = fieldValue > referenceValue;
				break;
			case GreaterOrEqual:
				valid = fieldValue >= referenceValue;
				break;
			}

			if (!valid) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(
						"{org.giste.spring.util.validation.Comparation." + relationship.getText() + ".message}")
						.addPropertyNode(field).addConstraintViolation();
			}

			return valid;
		} catch (Exception e) {
			// log error
			return false;
		}
	}

	private Object getFieldValue(Object object, String fieldName) throws Exception {
		Class<?> clazz = object.getClass();
		Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(object);
	}
}
