[![either](https://maven-badges.herokuapp.com/maven-central/io.github.jbock-java/either/badge.svg?subject=either)](https://maven-badges.herokuapp.com/maven-central/io.github.jbock-java/either)

The `Either` type is closely related to `Optional`, but can have different "failure" states, other than only *empty*.
An `Either` can be used to properly return error messages from stream operations,
or as a lightweight alternative to exceptions.

There are several popular libraries that offer an `Either` type,
including [vavr](https://github.com/vavr-io/vavr), [fugue](https://bitbucket.org/atlassian/fugue/src/master/), and [lambda](https://github.com/palatable/lambda).
This particular `Either` might be easier to work with, especially if you're already familiar with `Optional`.

