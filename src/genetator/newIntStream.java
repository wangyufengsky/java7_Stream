package genetator;

import impl.MyStream;
import impl.NextItemEvalProcess;
import interfaces.EvalFunction;


public class newIntStream {


    public static MyStream<Integer> create(int low, int high){
        return getIntegerStreamInner(low,high,true);
    }


    private static MyStream<Integer> getIntegerStreamInner(final int low, final int high, boolean isStart){
        if(low > high){
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
                    .head(low)
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
