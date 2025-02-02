package bistro.core;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import bistro.core.data.ColumnDataImpl;
import bistro.core.operations.*;

public class Column implements Element {

    private Schema schema;
    public Schema getSchema() {
        return this.schema;
    }

    private final UUID id;
    public UUID getId() {
        return this.id;
    }

    private String name;
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private Table input;
    public Table getInput() {
        return this.input;
    }
    public void setInput(Table table) {
        this.input = table;
    }

    private Table output;
    public Table getOutput() {
        return this.output;
    }
    public void setOutput(Table table) { this.output = table; this.getData().setValue(null); }

    //
    // Data
    //

    private ColumnData data;
    public ColumnData getData() { return this.data; }
    public void setData(ColumnData data) { this.data = data; }

    //
    // Element interface
    //

    @Override
    public Table getTable() {
        return null;
    }

    @Override
    public Column getColumn() {
        return this;
    }

    @Override
    public List<Element> getDependencies() {
        List<Element> deps = new ArrayList<>();

        if(this.getOperationType() == OperationType.ATTRIBUTE) {
            if(this.isAttribute() && this.getInput().getOperationType() == OperationType.PRODUCT) {
                deps.add(this.getInput()); // Key-columns depend on the product-table (if any) because they are filled by their population procedure
            }
        }
        else if(this.operation != null) {
            deps = this.operation.getDependencies();
            if(deps == null) deps = new ArrayList<>();
        }

        return deps;
    }
    @Override
    public boolean hasDependency(Element element) {
        for(Element dep : this.getDependencies()) {
            if(dep == element) return true;
            if(dep.hasDependency(element)) return true; // Recursion
        }
        return false;
    }

    @Override
    public List<Element> getDependents() {
        List<Element> cols = schema.getColumns().stream().filter(x -> x.getDependencies().contains(this)).collect(Collectors.<Element>toList());
        List<Element> tabs = schema.getTables().stream().filter(x -> x.getDependencies().contains(this)).collect(Collectors.<Element>toList());

        List<Element> ret = new ArrayList<>();
        ret.addAll(cols);
        for(Element d : tabs) {
            if(!ret.contains(d)) ret.add(d);
        }

        return ret;
    }
    @Override
    public boolean hasDependents(Element element) {
        for(Element dep : this.getDependents()) {
            if(dep == element) return true;
            if(dep.hasDependents(element)) return true;// Recursion
        }
        return false;
    }

    private List<BistroException> errors = new ArrayList<>();
    @Override
    public List<BistroException> getErrors() { // Empty list in the case of no errors
        return this.errors;
    }

    @Override
    public boolean hasErrorsDeep() {
        if(errors.size() > 0) return true; // Check this element

        // Otherwise check errors in dependencies (recursively)
        for(Element dep : this.getDependencies()) {
            if(dep.hasErrorsDeep()) return true;
        }

        return false;
    }

    @Override
    public boolean isDirty() {

        // Definition has changed
        if(this.operation != null) {
            if(this.getDefinitionChangedAt() > this.getData().getChangedAt()) return true; // Definition has changes
        }

        // One of its dependencies has changes or is dirty
        for(Element dep : this.getDependencies()) {

            if(dep instanceof Column) {
                if(((Column)dep).getData().isChanged()) return true;
            }
            else if(dep instanceof Table) {
                if(((Table)dep).getData().isChanged()) return true;
            }

            if(dep.isDirty()) return true; // Recursion
        }

        return false;
    }

    @Override
    public void evaluate() { // Evaluate only this individual column if possible

        // Skip non-derived elements since they do not participate in evaluation (nothing to evaluate)
        if(!this.isDerived()) {
            return;
        }

        //
        // Check can evaluate
        //

        this.errors.clear();

        if(this.hasErrorsDeep()) {
            // TODO: Add error: cannot evaluate because of execution error in a dependency
            return;
        }

        if(this.operation == null) {
            return;
        }

        //
        // Check need to evaluate
        //

        if(!this.isDirty()) {
            // TODO: Add error: cannot evaluate because of dirty dependency
            return;
        }

        //
        // Really evaluate
        //
        try {
            this.operation.evaluate();
        }
        catch(BistroException e) {
            this.errors.add(e);
        }
        catch(Exception e) {
            this.errors.add( new BistroException(BistroErrorCode.EVALUATION_ERROR, e.getMessage(), "Error evaluating column.") );
        }

    }

    //
    // Column (operation) kind
    //

    Operation operation;
    @Override
    public Operation getOperation() {
        return this.operation;
    }
    @Override
    public void setOperation(Operation operation) {
        this.errors.clear();
        this.definitionChangedAt = System.nanoTime();

        this.operation = operation;

        if(this.hasDependency(this)) {
            this.operation = null; // Reset definition because of failure to set new operation
            throw new BistroException(BistroErrorCode.DEFINITION_ERROR, "Cyclic dependency.", "This column depends on itself directly or indirectly.");
        }
    }

