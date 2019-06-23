# Defining columns

## Calculate columns

A column might have a *definition* which means that it uses some operation to automatically derive (infer) its output values from the data in other columns (which in turn can derive their outputs from other columns). Depending on the logic behind such inference, there are different column definition types. The simplest derived column type is a *calculate* column:

> For each input, a calculate column *computes* its output by using the outputs of other columns of this same table for this same input

For example, we could define a calculate column which increments the value stored in another column:

```java
Column calc = schema.createColumn("Name Length", things, objects);
calc.calculate(
        p -> ((String)p[0]).length(), // How to compute
        thingName // One parameter to compute the column
);
```

The first parameter is a adder function. Its argument `p` is an array of (output) values of other columns used to compute the output of the calculate column. The second parameter of the definition specifies the columns used for calculations. In this example, we want to find the length of the device name. The size of the `p` array has to be equal to the number of columns references passed via the second parameter (1 in this example).

There exist also other ways to define calculate columns which can be more convenient in different situations, for example, in the case of complex arithmetic operations or in the case of complex computations implemented programmatically. Note also that column outputs could contain `null` values and hence all adder functions must guarantee the validity of its computations including null-safety and type-safety.

## Link columns

*Link* columns are typed by user (not primitive) tables and their output essentially is a reference to some element in the output table:

> For each input, a link column *finds* its output in the output table by providing equality criteria for the output elements. These values for these criteria are computed from the columns in this table using this input similar to calculate columns.

Let us assume that the "EVENTS" table stores records with a property (column) which stores a name from the "THINGS" table:

```java
Column eventThingName = schema.createColumn("Thing Name", events, objects);

facts.add(3);
eventThingName.setValue(0, "oven");
eventThingName.setValue(1, "fridge");
eventThingName.setValue(2, "oven");
```

This property however cannot be used to access the elements of the "THINGS". Therefore, we define a new link column which will *directly* reference elements from "THINGS":

```java
Column link = schema.createColumn("Thing", events, things);
link.link(
        new Column[] { thingName }, // Columns to be used for search (in the type table)
        eventThingName // Columns providing search criteria (in this input table)
);
```

This definition essentially means that an event record will directly reference a thing record having the same name: 
`EVENTS::Name == THINGS::Name`.

The main benefit of having link columns is that they are evaluated once but can be then used in many other column definitions for *direct* access to elements of another table without searching or joining records. 

It is possible that many target elements satisfy the link criteria and then one of them is chosen as the output value. In the case no output element has been found, `null` is set as the output. There exist also other ways to define links, for example, by providing lambdas instead of declarative criteria.

## Accumulate columns

*Accumulate* columns are intended for data aggregation. In contrast to other columns, an output of an accumulate column is computed incrementally:

> For each input, an accumulate column computes its output by *updating* its current value several times for each element in another table which is mapped to this input by the specified grouping column.

It is important that a definition of an accumulate column involves two additional parameters:

* Link column from the fact table to this table (where the accumulate column is defined), called grouping column
* Table with the data being aggregated, called fact table (type of the link column)

How the data is being aggregated is specified in the *accumulate* or update function. This function has two major differences from calculate functions:

* Its parameters are read from the columns of the fact table - not this table (where the new column is being defined)
* It receives one additional parameters which is its own current output (resulted from the previous call to this function).

The function has to update its own current value using the parameters and return a new value (which it will receive next time).

If we want to count the number of events for each device then such a column can be defined as follows:

```java
Column counts = schema.createColumn("Event Count", things, objects);
counts.setDefaultValue(0.0); // It will be used as an initial value
counts.accumulate(
        link, // How to group facts
        (a,p) -> (Double)p[0] + 1.0 // How to accumulate/update
        // No additional parameters because we only count
);
```

Here the `link` column maps elements of the "EVENTS" table to elements of the "THINGS" table, and hence an element of "THINGS" (where we define the accumulate column) is a group of all elements of "EVENTS" which reference it via this column. For each element of "EVENTS", the specified accumulate function will be called and its result stored in the column output. Thus the accumulate function will be called as many times for each input of "THINGS", as it has facts that map to it.

## Numeric accumulation

Let us assume now that the "EVENTS" table has a property "Measure" and we want to numerically aggregate it (instead of simply counting):

```java
Column measure = schema.createColumn("Measure", things, objects);
measure.setValue(0, 1.0);
measure.setValue(1, 2.0);
measure.setValue(2, 3.0);
```

We can find the sum of the measure for each element in "THINGS" using this column definition:

```java
Column sums = schema.createColumn("Sum Measure", things, objects);
sums.setDefaultValue(0.0); // Start accumulation from this value
sums.accumulate(
        link, // Grouping column
        (a,p) -> (Double)a + (Double)p[0], // Add measure to the current aggregate
        measure // Measure
);

sums.evaluate();
value = sums.getValue(1); // 3 (1+2)
value = sums.getValue(2); // 3
```

## Rolling aggregation

*Rolling* columns are intended for rolling aggregation. Similar to accumulate columns, rolling columns also incrementally update the aggregate for each record belonging to the group. The difference is how groups are defined:

* Facts and groups are stored in one table while conventional aggregation uses two tables with facts and groups (and hence a link column is needed).
* Groups in rolling aggregation can overlap, that is, one (fact) element can belong to many groups and hence it will contribute to many aggregates.
* Group members (facts) can be characterized by their individual numerci degree of membership in the group which determines the strength or weight of their contribution to the aggregate. For instance, it is how exponential smoothing is computed.

> For each group element, a rolling column computes its output by *updating* its current value for each member of the group by taking into account its distance from the group center.

In the following example, a rolling column will aggregate the sum of this and half of the previous record by summing their values in the specified column:

```java
rollingColumn.roll(
        2, 0, // (2,0] - two previous records including this one
        (a,d,p) -> (Double)a + ((Double)p[0] / (d + 1)),
        measureColumn
);
```

The second parameter of the accumulate function is distance from this record. It is equal 0 for the current record (group center), 1 for the previous record and so on.
