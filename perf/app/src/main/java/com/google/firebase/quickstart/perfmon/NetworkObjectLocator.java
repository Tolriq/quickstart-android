package com.google.firebase.quickstart.perfmon;

public interface NetworkObjectLocator {

    void startDiscovery(int timeoutMs, EventListener listener);

    void stopDiscovery();

    interface EventListener {
        void onNetworkObjectFound(Object networkObject);

        void onNetworkObjectRemoved(Object networkObject);

        void onError(Exception error);

    }
}
