package cyborgcabbage.amethystgravity.gravity;

import java.util.ArrayList;

public interface GravityData {
    ArrayList<GravityEffect> getFieldList();
    ArrayList<GravityEffect> getLowerFieldList();
    void setFieldGravity(GravityEffect gravityEffect);
    GravityEffect getFieldGravity();
}
