# simple-algebra-lib
A simple algebra library. Currently a work in progress. Jars on Maven central should be working/useable.

Current features include:
- Outputting to mathml/html
- Outputing to prefix notation
- Performing fairly simple pattern matching and pattern based rewriting.
  - Basic identities such as commutativity/associativity of addition/multiplication implemented with pattern matching.
- Equals/hashcode implementation based on structure of expression.
  - In other words a + b equals b + a , but a + b does not equal a * b

```xml
<dependency>
  <groupId>io.github.pirocks</groupId>
  <artifactId>simple-algebra-lib</artifactId>
  <version>0.0.6</version>
</dependency>
```
 

