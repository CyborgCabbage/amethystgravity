package cyborgcabbage.amethystgravity.gravity;

import java.util.ArrayList;

public interface GravityData {
    ArrayList<GravityEffect> getGravityData();
    ArrayList<GravityEffect> getGravityData2();
    void setCurrentGravityEffect(GravityEffect gravityEffect);
    GravityEffect getCurrentGravityEffect();
    int getTimeSinceLastGravityChange();
    void setTimeSinceLastGravityChange(int time);
}
