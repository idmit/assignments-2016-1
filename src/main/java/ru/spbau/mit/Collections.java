package ru.spbau.mit;

import java.util.ArrayList;
import java.util.List;

public final class Collections {
    private Collections() {
    }

    public static <T, R> Iterable<R> map(Function1<? super T, ? extends R> fun, Iterable<T> data) {
        List<R> transformedData = new ArrayList<>(0);

        for (T elem : data) {
            transformedData.add(fun.apply(elem));
        }

        return transformedData;
    }

    // Extra type-parameter allows saving to a variable of
    // wider type in terms of elements.
    public static <T extends R, R> Iterable<R> filter(Predicate<? super T> p, Iterable<T> data) {
        List<R> filteredData = new ArrayList<>(0);

        for (T elem : data) {
            if (p.apply(elem)) {
                filteredData.add(elem);
            }
        }

        return filteredData;
    }

    // Extra type-parameter allows saving to a variable of
    // wider type in terms of elements.
    public static <T extends R, R> Iterable<R> takeWhile(Predicate<? super T> p, Iterable<T> data) {
        List<R> shortenedData = new ArrayList<>(0);

        // Somehow can't be covered by tests because of generated byte code.
        for (T elem : data) {
            if (!p.apply(elem)) {
                break;
            }
            shortenedData.add(elem);
        }

        return shortenedData;
    }

    // Extra type-parameter allows saving to a variable of
    // wider type in terms of elements.
    public static <T extends R, R> Iterable<R> takeUnless(Predicate<? super T> p, Iterable<T> data) {
        return takeWhile(p.not(), data);
    }

    // Extra type-parameter allows saving to a variable of
    // wider type in terms of result.
    public static <A extends R, T, R> R foldl(Function2<? super A, ? super T, ? extends A> fun,
                                              A z, Iterable<T> data) {
        for (T elem : data) {
            z = fun.apply(z, elem);
        }

        return z;
    }

    // Extra type-parameter allows saving to a variable of
    // wider type in terms of result.
    public static <A extends R, T, R> R foldr(Function2<? super T, ? super A, ? extends A> fun,
                                              A z, Iterable<T> data) {
        List<T> elements = new ArrayList<>(0);

        for (T elem : data) {
            elements.add(elem);
        }

        for (int k = elements.size() - 1; k >= 0; k--) {
            z = fun.apply(elements.get(k), z);
        }

        return z;
    }
}
