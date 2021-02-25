package impl;


import interfaces.*;


public interface Stream<T> {


    <R> MyStream<R> map(Function<T,R> mapper);


    <R> MyStream<R> flatMap(Function<T, ? extends MyStream<R>> mapper);


    MyStream<T> filter(Predicate<T> predicate);


    MyStream<T> limit(int n);


    MyStream<T> distinct();


    MyStream<T> peek(ForEach<T> consumer);


    void forEach(ForEach<T> consumer);

    /**
     * 浓缩
     * */
    <R> R reduce(R initVal, BiFunction<R, R, T> accumulator);

    /**
     * 收集
     * */
    <R, A> R collect(Collector<T,A,R> collector);

    /**
     * 最大值
     * */
    T max(Comparator<T> comparator);

    /**
     * 最小值
     * */
    T min(Comparator<T> comparator);


    int count();


    boolean anyMatch(Predicate<? super T> predicate);

    boolean allMatch(Predicate<? super T> predicate);

}
