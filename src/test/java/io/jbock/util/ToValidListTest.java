package io.jbock.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collector;

import static io.jbock.util.Either.left;
import static io.jbock.util.Either.right;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ToValidListTest {

    @Test
    void associativityTest() {
        checkAssociativity(right(1), right(2));
        checkAssociativity(left("1"), right(2));
        checkAssociativity(right(1), left("2"));
        checkAssociativity(left("1"), left("2"));
    }

    @Test
    void testFirstLeft() {
        assertEquals(left("1"), apply(List.of(left("1"), right(2), left("3"))));
        assertEquals(left("1"), apply(List.of(left("1"), left("2"), right(3))));
        assertEquals(left("2"), apply(List.of(right(1), left("2"), left("3"))));
        assertEquals(left("3"), apply(List.of(right(1), right(2), left("3"))));
    }

    @Test
    void testRight() {
        assertEquals(right(List.of(1, 2, 3)), apply(List.of(right(1), right(2), right(3))));
    }

    private Either<String, List<Integer>> apply(List<Either<String, Integer>> data) {
        return data.stream().collect(Eithers.firstFailure());
    }

    private void checkAssociativity(Either<String, Integer> t1, Either<String, Integer> t2) {
        @SuppressWarnings("unchecked")
        Collector<Either<? extends String, ? extends Integer>, Eithers.Acc<String, ?, Integer>, Either<String, List<Integer>>> coll =
                (Collector<Either<? extends String, ? extends Integer>, Eithers.Acc<String, ?, Integer>, Either<String, List<Integer>>>) Eithers.<String, Integer>firstFailure();

        Eithers.Acc<String, ?, Integer> a1 = coll.supplier().get();
        coll.accumulator().accept(a1, t1);
        coll.accumulator().accept(a1, t2);
        Either<String, List<Integer>> r1 = coll.finisher().apply(a1);// result without splitting

        Eithers.Acc<String, ?, Integer> a2 = coll.supplier().get();
        coll.accumulator().accept(a2, t1);
        Eithers.Acc<String, ?, Integer> a3 = coll.supplier().get();
        coll.accumulator().accept(a3, t2);
        Either<String, List<Integer>> r2 = coll.finisher().apply(coll.combiner().apply(a2, a3));// result with splitting
        assertEquals(r1, r2);
    }
}
