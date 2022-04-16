package com.orangomango.multiplayer;

import javafx.application.Platform;

import java.net.*;
import java.io.*;

public class Player{
	
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private int x, y;
	private String color;
	public Server.ServerState state;
	private String name;
	
	public Player(Socket socket, String username, int x, int y, String color){
		try {
			this.socket = socket;
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.x = x;
			this.y = y;
			this.name = username;
			this.color = color;
			this.writer.write(username);
			this.writer.newLine();
			this.writer.write(Integer.toString(x));
			this.writer.newLine();
			this.writer.write(Integer.toString(y));
			this.writer.newLine();
			this.writer.write(this.color);
			this.writer.newLine();
			this.writer.flush();
		} catch (IOException e){
			close();
		}
	}
	
	public String getName(){
		return this.name;
	}
	
	public void move(int sx, int sy){
		this.x += sx;
		this.y += sy;
		try {
			this.writer.write(String.format("%s[%s;%s][%s]", this.name, this.x, this.y, this.color));
			this.writer.newLine();
			this.writer.flush();
		} catch (IOException e){
			close();
		}
	}
	
	public void listen(){
		new Thread(() -> {
			while (socket.isConnected()){
				try {
					String data = reader.readLine();
					if (data == null){
						throw new IOException("Connection lost");
					}
					state = new Server.ServerState(data);
					Platform.runLater(() -> Game.draw());
				} catch (IOException e){
					close();
				}
			}
		}).start();
	}
	
	private void close(){
		System.out.println("Connection lost");
		try {
			if (this.socket != null) this.socket.close();
			if (this.reader != null) this.reader.close();
			if (this.writer != null) this.writer.close();
			System.exit(0);
		} catch (IOException ioe){
			ioe.printStackTrace();
		}
	}
}
