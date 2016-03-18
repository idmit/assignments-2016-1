package ru.spbau.mit;

/**
 * Created by idmit on 15/03/2016.
 */
public abstract class Function1<T, R> {
    public abstract R apply(T t);

    public <M> Function1<T, M> compose(final Function1<? super R, ? extends M> g) {
        return new Function1<T, M>() {
            @Override
            public M apply(T t) {
                return g.apply(Function1.this.apply(t));
            }
        };
    }
}
