[![either](https://maven-badges.herokuapp.com/maven-central/io.github.jbock-java/either/badge.svg?subject=either)](https://maven-badges.herokuapp.com/maven-central/io.github.jbock-java/either)

The `Either` type is closely related to `Optional`, but can have different "failure" states, other than only *empty*.
`Either` can be used to collect error messages in stream operations,
or simply as a lightweight alternative to throwing an Exception.

There are several popular libraries that offer an `Either` type,
including [vavr](https://github.com/vavr-io/vavr), [fugue](https://bitbucket.org/atlassian/fugue/src/master/), and [lambda](https://github.com/palatable/lambda).
This particular `Either` is easy to work with if you're already familiar with `Optional`.

