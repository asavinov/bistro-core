package bistro.core.operations;

import bistro.core.Column;
import bistro.core.Element;
import bistro.core.Operation;
import bistro.core.OperationType;

import java.util.ArrayList;
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
