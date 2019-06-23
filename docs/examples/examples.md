# Prosto examples

This sub-project provides basic examples of using Prosto with sample data. More details can be found in the source files.

## Example 1: Basic operations

Source code: [Example1.java](https://github.com/prosto-project/prosto/blob/master/examples/src/main/java/prosto/examples/core/Example1.java)

Operations: calculate, link, accumulate

## Example 2: Compute sales for each product

Source code: [Example2.java](https://github.com/prosto-project/prosto/blob/master/examples/src/main/java/prosto/examples/core/Example2.java)

Operations: calculate, link, accumulate

Data set ds1: OrderItems.csv, Products.csv

## Example 3: Compute sales for each order

Source code: [Example3.java](https://github.com/prosto-project/prosto/blob/master/examples/src/main/java/prosto/examples/core/Example3.java)

Operations: product, calculate, project, accumulate

Data set ds1: OrderItems.csv

## Example 4: Compute sales for each product category

Source code: [Example4.java](https://github.com/prosto-project/prosto/blob/master/examples/src/main/java/prosto/examples/core/Example4.java)

Operations: product, calculate, link, project, accumulate

Data set ds1: OrderItems.csv, Products.csv

## Example 5: Moving average (also smoothed) of daily bitcoin prices

Source code: [Example5.java](https://github.com/prosto-project/prosto/blob/master/examples/src/main/java/prosto/examples/core/Example5.java)

Operations: calculate, roll

Data set ds3: BTC-EUR.csv

## Example 6: Compute volume weighted average price of bitcoin

Source code: [Example6.java](https://github.com/prosto-project/prosto/blob/master/examples/src/main/java/prosto/examples/core/Example6.java)

Operations: calculate, roll

Data set ds4: .krakenEUR.csv

## Example 7: Compute hourly bitcoin prices

Source code: [Example7.java](https://github.com/prosto-project/prosto/blob/master/examples/src/main/java/prosto/examples/core/Example7.java)

Operations: calculate, range, project, accumulate

Data set ds4: .krakenEUR.csv
