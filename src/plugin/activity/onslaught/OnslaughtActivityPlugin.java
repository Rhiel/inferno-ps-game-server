package plugin.activity.onslaught;

import org.arios.game.content.activity.ActivityPlugin;
import org.arios.game.node.entity.player.Player;
import org.arios.game.world.map.Location;
import org.arios.game.world.map.zone.ZoneRestriction;


/**
 * Handles the new wave-based activity, the Onslaught minigame.
 * @author Splinter
 */
public final class OnslaughtActivityPlugin extends ActivityPlugin {

    public OnslaughtActivityPlugin(String name, boolean instanced, boolean multicombat, boolean safe, ZoneRestriction[] restrictions) {
	super(name, instanced, multicombat, safe, restrictions);
	// TODO Auto-generated constructor stub
    }

    @Override
    public ActivityPlugin newInstance(Player p) throws Throwable {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Location getSpawnLocation() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void configure() {
	// TODO Auto-generated method stub
	
    }
 
  
}