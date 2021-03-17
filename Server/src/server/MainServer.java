package server;

import server.db.DBConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer extends Thread{
    public static ServerSocket mainSocket;
    private DBConnection db;
    private static MainServer mainServerInstance;

    private MainServer(){
        try{
            mainSocket = new ServerSocket(5050);
        } catch (IOException e){
            System.out.println("Server problem");
        }
    }

    public static MainServer getInstance() {
        if (mainServerInstance == null) {
            mainServerInstance = new MainServer();
        }
        return mainServerInstance;
    }

    public static void deleteInstance() {
        mainServerInstance = null;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket s = mainSocket.accept();
                new GameHandler(s);
            } catch (IOException ex) {
                System.out.println("problem");
            }
        }
    }

    public static void stopClients() throws IOException {  //stops clients when closing server
        for (GameHandler s : GameHandler.clientVector) {
            s.clientSocket.close();
            s.ps.close();
            s.dis.close();
            s.stop();
        }
        GameHandler.clientVector.removeAllElements();
    }
}