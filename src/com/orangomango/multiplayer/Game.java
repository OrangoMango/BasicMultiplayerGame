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
		for (Server.ServerState.PlayerState s : player.state.getStates()){
			gc.setFill(Color.web(s.color));
			gc.fillRect(s.x, s.y, 50, 50);
			gc.setFill(Color.BLACK);
			gc.fillText(s.user, s.x, s.y-5);
		}
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
					player.move(0, -10);
					break;
				case A:
					player.move(-10, 0);
					break;
				case S:
					player.move(0, 10);
					break;
				case D:
					player.move(10, 0);
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
		System.out.println("Connected to "+args[0]+":"+args[1]+".");
		player = new Player(socket, args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]), args[5]);
		launch(args);
	}
}
