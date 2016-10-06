package net.codepixl.GLCraft.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.lwjgl.util.vector.Vector3f;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.GUI.GUIManager;
import net.codepixl.GLCraft.GUI.GUIServerError;
import net.codepixl.GLCraft.network.packet.Packet;
import net.codepixl.GLCraft.network.packet.PacketAddEntity;
import net.codepixl.GLCraft.network.packet.PacketBlockChange;
import net.codepixl.GLCraft.network.packet.PacketPlayerAdd;
import net.codepixl.GLCraft.network.packet.PacketPlayerLogin;
import net.codepixl.GLCraft.network.packet.PacketPlayerLoginResponse;
import net.codepixl.GLCraft.network.packet.PacketPlayerPos;
import net.codepixl.GLCraft.network.packet.PacketRemoveEntity;
import net.codepixl.GLCraft.network.packet.PacketRespawn;
import net.codepixl.GLCraft.network.packet.PacketSendChunk;
import net.codepixl.GLCraft.network.packet.PacketServerClose;
import net.codepixl.GLCraft.network.packet.PacketSetBufferSize;
import net.codepixl.GLCraft.network.packet.PacketSetInventory;
import net.codepixl.GLCraft.network.packet.PacketUpdateEntity;
import net.codepixl.GLCraft.network.packet.PacketUtil;
import net.codepixl.GLCraft.network.packet.PacketWorldTime;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.LogSource;
import net.codepixl.GLCraft.util.logging.GLogger;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayerMP;

public class Client{
	
	public static int DEFAULT_CLIENT_PORT = 54566;
	
	public DatagramSocket socket;
	public ConnectionRunnable connectionRunnable;
	public Thread connectionThread;
	public WorldManager worldManager;
	public ClientServer connectedServer;
	public int port;
	public volatile ServerConnectionState connectionState;
	
	public Client(WorldManager w, int port) throws IOException{
		if(!commonInit(w,port)){throw new IOException("Error binding to port");}
	}
	
	public Client(WorldManager w) throws IOException{
		int i = 0;
		while(!commonInit(w,Constants.randInt(5400, 6000)) && i < 50){i++;}
		if(i >= 50)
			throw new IOException("Error binding to all tried ports");
	}
	
	private boolean commonInit(WorldManager w, int port){
		try{
			socket = new DatagramSocket(port);
		}catch(SocketException e){
			e.printStackTrace();
			return false;
		}
		this.worldManager = w;
		GLCraft.getGLCraft().setClient(this);
		this.connectionState = new ServerConnectionState();
		connectionRunnable = new ConnectionRunnable(this);
		connectionThread = new Thread(connectionRunnable);
		GLogger.log("Running on port "+port, LogSource.CLIENT);
		return true;
	}
	
