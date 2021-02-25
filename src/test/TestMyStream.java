package test;


import genetator.newIntStream;
import impl.Stream;
import interfaces.BiFunction;
import interfaces.Function;
import interfaces.Predicate;

public class TestMyStream {

    public static void main(String[] args){
        // 生成关于list的流
        Stream<Integer> intStream = newIntStream.create(1,10);
        // intStream基础上过滤出偶数的流
        Stream<Integer> filterStream =  intStream.filter(new Predicate<Integer>() {
            @Override
            public boolean satisfy(Integer item) {
                return item % 2 == 0;
            }
        });
        // filterStream基础上映射为平方的流
        Stream<Integer> mapStream = filterStream.map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer item) {
                return item * item;
            }
        });
        // mapStream基础上截取前三个的流
        Stream<Integer> limitStream = mapStream.limit(2);

        // 最终结果累加求和(初始值为0)
        Integer sum = limitStream.reduce(0, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer i1, Integer i2) {
                return i1 + i2;
            }
        });

        System.out.println(sum); // 20
    }

    private static boolean idOdd(int num){
        return (num % 2 != 0);
    }

    private static Integer square(int num){
        return num * num;
    }

    private static Integer scaleTwo(int num){
        return num * 2;
    }

    private static String toStr(int num){
        return num + "";
    }
}
