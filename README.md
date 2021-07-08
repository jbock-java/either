[![either](https://maven-badges.herokuapp.com/maven-central/io.github.jbock-java/either/badge.svg?subject=either)](https://maven-badges.herokuapp.com/maven-central/io.github.jbock-java/either)

### `either:1.1`

The `Either` type is closely related to `Optional`, but can have different "failure" states, other than only *empty*.
`Either` can be used to collect error messages in stream operations,
or simply as a lightweight alternative to throwing an Exception.

There are several popular libraries that offer an `Either` type,
including [vavr](https://github.com/vavr-io/vavr), [fugue](https://bitbucket.org/atlassian/fugue/src/master/), and [lambda](https://github.com/palatable/lambda).
This particular `Either` is very small (around 400 SLOC), and easy to work with if you're already familiar with `Optional`.

### empty to Left

Sometimes, it can be desirable to put *something* into the "empty" value of an `Optional`.
Let's call this "adding a *Left* value", since this value is no longer *empty*.
Adding a Left value is as easy as mapping with `Either::right`,
followed by an `orElseGet` to supply the Left value:

````java
Either<String, BigInteger> possiblyPrime = Stream.generate(() -> 
                ThreadLocalRandom.current().nextInt(1000))
        .map(BigInteger::valueOf)
        .limit(10)
        .filter(n -> n.isProbablePrime(10))
        .findAny()
        .<Either<String, BigInteger>>map(Either::right)
        .orElseGet(() -> left("my Left value"));
````

Declaring the result type before the `map` operation is necessary, due to limitations of Java's typechecker.

### Working with Either

An Either has the familiar methods `map`, `flatMap`, `filter`, `accept` and `orElseThrow`.
All of these work on *Right* Eithers, and leave a *Left* unchanged.
This is intuitive, because the corresponding `Optional` methods leave *empty* unchanged.
There are also `mapLeft`, `flatMapLeft` and `acceptLeft` methods,
which leave a Right unchanged.

Finally there is the all-powerful `fold` method, and `getRight` to get the original `Optional` back.

### Testimonies

This library grew for several years as part of the [jbock](https://github.com/jbock-java/jbock) project,
until it was released independently. `jbock:5.3` uses it internally to perform input validation.
Jbock's generated `parse` method returns an `Either`, too.

