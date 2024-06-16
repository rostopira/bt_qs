package dev.rostopira.btqs;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Set;

public class BtStateReceiver extends BroadcastReceiver {
    final BtStateListener listener;

    BtStateReceiver(BtStateListener listener) {
        this.listener = listener;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isEnabled()) {
            listener.onBtStateChanged(BtState.DISABLED, null);
            return;
        }
        final Set<BluetoothDevice> paired = adapter.getBondedDevices();
        for (BluetoothDevice device: paired) {
            if (isConnected(device)) {
                listener.onBtStateChanged(BtState.CONNECTED, device.getAlias());
                return;
            }
        }
        listener.onBtStateChanged(BtState.ENABLED, null);
    }

    static BtStateReceiver createAndRegister(Context context, BtStateListener listener) {
        final BtStateReceiver receiver = new BtStateReceiver(listener);
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        context.registerReceiver(receiver, intentFilter);
        return receiver;
    }

    private static boolean isConnected(BluetoothDevice device) {
        try {
            //noinspection JavaReflectionMemberAccess
            final Method method = device.getClass().getMethod("isConnected");
            //noinspection DataFlowIssue
            return (Boolean) method.invoke(device);
        } catch (Exception e) {
            Log.e("BtStateReceiver", "Failed to check connection state", e);
            return false;
        }
    }
}
