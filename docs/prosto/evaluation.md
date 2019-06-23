# Schema evaluation

All columns in the schema are evaluated using the following method call:

```java
schema.evaluate();
```

A column can be evaluated individually, for example, if its definition has been changed:

```java
calc.evaluate();
```

Prosto manages all dependencies and it will automatically (recursively) evaluate all columns this column depends on (if necessary). If a column data has been modified or its definition has changed then it will also influence all columns that depend on it directly or indirectly. 

Now, if there were no errors, we can retrieve the output values:

```java
value = calc.getValue(0); // value = 6
value = calc.getValue(1); // value = 4

value = link.getValue(0); // value = 1
value = link.getValue(1); // value = 0
value = link.getValue(2); // value = 1

value = counts.getValue(0); // 1 event from fridge
value = counts.getValue(1); // 2 events from oven
```
