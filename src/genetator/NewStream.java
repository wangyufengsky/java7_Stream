package genetator;


import impl.MyStream;
import impl.NextItemEvalProcess;
import interfaces.EvalFunction;

import java.util.Iterator;
import java.util.List;


public class NewStream {


    public static <T> MyStream<T> create(List<T> list){
        return create(list.iterator(),true);
    }


    private static <T> MyStream<T> create(final Iterator<T> iterator, boolean isStart){
        if(!iterator.hasNext()){
            return MyStream.makeEmptyStream();
        }
        if(isStart){
            return new MyStream.Builder<T>()
                    .nextItemEvalProcess(new NextItemEvalProcess(new EvalFunction<T>() {
                        @Override
                        public MyStream<T> apply() {
                            return create(iterator, false);
                        }
                    }))
                    .build();
        }else{
            return new MyStream.Builder<T>()
                    .head(iterator.next())
                    .nextItemEvalProcess(new NextItemEvalProcess(new EvalFunction<T>() {
                        @Override
                        public MyStream<T> apply() {
                            return create(iterator, false);
                        }
                    }))
                    .build();
        }
    }
}
