### java::either

`Either` is a close relative of `Optional`, but can have other "failure" states than only *empty*.
It has many useful applications, like reporting error conditions from stream operations.

There are various good alternatives,
like [vavr](https://github.com/vavr-io/vavr), [fugue](https://bitbucket.org/atlassian/fugue/src/master/), and [lambda](https://github.com/palatable/lambda).
This particular project emphasizes interoperability with `Optional`.
Since `Optional` can't be extended, it comes with its own copy of `java.util.Optional`,
with two additional methods `orElseLeft()` and `flatMapLeft()`.

