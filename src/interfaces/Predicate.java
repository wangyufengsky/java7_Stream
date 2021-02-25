package interfaces;

public interface Predicate <T>{

    /**
     * 函数式接口
     * @return true 满足条件
     *          false 不满足条件
     * */
    boolean satisfy(T item);
}