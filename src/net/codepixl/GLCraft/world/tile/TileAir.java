package net.codepixl.GLCraft.world.tile;

import com.nishu.utils.Color4f;

public class TileAir extends Tile{

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Color4f getColor() {
		// TODO Auto-generated method stub
		return Color4f.WHITE;
	}

	@Override
	public float[] getTexCoords() {
		// TODO Auto-generated method stub
		return new float[]{-1,-1};
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Air";
	}

}
