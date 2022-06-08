package cyborgcabbage.amethystgravity.gravity;

import net.minecraft.util.math.Direction;

import java.util.ArrayList;

public interface GravityData {
    ArrayList<GravityEffect> getFieldList();
    ArrayList<GravityEffect> getLowerFieldList();
    void setFieldGravity(GravityEffect gravityEffect);
    GravityEffect getFieldGravity();
}
