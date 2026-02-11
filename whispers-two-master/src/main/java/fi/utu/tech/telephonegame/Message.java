package fi.utu.tech.telephonegame;

import fi.utu.tech.telephonegame.network.ClientHandler;

import java.io.Serializable;
import java.util.UUID;

/**
 * 
 * Message objects should carry information about the message contents being
 * sent "the actual message" (string) as well as the current "color" (integer)
 * of the message (shown in the GUI) and an identifier (UUID) to identify the 
 * message even if its string content was modified by other nodes.
 * 
 * You do not need to worry about the color too much. It is just an integer. Refiner
 * contains a ready-made function to keep the integer in an accepted range. GuiIO
 * GuiIO converts the integer into a visible color.
 * 
 * The message objects will be sent to other nodes by using ObjectStreams
 * 
 * You'll need to make changes to this class to:
 * 
 * 1. Make it possible to be sent over network (ie. it is serializable for Java Object Streams)
 * 2. It contains the required fields (UUID id, String message and Integer color)
 * 3. You are able to access and update the aforementioned attributes to "Refine" them
 * 
 */

public class Message implements Serializable {
	private String message;
	private Integer color;
	private UUID id;

	public Message(String message, Integer color) {
		this(UUID.randomUUID(), message, color);

	}

	public Message(UUID id, String message, Integer color) {
		this.id = id;
		this.message = message;
		this.color = color;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getColor() {
		return color;
	}

	public void setColor(Integer color) {
		this.color = color;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
}
