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
## Screenshot
![multi](https://user-images.githubusercontent.com/61402409/163669871-cec914d8-7b81-428d-a057-ede11963e4e9.png)
