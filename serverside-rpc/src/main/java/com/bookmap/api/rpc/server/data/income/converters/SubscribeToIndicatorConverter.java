package com.bookmap.api.rpc.server.data.income.converters;

import com.bookmap.api.rpc.server.data.income.SubscribeToIndicatorEvent;
import com.bookmap.api.rpc.server.data.utils.AbstractEvent;
import com.bookmap.api.rpc.server.data.utils.EventConverter;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.bookmap.api.rpc.server.data.utils.EventConverter.FIELDS_DELIMITER;

@Singleton
public class SubscribeToIndicatorConverter implements EventConverter<String, AbstractEvent> {

    @Inject
    SubscribeToIndicatorConverter(){}

    @Override
    /**
     * Converts the given entity string into a SubscribeToIndicatorEvent object.
     *
     * @param entity the entity string to be converted
     * @return a SubscribeToIndicatorEvent object
     * @throws ArrayIndexOutOfBoundsException if the entity string does not contain enough tokens
     * @throws NullPointerException if the entity string is null
     * @throws IllegalArgumentException if the tokens in the entity string are not in the expected format
     */
    public SubscribeToIndicatorEvent convert(String entity) {
        String[] tokens = entity.split(FIELDS_DELIMITER);
        System.out.println("SubscribeToIndicatorConverter: " + entity);
        return new SubscribeToIndicatorEvent(tokens[1], "None".equals(tokens[2]) ? null : tokens[2], Boolean.parseBoolean(tokens[3]));
    }
}
