package fi.utu.tech.telephonegame.network;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A concrete implementation of Network interface.
 * Should be able to listen for peers, connect to
 * peer network (by connecting to a peer in existing
 * peer network) as well as send and receive
 * messages from neighbouring peers
 * 
 * You probably need to create more methods and attributes
 * to this class as well as create additional classes
 * to be able to implement all the required functionality
 */
public class NetworkService extends Thread implements Network {
	LinkedBlockingQueue<Object> inputmessages = new LinkedBlockingQueue<>(20);
	LinkedBlockingQueue<Serializable> outputmessages = new LinkedBlockingQueue<>(20);
	private CopyOnWriteArrayList<ClientHandler> list = new CopyOnWriteArrayList<>();

	/*
	 * No need to change the constructor
	 */
	public NetworkService() {
		this.start();
	}

	/**
	 * Creates a server instance and starts listening for new peers on specified port
	 * 
	 * The port used for listening for incoming connections is provided automatically
	 * by the Resolver upon calling.
	 * 
	 * @param serverPort Which port should we start to listen to?
	 * 
	 */
	public void startListening(int serverPort) {
		System.out.printf("I should start listening for new peers at TCP port %d%n", serverPort);
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ServerSocket serverSocket = new ServerSocket(serverPort);
					while (true){
						Socket commSocket = serverSocket.accept();
						ClientHandler ch = new ClientHandler(commSocket, inputmessages, outputmessages);
						ch.start();
						list.add(ch);
					}
				} catch (IOException e){
					e.printStackTrace();
				}
			}
		});
		thread.start();

	}

	/**
	 * This method will be called when connecting to a peer (other whispers
	 * instance)
	 * The IP address and port will be provided by the template (by the resolver)
	 * upon calling.
	 * 
	 * @param peerIP   The IP address to connect to
	 * @param peerPort The TCP port to connect to
	 */
	public void connect(String peerIP, int peerPort) throws IOException, UnknownHostException {
		System.out.printf("I should connect myself to %s, TCP port %d%n", peerIP, peerPort);
		Socket socket = new Socket(peerIP, peerPort);
		ClientHandler ch = new ClientHandler(socket, inputmessages, outputmessages);
		ch.start();
		list.add(ch);
	}


	/**
	 * This method is used to send the message to all connected neighbours (directly connected nodes)
	 * 
	 * @param out The serializable object to be sent to all the connected nodes
	 * 
	 */
	private void sendToNeighbours(Serializable out) {
		// Send the object to all neighbouring nodes
		for(ClientHandler ch : list){
			ch.sendMessage(out);
		}
	}
	
	/**
	 * Add an object to the queue for sending it to the peer network (all neighbours)
	 * 
	 * Note: This method is made for others to use. Ie. The implementation
	 * here is called by others (eg. MessageBroker) to post messages INTO
	 * network. To work out the structure of the internal implementation
	 * see run method and sendToNeighbours
	 * 
	 * @param out The Serializable object to be sent
	 */
	public void postMessage(Serializable out) {
		try {
			outputmessages.put(out);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Read the next message from the queue of received messages.
	 * 
	 * Note: This method is made for others to use. Ie. The implementation
	 * here is called by others (eg. MessageBroker) to get access to messages
	 * received from the peer network.
	 * 
	 * @return The next message
	 */
	public Object retrieveMessage() throws InterruptedException {
		return inputmessages.take();
	}

	/**
	 * Waits for messages from the core application and forwards them to the network
	 * 
	 * Ie. When MessageBroker calls postMessage, the message-to-be-sent should be spooled
	 * into some kind of a producer-consumer-friendly data structure and picked up here for
	 * the actual delivery over sockets.
	 * 
	 * Thread running this method is started in the constructor of NetworkService.
	 * 
	 */
	public void run() {
		while (true) {
			try {
				// We do not have structure (yet) where messages being sent are spooled
				sendToNeighbours(outputmessages.take());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
