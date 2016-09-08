package net.codepixl.GLCraft.GUI;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.concurrent.Callable;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.GUI.Elements.GUIButton;
import net.codepixl.GLCraft.GUI.Elements.GUILabel;
import net.codepixl.GLCraft.GUI.Elements.GUILabel.Alignment;
import net.codepixl.GLCraft.plugin.PluginManagerWindow;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.render.texturepack.TexturePackManagerWindow;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.world.tile.Tile;

public class GUIStartScreen extends GUIScreen{
	
	private static final int MIDDLE = Constants.WIDTH/2;
	private static final int MIDDLEY = (int) (Constants.HEIGHT*0.7);
	private static final int BUGY = (int) (Constants.HEIGHT * 0.8);
	private static final int BUTTONWIDTH = Constants.WIDTH/2;
	private static final int BBUTTONWIDTH = Constants.WIDTH/4-5;
	
	private GUIButton startButton, pluginManagerButton, quitButton, bugButton, texturepackButton, multiplayerButton;
	private GUILabel title;

	public GUIStartScreen() {
		setDrawStoneBackground(true);
		
		startButton = new GUIButton("Singleplayer", MIDDLE, MIDDLEY-GUIButton.BTNHEIGHT*2-20, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				GUIManager.getMainManager().showGUI("singleplayer");
				return null;
			}
		});
		startButton.width = BUTTONWIDTH;
		
		multiplayerButton = new GUIButton("Multiplayer", MIDDLE, MIDDLEY-GUIButton.BTNHEIGHT-10, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				return null;
			}
		});
		multiplayerButton.width = BUTTONWIDTH;
		multiplayerButton.enabled = false;
		
		pluginManagerButton = new GUIButton("Plugin Manager", MIDDLE, MIDDLEY, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				new PluginManagerWindow(GLCraft.getGLCraft().getPluginManager()).setVisible(true);
				return null;
			}
		});
		pluginManagerButton.width = BUTTONWIDTH;
		
		texturepackButton = new GUIButton("Texturepacks", MIDDLE, MIDDLEY+GUIButton.BTNHEIGHT+10, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				new TexturePackManagerWindow();
				return null;
			}
		});
		texturepackButton.width = BUTTONWIDTH;
		
		quitButton = new GUIButton("Quit", MIDDLE-BBUTTONWIDTH/2-5, MIDDLEY+GUIButton.BTNHEIGHT*2+20, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				System.exit(0);
				return null;
			}
		});
		quitButton.width = BBUTTONWIDTH;
		
		bugButton = new GUIButton("Report a bug", MIDDLE+BBUTTONWIDTH/2+5, MIDDLEY+GUIButton.BTNHEIGHT*2+20, new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				Desktop.getDesktop().browse(new URI("https://gitreports.com/issue/Codepixl/GLCraft"));
				return null;
			}
		});
		bugButton.width = BBUTTONWIDTH;
		
		title = new GUILabel(MIDDLE, 150, "GLCraft");
		title.size = 6f;
		title.alignment = Alignment.CENTER;
		
		this.addElement(title);
		this.addElement(quitButton);
		this.addElement(startButton);
		this.addElement(pluginManagerButton);
		this.addElement(bugButton);
		this.addElement(texturepackButton);
		this.addElement(multiplayerButton);
	}
	
	@Override
	public void update(){
		super.update();
		if(TextureManager.setAtlas){
			File outputfile = new File(Constants.GLCRAFTDIR,"temp/atlas.png");
			Spritesheet.atlas = new Spritesheet(outputfile.getAbsolutePath(),TextureManager.maxWidth,true);
			TextureManager.setAtlas = false;
		}
	}
	
	@Override
	public boolean canBeExited() {
		return false;
	}
}
