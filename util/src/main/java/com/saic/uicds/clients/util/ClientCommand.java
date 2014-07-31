package com.saic.uicds.clients.util;

/**
 * @author roger
 *
 */
public interface ClientCommand {
	public void execute();
	public String getCommand();
	public String getDescription();
}
