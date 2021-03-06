package net.codepixl.GLCraft.world.tile;

import com.nishu.utils.Color4f;

public class TileLamp extends Tile{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Lamp";
	}

	@Override
	public Color4f getColor() {
		// TODO Auto-generated method stub
		return new Color4f(1,1,1,1);
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
	
	@Override
	public byte getLightLevel(byte meta){
		return 15;
	}

}
