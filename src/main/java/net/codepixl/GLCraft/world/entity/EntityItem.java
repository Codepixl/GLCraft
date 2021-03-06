package net.codepixl.GLCraft.world.entity;

import com.evilco.mc.nbt.error.TagNotFoundException;
import com.evilco.mc.nbt.error.UnexpectedTagTypeException;
import com.evilco.mc.nbt.tag.TagCompound;
import com.nishu.utils.Time;
import net.codepixl.GLCraft.render.RenderType;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.util.AABB;
import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.item.Item;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.Tile;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import java.util.Iterator;

public class EntityItem extends EntitySolid {
	private ItemStack itemstack;
	private float yPos, yRot;
	private final float size = 0.3f;
	
	public EntityItem(ItemStack s, float x, float y, float z, WorldManager worldManager) {
		super(new Vector3f(x,y,z), new Vector3f(), new Vector3f(), worldManager);
		itemstack = s;
	}
	
	public EntityItem(ItemStack s, Vector3f pos, WorldManager worldManager) {
		super(pos, new Vector3f(), new Vector3f(), worldManager);
		itemstack = s;
		yPos = 0;
	}
	
	public EntityItem(Vector3f pos, Vector3f rot, Vector3f vel, TagCompound t, WorldManager w) throws UnexpectedTagTypeException, TagNotFoundException{
		super(pos, new Vector3f(), new Vector3f(), w);
		this.rot = rot;
		this.setVel(vel);
		yPos = 0;
		try{
			itemstack = ItemStack.fromNBT(t.getCompound("itemstack"));
		}catch(TagNotFoundException e){
			itemstack = new ItemStack();
			setDead(true);
		}
	}

	@Override
	public void render(){
		int count = this.itemstack.count/16;
		if(count == 0){
			count = 1;
		}
		if(this.itemstack.count > 1){
			count+=1;
		}
		for(int i = 0; i < count; i++){
			GL11.glPushMatrix();
			GL11.glTranslatef(getX(), getY(), getZ());
			GL11.glRotatef(this.yRot, 0f, 1.0f, 0f);
			if(itemstack.isTile()){
				GL11.glTranslatef(-size/2, 0f, -size/2f);
			}else{
				GL11.glTranslatef(-size/2, 0.8f, 0f);
				GL11.glRotatef(180, 1.0f, 0, 0);
			}
			GL11.glBegin(GL11.GL_QUADS);
			if(itemstack.isTile()){
				GL11.glRotatef(this.yRot, 0f, 1.0f, 0f);
				Tile tile = itemstack.getTile();
				if(tile.getRenderType() == RenderType.CUBE){
					if(tile.hasMetaTextures()){
						Shape.createCube(0+((float)i*0.05f), yPos+((float)i*0.05f), 0+((float)i*0.05f), this.getColor(), tile.getTexCoords(itemstack.getMeta()), size);
					}else{
						Shape.createCube(0+((float)i*0.05f), yPos+((float)i*0.05f), 0+((float)i*0.05f), this.getColor(), tile.getTexCoords(), size);
					}
				}else if(tile.getRenderType() == RenderType.CROSS){
					if(tile.hasMetaTextures()){
						Shape.createCross(0+((float)i*0.05f), yPos+((float)i*0.05f), 0+((float)i*0.05f), this.getColor(), tile.getTexCoords(itemstack.getMeta()), size);
					}else{
						Shape.createCross(0+((float)i*0.05f), yPos+((float)i*0.05f), 0+((float)i*0.05f), this.getColor(), tile.getTexCoords(), size);
					}
				}else if(tile.getRenderType() == RenderType.FLAT){
					if(tile.hasMetaTextures()){
						Shape.createPlane(0+((float)i*0.05f), yPos+((float)i*0.05f), 0+((float)i*0.05f), this.getColor(), tile.getTexCoords(itemstack.getMeta()), size);
					}else{
						Shape.createPlane(0+((float)i*0.05f), yPos+((float)i*0.05f), 0+((float)i*0.05f), this.getColor(), tile.getTexCoords(), size);
					}
				}else{
					if(tile.getCustomRenderType() == RenderType.CUBE){
						if(tile.hasMetaTextures()){
							Shape.createCube(0+((float)i*0.05f), yPos+((float)i*0.05f), 0+((float)i*0.05f), this.getColor(), tile.getTexCoords(itemstack.getMeta()), size);
						}else{
							Shape.createCube(0+((float)i*0.05f), yPos+((float)i*0.05f), 0+((float)i*0.05f), this.getColor(), tile.getTexCoords(), size);
						}
					}else if(tile.getCustomRenderType() == RenderType.CROSS){
						if(tile.hasMetaTextures()){
							Shape.createCross(0+((float)i*0.05f), yPos+((float)i*0.05f), 0+((float)i*0.05f), this.getColor(), tile.getTexCoords(itemstack.getMeta()), size);
						}else{
							Shape.createCross(0+((float)i*0.05f), yPos+((float)i*0.05f), 0+((float)i*0.05f), this.getColor(), tile.getTexCoords(), size);
						}
					}else{
						GL11.glEnd();
						GL11.glTranslatef(size/2, 0f, size/2f);
						GL11.glTranslatef(-size/2, 0.8f, 0f);
						GL11.glRotatef(180, 1.0f, 0, 0);
						GL11.glBegin(GL11.GL_QUADS);
						if(tile.hasMetaTextures()){
							Shape.createPlane(0+((float)i*0.05f), yPos+((float)i*0.05f), 0+((float)i*0.05f), this.getColor(), tile.getIconCoords(itemstack.getMeta()), size);
						}else{
							Shape.createPlane(0+((float)i*0.05f), yPos+((float)i*0.05f), 0+((float)i*0.05f), this.getColor(), tile.getIconCoords(), size);
						}
					}
				}
			}else if(!itemstack.isNull()){
				Item item = itemstack.getItem();
				Shape.createPlane(0+((float)i*0.05f), yPos+((float)i*0.05f), 0+((float)i*0.05f),this.getColor(), item.getTexCoords(), size);
			}
			GL11.glEnd();
			GL11.glPopMatrix();
		}
	}
	
