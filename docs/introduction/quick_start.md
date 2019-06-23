# Quick start

## Creating schema

First, it is necessary to create a *schema* object which is essentially a database:

```java
Schema schema = new Schema("My Schema");
```

The schema is then used to create and access all other elements as well as perform various operations with data.

## Creating tables

Tables are created within the schema by providing a unique name:

```java
Table things = schema.createTable("THINGS");
Table events = schema.createTable("EVENTS");
```

A table in the concept-oriented model is a mathematical set, that is, a number of (unique) values. In Prosto, all user-defined tables are sets of primitive values the structure of which cannot be changed. These values are of long type and are interpreted as row identifiers without any additional semantics.

There exist predefined *primitive tables* which consist of only primitive values. Currently, Prosto has one primitive table with the name `Object` which is a set of Java objects. It is impossible to create another table with this name or do any operations with this table.

Tables can be found by using their name:

```java
Table table = schema.getTable("THINGS");
Table objects = schema.getTable("Object"); // Primitive
```

Elements can be appended to a table and the returned result is their identifier:

```java
long id;
id = things.add(); // id = 0
id = things.add(); // id = 1
```

Elements are added and removed in the FIFO order, that is, the oldest element is always removed. The current range of valid identifiers can be retrieved using this method:

```java
Range range = table.getIdRange();
```

The `Range` object provides a start id (inclusive) and an end id (exclusive) for this table. These ids can be then used for data access using column objects.

Any table can be used as a *data type* for schema columns.

## Creating columns

Data in Prosto is stored in columns. Formally, a column is a function and hence it defines a mathematical *mapping* from all table inputs to the values in the output table. Input and output tables of a column are specified in the constructor:

```java
Column thingName = schema.createColumn("Name", things, objects);
```

This column defines a mapping from "THINGS" to the "Object" (primitive) table.

A new column does not have a definition and hence it cannot derive its output values. The only way to define their mapping is to explicitly set the output value for certain inputs using API:

```java
thingName.setValue(0, "fridge");
thingName.setValue(1, "oven");
Object value = thingName.getValue(1); // "oven"
```

## Column paths

A *column path* is a sequence of columns where each next column belongs to the type of the previous column. Column paths are analogous to dot notation in programming. For example, we could define a column object and then use it to *directly* access the number of events received from this same event device:

```java
ColumnPath path = new ColumnPath(link, accu);
value = path.getValue(0);
```

Many column definition methods accept column paths as parameters rather than simple column. 
