package luckmerlin.core.match;

public interface Matchable<T>{
    int MATCHED=-2010;
    int CONTINUE=-2011;
    int BREAK=-2012;

    Integer onMatch(T arg);

    default int getMax(){
        return -1;
    }
}
