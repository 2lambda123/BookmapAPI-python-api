package com.bookmap.api.rpc.server.addon.listeners.broadcasting;

import com.bookmap.addons.broadcasting.api.view.listeners.UpdateFilterListener;
import com.bookmap.api.rpc.server.log.RpcLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FilterListener implements UpdateFilterListener {

    private Object filter;
    private Method toFilter;
    private final boolean doesRequireFiltering;

    public FilterListener(boolean doesRequireFiltering) {
        this.doesRequireFiltering = doesRequireFiltering;
    }

    @Override
    /**
     * Reacts to filter updates by updating the filter and performing necessary actions.
     *
     * @param o the object representing the updated filter
     * @throws RuntimeException if the method "toFilter" is not found in the filter class
     */
    public void reactToFilterUpdates(Object o) {
        System.out.println("Filter updated " + o);
        if (o != null) {
            System.out.println("Filter is not null");
            filter = o;
            try {
                toFilter = filter.getClass().getDeclaredMethod("toFilter", Object.class);
            } catch (NoSuchMethodException e) {
                RpcLogger.error("Error filter updating", e);
                throw new RuntimeException(e);
            }
        } else {
            RpcLogger.error("Passed filter is null");
        }
    }

    /**
     * Filters the input event using the specified filter.
     *
     * @param event the input event to be filtered
     * @return the filtered event, or the original event if no filter is specified or filtering is not required
     * @throws RuntimeException if an error occurs during filtering
     */
    public Object toFilter(Object event) {
        if (filter != null && doesRequireFiltering) {
            try {
                return toFilter.invoke(filter, event);
            } catch (InvocationTargetException | IllegalAccessException e) {
                RpcLogger.error("Error during filtering", e);
                throw new RuntimeException(e);
            }
        }
        return event;
    }
}
