/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.ontology;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Holds default objects and values.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class Defaults {

    public static final BigDecimal defaultTickSize = new BigDecimal("0.001").setScale(3);

    public static final AtomicLong curOrderID = new AtomicLong(1L);

    public static final String SENDER_AGENT = "sender";

    public static final String RECEIVER_AGENT = "receiver";

    public static final Long defaultOrdQty = 100L;

    public static final BigDecimal defaultPrice = new BigDecimal("100.0").setScale(3, RoundingMode.HALF_UP);

    public static final String defaultClOrdID = "client01";
    
    public static final String defaultTradeID = "01";

    public static final BigDecimal Price = new BigDecimal("1.2").setScale(3, RoundingMode.HALF_UP);

    public static final String defaultSymbol = "VOD.L";

    public static final String defaultOrderID = "1";

    public static final Long defaultLeavesQty = 1L;

    public static final Long defaultCumQty = 99L;
    
    public static final Long defaultLastQty = 99L;

    public static final BigDecimal defaultLastPx = new BigDecimal("100.0").setScale(3, RoundingMode.HALF_UP);

    public static final BigDecimal defaultAvgPx = new BigDecimal("1.2").setScale(3, RoundingMode.HALF_UP);
}
