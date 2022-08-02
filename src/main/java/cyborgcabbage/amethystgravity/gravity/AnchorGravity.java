package cyborgcabbage.amethystgravity.gravity;

import com.fusionflux.gravity_api.api.RotationParameters;
import com.fusionflux.gravity_api.util.Gravity;
import cyborgcabbage.amethystgravity.AmethystGravity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class AnchorGravity {
    public static Identifier FIELD_GRAVITY_SOURCE = new Identifier(AmethystGravity.MOD_ID, "anchor");
    public static int FIELD_GRAVITY_PRIORITY = 200;
    public static int FIELD_GRAVITY_MAX_DURATION = 100;

    public static Gravity newGravity(Direction direction, RotationParameters rp){
        return new Gravity(direction, FIELD_GRAVITY_PRIORITY, FIELD_GRAVITY_MAX_DURATION, FIELD_GRAVITY_SOURCE.toString(), rp);
    }
}
