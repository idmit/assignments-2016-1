package ru.spbau.mit;

public abstract class Predicate<T> extends Function1<T, Boolean> {
    public static final Predicate<Object> ALWAYS_TRUE = new Predicate<Object>() {
        @Override
        public Boolean apply(Object o) {
            return true;
        }
    };

    public static final Predicate<Object> ALWAYS_FALSE = new Predicate<Object>() {
        @Override
        public Boolean apply(Object o) {
            return false;
        }
    };

    @Override
    public abstract Boolean apply(T t);

    // Extra type-parameter allows saving to a variable of
    // narrower type in terms of function's argument.
    public <U extends T> Predicate<U> and(final Predicate<? super T> p) {
        return new Predicate<U>() {
            @Override
            public Boolean apply(U u) {
                return Predicate.this.apply(u) && p.apply(u);
            }
        };
    }

    // Extra type-parameter allows saving to a variable of
    // narrower type in terms of function's argument.
    public <U extends T> Predicate<U> or(final Predicate<? super T> p) {
        return new Predicate<U>() {
            @Override
            public Boolean apply(U u) {
                return Predicate.this.apply(u) || p.apply(u);
            }
        };
    }

    // Extra type-parameter allows saving to a variable of
    // narrower type in terms of function's argument.
    public <U extends T> Predicate<U> not() {
        return new Predicate<U>() {
            @Override
            public Boolean apply(U u) {
                return !Predicate.this.apply(u);
            }
        };
    }
}
