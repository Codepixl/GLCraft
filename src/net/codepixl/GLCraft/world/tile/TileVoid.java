package net.codepixl.GLCraft.world.tile;

import com.nishu.utils.Color4f;

public class TileVoid extends Tile{

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return -1;
	}
	
	@Override
	public Color4f getColor(){
		return Color4f.WHITE;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Void";
	}
	
	@Override
	public boolean hasTexture(){
		return false;
	}

	@Override
	public boolean isTransparent() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canPassThrough() {
		// TODO Auto-generated method stub
		return true;
	}

}
