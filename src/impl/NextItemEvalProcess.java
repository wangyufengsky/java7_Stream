package impl;


import interfaces.EvalFunction;


public class NextItemEvalProcess<T> {


    private final EvalFunction<T> evalFunction;

    public NextItemEvalProcess(EvalFunction<T> evalFunction) {
        this.evalFunction = evalFunction;
    }

    MyStream<T> eval(){
        return evalFunction.apply();
    }
}
