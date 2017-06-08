package org.giste.spring.util.config;

/**
 * Interface for getting access to REST server properties. It has getters
 * for the different properties needed to identify the REST server.
 * 
 * @author Giste
 */
public interface RestProperties {

	/**
	 * Gets the scheme of the REST server (usually HTTP).
	 *  
	 * @return The scheme of the REST server.
	 */
	String getScheme();

	/**
	 * Gets the host part of the URI of the REST server.
	 * 
	 * @return The host name. 
	 */
	String getHost();

	/**
	 * Gets the port to contact REST server.
	 * 
	 * @return The port number.
	 */
	int getPort();

	/**
	 * Gets the common part of the path of the REST server.
	 * 
	 * @return The common part of the path.
	 */
	String getPath();

}