package eugene.market.esma.impl;

import eugene.market.book.Order;
import eugene.market.esma.impl.Repository.Tuple;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import org.testng.annotations.Test;

import static eugene.market.book.MockOrders.buy;
import static eugene.market.book.MockOrders.order;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link Repository}.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class RepositoryTest {
    
    private static final AID aid = mock(AID.class);
    
    private static final ACLMessage aclMessage = when(mock(ACLMessage.class).getSender()).thenReturn(aid).getMock();

    private static final Tuple tuple = new Tuple(aclMessage, TupleTest.clOrdID);
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testAddNullAid() {
        final Repository repository = new Repository();
        repository.add(null);
    }
    
    @Test
    public void testAdd() {
        final Repository repository = new Repository();
        repository.add(aid);
        assertThat(repository.getAIDs().size(), is(1));
        assertThat(repository.getAIDs(), hasItem(aid));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testPutNullOrder() {
        final Repository repository = new Repository();
        repository.put(null, tuple);
    }
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testPutNullTuple() {
        final Repository repository = new Repository();
        repository.put(mock(Order.class), null);
    }

    @Test
    public void testPutGetWithSameInstance() {
        final Repository repository = new Repository();
        final Order order = order(buy());
        repository.put(order, tuple);
        assertThat(repository.get(tuple), sameInstance(order));
        assertThat(repository.get(order), sameInstance(tuple));
    }
    
    @Test
    public void testPutGetWithAnotherTuple() {
        final Repository repository = new Repository();
        final Order order = order(buy());
        final Tuple tupleCopy = new Tuple(aclMessage, TupleTest.clOrdID);
        repository.put(order, tuple);
        assertThat(repository.get(order), sameInstance(tuple));
        assertThat(repository.get(tupleCopy), sameInstance(order));
    }
}
