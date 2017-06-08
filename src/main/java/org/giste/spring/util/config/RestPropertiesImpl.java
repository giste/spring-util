package org.giste.spring.util.config;

/**
 * Configuration class for storing Rest Server properties.
 * 
 * @author Giste
 */
public class RestPropertiesImpl implements RestProperties {

	private String scheme;
	private String host;
	private int port;
	private String path;

	@Override
	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	@Override
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
