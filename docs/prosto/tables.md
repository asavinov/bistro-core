# Defining tables

## Product tables

When a new table is created, it by default has no definition and hence it will not participate in inference. The only way to populate such tables is to add or remove its elements using API. If we want to populate a table using data in other columns and tables then it has to be defined as a *product table*:

```java
myTable.product();
```

In addition, product tables must have one or more *key columns* of non-primitive type. They are defined as columns with no definition with an additional parameter specifying that it is a key column:

```java
Column myKey1 = schema.createColumn("Key1", myTable, T1);
myKey1.noop(true);
Column myKey2 = schema.createColumn("Key2", myTable, T2);
myKey2.noop(true);
```

Now the product table will be populated by all combinations of records currently stored in tables `T1` and `T2`.

## Where functions

Elements of a table can be filtered by defining a *where-function* which returns a boolean value.

> A table will store a record only if the where-function is true.

It is defined by providing a adder-function as well as the necessary parameters:

```java
myTable.where(
        p -> p[0] == 123 || p[1] == 456,
        myKey1, myKey2
);
```

(Currently, only key columns can be used in where-functions.)

## Project columns

> A *project column* is equivalent to a link column but in addition it appends a new record to the linked table if it has not been found

Thus project columns will always have some output value by linking to some existing record in the linked table. In contrast, if a link column does not find a record then its output is empty.

## Range tables

> A *range* table populates itself with records which represent intervals on an axis of certain type

A range table can be defined on a numeric axis. In this case, each its record will represent a numeric interval. For example, the following table will generate 5 numeric intervals starting from 10.0 and each interval having length 20.0:

```java
myTable.range(
        10.0, 20.0, 5L
);
```

It is also possible to define ranges of time durations and date periods.

A typical use of range tables is aggregation over (numeric or date) intervals using inequality conditions as opposed to aggregation over discrete values using equality of values as a condition belonging to a group.
