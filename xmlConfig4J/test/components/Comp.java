package components;

public enum Comp {
    LESS(-1),
    EQUAL(0),
    MORE(1);

    public final int sigNum;

    Comp(int sigNum) {
        this.sigNum = sigNum;
    }

    public static Comp fromSignum(int num) {
        return Comp.values()[Integer.signum(num) + 1];
    }
}
