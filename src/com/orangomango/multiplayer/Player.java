package com.orangomango.multiplayer;

import javafx.application.Platform;
import javafx.animation.*;
import javafx.util.Duration;

import java.net.*;
import java.io.*;
import java.util.*;
import java.time.Instant;

public class Player{
	
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private int x, y, w = 40;
	private String color;
	public Server.ServerState state;
	private String name;
	private boolean jumping, takingDamage;
	public int hp = 100;
	
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
			this.takingDamage = true;
			new Timer().schedule(new TimerTask(){
				@Override
				public void run(){
					takingDamage = false;
				}
			}, 1000); // Temp shield (1s) on start
		} catch (IOException e){
			close();
		}
	}
	
	public String getName(){
		return this.name;
	}
	
	public int getX(){
		return this.x;
	}
	
	public int getY(){
		return this.y;
	}
	
	private void update(){
		try {
			this.writer.write(String.format("%s[%s;%s;%s;%s][%s]", this.name, this.x, this.y, this.w, this.hp, this.color));
			this.writer.newLine();
			this.writer.flush();
		} catch (IOException e){
			close();
		}
	}
	
	public void move(int sx, int sy){
		this.x += sx;
		this.y += sy;
		update();
	}
	
	public void jump(){
		if (this.jumping) return;
		Timeline j = new Timeline(new KeyFrame(Duration.millis(100), e -> {
			this.jumping = true;
			this.w += 3;
			update();
		}));
		j.setCycleCount(4);
		j.setOnFinished(e -> {
			Timeline f = new Timeline(new KeyFrame(Duration.millis(100), ev -> {
				this.w -= 3;
				update();
			}));
			f.setCycleCount(4);
			f.setOnFinished(eve -> this.jumping = false);
			f.play();
		});
		j.play();
	}
	
	public void listen(){
		new Thread(() -> {
			while (socket.isConnected()){
				try {
					String data = reader.readLine();
					if (data == null){
						throw new IOException("Connection lost");
					}
					if (data.startsWith("server_message:")){
						System.out.println(data.split(":", 2)[1]);
						continue;
					}
					try {
						state = new Server.ServerState(data);
					} catch (NumberFormatException ex){
						System.out.println("[ERROR]: "+ex.getMessage());
						continue;
					}
					if (!this.jumping && !this.takingDamage){
						for (Server.ServerState.BarState bs : this.state.getBarStates()){
							if ((bs.point >= this.x && bs.point <= this.x && (bs.direction.equals("e") || bs.direction.equals("w"))) || (bs.point >= this.y && bs.point <= this.y && (bs.direction.equals("n") || bs.direction.equals("s")))){
								this.hp -= 20;
								this.takingDamage = true;
								new Timer().schedule(new TimerTask(){
									@Override
									public void run(){
										takingDamage = false;
									}
								}, 300);
								update();
								if (this.hp <= 0){
									System.out.println("["+Instant.now().toString()+"] You died!");
									close();
									break;
								}
							}
						}
					}
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
