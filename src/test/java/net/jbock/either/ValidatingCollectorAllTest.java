package net.jbock.either;

import org.junit.jupiter.api.Test;

import java.util.List;

import static net.jbock.either.Either.left;
import static net.jbock.either.Either.right;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidatingCollectorAllTest {

    @Test
    void associativityTest() {
        checkAssociativity(right(1), right(2));
        checkAssociativity(left("1"), right(2));
        checkAssociativity(right(1), left("2"));
        checkAssociativity(left("1"), left("2"));
    }

    @Test
    void testFirstLeft() {
        assertEquals(left(List.of("1", "3")), apply(List.of(left("1"), right(2), left("3"))));
        assertEquals(left(List.of("1", "2")), apply(List.of(left("1"), left("2"), right(3))));
        assertEquals(left(List.of("2", "3")), apply(List.of(right(1), left("2"), left("3"))));
        assertEquals(left(List.of("3")), apply(List.of(right(1), right(2), left("3"))));
    }

    @Test
    void testRight() {
        assertEquals(right(List.of(1, 2, 3)), apply(List.of(right(1), right(2), right(3))));
    }

    private Either<List<String>, List<Integer>> apply(List<Either<String, Integer>> data) {
        return data.stream().collect(new ValidatingCollectorAll<>());
    }

    private void checkAssociativity(Either<String, Integer> t1, Either<String, Integer> t2) {
        ValidatingCollectorAll<String, Integer> coll = new ValidatingCollectorAll<>();

        ValidatingCollectorAll.Acc<String, Integer> a1 = coll.supplier().get();
        coll.accumulator().accept(a1, t1);
        coll.accumulator().accept(a1, t2);
        Either<List<String>, List<Integer>> r1 = coll.finisher().apply(a1);// result without splitting

        ValidatingCollectorAll.Acc<String, Integer> a2 = coll.supplier().get();
        coll.accumulator().accept(a2, t1);
        ValidatingCollectorAll.Acc<String, Integer> a3 = coll.supplier().get();
        coll.accumulator().accept(a3, t2);
        Either<List<String>, List<Integer>> r2 = coll.finisher().apply(coll.combiner().apply(a2, a3));// result with splitting
        assertEquals(r1, r2);
    }
}