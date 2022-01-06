package cyborgcabbage.amethystgravity.access;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;

public interface GravityData {
    List<Direction> getGravityData();
    boolean isOnPlate();
    void setOnPlate(boolean b);
}
