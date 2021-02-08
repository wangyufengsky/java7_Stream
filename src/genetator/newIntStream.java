package genetator;


import impl.MyStream;
import impl.NextItemEvalProcess;
import interfaces.EvalFunction;

/**
 * 整数流生成器
 */
public class newIntStream {

    /**
     * 获得一个有限的整数流 介于[low-high]之间
     * @param low 下界
     * @param high 上界
     * */
    public static MyStream<Integer> create(int low, int high){
        return getIntegerStreamInner(low,high,true);
    }

    /**
     * 递归函数。配合getIntegerStream(int low,int high)
     * */
    private static MyStream<Integer> getIntegerStreamInner(final int low, final int high, boolean isStart){
        if(low > high){
            // 到达边界条件，返回空的流
            return MyStream.makeEmptyStream();
        }

        if(isStart){
            return new MyStream.Builder<Integer>()
                    .nextItemEvalProcess(new NextItemEvalProcess(new EvalFunction<Integer>() {
                        @Override
                        public MyStream<Integer> apply() {
                            return getIntegerStreamInner(low, high, false);
                        }
                    }))
                    .build();
        }else{
            return new MyStream.Builder<Integer>()
                    // 当前元素 low
                    .head(low)
                    // 下一个元素 low+1
                    .nextItemEvalProcess(new NextItemEvalProcess(new EvalFunction<Integer>() {
                        @Override
                        public MyStream<Integer> apply() {
                            return getIntegerStreamInner(low + 1, high, false);
                        }
                    }))
                    .build();
        }
    }
}
