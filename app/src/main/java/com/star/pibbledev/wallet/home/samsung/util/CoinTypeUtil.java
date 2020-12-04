package com.star.pibbledev.wallet.home.samsung.util;

import com.samsung.android.sdk.coldwallet.ScwCoinType;

public class CoinTypeUtil {

    public static String coinType2String(int coinType) {
        String ret;
        switch (coinType) {
            case ScwCoinType.BTC:
                ret = "Bitcoin";
                break;
            case ScwCoinType.ETH:
                ret = "Ether";
                break;
            default:
                ret = String.format("Unknown coin type, type=%x", coinType);
                break;
        }
        return ret;
    }
}
