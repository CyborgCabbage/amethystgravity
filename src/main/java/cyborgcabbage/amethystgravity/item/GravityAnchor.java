package cyborgcabbage.amethystgravity.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class GravityAnchor extends Item {
    public final Direction direction;
    public GravityAnchor(Direction _direction, Settings settings) {
        super(settings);
        direction = _direction;
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(Text.translatable("amethystgravity.gravity_anchor.tooltip").formatted(Formatting.GRAY));
    }
}
