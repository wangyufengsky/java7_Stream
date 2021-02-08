package util;



import impl.Collector;
import interfaces.BiFunction;
import interfaces.Function;
import interfaces.Supplier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * stream.collect() 参数常用工具类
 */
public class CollectUtils {

    /**
     * stream 转换为 List
     * */
    public static <T> Collector<T, List<T>, List<T>> toList(){
        return new Collector<T, List<T>, List<T>>() {
            @Override
            public Supplier<List<T>> supplier() {
                return new Supplier<List<T>>() {
                    @Override
                    public List<T> get() {
                        return new ArrayList<>();
                    }
                };
            }
            @Override
            public BiFunction<List<T>, List<T>, T> accumulator() {
                return new BiFunction<List<T>, List<T>, T>() {
                    @Override
                    public List<T> apply(List<T> list, T item) {
                        list.add(item);
                        return list;
                    }
                };
            }
            @Override
            public Function<List<T>, List<T>> finisher() {
                return new Function<List<T>, List<T>>() {
                    @Override
                    public List<T> apply(List<T> list) {
                        return list;
                    }
                };
            }
        };
    }

    /**
     * stream 转换为 Set
     * */
    public static <T> Collector<T, Set<T>, Set<T>> toSet(){
        return new Collector<T, Set<T>, Set<T>>() {
            @Override
            public Supplier<Set<T>> supplier() {
                return new Supplier<Set<T>>() {
                    @Override
                    public Set<T> get() {
                        return new HashSet<>();
                    }
                };
            }
            @Override
            public BiFunction<Set<T>, Set<T>, T> accumulator() {
                return new BiFunction<Set<T>, Set<T>, T>() {
                    @Override
                    public Set<T> apply(Set<T> set, T item) {
                        set.add(item);
                        return set;
                    }
                };
            }
            @Override
            public Function<Set<T>, Set<T>> finisher() {
                return new Function<Set<T>, Set<T>>() {
                    @Override
                    public Set<T> apply(Set<T> set) {
                        return set;
                    }
                };
            }
        };
    }
}
