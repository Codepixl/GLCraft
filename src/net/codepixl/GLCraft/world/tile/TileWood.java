package net.codepixl.GLCraft.world.tile;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.Spritesheet;

public class TileWood extends Tile{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Wood";
	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 16;
	}

	@Override
	public Color4f getColor() {
		// TODO Auto-generated method stub
		return Color4f.WHITE;
	}
	
	@Override
	public float getHardness(){
		return 1.5f;
	}

	@Override
	public boolean isTransparent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canPassThrough() {
		// TODO Auto-generated method stub
		return false;
	}

}