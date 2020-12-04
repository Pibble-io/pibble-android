package com.star.pibbledev.wallet.home.samsung.util;

import com.samsung.android.sdk.coldwallet.ScwService;

public class ScwGlobal {

    public static boolean getKeystoreApiLevel() {
        int keystoreApiLevel = ScwService.getInstance().getKeystoreApiLevel();
        return keystoreApiLevel > 0;
    }

    public static boolean getSeedHash() {
        String seedHash = ScwService.getInstance().getSeedHash();
        return (seedHash != null && seedHash.length() > 0);
    }

}
