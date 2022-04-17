package com.orangomango.multiplayer;

import java.io.*;
import java.net.*;
import java.util.*;

public class PlayerManager implements Runnable {
	public static ArrayList<PlayerManager> players = new ArrayList<>();
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private Server.ServerState.PlayerState state;
	
	private static final int MAX_PLAYERS = 3;
	
	public PlayerManager(Socket socket){
		try {
			this.socket = socket;
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			if (players.size() == MAX_PLAYERS){
				this.writer.write("server_message:Error: server is full ("+players.size()+"/"+MAX_PLAYERS+")");
				this.writer.newLine();
				this.writer.flush();
				close();
				return;
			}
			players.add(this);
			String name = reader.readLine();
			int x = Integer.parseInt(reader.readLine());
			int y = Integer.parseInt(reader.readLine());
			String color = reader.readLine();
			this.state = new Server.ServerState.PlayerState(name, x, y, 40, 100, color);
			Server.state.add(this.state);
			System.out.println(toString()+" connected");
			broadcast();
		} catch (IOException e){
			close();
		}
	}
	
	public static void broadcast(){
		for (PlayerManager manager : players){
			try {
				manager.writer.write(Server.state.toString());
				manager.writer.newLine();
				manager.writer.flush();
			} catch (IOException e){
				manager.close();
			}
		}
	}
	
	private void close(){
		try {
			if (players.contains(this)){
				players.remove(this);
				Server.state.remove(this.state);
				broadcast();
				System.out.println(toString()+" disconnected");
			}
			if (this.socket != null) this.socket.close();
			if (this.reader != null) this.reader.close();
			if (this.writer != null) this.writer.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString(){
		return this.socket.getInetAddress().getHostAddress()+":"+this.socket.getLocalPort()+"["+this.state.user+"]";
	}
	
	@Override
	public void run(){
		while (this.socket.isConnected()){
			try {
				String data = this.reader.readLine();
				if (data == null){
					throw new IOException("Client disconnected");
				}
				this.state.update(data);
				broadcast();
			} catch (IOException e){
				close();
			}
		}
	}
}
