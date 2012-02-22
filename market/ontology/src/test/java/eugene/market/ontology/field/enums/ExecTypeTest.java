/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.ontology.field.enums;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests {@link ExecType}.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class ExecTypeTest {

    @Test
    public void testField() {
        assertThat(ExecType.NEW.field(), is(new eugene.market.ontology.field.ExecType(eugene.market.ontology.field
                                                                                              .ExecType.NEW)));
        assertThat(ExecType.CANCELED.field(), is(new eugene.market.ontology.field.ExecType(eugene.market.ontology.field
                                                                                                   .ExecType.CANCELED)));
        assertThat(ExecType.REJECTED.field(), is(new eugene.market.ontology.field.ExecType(eugene.market.ontology.field
                                                                                              .ExecType.REJECTED)));
        assertThat(ExecType.TRADE.field(), is(new eugene.market.ontology.field.ExecType(eugene.market.ontology.field
                                                                                              .ExecType.TRADE)));
    }
}
