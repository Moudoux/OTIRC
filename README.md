# OT IRC
This is a custom socket server wich connects with the custom client. Feel free to modify this server to fit your needs, or add new commands, features, etc.

### Requirements

This server requires Java 7 or higher, it works on Windows, Linux and Mac.

### Starting the Server or Client

First please compile it from the source in Eclipse or your preferred Java IDE or via command line. Or you can download a precompiled version here: https://dl.opentexon.com/IRC.zip

If you downloaded the precompiled version, extract it. After that go inside the folder wich contains the .jar file or the directory where you compiled the jar from the source.

Then open a terminal/cmd, to start the server type:

```sh
java -cp IRC.jar com.opentexon.Server.Main.Main
```
To start the Client type:

```sh
java -cp IRC.jar com.opentexon.Client.Main.Main
```

### Commands

There are a few commands to operate the server:

```commands
/kick [Username] [Message] (OP/Console) - Kicks a user, requires operator or executed by console
/ban [Username/IP] [Message] (OP/Console) - Ban a user, requires operator or executed by console
/mute [Username] [Message] (OP/Console) - Mutes a user, requires operator or executed by console
/op [Username/IP] (OP/Console) - Sets operator status on a user, requires operator or executed by console
/deop [Username/IP] (OP/Console) - Removes operator status from a user, requires operator or executed by console
/whois [Username] (OP/Console) - Gives information about a user (ip, current channel, etc), requires operator or executed by console
/unban [Username/IP] (OP/Console) - Unbans a user or ip, requires operator or executed by console
/banlist (OP/Console) - Shows all banned users, requires operator or executed by console
/msg [Username] [Message] - Sends a private messages to someone
/users - Shows all users online
/ver - Shows server version
/glob [Message] - Sends a message to all channels, only usable from console
/channel [Channel] [Message] - Sends a message to a specific channel, only usable from console
```

# License

This software is licensed under the MIT license.

If you wish to modify this software please give credit and link to this git.

