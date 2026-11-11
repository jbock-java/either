[![either](https://img.shields.io/maven-central/v/io.github.jbock-java/either)](https://central.sonatype.com/artifact/io.github.jbock-java/either)

The `Either` type is related to `Optional`, but can have different "failure" states, other than only *empty*.
`Either` can be used to collect error messages
or simply as a typed alternative to exceptions.

There are several popular libraries that offer an `Either` type,
including [vavr](https://github.com/vavr-io/vavr), [fugue](https://bitbucket.org/atlassian/fugue/src/master/), and [lambda](https://github.com/palatable/lambda).

### empty to Left

It can sometimes be desirable to put *something* into the "empty" value of an `Optional`.
Let's call this "adding a *Left* value", since this value is no longer *empty*.
Going from Optional to Either is as easy as mapping with `Either::right`,
followed by `orElseGet` to supply the Left value:

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

`Either` has the `map` and `filter` methods, which are familiar from `Optional`.
These return a *Left* value unchanged.

There is `ifLeftOrElse` and `fold`,
as well as `getRightOrElseThrow()` and `getLeftOrElseThrow()` to get the value.

### Working with streams

Given a stream of eithers, you can use the built-in collectors
`firstFailure` or `allFailures` to search for *Left* values:

````java
Either<BigInteger, List<BigInteger>> twoPrimesOrOneComposite = Stream.generate(() ->
        ThreadLocalRandom.current().nextInt(1000))
        .map(BigInteger::valueOf)
        .limit(2)
        .<Either<BigInteger, BigInteger>>map(n -> n.isProbablePrime(10) ?
                Either.right(n) : Either.left(n))
        .collect(Eithers.firstFailure());
````

### Sample code

The [jbock](https://github.com/jbock-java/jbock) compiler uses either internally.

### Either gang

* [spencerwi](https://github.com/spencerwi/Either.java)
* [ambivalence](https://github.com/poetix/ambivalence)
* [derive4j](https://github.com/derive4j/derive4j)
* [mediascience](https://github.com/mediascience/java-either)
* [pragmatica](https://github.com/siy/pragmatica)
* [gradle's internal either](https://github.com/gradle/gradle/tree/master/subprojects/functional)
* [dichotomy](https://github.com/xyzsd/dichotomy)

