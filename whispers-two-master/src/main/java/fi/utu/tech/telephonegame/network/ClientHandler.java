package fi.utu.tech.telephonegame.network;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientHandler extends Thread {
    private Socket cs;
    LinkedBlockingQueue<Object> inputmessages;
    LinkedBlockingQueue<Serializable> outputmessages;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;

    public ClientHandler(Socket cs, LinkedBlockingQueue<Object> inputmessages, LinkedBlockingQueue<Serializable> outputmessages) {
        this.cs = cs;
        this.inputmessages = inputmessages;
        this.outputmessages = outputmessages;
        try {
            outputStream = new ObjectOutputStream(cs.getOutputStream());
            inputStream = new ObjectInputStream(cs.getInputStream());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendMessage(Serializable msg){
        try {
            outputStream.writeObject(msg);
            outputStream.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
            while (true) {
                try {
                    Object obj = inputStream.readObject();
                    inputmessages.put(obj);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    break;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
    }
}
