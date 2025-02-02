package bistro.core;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import bistro.core.operations.OpCalculate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CalculateTests {

    @BeforeClass
    public static void setUpClass() {
    }

    @Before
    public void setUp() {
    }

    @Test
    public void calcTest() {
        Schema s = this.createSchema();
        Table t = s.getTable("T");
        Column ta = t.getColumn("A");
        Column tb = t.getColumn("B");

        // Lambda
        tb.calculate(
                p -> 2.0 * (double) (p[0] == null ? Double.NaN : p[0]) + 1,
                ta
        );
        tb.evaluate();

        assertTrue(tb.getDependencies().contains(ta)); // Check correctness of dependencies

        assertEquals(11.0, (Double) tb.getData().getValue(0), Double.MIN_VALUE);
        assertEquals(Double.NaN, tb.getData().getValue(1));
        assertEquals(13.0, (Double) tb.getData().getValue(2), Double.MIN_VALUE);
    }

    Schema createSchema() {
        Schema s = new Schema("My Schema");
        Table t = s.createTable("T");
        Column ta = s.createColumn("A", t);
        Column tb = s.createColumn("B", t);

        t.getData().add();
        t.getData().add();
        t.getData().add();
        ta.getData().setValue(0, 5.0);
        ta.getData().setValue(1, null);
        ta.getData().setValue(2, 6.0);

        return s;
    }

    @Test
    public void newCalcTest() {
        Schema s = new Schema("My Schema");
        Table t = s.createTable("T");
        Column ta = s.createAttribute("A", "T");

        t.getData().add();
        t.getData().add();
        t.getData().add();
        ta.getData().setValue(0, 5.0);
        ta.getData().setValue(1, null);
        ta.getData().setValue(2, 6.0);

        Column tb = s.createCalculate(
                "B", "T", null,
                p -> 2.0 * (double) (p[0] == null ? Double.NaN : p[0]) + 1,
                new String[] {"A"}, null, "value"
        );

        ((OpCalculate)tb.getOperation()).translate();

        tb.evaluate();

        assertTrue(tb.getDependencies().contains(ta)); // Check correctness of dependencies

        assertEquals(11.0, (Double) tb.getData().getValue(0), Double.MIN_VALUE);
        assertEquals(Double.NaN, tb.getData().getValue(1));
        assertEquals(13.0, (Double) tb.getData().getValue(2), Double.MIN_VALUE);
    }

}
