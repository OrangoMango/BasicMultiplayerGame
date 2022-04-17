package com.orangomango.multiplayer;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.TilePane;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;

import java.net.*;
import java.io.*;

public class Game extends Application{
	private static GraphicsContext gc;
	private static Player player;
	
	public static void draw(){
		gc.clearRect(0, 0, 500, 500);
		gc.setFill(Color.ORANGE);
		gc.fillRect(0, 0, 500, 500);
		for (Server.ServerState.BarState s : player.state.getBarStates()){
			gc.setStroke(Color.BLACK);
			if (s.direction.equals("n") || s.direction.equals("s")){
				gc.strokeLine(0, s.point, 500, s.point);
			} else if (s.direction.equals("e") || s.direction.equals("w")){
				gc.strokeLine(s.point, 0, s.point, 500);
			}
		}
		gc.setLineWidth(3);
		for (Server.ServerState.PlayerState s : player.state.getStates()){
			gc.setFill(Color.web(s.color));
			gc.fillRect(s.x, s.y, s.w, s.w);
			gc.setFill(Color.BLACK);
			gc.fillText(s.user+"("+s.hp+")", s.x+s.w/10, s.y-5);
		}
		// Draw hp bar
		gc.setFill(Color.LIME);
		gc.fillRect(10, 10, 100*player.hp/100, 20);
		gc.setStroke(Color.BLUE);
		gc.strokeRect(10, 10, 100, 20);
	}
	
	@Override
	public void start(Stage stage){
		stage.setTitle("Multiplayer - Client: "+player.getName());
		stage.setResizable(false);
		stage.setOnCloseRequest(e -> System.exit(0));
		TilePane layout = new TilePane();
		Canvas canvas = new Canvas(500, 500);
		canvas.setFocusTraversable(true);
		gc = canvas.getGraphicsContext2D();
		canvas.setOnKeyPressed(e -> {
			switch (e.getCode()){
				case W:
				case UP:
					if (player.getY() > 0){
						player.move(0, -10);
					}
					break;
				case A:
				case LEFT:
					if (player.getX() > 0){
						player.move(-10, 0);
					}
					break;
				case S:
				case DOWN:
					if (player.getY() < 450){
						player.move(0, 10);
					}
					break;
				case D:
				case RIGHT:
					if (player.getX() < 450){
						player.move(10, 0);
					}
					break;
			}
		});
		canvas.setOnKeyReleased(e -> {
			switch (e.getCode()){
				case SPACE:
					player.jump();
					break;
			}
		});
		layout.getChildren().add(canvas);
		stage.setScene(new Scene(layout, 500, 500));
		player.listen();
		stage.show();
	}
	
	public static void main(String[] args) throws IOException{
		if (args.length < 6){
			System.out.println("usage: Game <host> <port> <username> <xPos> <yPos> <color>");
			System.exit(0);
		}
		Socket socket = new Socket(args[0], Integer.parseInt(args[1]));
		System.out.println("Connected to "+args[0]+":"+args[1]+". Use WASD or ARROWS to move around and SPACE to jump.");
		player = new Player(socket, args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]), args[5]);
		launch(args);
	}
}
