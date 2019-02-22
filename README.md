# <a href="http://hrorm.org">hrorm</a>


Hrorm is a concise, declarative, opinionated, type-checked library for the creation of Data Access 
Objects (DAOs) that will not inflict your codebase with XMLosis or annotationitis.

The <a href="http://hrorm.org">hrorm</a> website contains documentation for using
it as well as detailed descriptions of the hows and whys of hrorm and its ethos.

### Hacking

There is not much to say here.
The code builds with maven.
Hrorm itself has no dependencies.
To build and run the tests requires only a few things and the tests run against 
an in-memory database.
There is not much code.
Once you clone it, you're basically ready to go.

Hrorm requires Java 8.

### Improvement Ideas and Questions

* Where object improvements:
    * Think about how isNull and isNotNull works, maybe something better can be found
    * Is creating strange statements like "a OR b AND c" desirable? Should it be prohibited somehow?
    * Can we use the column types (or field types) to improve type checking when building where clauses?
* Add support for more Java types
* DaoBuilder classes do not share code very well. Could it be improved?
* Support different types, e.g. String GUIDs, for primary keys
* Add methods that allow for updates and deletes based on Where objects
* Add support for boolean fields backed by strings or ints
* Add tests and make sure case insensitivy for columns