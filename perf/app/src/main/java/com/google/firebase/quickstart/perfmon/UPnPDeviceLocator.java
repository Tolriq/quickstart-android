package com.google.firebase.quickstart.perfmon;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

import java.util.Collection;

public class UPnPDeviceLocator implements NetworkObjectLocator {

    private static final int STOP_DELAY = 5000;

    private AndroidUpnpService androidUpnpService;
    private boolean stopped;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Context context;

    private final ServiceConnection uPnPServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            if (service instanceof AndroidUpnpService) {
                androidUpnpService = (AndroidUpnpService) service;
                if (stopped) {
                    try {
                        context.unbindService(this);
                    } catch (Exception ignore) {
                    }
                } else {
                    startDiscovery();
                }
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            androidUpnpService = null;
        }
    };

    private DefaultRegistryListener upnpListener = new DefaultRegistryListener() {
        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
        }

        @Override
        public void remoteDeviceAdded(Registry registry, final RemoteDevice device) {
        }
    };

    private Runnable stopServices = new Runnable() {
        @Override
        public void run() {
            stopped = true;
            try {
                if (androidUpnpService != null) {
                    context.unbindService(uPnPServiceConnection);
                    androidUpnpService = null;
                }
            } catch (Exception ignore) {
            }
        }
    };

    public UPnPDeviceLocator(Object function, Context context) {
        this.context = context;
    }

    private void startDiscovery() {
        if (androidUpnpService == null) {
            return;
        }
        try {
            androidUpnpService.getRegistry().removeListener(upnpListener);
        } catch (Exception ignore) {
        }
        try {
            androidUpnpService.getRegistry().addListener(upnpListener);
        } catch (Exception ignore) {
        }

        Collection<RemoteDevice> oldDevices = androidUpnpService.getRegistry().getRemoteDevices();
        for (RemoteDevice device : oldDevices) {
            upnpListener.remoteDeviceAdded(androidUpnpService.getRegistry(), device);
        }

        androidUpnpService.getControlPoint().search();
    }

    @Override
    public void startDiscovery(int timeoutMs, EventListener eventListener) {
        stopped = false;
        handler.removeCallbacks(stopServices);
        if (androidUpnpService == null) {
            context.bindService(new Intent(context, UPnPService.class), uPnPServiceConnection, Context.BIND_AUTO_CREATE);
        } else {
            startDiscovery();
        }
        if (timeoutMs > 0) {
            handler.postDelayed(this::stopDiscovery, timeoutMs);
        }
    }

    @Override
    public void stopDiscovery() {
        if (androidUpnpService != null) {
            androidUpnpService.getRegistry().removeListener(upnpListener);
        }
        handler.postDelayed(stopServices, STOP_DELAY);
    }
}
