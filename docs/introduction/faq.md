# Frequently asked questions

## What is Prosto intended for?

The main general goal of Prosto is *data processing*. By data processing we mean deriving new data from existing data.

## What kind of data can Prosto process?

Prosto assumes that data is represented as a number of *sets* of elements. Each *element* is a tuple which is a combination of column values. A *value* can be any (Java) object.

## How does Prosto process data?

Tables and columns in Prosto may have *definitions*. A table definition specifies how elements of this set are produced (inferred or derived) from elements of other sets. A column definition specifies how the values of this column are computed from the values of other columns (in this or other tables). Table and column definitions in Prosto are analogous to queries in conventional DBMS.

## How is Prosto positioned among other data processing technologies?

We can distinguish between two big approaches:
* set-oriented or set theoretic approaches rely on sets for representing data and set operations for manipulating data (inference)
* column-oriented or functional approaches rely on functions for representing data and operations with functions for manipulating data (inference)

The following table shows some typical technologies and how they are positioned along the two dimensions:

|  | Column-oriented | Set-oriented
--- | --- | ---
Data models (logical) | Functional, ODM, Concept-oriented model | Relational model
Data stores (physical) | Vertica, SAP HANA etc. | Classical DBMSs
Data processing (batch) | **Prosto Core** | MapReduce, SQL
Stream processing | **Prosto Streams** | Kafka Streams, Spark Streaming, Flink etc.

Notes:
* This table is a very rough representation because many technologies have significant overlaps 
* Prosto and the concept-oriented model do support set operations. They simply shift priority from set operations to column operations

## (MD)How Prosto is positioned among other data processing technologies?

We can distinguish between two big approaches:
* set-oriented or set theoretic approaches rely on sets for representing data and set operations for manipulating data (inference)
* column-oriented or functional approaches rely on functions for representing data and operations with functions for manipulating data (inference)

The following table shows some typical technologies and how they are positioned along the two dimensions:

```
=======================|========================================|======================================
                       | Column-oriented                        | Set-oriented
=======================|========================================|======================================
Data models (logical)  | Functional, ODM, Concept-oriented model| Relational model
Data stores (physical) | Vertica, SAP HANA etc.                 | Classical DBMSs
Data processing (batch)| **Prosto Engine**                      | MapReduce, SQL
Stream processing      | **Prosto Streams**                     | Kafka Streams, Spark Streaming, Flink
=======================|========================================|======================================
```

Notes:
* This table is a very rough representation because many technologies have significant overlaps 
* Prosto and the concept-oriented model do support set operations. They simply shift priority from set operations to column operations

## Does Prosto have a query language?

No, Prosto does not provide any query language. Instead, Prosto uses definitions which are *evaluated* against the data as opposed to executing a query. These definitions (in contrast to queries) are integral part of the database. A table or column with a definition is treated equally to all other tables and columns. It is similar to defining views in a database which can be updated if some data changes.

Prosto has a sub-project called prosto-formula which is intended for supporting expression languages instead of native Java code. Yet, such expressions (similar to Excel formulas) will be translated into native code in column definitions.

## What are unique features of Prosto?

Prosto relies on column definitions and much less uses table definitions. In contrast, most traditional approaches (including SQL and, map-reduce) use set operations for data transformations.

## What are benefits of column-orientation?

Describing data processing logic using column operations can be much more natural and simpler in many scenarios (for the same reason why spreadsheets are). In particular, Prosto does not use joins and group-by which are known to be difficult to understand and use but which are very hard to get rid of. Logical column operations are also naturally mapped to physical column operations.

## What is the formal basis of Prosto?

Prosto relies on the *concept-oriented model* (COM) [2] where the main unit of representation and processing is a *function* as opposed to using only sets in the relational and other set-oriented models. Data in this model is stored in functions (mappings between sets) and it provides operations for computing new functions from existing functions. COM supports set operations but they have weaker role in comparison to set-oriented models.
