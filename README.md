[![either](https://maven-badges.herokuapp.com/maven-central/io.github.jbock-java/either/badge.svg?subject=either)](https://maven-badges.herokuapp.com/maven-central/io.github.jbock-java/either)

The `Either` type is closely related to `Optional`, but can have different "failure" states, other than only *empty*.
`Either` can be used to collect error messages in stream operations,
or simply as a lightweight alternative to throwing an Exception.

There are several popular libraries that offer an `Either` type,
including [vavr](https://github.com/vavr-io/vavr), [fugue](https://bitbucket.org/atlassian/fugue/src/master/), and [lambda](https://github.com/palatable/lambda).
This particular `Either` is lightweight, and very easy to work with if you're already familiar with `Optional`.

### empty to Left

Sometimes, it can be desirable to put *something* into the "empty" value of an `Optional`.
Let's call this "adding a *Left* value", since this value is no longer *empty*.
Going from Optional to Either is as easy as mapping with `Either::right`,
followed by an `orElseGet` to supply the Left value:

````java
Either<String, BigInteger> possiblyPrime = Stream.generate(() -> 
                ThreadLocalRandom.current().nextInt(1000))
        .map(BigInteger::valueOf)
        .limit(10)
        .filter(n -> n.isProbablePrime(10))
        .findAny()                                      // Optional<BigInteger>
        .<Either<String, BigInteger>>map(Either::right) // Optional<Either<String, BigInteger>>
        .orElseGet(() -> Either.left("no such value")); // Either<String, BigInteger>
````

Repeating the result type in the penultimate line is necessary, due to a limitation of Java's typechecker.

### Working with Either

`Either` has the familiar methods from `Optional`: `map`, `flatMap` and `filter`.
These will always return a *Left* value unchanged,
just like the corresponding methods in `Optional`, which return an *empty* value unchanged.

Symmetrically there are `mapLeft`, `flatMapLeft` and `filterLeft`, which return a *Right* value unchanged.

Finally there is ~~ifPresentOrElse~~ `ifLeftOrElse` (1.3) and the all-powerful `fold` method,
as well as `getRight` and `getLeft` to convert back to `Optional`.

### Working with streams

If you have a stream of `Either`, you can search for *Left* values with custom collectors
`firstFailure` or `allFailures`:

````java
Either<BigInteger, List<BigInteger>> twoPrimesOrOneComposite = Stream.generate(() ->
        ThreadLocalRandom.current().nextInt(1000))
        .map(BigInteger::valueOf)
        .limit(2)
        .<Either<BigInteger, BigInteger>>map(n -> n.isProbablePrime(10) ?
                Either.right(n) : Either.left(n))
        .collect(Eithers.firstFailure());
````

### Testimonies

This library grew for several months as part of the [jbock](https://github.com/jbock-java/jbock) project,
until it was released independently. jbock uses it internally to perform input validation,
and its generated `parse` method returns an `Either`.

### Either gang

* [spencerwi](https://github.com/spencerwi/Either.java)
* [ambivalence](https://github.com/poetix/ambivalence)
* [derive4j](https://github.com/derive4j/derive4j)
* [mediascience](https://github.com/mediascience/java-either)
* [pragmatica](https://github.com/siy/pragmatica)
* [gradle's internal either](https://github.com/gradle/gradle/tree/master/subprojects/functional)

