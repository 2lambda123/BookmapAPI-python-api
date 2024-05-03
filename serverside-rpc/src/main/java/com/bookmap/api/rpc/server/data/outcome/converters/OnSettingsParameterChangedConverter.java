package com.bookmap.api.rpc.server.data.outcome.converters;

import com.bookmap.api.rpc.server.data.utils.EventConverter;
import com.bookmap.api.rpc.server.data.outcome.OnSettingsParameterChangedEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class OnSettingsParameterChangedConverter implements EventConverter<OnSettingsParameterChangedEvent, String> {

	@Inject
	OnSettingsParameterChangedConverter(){}

	@Override
	/**
	 * Converts the OnSettingsParameterChangedEvent entity to a string representation.
	 *
	 * @param entity the OnSettingsParameterChangedEvent entity to be converted
	 * @return a string representation of the entity
	 * @throws NullPointerException if the entity is null
 	*/
	public String convert(OnSettingsParameterChangedEvent entity) {
		StringBuilder builder = new StringBuilder();
		builder.append(entity.type.code)
				.append(FIELDS_DELIMITER)
				.append(entity.alias)
				.append(FIELDS_DELIMITER)
				.append(entity.name)
				.append(FIELDS_DELIMITER)
				.append(entity.parameterType)
				.append(FIELDS_DELIMITER)
				.append(entity.newValue);
		System.out.println("OnSettingsParameterChangedConverter: " + builder.toString());
		return builder.toString();
	}
}
