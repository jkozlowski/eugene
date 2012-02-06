package eugene.market.client.impl;

import eugene.market.book.Order;
import eugene.market.book.OrderBook;
import eugene.market.book.OrderBooks;
import org.testng.annotations.Test;

import static eugene.market.ontology.Defaults.defaultOrdQty;
import static eugene.market.ontology.Defaults.defaultOrderID;
import static eugene.market.ontology.Defaults.defaultPrice;
import static eugene.market.ontology.field.enums.OrdType.LIMIT;
import static eugene.market.ontology.field.enums.Side.BUY;
import static eugene.market.ontology.field.enums.Side.SELL;
import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests {@link TopOfBookPrinter}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public class TopOfBookPrinterTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullOrderBook() {
        new TopOfBookPrinter(null);
    }

    @Test
    public void testPrint() {
        final OrderBook orderBook = OrderBooks.defaultOrderBook();
        orderBook.insert(new Order(Long.valueOf(defaultOrderID), LIMIT, BUY, defaultOrdQty, defaultPrice));
        orderBook.insert(new Order(Long.valueOf(defaultOrderID), LIMIT, BUY, defaultOrdQty + 2, defaultPrice));
        orderBook.insert(new Order(Long.valueOf(defaultOrderID), LIMIT, BUY, defaultOrdQty + 3, defaultPrice - 1.0D));

        orderBook.insert(new Order(Long.valueOf(defaultOrderID), LIMIT, SELL, defaultOrdQty, defaultPrice + 1.0D));
        orderBook.insert(new Order(Long.valueOf(defaultOrderID), LIMIT, SELL, defaultOrdQty + 3, defaultPrice + 1.0D));
        orderBook.insert(new Order(Long.valueOf(defaultOrderID), LIMIT, SELL, defaultOrdQty + 4, defaultPrice + 2.0D));

        final String actual = new TopOfBookPrinter(orderBook).print();
        final String expected = format(TopOfBookPrinter.PRINT_FORMAT, 2 * defaultOrdQty + 2, defaultPrice,
                                       2 * defaultOrdQty + 3, defaultPrice + 1.0D);
        assertThat(actual, is(expected));
    }
}
