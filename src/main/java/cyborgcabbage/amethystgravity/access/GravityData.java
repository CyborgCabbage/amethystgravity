package cyborgcabbage.amethystgravity.access;

import cyborgcabbage.amethystgravity.gravity.GravityEffect;

import java.util.ArrayList;

public interface GravityData {
    ArrayList<GravityEffect> getGravityData();
    ArrayList<GravityEffect> getGravityData2();
    void setCurrentGravityEffect(GravityEffect gravityEffect);
    GravityEffect getCurrentGravityEffect();
    int getTimeSinceLastGravityChange();
    void setTimeSinceLastGravityChange(int time);
}
