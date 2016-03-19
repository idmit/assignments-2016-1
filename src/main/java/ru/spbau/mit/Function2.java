package ru.spbau.mit;

public abstract class Function2<T, U, R> {
    public abstract R apply(T t, U u);

    // Extra type-parameter allows saving to a variable of
    // wider type than a function's return type. Alternatives:
    // 1) Types should match exactly:
    // public <M> Function2<T, U, M> compose(final Function1<? super R, M> g)
    // 2) Needs explicit specification:
    // public <M> Function2<T, U, M> compose(final Function1<? super R, ? extends M> g)
    public <X, M extends X> Function2<T, U, X> compose(final Function1<? super R, M> g) {
        return new Function2<T, U, X>() {
            @Override
            public X apply(T t, U u) {
                return g.apply(Function2.this.apply(t, u));
            }
        };
    }

    public Function1<U, R> bind1(final T t) {
        return new Function1<U, R>() {
            @Override
            public R apply(U u) {
                return Function2.this.apply(t, u);
            }
        };
    }

    public Function1<T, R> bind2(final U u) {
        return new Function1<T, R>() {
            @Override
            public R apply(T t) {
                return Function2.this.apply(t, u);
            }
        };
    }

    // Extra type-parameter allows saving to a variable of
    // narrower type in terms of function's arguments.
    public <X extends T, Y extends U> Function1<X, Function1<Y, R>> curry() {
        return new Function1<X, Function1<Y, R>>() {
            @Override
            public Function1<Y, R> apply(final X x) {
                return new Function1<Y, R>() {
                    @Override
                    public R apply(Y y) {
                        return Function2.this.apply(x, y);
                    }
                };
            }
        };
    }

}