    @Override
    public OperationType getOperationType() {
        if(this.operation == null) return OperationType.ATTRIBUTE;
        else return this.operation.getOperationType();
    }

    public boolean isDerived() {
        if(this.getOperationType() == OperationType.ATTRIBUTE) {
            return false;
        }
        return true;
    }

    protected long definitionChangedAt; // Time of latest change
    public long getDefinitionChangedAt() {
        return this.definitionChangedAt;
    }

    //
    // Attributes
    //

    public boolean isAttribute() {
        return this.getOperationType() == OperationType.ATTRIBUTE;
    }

    public void attribute() {
        Operation op = new OpAttribute(this);
        this.setOperation(op);
    }

    //
    // Calculate column
    //

    public void calculate(EvalCalculate lambda, ColumnPath... paths) {
        Operation op = new OpCalculate(this, lambda, paths);
        this.setOperation(op);
    }

    public void calculate(EvalCalculate lambda, Column... columns) {
        Operation op = new OpCalculate(this, lambda, columns);
        this.setOperation(op);
    }

    //
    // Link column
    //

    public void link(ColumnPath[] valuePaths, Column... keyColumns) {
        Operation op = new OpLink(this, valuePaths, keyColumns);
        this.setOperation(op);
    }

    public void link(Column[] valueColumns, Column... keyColumns) {
        Operation op = new OpLink(this, valueColumns, keyColumns);
        this.setOperation(op);
    }

    public void link(ColumnPath valuePath) { // Link to range table (using inequality as a condition)
        Operation op = new OpLink(this, valuePath);
        this.setOperation(op);
    }

    //
    // Project to values (using equality)
    //

    public void project(ColumnPath[] valuePaths, Column... keyColumns) {
        Operation op = new OpProject(this, valuePaths, keyColumns);
        this.setOperation(op);
    }

    public void project(Column[] valueColumns, Column... keyColumns) {
        Operation op = new OpProject(this, valueColumns, keyColumns);
        this.setOperation(op);
    }

    //
    // Project to ranges/intervals (using inequality)
    //

    public void project(ColumnPath valuePath) {
        Operation op = new OpProject(this, valuePath);
        this.setOperation(op);
    }

    public void project(Column valueColumn) {
        Operation op = new OpProject(this, new ColumnPath(valueColumn));
        this.setOperation(op);
    }

    //
    // Accumulate column
    //

    public void accumulate(ColumnPath groupPath, EvalAccumulate adder, EvalAccumulate remover, ColumnPath... paths) {
        Operation op = new OpAccumulate(this, groupPath, adder, remover, paths);
        this.setOperation(op);
    }

    public void accumulate(Column groupColumn, EvalAccumulate adder, EvalAccumulate remover, Column... columns) {
        Operation op = new OpAccumulate(this, groupColumn, adder, remover, columns);
        this.setOperation(op);
    }

    //
    // Rolling column
    //

    public void roll(int sizePast, int sizeFuture, EvalRoll lambda, ColumnPath... paths) {
        Operation op = new OpRoll(this, null, sizePast, sizeFuture, lambda, paths);
        this.setOperation(op);
    }

    public void roll(int sizePast, int sizeFuture, EvalRoll lambda, Column... columns) {
        Operation op = new OpRoll(this, null, sizePast, sizeFuture, lambda, columns);
        this.setOperation(op);
    }

    public void roll(ColumnPath distancePath, int sizePast, int sizeFuture, EvalRoll lambda, ColumnPath... paths) {
        Operation op = new OpRoll(this, distancePath, sizePast, sizeFuture, lambda, paths);
        this.setOperation(op);
    }

    public void roll(Column distanceColumn, int sizePast, int sizeFuture, EvalRoll lambda, Column... columns) {
        Operation op = new OpRoll(this, distanceColumn, sizePast, sizeFuture, lambda, columns);
        this.setOperation(op);
    }

    //
    // Serialization and construction
    //

    @Override
    public String toString() {
        return "[" + getName() + "]: " + input.getName() + " -> " + this.getOperationType() + " -> " + output.getName();
    }

    @Override
    public boolean equals(Object aThat) {
        if (this == aThat) return true;
        if ( !(aThat instanceof Column) ) return false;

        Column that = (Column)aThat;

        if(!that.getId().toString().equals(id.toString())) return false;

        return true;
    }

    public Column(Schema schema, String name, Table input, Table output) {
        this.schema = schema;
        this.id = UUID.randomUUID();
        this.name = name;
        this.input = input;
        this.output = output;

        // Where its output values are stored
        this.data = new ColumnDataImpl(this.input.getData().getIdRange().start, this.input.getData().getIdRange().end);
    }
}
