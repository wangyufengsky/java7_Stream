package impl;


import interfaces.*;

import java.util.HashSet;
import java.util.Set;

/**
 * stream实现
 */
public class MyStream<T> implements Stream<T> {

    /**
     * 流的头
     * */
    private T head;

    /**
     * 流的下一项
     * */
    private NextItemEvalProcess nextItemEvalProcess;

    /**
     * 是否是流的结尾
     * */
    private boolean isEnd;

    public static class Builder<T>{
        private final MyStream<T> target;

        public Builder() {
            this.target = new MyStream<>();
        }

        public Builder<T> head(T head){
            target.head = head;
            return this;
        }

        Builder<T> isEnd(boolean isEnd){
            target.isEnd = isEnd;
            return this;
        }

        public Builder<T> nextItemEvalProcess(NextItemEvalProcess<T> nextItemEvalProcess){
            target.nextItemEvalProcess = nextItemEvalProcess;
            return this;
        }

        public MyStream<T> build(){
            return target;
        }
    }

    //=================================API接口实现==============================

    @Override
    public <R> MyStream<R> map(final Function<T, R> mapper) {
        final NextItemEvalProcess<T> lastNextItemEvalProcess = this.nextItemEvalProcess;
        this.nextItemEvalProcess = new NextItemEvalProcess<>(
                new EvalFunction<R>() {
                    @Override
                    public MyStream<R> apply() {
                        MyStream<T> myStream = lastNextItemEvalProcess.eval();
                        return map(mapper, myStream);
                    }
                }
        );
        return new Builder<R>()
                .nextItemEvalProcess(this.nextItemEvalProcess)
                .build();
    }

    @Override
    public <R> MyStream<R> flatMap(final Function<T,? extends MyStream<R>> mapper) {
        final NextItemEvalProcess<T> lastNextItemEvalProcess = this.nextItemEvalProcess;
        this.nextItemEvalProcess = new NextItemEvalProcess<>(
                new EvalFunction<R>() {
                    @Override
                    public MyStream<R> apply() {
                        MyStream<T> myStream = lastNextItemEvalProcess.eval();
                        return flatMap(mapper, MyStream.<R>makeEmptyStream(), myStream);
                    }
                }
        );
        return new Builder<R>()
            .nextItemEvalProcess(this.nextItemEvalProcess)
            .build();
    }

    @Override
    public MyStream<T> filter(final Predicate<T> predicate) {
        final NextItemEvalProcess<T> lastNextItemEvalProcess = this.nextItemEvalProcess;
        this.nextItemEvalProcess = new NextItemEvalProcess<>(
                new EvalFunction<T>() {
                    @Override
                    public MyStream<T> apply() {
                        MyStream<T> myStream = lastNextItemEvalProcess.eval();
                        return filter(predicate, myStream);
                    }
                }
        );
        return this;
    }

    @Override
    public MyStream<T> limit(final int n) {
        final NextItemEvalProcess<T> lastNextItemEvalProcess = this.nextItemEvalProcess;
        this.nextItemEvalProcess = new NextItemEvalProcess<>(
                new EvalFunction<T>() {
                    @Override
                    public MyStream<T> apply() {
                        MyStream<T> myStream = lastNextItemEvalProcess.eval();
                        return limit(n, myStream);
                    }
                }
        );
        return this;
    }

    @Override
    public MyStream<T> distinct() {
        final NextItemEvalProcess<T> lastNextItemEvalProcess = this.nextItemEvalProcess;
        this.nextItemEvalProcess = new NextItemEvalProcess<>(
                new EvalFunction<T>() {
                    @Override
                    public MyStream<T> apply() {
                        MyStream<T> myStream = lastNextItemEvalProcess.eval();
                        return distinct(new HashSet<T>(), myStream);
                    }
                }
        );
        return this;
    }

