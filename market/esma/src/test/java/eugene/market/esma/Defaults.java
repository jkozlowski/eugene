package eugene.market.esma;

import eugene.market.esma.Repository.Tuple;
import jade.core.AID;

import java.util.concurrent.atomic.AtomicLong;

import static org.mockito.Mockito.mock;

/**
 * Holds default objects and values.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class Defaults {

    public static final AtomicLong curOrderID = new AtomicLong(1L);

    public static final String defaultSymbol = "VOD.L";

    public static final Long defaultOrdQty = 100L;

    public static final Double defaultPrice = 100.0D;

    public static final String defaultClOrdID = "client01";

    public static final Tuple defaultTuple = new Tuple(mock(AID.class), defaultClOrdID);
}
