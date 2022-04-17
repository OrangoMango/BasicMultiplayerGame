# BasicMultiplayerGame
## What you need
* java 8+
* javafx 11+
* every player and the server in the same LAN
## How to run
* Clone the repository by using this command:
```bash
git clone https://github.com/OrangoMango/BasicMultiplayerGame
cd BasicMultiplayerGame/bin
```
* Create an environment variable for your javafx path:
```bash
export FX_PATH=/path/to/javafx/lib
```
### Server
Run the server:
```
java com.orangomango.multiplayer.Server <host> <port>
```
### Player
Connect a player:
```
java --module-path $FX_PATH --add-modules javafx.controls com.orangomango.multiplayer.Game <host> <port> <userName> <xPos> <yPos> <web-color>
```
For example: `java --module-path $FX_PATH --add-modules javafx.controls com.orangomango.multiplayer.Game 127.0.0.1 1234 Paul 200 200 "#ff3333"`
## Controls
Use <Kbd>W</Kbd><Kbd>A</Kbd><Kbd>S</Kbd><Kbd>D</Kbd> or arrow keys to move the player
## Screenshot
![multi](https://user-images.githubusercontent.com/61402409/163718305-ebf5a9d3-2aab-4d4e-bd29-56c1a4b0e78d.png)

