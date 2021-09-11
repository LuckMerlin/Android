package luckmerlin.core.match;

public class Matcher<T> implements Matchable<T>{
    private Matchable<T> mMatchable;

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

    @Override
    public int getMax() {
        Matchable<T> matchable=mMatchable;
        return null!=matchable?matchable.getMax():-1;
    }

    public final Matchable<T> getMatchable() {
        return mMatchable;
    }
}