	public void handlePacket(DatagramPacket dgp, Packet op){
		try{
			if(op instanceof PacketSendChunk){
				PacketSendChunk p = (PacketSendChunk)op;
				if(!p.failed){
					if(p.type == PacketSendChunk.TYPE_CHUNK){
						worldManager.updateChunk(p, true);
					}else{
						worldManager.doneGenerating = false;
						worldManager.chunksLeftToDownload = p.numChunks;
					}
				}
			}else if(op instanceof PacketSetBufferSize){
				PacketSetBufferSize p = (PacketSetBufferSize)op;
				if(p.bufferSize <= 1000000){ //Make sure the size is <= 1M (to prevent attacks)
					this.socket.setReceiveBufferSize(p.bufferSize);
				}
			}else if(op instanceof PacketRespawn){
				this.worldManager.getEntityManager().getPlayer().respawn();
			}else if(op instanceof PacketWorldTime){
				this.worldManager.setWorldTime(((PacketWorldTime)op).worldTime);
			}else if(op instanceof PacketPlayerAdd){
				PacketPlayerAdd p = ((PacketPlayerAdd) op);
				if(p.entityID != this.worldManager.getEntityManager().getPlayer().getID())
					this.worldManager.spawnEntity(new EntityPlayerMP(new Vector3f(p.x, p.y, p.z), this.worldManager));
			}else if(op instanceof PacketPlayerPos){
				PacketPlayerPos p = (PacketPlayerPos)op;
				Entity e = this.worldManager.entityManager.getEntity(p.entityID);
				e.setPos(p.pos[0], p.pos[1], p.pos[2]);
				e.setRot(p.rot[0], p.rot[1], p.rot[2]);
			}else if(op instanceof PacketBlockChange){
				PacketBlockChange p = (PacketBlockChange) op;
				if(!p.justMeta)
					worldManager.setTileAtPos(p.x, p.y, p.z, p.id, p.source, true, p.meta);
				else{
					worldManager.setMetaAtPos(p.x, p.y, p.z, p.meta, false, true);
				}
			}else if(op instanceof PacketAddEntity){
				PacketAddEntity p = (PacketAddEntity) op;
				Entity e = ((PacketAddEntity) op).getEntity(worldManager);
				if(e != null){
					worldManager.spawnEntity(e);
				}
			}else if(op instanceof PacketUpdateEntity){
				PacketUpdateEntity p = (PacketUpdateEntity) op;
				Entity e;
				switch(p.type){
					case POSITION:
						e = worldManager.getEntityManager().getEntity(p.entityID);
						if(e != null){
							e.setPos(p.x, p.y, p.z);
							e.setRot(p.xr, p.yr, p.zr);
							e.setVel(p.xv, p.yv, p.zv);
						}
						break;
					case UPDATENBT:
						e = worldManager.getEntityManager().getEntity(p.entityID);
						if(e != null){
							worldManager.getEntityManager().remove(e);
							e = p.getEntity(worldManager);
							worldManager.getEntityManager().add(e);
						}
						break;
				}
			}else if(op instanceof PacketRemoveEntity){
				PacketRemoveEntity p = (PacketRemoveEntity)op;
				Entity e = worldManager.getEntityManager().getEntity(p.entityID);
				if(e != null){
					worldManager.entityManager.remove(e);
				}
			}else if(op instanceof PacketSetInventory){
				PacketSetInventory p = (PacketSetInventory)op;
				if(this.worldManager.getEntityManager().getPlayer().getID() == p.entityID){
					p.setInventory(worldManager);
				}
			}else if(op instanceof PacketServerClose){
				PacketServerClose p = (PacketServerClose)op;
				worldManager.closeWorld();
				GUIManager.getMainManager().showGUI(new GUIServerError("Server closed: ",p.message));
			}else{
				System.err.println("[CLIENT] Received unhandled packet: "+op.getClass());
				//throw new IOException("Invalid Packet "+op.getClass());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void close() {
		socket.close();
		connectionThread.interrupt();
	}

	public ServerConnectionState connectToServer(InetAddress addr, int port) throws IOException{
		PacketPlayerLogin ppl = new PacketPlayerLogin(worldManager.getEntityManager().getPlayer().getName());
		byte[] b = ppl.getBytes();
		DatagramPacket dgp = new DatagramPacket(b,b.length,addr,port);
		socket.send(dgp);
		
		socket.setSoTimeout(5000);
		
		try{
			DatagramPacket rec = new DatagramPacket(connectionRunnable.buf,connectionRunnable.buf.length);
			socket.receive(rec);
			Packet op = PacketUtil.getPacket(rec.getData());
			if(op instanceof PacketPlayerLoginResponse){
				PacketPlayerLoginResponse p = (PacketPlayerLoginResponse)op;
				if(p.accept){
					this.connectionState = new ServerConnectionState(p.entityID);
					this.worldManager.getEntityManager().getPlayer().setId(p.entityID);
					this.connectedServer = new ClientServer(this, addr, port);
					connectionThread.start();
				}else{
					this.connectionState = new ServerConnectionState(p.message);
				}
			}else{
				this.connectionState = new ServerConnectionState("Server sent malformed packet: "+op.getClass());
			}
		}catch(SocketTimeoutException e){
			this.connectionState = new ServerConnectionState(e.getMessage());
		}
		
		socket.setSoTimeout(0);
		
		return connectionState;
	}
	
	public void sendToServer(Packet p) throws IOException{
		this.connectedServer.sendPacket(p);
	}
	
	public class ConnectionRunnable implements Runnable{
		
		private Client client;
		private byte[] buf = new byte[10000];
		
		public ConnectionRunnable(Client s){
			this.client = s;
		}
		
		@Override
		public void run() {
			while(!Thread.interrupted()){
				try{
					DatagramPacket rec = new DatagramPacket(buf,buf.length);
					client.socket.receive(rec);
					Packet p = PacketUtil.getPacket(rec.getData());
					client.handlePacket(rec, p);
				}catch (IOException e){
					if(!client.socket.isClosed())
						e.printStackTrace();
				}
			}
		}
	}
	
	public class ClientServer{
		public Client client;
		public InetAddress serverAddr;
		public int serverPort;
		public ClientServer(Client c, InetAddress serverAddr, int serverPort){
			this.client = c;
			this.serverAddr = serverAddr;
			this.serverPort = serverPort;
		}
		
		public void sendPacket(Packet p) throws IOException{
			byte[] b = p.getBytes();
			DatagramPacket dgp = new DatagramPacket(b,b.length);
			dgp.setAddress(serverAddr);
			dgp.setPort(serverPort);
			this.client.socket.send(dgp);
		}
	}
	
	public class ServerConnectionState{
		public boolean success;
		public int entityID;
		public String message;
		public boolean connected = true;
		public ServerConnectionState(String rejectMessage){
			success = false;
			connected = false;
			message = rejectMessage;
		}
		public ServerConnectionState(int entityID){
			success = true;
			connected = true;
			this.entityID = entityID;
		}
		public ServerConnectionState(){
			this.connected = false;
			success = false;
		}
	}
	
	public void destroy(){
		this.connectionThread.interrupt();
		this.socket.close();
	}

	public void reinit() {
		try {
			socket = new DatagramSocket(port);
			connectionThread = new Thread(connectionRunnable);
			connectionThread.start();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
}
