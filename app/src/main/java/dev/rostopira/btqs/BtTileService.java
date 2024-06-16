package dev.rostopira.btqs;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class BtTileService extends TileService implements BtStateListener {
    BtStateReceiver btStateReceiver;

    @Override
    public void onStartListening() {
        super.onStartListening();
        if (btStateReceiver != null) {
            return;
        }
        btStateReceiver = BtStateReceiver.createAndRegister(this, this);
        btStateReceiver.onReceive(this, null);
    }

    @Override
    public void onBtStateChanged(BtState state, String deviceName) {
        final Tile tile = getQsTile();
        if (tile == null) {
            return;
        }
        tile.setLabel("Bluetooth");
        switch (state) {
            case DISABLED:
                tile.setIcon(Icon.createWithResource(this, R.drawable.disabled));
                tile.setSubtitle(getString(R.string.off));
                tile.setState(Tile.STATE_INACTIVE);
                break;
            case ENABLED:
                tile.setIcon(Icon.createWithResource(this, R.drawable.enabled));
                tile.setSubtitle(getString(R.string.disconnected));
                tile.setState(Tile.STATE_ACTIVE);
                break;
            case CONNECTED:
                tile.setIcon(Icon.createWithResource(this, R.drawable.connected));
                tile.setSubtitle(deviceName != null ? deviceName : getString(R.string.connected));
                tile.setState(Tile.STATE_ACTIVE);
                break;
        }
        tile.updateTile();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onClick() {
        super.onClick();
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter.isEnabled()) {
            adapter.disable();
        } else {
            adapter.enable();
        }
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        if (btStateReceiver != null) {
            unregisterReceiver(btStateReceiver);
            btStateReceiver = null;
        }
    }
}
