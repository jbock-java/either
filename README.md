[![either](https://maven-badges.herokuapp.com/maven-central/io.github.jbock-java/either/badge.svg?subject=either)](https://maven-badges.herokuapp.com/maven-central/io.github.jbock-java/either)

### java::either

`Either` is a close relative of `Optional`, but can have other "failure" states than only *empty*.
It has many useful applications. For example, reporting error conditions from stream operations.

There are various good alternatives,
like [vavr](https://github.com/vavr-io/vavr), [fugue](https://bitbucket.org/atlassian/fugue/src/master/), and [lambda](https://github.com/palatable/lambda).
This particular project emphasizes interoperability with `Optional`.
Since `java.util.Optional` can't be extended, it comes with its own copy `io.jbock.util.Optional`,
with two additional methods `orElseLeft()` and `flatMapLeft()`.