    @Override
    public MyStream<T> peek(final ForEach<T> consumer) {
        final NextItemEvalProcess<T> lastNextItemEvalProcess = this.nextItemEvalProcess;
        this.nextItemEvalProcess = new NextItemEvalProcess<>(
                new EvalFunction<T>() {
                    @Override
                    public MyStream<T> apply() {
                        MyStream<T> myStream = lastNextItemEvalProcess.eval();
                        return peek(consumer, myStream);
                    }
                }
        );
        return this;
    }

    @Override
    public void forEach(ForEach<T> consumer) {
        forEach(consumer,this.eval());
    }

    @Override
    public <R> R reduce(R initVal, BiFunction<R, R, T> accumulator) {
        return reduce(initVal,accumulator,this.eval());
    }

    @Override
    public <R, A> R collect(Collector<T, A, R> collector) {
        A result = collect(collector,this.eval());
        return collector.finisher().apply(result);
    }

    @Override
    public T max(Comparator<T> comparator) {
        MyStream<T> eval = this.eval();
        if(eval.isEmptyStream()) return null;
        else return max(comparator, eval, eval.head);
    }

    @Override
    public T min(Comparator<T> comparator) {
        MyStream<T> eval = this.eval();
        if(eval.isEmptyStream()){
            return null;
        }else{
            return min(comparator,eval,eval.head);
        }
    }

    @Override
    public int count() {
        return count(this.eval(),0);
    }

    @Override
    public boolean anyMatch(Predicate<? super T> predicate) {
        return anyMatch(predicate,this.eval());
    }

    @Override
    public boolean allMatch(Predicate<? super T> predicate) {
        return allMatch(predicate,this.eval());
    }

    //===============================私有方法====================================


    private static <T,R> MyStream<R> map(final Function<T, R> mapper, final MyStream<T> myStream){
        if(myStream.isEmptyStream()) return MyStream.makeEmptyStream();
        R head = mapper.apply(myStream.head);
        return new Builder<R>()
                .head(head)
                .nextItemEvalProcess(new NextItemEvalProcess<>(new EvalFunction<R>() {
                    @Override
                    public MyStream<R> apply() {
                        return map(mapper, myStream.eval());
                    }
                }))
                .build();
    }


    private static <T,R> MyStream<R> flatMap(final Function<T,? extends MyStream<R>> mapper, final MyStream<R> headMyStream, final MyStream<T> myStream){
        if(headMyStream.isEmptyStream()){
            if(myStream.isEmptyStream()) return MyStream.makeEmptyStream();
            else{
                T outerHead = myStream.head;
                MyStream<R> newHeadMyStream = mapper.apply(outerHead);
                return flatMap(mapper, newHeadMyStream.eval(), myStream.eval());
            }
        }else{
            return new Builder<R>()
                        .head(headMyStream.head)
                        .nextItemEvalProcess(new NextItemEvalProcess<>(new EvalFunction<R>() {
                            @Override
                            public MyStream<R> apply() {
                                return flatMap(mapper, headMyStream.eval(), myStream);
                            }
                        }))
                        .build();
        }
    }


    private static <T> MyStream<T> filter(final Predicate<T> predicate, final MyStream<T> myStream){
        if(myStream.isEmptyStream()) return MyStream.makeEmptyStream();
        if(predicate.satisfy(myStream.head)){
            return new Builder<T>()
                    .head(myStream.head)
                    .nextItemEvalProcess(new NextItemEvalProcess<>(new EvalFunction<T>() {
                        @Override
                        public MyStream<T> apply() {
                            return filter(predicate, myStream.eval());
                        }
                    }))
                    .build();
        }else{
            return filter(predicate, myStream.eval());
        }
    }


    private static <T> MyStream<T> limit(final int num, final MyStream<T> myStream){
        if(num == 0 || myStream.isEmptyStream()) return MyStream.makeEmptyStream();
        return new Builder<T>()
                .head(myStream.head)
                .nextItemEvalProcess(new NextItemEvalProcess<>(new EvalFunction<T>() {
                    @Override
                    public MyStream<T> apply() {
                        return limit(num - 1, myStream.eval());
                    }
                }))
                .build();
    }


