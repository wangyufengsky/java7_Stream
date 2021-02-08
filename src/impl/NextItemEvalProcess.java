package impl;


import interfaces.EvalFunction;

/**
 * 下一个元素求值过程
 */
public class NextItemEvalProcess<T> {

    /**
     * 求值方法
     * */
    private final EvalFunction<T> evalFunction;

    public NextItemEvalProcess(EvalFunction<T> evalFunction) {
        this.evalFunction = evalFunction;
    }

    MyStream<T> eval(){
        return evalFunction.apply();
    }
}
