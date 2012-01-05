package eugene.market.esma.execution;

import eugene.market.esma.Repository;
import eugene.market.esma.Repository.Tuple;
import jade.core.AID;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;

/**
 * Tests {@link Repository.Tuple}.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class TupleTest {
    
    public static final String clOrdID = "order01";
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullAID() {
        new Tuple(null, clOrdID);
    }
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullClOrdID() {
        new Tuple(mock(AID.class), null);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorEmptyClOrdID() {
        new Tuple(mock(AID.class), "");
    }
}