    private static <T> MyStream<T> distinct(final Set<T> distinctSet, final MyStream<T> myStream){
        if(myStream.isEmptyStream()) return MyStream.makeEmptyStream();
        if(!distinctSet.contains(myStream.head)){
            distinctSet.add(myStream.head);
            return new Builder<T>()
                .head(myStream.head)
                .nextItemEvalProcess(new NextItemEvalProcess<>(new EvalFunction<T>() {
                    @Override
                    public MyStream<T> apply() {
                        return distinct(distinctSet, myStream.eval());
                    }
                }))
                .build();
        }else{
            return distinct(distinctSet, myStream.eval());
        }
    }

    private static <T> MyStream<T> peek(final ForEach<T> consumer, final MyStream<T> myStream){
        if(myStream.isEmptyStream()) return MyStream.makeEmptyStream();
        consumer.apply(myStream.head);
        return new Builder<T>()
            .head(myStream.head)
            .nextItemEvalProcess(new NextItemEvalProcess<>(new EvalFunction<T>() {
                @Override
                public MyStream<T> apply() {
                    return peek(consumer, myStream.eval());
                }
            }))
            .build();
    }


    private static <T> void forEach(ForEach<T> consumer, MyStream<T> myStream){
        if(myStream.isEmptyStream()) return;
        consumer.apply(myStream.head);
        forEach(consumer, myStream.eval());
    }


    private static <R,T> R reduce(R initVal, BiFunction<R,R,T> accumulator, MyStream<T> myStream){
        if(myStream.isEmptyStream()){
            return initVal;
        }
        T head = myStream.head;
        R result = reduce(initVal,accumulator, myStream.eval());
        return accumulator.apply(result,head);
    }


    private static <R, A, T> A collect(Collector<T, A, R> collector, MyStream<T> myStream){
        if(myStream.isEmptyStream()){
            return collector.supplier().get();
        }
        T head = myStream.head;
        A tail = collect(collector, myStream.eval());
        return collector.accumulator().apply(tail,head);
    }


    private static <T> T max(Comparator<T> comparator, MyStream<T> myStream, T max){
        if(myStream.isEnd){
            return max;
        }
        T head = myStream.head;
        if(comparator.compare(head,max) > 0) return max(comparator, myStream.eval(), head);
        else return max(comparator, myStream.eval(), max);
    }


    private static <T> T min(Comparator<T> comparator, MyStream<T> myStream, T min){
        if(myStream.isEnd){
            return min;
        }
        T head = myStream.head;
        if(comparator.compare(head,min) < 0) return min(comparator, myStream.eval(), head);
        else return min(comparator, myStream.eval(), min);
    }


    private static <T> int count(MyStream<T> myStream, int count){
        if(myStream.isEmptyStream()) return count;
        return count(myStream.eval(),count+1);
    }


    private static <T> boolean anyMatch(Predicate<? super T> predicate,MyStream<T> myStream){
        if(myStream.isEmptyStream()){
            return false;
        }
        if(predicate.satisfy(myStream.head)) return true;
        else return anyMatch(predicate, myStream.eval());
    }

    private static <T> boolean allMatch(Predicate<? super T> predicate,MyStream<T> myStream){
        if(myStream.isEmptyStream()){
            return true;
        }
        if(predicate.satisfy(myStream.head)) return allMatch(predicate, myStream.eval());
        else return false;
    }

    private MyStream<T> eval(){
        return this.nextItemEvalProcess.eval();
    }

    private boolean isEmptyStream(){
        return this.isEnd;
    }


    public static<T> MyStream<T> makeEmptyStream(){
        return new MyStream.Builder<T>().isEnd(true).build();
    }

    @Override
    public String toString() {
        return "MyStream{" +
                "head=" + head +
                ", isEnd=" + isEnd +
                '}';
    }
}
