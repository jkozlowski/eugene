/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.ontology;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests {@link Field}.
 * 
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class FieldTest {
    
    private static final class TestField extends Field<Long> {

        @Override
        public Integer getTag() {
            return -1;
        }

        @Override
        public Boolean isEnumField() {
            return Boolean.FALSE;
        }
    }
    
    @Test
    public void testEqualsSameInstance() {
        final TestField testField = new TestField();
        assertThat(testField.equals(testField), is(true));
    }
    
    @Test
    public void testEqualsDifferentClass() {
        assertThat(new TestField().equals(new Object()), is(false));
    }
    
    @Test
    public void testEqualsNullObject() {
        assertThat(new TestField().equals(null), is(false));
    }
    
    @Test
    public void testEqualsTestedNullPassedNotNull() {
        final TestField testedField = new TestField();
        final TestField passedField = new TestField();
        passedField.setValue(1L);
        assertThat(testedField.equals(passedField), is(false));
        assertThat(passedField.equals(testedField), is(false));
    }

    @Test
    public void testEqualsTestedNotNullPassedNull() {
        final TestField testedField = new TestField();
        final TestField passedField = new TestField();
        testedField.setValue(1L);
        assertThat(testedField.equals(passedField), is(false));
        assertThat(passedField.equals(testedField), is(false));
    }

    @Test
    public void testEqualsDifferentValue() {
        final TestField testedField = new TestField();
        final TestField passedField = new TestField();
        testedField.setValue(1L);
        passedField.setValue(2L);
        assertThat(testedField.equals(passedField), is(false));
        assertThat(passedField.equals(testedField), is(false));
    }

    @Test
    public void testEqualsSameValueDifferentInstance() {
        final TestField testedField = new TestField();
        final TestField passedField = new TestField();
        testedField.setValue(1L);
        passedField.setValue(1L);
        assertThat(testedField.equals(passedField), is(true));
        assertThat(passedField.equals(testedField), is(true));
    }
    
    @Test
    public void testHashCodeNullValue() {
        assertThat(new TestField().hashCode(), is(0));
    }

    @Test
    public void testHashCodeNotNullValue() {
        final TestField testField = new TestField();
        testField.setValue(1L);
        assertThat(testField.hashCode(), is(new Long(1L).hashCode()));
    }
    
    @Test
    public void testToString() {
        final TestField testField = new TestField();
        testField.setValue(1L);
        assertThat(testField.toString(), is("TestField=1"));
    }
}
