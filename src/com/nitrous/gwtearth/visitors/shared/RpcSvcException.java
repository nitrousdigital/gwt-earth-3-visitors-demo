package com.nitrous.gwtearth.visitors.shared;

import java.io.Serializable;

/**
 * An exception that can be sent to the GWT client with a user-friendly message
 * @author nick
 *
 */
public class RpcSvcException extends Exception implements Serializable {
	private static final long serialVersionUID = 2061848149501995220L;

	public RpcSvcException() {
	}
	
	public RpcSvcException(String message) {
		super(message);
	}
}
