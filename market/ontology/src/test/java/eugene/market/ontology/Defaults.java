package eugene.market.ontology;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Holds default objects and values.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class Defaults {

    public static final AtomicLong curOrderID = new AtomicLong(1L);

    public static final String SENDER_AGENT = "sender";

    public static final String RECEIVER_AGENT = "receiver";

    public static final Long defaultOrdQty = 100L;

    public static final Double defaultPrice = 100.0D;

    public static final String defaultClOrdID = "client01";
    
    public static final String defaultTradeID = "01";

    public static final Double Price = 1.2;

    public static final String defaultSymbol = "VOD.L";

    public static final String defaultOrderID = "1";

    public static final Long defaultLeavesQty = 1L;

    public static final Long defaultCumQty = 2L;

    public static final Double defaultAvgPx = 1.2;
}
