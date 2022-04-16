package com.orangomango.multiplayer;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server {	
	public static class ServerState {	
		public static class PlayerState {
			public int x, y;
			public String color;
			public String user;
			public PlayerState(String user, int x, int y, String color){
				this.x = x;
				this.y = y;
				this.user = user;
				this.color = color;
			}
			
			public PlayerState(String status){
				update(status);
			}
			
			public void update(String data){
				String[] dt = data.split("\\["); // name x;y] color]
				this.user = dt[0];
				String values = dt[1].substring(0, dt[1].length()-1);
				this.x = Integer.parseInt(values.split(";")[0]);
				this.y = Integer.parseInt(values.split(";")[1]);
				this.color = dt[2].substring(0, dt[2].length()-1);
			}
			
			@Override
			public String toString(){
				return String.format("%s[%s;%s][%s]", this.user, this.x, this.y, this.color);
			}
		}
		
		private ArrayList<PlayerState> states = new ArrayList<>();
		
		public ServerState(String status){
			if (status != null){
				for (String piece : status.split(" ")){
					add(new PlayerState(piece));
				}
			}
		}
		
		public ArrayList<PlayerState> getStates(){
			return this.states;
		}
		
		public void add(PlayerState ps){
			states.add(ps);
		}
		
		public void remove(PlayerState ps){
			states.remove(ps);
		}
		
		@Override
		public String toString(){
			StringBuilder builder = new StringBuilder();
			for (PlayerState s : states){
				builder.append(s).append(" ");
			}
			return builder.toString();
		}
	}
	
	private ServerSocket server;
	public static ServerState state;
	
	static {
		state = new ServerState(null);
	}
	
	public Server(ServerSocket server){
		this.server = server;
	}
	
	public void start(){
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
