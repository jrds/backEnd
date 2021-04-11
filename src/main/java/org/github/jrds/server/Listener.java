package org.github.jrds.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener
{
    public static void main(String[] args) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(7000);
        Socket clientSocket = serverSocket.accept();
        System.out.println("Hello");
        clientSocket.close();
        serverSocket.close();
    }
}
