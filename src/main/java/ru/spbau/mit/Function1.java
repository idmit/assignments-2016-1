package ru.spbau.mit;

public abstract class Function1<T, R> {
    public abstract R apply(T t);

    // Extra type-parameter allows saving to a variable of
    // wider type than a function's return type. Alternatives:
    // 1) Types should match exactly:
    // public <M> Function1<T, M> compose(final Function1<? super R, M> g)
    // 2) Needs explicit specification:
    // public <M> Function1<T, M> compose(final Function1<? super R, ? extends M> g)
    public <X, M extends X> Function1<T, X> compose(final Function1<? super R, M> g) {
        return new Function1<T, X>() {
            @Override
            public X apply(T t) {
                return g.apply(Function1.this.apply(t));
            }
        };
    }
}
