package net.codepixl.GLCraft.world.entity.mob.AI;

import com.nishu.utils.Time;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.mob.Mob;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

public class AIFollowNearest extends AIPathfind{
	
	private Class<Entity> follow;
	private double repathTime = 0;
	
	public AIFollowNearest(Mob m, Class type) {
		super(m);
		this.follow = type;
	}
	
	public void executeAI(){
		super.executeAI();
		if(repathTime <= 0 || this.getLocation() == null){
			List<Entity> e = mob.worldManager.entityManager.getEntitiesInRadiusOfEntityOfType(mob, follow, 20f);
			if(e.size() > 0){
				Vector3f ppos = e.get(0).getPos();
				ppos = new Vector3f((int)ppos.x, (int)ppos.y, (int)ppos.z);
				this.setLocation(ppos);
				mob.setAiBusy(true);
			}else{
				//this.setLocation(null);
				mob.setAiBusy(false);
			}
		}
		if(repathTime <= 0){ repathTime = 0.5d;}
		repathTime -= Time.getDelta();
	}

}
