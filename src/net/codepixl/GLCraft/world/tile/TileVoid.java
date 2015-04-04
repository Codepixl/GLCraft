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
	public float[] getTexCoords() {
		// TODO Auto-generated method stub
		return new float[]{0,0};
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Void";
	}

}
