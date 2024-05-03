package com.bookmap.api.rpc.server.addon.listeners.broadcasting;

import com.bookmap.addons.broadcasting.api.view.GeneratorInfo;
import com.bookmap.addons.broadcasting.api.view.listeners.ProviderStatusListener;
import com.bookmap.api.rpc.server.services.ProviderStatusService;

import java.util.List;

public class RpcProviderStatusListener implements ProviderStatusListener {

    private final ProviderStatusService providerStatusService;

    public RpcProviderStatusListener(ProviderStatusService providerStatusService) {
        this.providerStatusService = providerStatusService;
    }

    @Override
    public void providerBecameAvailable(String providerName) {
        providerStatusService.addProvider(providerName);
    }

    @Override
    public void providerBecameUnavailable(String providerName) {
        providerStatusService.removeProvider(providerName);
    }

    @Override
    /**
     * Updates the generators for a specific provider.
     *
     * @param providerName   the name of the provider
     * @param generators     the list of GeneratorInfo objects to update
     * @throws SomeException if there is an issue updating the provider status
     */
    public void providerUpdateGenerators(String providerName, List<GeneratorInfo> generators) {
        System.out.println("providerUpdateGenerators " + generators);
        providerStatusService.updateProvider(providerName, generators);
    }
}
