package com.google.firebase.quickstart.perfmon;

import android.content.Context;

import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidRouter;
import org.fourthline.cling.android.AndroidUpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.protocol.ProtocolFactory;

public class UPnPService extends AndroidUpnpServiceImpl {

    @Override
    protected AndroidUpnpServiceConfiguration createConfiguration() {
        return new AndroidUpnpServiceConfiguration() {

            @Override
            public int getRegistryMaintenanceIntervalMillis() {
                return 3000;
            }

            @Override
            public boolean isReceivedSubscriptionTimeoutIgnored() {
                return false;
            }
        };
    }


    @Override
    protected AndroidRouter createRouter(UpnpServiceConfiguration configuration, ProtocolFactory protocolFactory, Context context) {
        return super.createRouter(configuration, protocolFactory, context);
    }
}
