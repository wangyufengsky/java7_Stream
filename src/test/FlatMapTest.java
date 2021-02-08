package test;




import genetator.NewStream;
import impl.MyStream;
import interfaces.Function;
import interfaces.Predicate;
import util.CollectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FlatMapTest {

    public static void main(String[] args){
        testCollectionGenerator();
    }

    private static void testCollectionGenerator(){
        List<String> cao = Arrays.asList("11", "12", "13");
        List<String> wang = Arrays.asList("21", "22", "23","24");
        List<String> ma = Arrays.asList("31", "32", "33");
        List<String> lou = Arrays.asList("41", "42", "43");
        List<String> pan = Arrays.asList("52", "53");

        List<List<String>> JZDSFGroup = new ArrayList<>();
        JZDSFGroup.add(cao);
        JZDSFGroup.add(wang);
        JZDSFGroup.add(ma);
        JZDSFGroup.add(lou);
        JZDSFGroup.add(pan);

        List<String> list = NewStream.create(JZDSFGroup)
                .flatMap(new Function<List<String>,MyStream<String>>() {
                    @Override
                    public MyStream<String> apply(List<String> strings) {
                        return NewStream.create(strings);
                    }
                })
                .filter(new Predicate<String>() {
                    @Override
                    public boolean satisfy(String item) {
                        return item.endsWith("2");
                    }
                })
                .limit(2)
                .collect(CollectUtils.<String>toList());
        System.out.println(NewStream.create(list).anyMatch(new Predicate<String>() {
            @Override
            public boolean satisfy(String item) {
                return "22".equals(item);
            }
        }));
        System.out.println(list);
    }
}
