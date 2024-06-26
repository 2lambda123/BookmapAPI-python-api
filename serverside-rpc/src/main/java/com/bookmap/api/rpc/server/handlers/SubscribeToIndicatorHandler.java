package com.bookmap.api.rpc.server.handlers;

import com.bookmap.api.rpc.server.EventLoop;
import com.bookmap.api.rpc.server.addon.Connector;
import com.bookmap.api.rpc.server.data.income.SubscribeToIndicatorEvent;
import com.bookmap.api.rpc.server.log.RpcLogger;

import java.util.concurrent.*;

public class SubscribeToIndicatorHandler implements Handler<SubscribeToIndicatorEvent> {
    private final EventLoop eventLoop;
    private final Connector connector;
    private final ExecutorService service;

    public SubscribeToIndicatorHandler(EventLoop eventLoop, Connector connector, ExecutorService service) {
        this.eventLoop = eventLoop;
        this.connector = connector;
        this.service = service;
    }

    @Override
    /**
     * Handles the SubscribeToIndicatorEvent by connecting to the provider and subscribing to live data.
     * If the event alias is null, it connects to the provider and subscribes to live data. If the event alias is not null,
     * it checks if already connected to the provider, and if not, connects to the provider and subscribes to live data.
     *
     * @param event the SubscribeToIndicatorEvent to handle
     * @throws RuntimeException if there is an exception during subscribing to live data
     */
    public void handle(SubscribeToIndicatorEvent event) {
        if (event.alias == null) {
            RpcLogger.info("Generator name is null, connecting to provider " + event.addonName);
            Future<Boolean> isConnected = connector.connect(event.addonName);
            service.execute(() -> {
                try {
                    if (isConnected.get()) {
                        RpcLogger.info("Successfully connected to " + event.addonName);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(String.format(
                            "Exception during subscribing to live data for %s," +
                                    " indicator name: %s", event.alias, event.addonName), e);
                }
                System.out.println(connector.getBroadcasterConsumer().getGeneratorsInfo("com.bookmap.addons.marketpulse.app.MarketPulse"));
            });
        } else {
            if (connector.isConnectedToProvider(event.addonName)) {
                connector.subscribeToLiveData(event.alias, eventLoop, event.addonName, event.doesRequireFiltering);
                RpcLogger.info("Successfully connected to " + event.addonName + " " + event.alias);
                return;
            }
            Future<Boolean> isConnected = connector.connect(event.addonName);
            service.execute(() -> {
                try {
                    if (isConnected.get()) {
                        connector.subscribeToLiveData(event.alias, eventLoop, event.addonName, event.doesRequireFiltering);
                        RpcLogger.info("Successfully connected to " + event.addonName + " " + event.alias);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(String.format(
                            "Exception during subscribing to live data for %s," +
                                    " indicator name: %s", event.alias, event.addonName), e);
                }
            });
        }
    }
}
