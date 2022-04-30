package iamcreeper;

public interface PlayerInterface {

    boolean isIgnited();
    int getFuseSpeed();
    void setFuseSpeed(int fuseSpeed);
    boolean isCharged();
    void ignite();
    boolean isCreeper();
    void setCreeper(boolean creeper);
}
