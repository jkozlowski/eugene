import org.testng.annotations.Test;

@Test(expectedExceptions = NullPointerException.class)
public void testDoSomethingNullS() {
  doSomething(null);
}

@Test(expectedExceptions = IllegalArgumentException.class)
public void testDoSomethingEmptyS() {
  doSomething("");
}
