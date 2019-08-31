package bistro.core.operations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bistro.core.*;

/**
 * The logic of evaluation of calculate columns.
 */
public class OpCalculate implements Operation {

    // Operations are viewed as extensions of this base class
    Column column;

    // Definition
    List<String> inputs = new ArrayList<>();
    String lambdaName;

    Object model;
    String input_length = "value";

    // Operation
    List<ColumnPath> parameterPaths = new ArrayList<>();
    EvalCalculate lambda;

    @Override
    public OperationType getOperationType() {
        return OperationType.CALCULATE;
    }

    @Override
    public List<Element> getDependencies() {
        List<Element> deps = new ArrayList<>();

        deps.add(this.column.getInput()); // Columns depend on their input table

        List<Column> cols = ColumnPath.getColumns(this.parameterPaths);
        for(Column col : cols) deps.add(col);
        return deps;
    }

    @Override
    public void evaluate() {
        if(this.lambda == null) { // Default
            this.column.getData().setValue(); // Reset
            return;
        }

        Table mainTable = this.column.getInput(); // Loop/scan table

        //
        // Determine the scope of dirtiness
        //

        Range mainRange = mainTable.getData().getIdRange();

        boolean fullScope = false;

        if(!fullScope) {
            if (this.column.getDefinitionChangedAt() > this.column.getData().getChangedAt()) { // Definition has changes
                fullScope = true;
            }
        }

        if(!fullScope) { // Some column dependency has changes
            List<Element> deps = this.getDependencies();
            for(Element e : deps) {
                if(!(e instanceof Column)) continue;
                if(((Column)e).getData().isChanged()) { // There is a column with some changes
                    fullScope = true;
                    break;
                }
            }
        }

        if(!fullScope) {
            mainRange = mainTable.getData().getAddedRange();
        }

        //
        // Update dirty elements
        //

        // Get all necessary parameters and prepare (resolve) the corresponding data (function) objects for reading valuePaths
        List<ColumnPath> paramPaths = this.parameterPaths;
        Object[] paramValues = new Object[paramPaths.size() + 1]; // Will store valuePaths for all params and current output at the end
        Object result; // Will be written to output for each input

        for(long i=mainRange.start; i<mainRange.end; i++) {

            // Read all parameter valuePaths
            for(int p=0; p<paramPaths.size(); p++) {
                paramValues[p] = paramPaths.get(p).getValue(i);
            }

            //
            // Call user-defined function
            //
            try {
                result = this.lambda.evaluate(paramValues);
            }
            catch(BistroException e) {
                throw(e);
            }
            catch(Exception e) {
                throw( new BistroException(BistroErrorCode.EVALUATION_ERROR, e.getMessage(), "Error executing user-defined function.") );
            }

            // Update output
            this.column.getData().setValue(i, result);
        }

    }

    // Prepare the definition to execution (convert definition to executable operation)
    public void translate() {
        Schema schema = this.column.getSchema();

        // TODO: We use it only because there are two creation approaches: by name and by object
        if(!this.parameterPaths.isEmpty()) {
            return;
        }

        // Resolve all input names
        this.parameterPaths = new ArrayList<>();
        for(String name : this.inputs) {
            // TODO: input names could be complex columns so we need to
            // - add auxiliary operations for merge columns by breaking input column into segments
            // - resolve only the result merge column and add it to input list (rather than the complex input which will be used in the merge column definition)
            // Important: during translation phase, we need to have access to the list of all operations so that we can add additional operations.
            //   Currently, it can be the schema itself (so we cannot distinguish user and auxiliary operations) but later we can separate them.
            //   Alternatively, it can be the topology but then the translation method need to have access to it

            Column col = schema.getColumn(this.column.getInput().getName(), name);
            this.parameterPaths.add(new ColumnPath(col));
        }
    }

    public OpCalculate(Column column, EvalCalculate lambda, String[] inputs, Object model, String input_length) {
        this.column = column;
        this.lambda = lambda;
        this.inputs = Arrays.asList(inputs);
    }

    public OpCalculate(Column column, EvalCalculate lambda, ColumnPath[] paths) {
        this.column = column;
        this.lambda = lambda;
        this.parameterPaths = Arrays.asList(paths);
    }

    public OpCalculate(Column column, EvalCalculate lambda, Column[] columns) {
        this.column = column;
        this.lambda = lambda;
        for (int i = 0; i < columns.length; i++) {
            this.parameterPaths.add(new ColumnPath(columns[i]));
        }
    }
}
