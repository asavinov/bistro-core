.. Prosto documentation master file, created by
   sphinx-quickstart on Sat Jun 30 11:29:56 2018.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

Welcome to Prosto's documentation!
==================================

About Prosto
````````````

The goal of this project is to implement a novel general-purpose *data processing* technology which radically differs from most of the existing approaches. Shortly, it can be viewed as a major alternative to set-orientation like SQL and MapReduce.

Prosto makes columns first-class elements of data processing equal to tables. Formally, columns are treated as functions and tables are treated as sets. Thus Prosto relies on operations with functions as opposed to manipulating sets in existing approaches.

.. toctree::
   :maxdepth: 1
   :caption: Introduction

   introduction/motivation
   introduction/quick_start
   introduction/faq
   introduction/references
   introduction/license

.. toctree::
   :maxdepth: 2
   :caption: Documentation

   prosto/tables
   prosto/columns
   prosto/evaluation

.. toctree::
   :maxdepth: 2
   :caption: Examples

   examples/examples
