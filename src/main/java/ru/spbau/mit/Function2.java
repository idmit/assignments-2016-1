package ru.spbau.mit;

/**
 * Created by idmit on 15/03/2016.
 */
public abstract class Function2<T, U, R> {
    public abstract R apply(T t, U u);

    public <M> Function2<T, U, M> compose(final Function1<? super R, ? extends M> g) {
        return new Function2<T, U, M>() {
            @Override
            public M apply(T t, U u) {
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

    public Function1<T, Function1<U, R>> curry() {
        return new Function1<T, Function1<U, R>>() {
            @Override
            public Function1<U, R> apply(final T t) {
                return new Function1<U, R>() {
                    @Override
                    public R apply(U u) {
                        return Function2.this.apply(t, u);
                    }
                };
            }
        };
    }

}