	@Override
	public void update(){
		super.update();
		if(this.getCount() <= 0 || this.timeAlive > 300000 || this.itemstack.isNull()){
			this.setDead(true);
		}
		Iterator<Entity> i = worldManager.getEntityManager().getEntitiesInRadiusOfEntityOfType(this, EntityItem.class, 1.1f).iterator();
		while(i.hasNext()){
			EntityItem e = (EntityItem)i.next();
			if(
				e.itemstack.count + this.itemstack.count <= this.itemstack.getMaxStackSize() &&
				e.itemstack.compatible(this.itemstack) &&
				e.timeAlive >= this.timeAlive
			){
				e.setDead(true);
				this.itemstack.count += e.itemstack.count;
			}
		}
		if(!Tile.getTile((byte)worldManager.getTileAtPos(this.getPos())).canPassThrough()){
			this.setY((float) (this.getY()+0.5f));
		}
	}
	
	@Override
	public void clientUpdate(){
		super.clientUpdate();
		if(timeAlive % 2000 <= 1000){
			yPos = MathUtils.easeInOutQuad((float)timeAlive/1000f, 0.05f, 0.25f, 1f);
		}else{
			yPos = MathUtils.easeInOutQuad((float)timeAlive/1000f, 0.3f, -0.25f, 1f);
		}

		this.yRot = (float) (this.yRot+Time.getDelta()*50);
	}
	
	@Override
	public AABB getDefaultAABB(){
		return new AABB(0.3f,0.3f,0.3f);
	}
	
	@Override
	public void writeToNBT(TagCompound t){
		t.setTag(itemstack.toNBT("itemstack"));
	}
	
	public static Entity fromNBT(TagCompound t, WorldManager w) throws UnexpectedTagTypeException, TagNotFoundException{
		Vector3f pos = NBTUtil.vecFromList("Pos",t);
		Vector3f rot = NBTUtil.vecFromList("Rot",t);
		Vector3f vel = NBTUtil.vecFromList("Vel",t);
		return new EntityItem(pos, rot, vel, t, w);
	}
	
	public ItemStack getItemStack(){
		return this.itemstack;
	}
	
	public int getCount(){
		return this.itemstack.count;
	}
	
	public void setCount(int i){
		this.itemstack.count = i;
	}
	
	public void addCount(int i){
		this.itemstack.count+=i;
	}
	
}
