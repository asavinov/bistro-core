package prosto.core.operations;

import prosto.core.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Attribute column are managed by tables.
 */
public class OpAttribute implements Operation {

    Column column;

    @Override
    public OperationType getOperationType() {
        return OperationType.ATTRIBUTE;
    }

    @Override
    public List<Element> getDependencies() {
        List<Element> deps = new ArrayList<>();

        deps.add(this.column.getInput()); // Columns depend on their input table

        return deps;
    }

    @Override
    public void evaluate() {
        // Attributes are evaluated by table population procedures
    }

    public OpAttribute(Column column) {
        this.column = column;
    }
}
