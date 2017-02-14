package Sockets;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

/**
 * Handles and accepts new connections.
 * @version 1.0b r34
 */
public class ServerBase implements Runnable {
    protected ServerSocket server;
    public ServerLobby lobby;
    public static String serverIP;
    protected int port = 5588;
    private boolean _run = true;

    public ServerBase(String _serverIP) {
        serverIP = _serverIP;
    }

    public void writeLog(String msg, int txtarea)
    {
        Interface.writeLog(msg, txtarea);
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(port);
            lobby = new ServerLobby();
            writeLog("[Server] Server boot-up success! Running at " + serverIP + ":" + port, 2);
            Interface.btnRestart.setEnabled(true);
            Interface.btnShutdown.setEnabled(true);
            Interface.mUntrace.setEnabled(true);
            Interface.mTrace.setEnabled(true);
            _run = true;
            while (_run) {
                try {
                    Socket socket = server.accept();
                    new Thread(new ConnectionHandler(socket, this, lobby)).start();
                } catch (IOException e) {
                    if(!e.getMessage().contains("socket closed"))
                        writeLog("[Server] " + e.getMessage(), 2);
                }
            }
        } catch (IOException e) {
            writeLog("[Server] " + e.getMessage(), 2);
        }
    }

    public void close() {
        try {
            _run = false;
            server.close();
        } catch (Exception e) {
            writeLog("[Server] " + e.getMessage(), 2);
        }
    }
}
