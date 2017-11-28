package org.giste.spring.util.validation;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;

public class IntComparationTest {

	private static Validator validator;

	@Before
	public void setUp() throws Exception {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	public void lessIsValid() {
		Less mock = new Less();

		mock.setReference(2);
		mock.setField(1);

		Set<ConstraintViolation<Less>> violations = validator.validate(mock);
		assertEquals(0, violations.size());
	}

	@Test
	public void lessIsNotValid() {
		Less mock = new Less();

		// Greater
		mock.setReference(1);
		mock.setField(2);

		Set<ConstraintViolation<Less>> violations = validator.validate(mock);
		assertEquals(1, violations.size());
		ConstraintViolation<Less> violation = violations.iterator().next();
		assertEquals("field", violation.getPropertyPath().toString());
		assertEquals("{org.giste.spring.util.validation.Comparation.less.message}", violation.getMessageTemplate());

		// Equal
		mock.setField(1);

		violations = validator.validate(mock);
		assertEquals(1, violations.size());
		violation = violations.iterator().next();
		assertEquals("field", violation.getPropertyPath().toString());
		assertEquals("{org.giste.spring.util.validation.Comparation.less.message}", violation.getMessageTemplate());
	}

	@Test
	public void lessOrEqualIsValid() {
		LessOrEqual mock = new LessOrEqual();

		// Less
		mock.setReference(2);
		mock.setField(1);

		Set<ConstraintViolation<LessOrEqual>> violations = validator.validate(mock);
		assertEquals(0, violations.size());

		// Equal
		mock.setField(2);

		violations = validator.validate(mock);
		assertEquals(0, violations.size());
	}

	@Test
	public void lessOrEqualIsNotValid() {
		LessOrEqual mock = new LessOrEqual();

		mock.setReference(1);
		mock.setField(2);

		Set<ConstraintViolation<LessOrEqual>> violations = validator.validate(mock);
		assertEquals(1, violations.size());
		ConstraintViolation<LessOrEqual> violation = violations.iterator().next();
		assertEquals("field", violation.getPropertyPath().toString());
		assertEquals("{org.giste.spring.util.validation.Comparation.lessOrEqual.message}",
				violation.getMessageTemplate());
	}

	@Test
	public void equalIsValid() {
		Equal mock = new Equal();

		mock.setReference(2);
		mock.setField(2);

		Set<ConstraintViolation<Equal>> violations = validator.validate(mock);
		assertEquals(0, violations.size());
	}

	@Test
	public void equalIsNotValid() {
		Equal mock = new Equal();

		mock.setReference(1);
		mock.setField(2);

		Set<ConstraintViolation<Equal>> violations = validator.validate(mock);
		assertEquals(1, violations.size());
		ConstraintViolation<Equal> violation = violations.iterator().next();
		assertEquals("field", violation.getPropertyPath().toString());
		assertEquals("{org.giste.spring.util.validation.Comparation.equal.message}", violation.getMessageTemplate());
	}

	@Test
	public void greaterIsValid() {
		Greater mock = new Greater();

		// Greater
		mock.setReference(2);
		mock.setField(3);

		Set<ConstraintViolation<Greater>> violations = validator.validate(mock);
		assertEquals(0, violations.size());
	}

	@Test
	public void greaterIsNotValid() {
		Greater mock = new Greater();

		// Less
		mock.setReference(2);
		mock.setField(1);

		Set<ConstraintViolation<Greater>> violations = validator.validate(mock);
		assertEquals(1, violations.size());
		ConstraintViolation<Greater> violation = violations.iterator().next();
		assertEquals("field", violation.getPropertyPath().toString());
		assertEquals("{org.giste.spring.util.validation.Comparation.greater.message}", violation.getMessageTemplate());

		// Equal
		mock.setField(2);

		violations = validator.validate(mock);
		assertEquals(1, violations.size());
		violation = violations.iterator().next();
		assertEquals("field", violation.getPropertyPath().toString());
		assertEquals("{org.giste.spring.util.validation.Comparation.greater.message}", violation.getMessageTemplate());

	}

	@Test
	public void greaterOrEqualIsValid() {
		GreaterOrEqual mock = new GreaterOrEqual();

		// Greater
		mock.setReference(2);
		mock.setField(3);

		Set<ConstraintViolation<GreaterOrEqual>> violations = validator.validate(mock);
		assertEquals(0, violations.size());

		// Equal
		mock.setField(2);

		violations = validator.validate(mock);
		assertEquals(0, violations.size());
	}

	@Test
	public void greaterOrEqualIsNotValid() {
		GreaterOrEqual mock = new GreaterOrEqual();

		mock.setReference(2);
		mock.setField(1);

		Set<ConstraintViolation<GreaterOrEqual>> violations = validator.validate(mock);
		assertEquals(1, violations.size());
		ConstraintViolation<GreaterOrEqual> violation = violations.iterator().next();
		assertEquals("field", violation.getPropertyPath().toString());
		assertEquals("{org.giste.spring.util.validation.Comparation.greaterOrEqual.message}",
				violation.getMessageTemplate());
	}

	@Test
	public void multipleIsValid() {
		Multiple mock = new Multiple();

		// 1 - Less
		mock.setReference1(2);
		mock.setField1(1);
		// 2 - Greater
		mock.setReference2(2);
		mock.setField2(3);

		Set<ConstraintViolation<Multiple>> violations = validator.validate(mock);
		assertEquals(0, violations.size());
	}
	
	@Test
	public void multipleIsNotValid() {
		Multiple mock = new Multiple();

		// 1 - Less
		mock.setReference1(2);
		mock.setField1(3);
		// 2 - Greater
		mock.setReference2(2);
		mock.setField2(1);

		Set<ConstraintViolation<Multiple>> violations = validator.validate(mock);
		assertEquals(2, violations.size());
	}

	@IntComparation(field = "field", reference = "reference", relationship = Relationship.Less)
	class Less {
		private Integer reference;
		private Integer field;

		public Integer getReference() {
			return reference;
		}

		public void setReference(Integer reference) {
			this.reference = reference;
		}

		public Integer getField() {
			return field;
		}

		public void setField(Integer field) {
			this.field = field;
		}
	}

	@IntComparation(field = "field", reference = "reference", relationship = Relationship.LessOrEqual)
	class LessOrEqual {
		private int reference;
		private int field;

		public int getReference() {
			return reference;
		}

		public void setReference(int reference) {
			this.reference = reference;
		}

		public int getField() {
			return field;
		}

		public void setField(int field) {
			this.field = field;
		}
	}

	@IntComparation(field = "field", reference = "reference", relationship = Relationship.Equal)
	class Equal {
		private int reference;
		private int field;

		public int getReference() {
			return reference;
		}

		public void setReference(int reference) {
			this.reference = reference;
		}

		public int getField() {
			return field;
		}

		public void setField(int field) {
			this.field = field;
		}
	}

	@IntComparation(field = "field", reference = "reference", relationship = Relationship.Greater)
	class Greater {
		private int reference;
		private int field;

		public int getReference() {
			return reference;
		}

		public void setReference(int reference) {
			this.reference = reference;
		}

		public int getField() {
			return field;
		}

		public void setField(int field) {
			this.field = field;
		}
	}

	@IntComparation(field = "field", reference = "reference", relationship = Relationship.GreaterOrEqual)
	class GreaterOrEqual {
		private int reference;
		private int field;

		public int getReference() {
			return reference;
		}

		public void setReference(int reference) {
			this.reference = reference;
		}

		public int getField() {
			return field;
		}

		public void setField(int field) {
			this.field = field;
		}
	}

	@IntComparation(field = "field1", reference = "reference1", relationship = Relationship.Less)
	@IntComparation(field = "field2", reference = "reference2", relationship = Relationship.Greater)
	class Multiple {
		private int reference1;
		private int field1;

		private int reference2;
		private int field2;

		public int getReference1() {
			return reference1;
		}

		public void setReference1(int reference1) {
			this.reference1 = reference1;
		}

		public int getField1() {
			return field1;
		}

		public void setField1(int field1) {
			this.field1 = field1;
		}

		public int getReference2() {
			return reference2;
		}

		public void setReference2(int reference2) {
			this.reference2 = reference2;
		}

		public int getField2() {
			return field2;
		}

		public void setField2(int field2) {
			this.field2 = field2;
		}
	}
}
