package luckmerlin.core.match;

public class Matcher<T> implements Matchable<T>{
    private Matchable<T> mMatchable;
    private int mMax;

    public Matcher(){
        this(null);
    }

    public Matcher(Matchable<T> matchable){
        mMatchable=matchable;
    }

    public final Matcher<T> setMatchable(Matchable<T> matchable) {
        this.mMatchable = matchable;
        return this;
    }

    @Override
    public Integer onMatch(T arg) {
        Matchable<T> matchable=mMatchable;
        return null!=matchable?matchable.onMatch(arg):null;
    }

    public final Matcher<T> setMax(int max) {
        this.mMax = max;
        return this;
    }

    public final int getMax() {
        return mMax;
    }

    public final Matchable<T> getMatchable() {
        return mMatchable;
    }
}
