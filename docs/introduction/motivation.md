# Motivation

## What is Prosto: a data processing engine

Prosto is a light-weight column-oriented *data processing engine* which radically changes the way data is processed. As a *general-purpose* data processing engine, Prosto can be applied to such problems like big data processing, data integration, data migration, extract-transform-load (ETL), stream analytics, IoT analytics. Prosto is based on a novel data model and is an alternative to map-reduce, conventional SQL-like languages and other set-oriented approaches.

## How it works: a novel data processing paradigm

At its core, Prosto relies on a novel *column-oriented* logical data model which describes data processing as a DAG of *column operations* as opposed to having only set operations in conventional approaches. Computations in Prosto are performed by *evaluating* column definitions. Each definition describes how this column output values are expressed in terms of other columns. Currently Prosto provides three column definition (operation) types:

* calculate - roughly corresponds to the Map and SQL select operations
* link [3] - roughly corresponds to the join operation
* accumulate [1] - a column-oriented analogue of Group-by and Reduce
* roll - rolling aggregation using accumulate functions

Prosto is a major alternative to most other data models and data processing frameworks which are based on table (set) operations including SQL-like languages and MapReduce. In set-oriented approaches, data is being processed by producing new sets (tables, collections etc.) from the data stored in other sets by applying various set operations like join, group-by, filter, map or reduce. In contrast, Prosto processes data by producing new columns from existing columns by applying function operations.

## Benefits of Prosto

Here are some benefits of Prosto and the underlying column-oriented data processing model:

* Prosto does not use such operations as join and group-by which are known to be error-prone, difficult to comprehend, require high expertise and might be inefficient when applied to analytical data processing workloads.
* The use of column definitions makes Prosto similar to conventional spreadsheets, which are known to be rather intuitive and easy to use for data processing. The difference from spreadsheets is that Prosto uses column definitions instead of cell formulas.
* The use of columnar physical representation is known to be faster for analytical data processing workloads.
* The use of column operations can provide additional performance improvement in comparison to the use of set operations because the latter essentially copy significant portions of data between set. Prosto avoids such unnecessary copy operations by using column operations so that no new sets are created without necessity.
* Prosto uses the conception of evaluation (of column and table definitions) instead of query execution. This can provide significant performance improvements in the case of incremental updates when only some part of the database is changed (for example, new events have been received from a stream). Prosto ensures that only the necessary changes are propagated through the database to other elements via inference.

## Why column-orientation?

### Calculating data

One of the simplest data processing operations is computing a new attribute using already existing attributes. For example, if we have a table with order `Items` each characterized by `Quantity` and `Price` then we could compute a new attribute `Amount` as their arithmetic product:

```sql
SELECT *, Quantity * Price AS Amount FROM Items
```

Although this wide spread data processing pattern may seem very natural and almost trivial it does have one significant flaw: 

> the task was to compute a new *attribute* while the query produces a new *table*

Although the result table does contain the required attribute, the question is why not to do exactly what has been requested? Why is it necessary to produce a new table if we actually want to compute only an attribute?

The same problem exists in MapReduce. If our goal is to compute a new field then we apply the map operation which will emit completely new objects each having this new field. Here again the same problem: our intention was not to create a new collection with new objects – we wanted to add a new computed property to already existing objects. However, the data processing framework forces us to describe this task in terms of operations with collections. We simply do not have any choice because these data models provide only sets and set operations and the only way to add a new attribute is to produce a set with this attribute.

An alternative approach consists in using column operations for data transformations and then we could do exactly what is requested: adding (calculated) attributes to existing tables.

### Linking data

Another wide spread task consists in computing links or references between different tables: given an element of one table, how can I access attributes in a related table? For example, assume that `Price` is not an attribute of the `Items` table as in the above example but rather it is an attribute of a `Products` table. Here we have two tables, `Items` and `Products`, with attributes `ProductId` and `Id`, respectively, which relate their records. If now we want to compute the amount for each item then the price needs to be retrieved from the related `Products` table. The standard solution is to copy the necessary attributes into a *new table* by using the relational (left) join operation for matching the records:

```sql
SELECT item.*, product.Price FROM Items item
JOIN Products product ON item.ProductId = product.Id
```

This new result table can be now used for computing the amount precisely as we described earlier because it has the necessary attributes copied from the two source tables. Let us again compare this solution with the problem formulation. Do we really need a new table? No. Our goal was to have a possibility to access attributes from the second `Products` table (while computing a new attribute in the first table). Hence it again can be viewed as a workaround rather than a solution:

> a new set is produced just because there is no possibility not to produce it while it is not needed for the solution

A principled solution to this problem is a data model which uses column operations for data processing so that a link can be defined as a new column in an existing table [3].

### Aggregating data

Assume that for each product, we want to compute the total number of items ordered. This task can be solved using group-by operation:

```sql
SELECT ProductId, COUNT(ProductID) AS TotalQuantity
FROM Items GROUP BY ProductId
```

Here again see the same problem:

> a new unnecessary *table* is produced although the goal is to produce a new (aggregated) attribute in an existing table

Indeed, what we really want is to add a new attribute to the `Products` table which would be equivalent to all other attributes (like product `Price` used in the previous example). This `TotalQuantity` could be then used to compute some other properties of products. Of course, this also can be done using set operations in SQL but then we will have to again use join to combine the group-by result with the original `Products` table followed by producing yet another table with new calculated attributes. It is apparently not how it should work in a good data model because the task formulation does not mention and does not actually require any new tables - only attributes. Thus we see that the use of set operations in this and above cases is a problem-solution mismatch.

A solution to this problem again is provided by a column oriented data model where aggregated columns can be defined without adding new tables [1].
