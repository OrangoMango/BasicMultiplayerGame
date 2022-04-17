package com.orangomango.multiplayer;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server {	
	public static class ServerState {	
		public static class PlayerState {
			public int x, y, w, hp;
			public String color;
			public String user;
			public PlayerState(String user, int x, int y, int w, int hp, String color){
				this.x = x;
				this.y = y;
				this.w = w;
				this.hp = hp;
				this.user = user;
				this.color = color;
			}
			
			public PlayerState(String status){
				update(status);
			}
			
			public void update(String data){
				String[] dt = data.split("\\["); // name x;y;w;hp] color]
				this.user = dt[0];
				String values = dt[1].substring(0, dt[1].length()-1);
				this.x = Integer.parseInt(values.split(";")[0]);
				this.y = Integer.parseInt(values.split(";")[1]);
				this.w = Integer.parseInt(values.split(";")[2]);
				this.hp = Integer.parseInt(values.split(";")[3]);
				this.color = dt[2].substring(0, dt[2].length()-1);
			}
			
			@Override
			public String toString(){
				return String.format("%s[%s;%s;%s;%s][%s]", this.user, this.x, this.y, this.w, this.hp, this.color);
			}
		}
		
		public static class BarState {
			public int point;
			public String direction;
			public BarState(int point, String direction){
				this.point = point;
				this.direction = direction;
			}
			
			public BarState(String status){
				update(status);
			}
			
			public void update(String data){
				data = data.substring(1, data.length()-1);
				String[] dt = data.split(";");
				this.point = Integer.parseInt(dt[0]);
				this.direction = dt[1];
			}
			
			@Override
			public String toString(){
				return String.format("(%s;%s)", this.point, this.direction);
			}
		}
		
		private ArrayList<PlayerState> states = new ArrayList<>();
		private ArrayList<BarState> barStates = new ArrayList<>();
		
		public ServerState(String status){
			if (status != null){
				for (String piece : status.split("\\|")[0].split(" ")){
					add(new PlayerState(piece));
				}
				if (status.split("\\|").length >= 2){
					for (String piece : status.split("\\|")[1].split(" ")){
						add(new BarState(piece));
					}
				}
			}
		}
		
		public ArrayList<PlayerState> getStates(){
			return this.states;
		}
		
		public ArrayList<BarState> getBarStates(){
			return this.barStates;
		}
		
		public void add(PlayerState ps){
			states.add(ps);
		}
		
		public void add(BarState bs){
			barStates.add(bs);
		}
		
		public void remove(PlayerState ps){
			states.remove(ps);
		}
		
		public void remove(BarState bs){
			barStates.remove(bs);
		}
		
		@Override
		public String toString(){
			StringBuilder builder = new StringBuilder();
			for (PlayerState s : states){
				builder.append(s).append(" ");
			}
			if (barStates.size() > 0){
				builder.append("|");
				for (BarState s : barStates){
					builder.append(s).append(" ");
				}
			}
			return builder.toString();
		}
	}
	
	private ServerSocket server;
	public static volatile ServerState state;
	
	static {
		state = new ServerState(null);
	}
	
	public Server(ServerSocket server){
		this.server = server;
	}
	
	private void moveBars(){
		new Thread(() -> {
			while (true){
				if (state.getBarStates().size() == 0){
					continue;
				}
				Iterator<ServerState.BarState> iterator = state.getBarStates().iterator();
				while (iterator.hasNext()){
					ServerState.BarState bs = iterator.next();
					if (bs.point > 500 || bs.point < 0){
						iterator.remove();
					}
					if (bs.direction.equals("n") || bs.direction.equals("w")){
						bs.point -= 10;
					} else if (bs.direction.equals("s") || bs.direction.equals("e")){
						bs.point += 10;
					}
					bs.update(bs.toString());
				}
				PlayerManager.broadcast();
				try {
					Thread.sleep(170);
				} catch (InterruptedException ex){
					ex.printStackTrace();
				}
			}
		}).start();
	}
	
	private void spawnBars(){
		Random generator = new Random();
		String[] options = new String[]{"n", "e", "s", "w"};
		new Thread(() -> {
			while (true){
				if (state.getStates().size() == 0){
					continue;
				}
				System.out.println("[INFO] New bar added");
				String choice = options[generator.nextInt(4)];
				if (choice.equals("n") || choice.equals("w")){
					state.add(new ServerState.BarState(500, choice));
				} else if (choice.equals("e") || choice.equals("s")){
					state.add(new ServerState.BarState(0, choice));
				}
				try {
					Thread.sleep(generator.nextInt(7000-3000)+3000); // wait between 3 and 7 seconds
				} catch (InterruptedException ex){
					ex.printStackTrace();
				}
			}
		}).start();
	}
	
	public void start(){
		spawnBars();
		moveBars();
		while (!this.server.isClosed()){
			try {
				Socket socket = this.server.accept();
				new Thread(new PlayerManager(socket)).start();
			} catch (IOException e){
				close();
			}
		}
	}
	
	private void close(){
		try {
			if (server != null){
				server.close();
			}
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException{
		if (args.length < 2){
			System.out.println("usage: Server <host> <port>");
			return;
		}
		ServerSocket ss = new ServerSocket(Integer.parseInt(args[1]), 10, InetAddress.getByName(args[0]));
		System.out.println("Server started at "+args[0]+":"+args[1]+".");
		Server server = new Server(ss);
		server.start();
	}
}
