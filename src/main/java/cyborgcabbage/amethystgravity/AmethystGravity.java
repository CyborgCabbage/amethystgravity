package cyborgcabbage.amethystgravity;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AmethystGravity implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("amethystgravity");

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
	}
}
