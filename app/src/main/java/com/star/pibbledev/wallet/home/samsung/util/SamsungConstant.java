package com.star.pibbledev.wallet.home.samsung.util;

public class SamsungConstant {

    public static final String KEY_USER_DATA_SAMPLE = "user_data_sample";
    public static final String CWS_PKG_NAME = "com.samsung.android.coldwalletservice";
    public static final String CWS_CLASS_NAME = CWS_PKG_NAME + ".core.CWWalletService";
    public static final String CWHOME_CLASS_NAME = CWS_PKG_NAME + ".ui.CWHomeActivity";

    public static final String mApplicationId = "pm7ufRYJ0b";

    public static final String HD_PATH_ETH = "m/44'/60'/0'/0/";
    public static final String HD_PATH_BTC = "m/44'/0'/0'/0/";

    public static final String SMART_CONTRACT_ADDRESS = "0x1864cE27E9F7517047933CaAE530674e8C70b8A7";
    public static final int DECIMALS = 18;

    public static final String TRANSACTION_FEE_PIB = "300";
    public static final String TRANSACTION_FEE_ETH = "0.001";

    public static final int TOKEN_GET_XPUB_KEY_FOR_ETH = 10001;
    public static final int TOKEN_GET_XPUB_KEY_FOR_BTC = 10002;
    public static final int TOKEN_GET_XPUB_KEY_LIST = 10003;
    public static final int TOKEN_SIGN_ETH = 10004;
    public static final int TOKEN_CHECK_APP_VERSION = 10005;
    public static final int TOKEN_SIGN_MESSAGE = 10006;

    public static final int GAS_LIMIT_ETH = 21000;
    public static final int GAS_LIMIT_PIB = 70000;
}
