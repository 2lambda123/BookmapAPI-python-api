package com.bookmap.api.rpc.server.addon;

import com.bookmap.addons.broadcasting.api.view.BroadcasterConsumer;
import com.bookmap.addons.broadcasting.api.view.Event;
import com.bookmap.addons.broadcasting.api.view.GeneratorInfo;

import com.bookmap.api.rpc.server.EventLoop;
import com.bookmap.api.rpc.server.addon.listeners.broadcasting.*;
import com.bookmap.api.rpc.server.log.RpcLogger;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

public class Connector {
    private final BroadcasterConsumer broadcasterConsumer;
    private final ConnectionListener connectionListener;
    private final Map<String, LiveConnectionListener> liveSubscriptionListenersByAlias = new ConcurrentHashMap<>();
    private final ExecutorService service = Executors.newSingleThreadExecutor();

    public Connector(BroadcasterConsumer broadcasterConsumer) {
        this.broadcasterConsumer = broadcasterConsumer;
        connectionListener = new ConnectionListener();
    }

    /**
     * Returns the BroadcasterConsumer object.
     *
     * @return the BroadcasterConsumer object
     * @throws NullPointerException if the BroadcasterConsumer object is null
     */
    public BroadcasterConsumer getBroadcasterConsumer() {
        return broadcasterConsumer;
    }

    public Future<Boolean> connect(String providerName) {
        RpcLogger.info("Connecting to " + providerName);
        broadcasterConsumer.connectToProvider(providerName, connectionListener);
        return service.submit(() -> {
            int numberOfTries = 30;
            for (int i = 1; i <= numberOfTries; i++) {
                if (isConnected()) {
                    RpcLogger.info("Successfully connected on " + i + " try");
                    return true;
                }
                try {
                    if (i % 10 == 0) {
                        RpcLogger.info(String.format("Tried to connect %d times", i));
                    }
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    RpcLogger.error("Subscribing to live data interrupted ", e);
                }
            }
            return false;
        });
    }

    public boolean isConnected(){
        return connectionListener.isConnected();
    }

    /**
     * Checks if the application is connected to the specified provider.
     *
     * @param providerName the name of the provider to check connection with
     * @return true if the application is connected to the specified provider, false otherwise
     * @throws NullPointerException if the specified provider name is null
     */
    public boolean isConnectedToProvider(String providerName) {
        System.out.println(broadcasterConsumer.getSubscriptionProviders());
        return broadcasterConsumer.getSubscriptionProviders().contains(providerName);
    }


    /**
     * Subscribes to live data from a specified generator.
     *
     * @param generatorName the name of the generator
     * @param eventLoop the event loop to use for handling events
     * @param providerName the name of the data provider
     * @param doesRequireFiltering indicates whether filtering is required
     * @throws IllegalStateException if not connected to the data source
     */
    public void subscribeToLiveData(String generatorName, EventLoop eventLoop, String providerName, boolean doesRequireFiltering) {
        if (isConnected()) {
            Optional<GeneratorInfo> generatorInfoOptional = getGeneratorInfo(generatorName, providerName);
            generatorInfoOptional.ifPresent(generatorInfo -> {
                LiveConnectionListener subscriptionListener =
                        liveSubscriptionListenersByAlias.computeIfAbsent(generatorName, listener ->
                                new LiveConnectionListener(generatorInfo, broadcasterConsumer));

                FilterListener filterListener = new FilterListener(doesRequireFiltering);

                // Creating a listener for the events themselves.
                EventListener eventListener = new EventListener(eventLoop, generatorName, filterListener);

                // Trying to subscribe for live events.
                // Broadcasting will notify us of a successful subscription through the LiveConnectionListener.

                broadcasterConsumer.setListenersForGenerator(providerName, generatorName, filterListener, new SettingsListener(eventLoop, generatorName));
                broadcasterConsumer.subscribeToLiveData(providerName, generatorInfo.getGeneratorName(),
                        Event.class, eventListener, subscriptionListener);
            });
        }
    }

    private Optional<GeneratorInfo> getGeneratorInfo(String generatorName, String providerName) {
        GeneratorInfo generatorInfo = null;
        List<GeneratorInfo> generatorsInfo = broadcasterConsumer.getGeneratorsInfo(providerName);
        for (GeneratorInfo generator : generatorsInfo) {
            // In AbsorptionIndicator, generators have the names of the aliases they work with.
            // Therefore, according to the alias, we will get a suitable generator for us.
            if (generator.getGeneratorName().equals(generatorName)) {
                generatorInfo = generator;
                break;
            }
        }
        return Optional.ofNullable(generatorInfo);
    }

    public void finish() {
        service.shutdownNow();
    }

    public boolean isSubscribeToLive(String alias){
        LiveConnectionListener listener = liveSubscriptionListenersByAlias.get(alias);
        return listener != null && listener.isConnect();
    }
}
