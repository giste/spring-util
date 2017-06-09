package org.giste.spring.util.locale;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Implementation for {@link LocaleMessage}
 * 
 * @author Giste
 */
public class LocaleMessageImpl implements LocaleMessage {

	private static final Logger logger = LoggerFactory.getLogger(LocaleMessage.class);

	private MessageSource messageSource;

	/**
	 * Creates a new LocaleMessageImpl with a given MessageSource.
	 * 
	 * @param messageSource The MessageSource to use.
	 */
	public LocaleMessageImpl(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public String getMessage(String code, Object[] params) {
		final Locale locale = LocaleContextHolder.getLocale();

		logger.debug("Locale: {}", locale);

		return messageSource.getMessage(code, params, locale);
	}

	@Override
	public String getMessage(String code) {
		return getMessage(code, null);
	}

	@Override
	public String getMessage(MessageSourceResolvable resolvable) {
		final Locale locale = LocaleContextHolder.getLocale();

		logger.debug("Locale: {}", locale);

		return messageSource.getMessage(resolvable, locale);
	}

}
