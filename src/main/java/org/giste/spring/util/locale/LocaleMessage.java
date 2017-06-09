package org.giste.spring.util.locale;

import org.springframework.context.MessageSourceResolvable;

/**
 * Interface to look for localized messages.
 * 
 * @author Giste
 */
public interface LocaleMessage {
	/**
	 * Retrieves a localized message formatted with parameters.
	 * 
	 * @param id Identifier of message to look for.
	 * @param params Parameters to format the message.
	 * @return The localized formatted message.
	 */
	String getMessage(String id, Object[] params);

	/**
	 * Retrieves a localized message without parameters.
	 * 
	 * @param id Identifier of message to look for.
	 * @return The localized message.
	 */
	String getMessage(String id);

	/**
	 * Retrieves a localized message from a MessageSourceResolvable.
	 * 
	 * @param resolvable The MessageSourceResolvable for retrieving the message.
	 * @return The localized message.
	 */
	String getMessage(MessageSourceResolvable resolvable);
}
