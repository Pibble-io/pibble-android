package com.star.pibbledev.auth.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.star.pibbledev.R;
import com.star.pibbledev.auth.EmailVerifyActivity;
import com.star.pibbledev.auth.PhoneVerificationActivity;
import com.star.pibbledev.auth.SigninActivity;
import com.star.pibbledev.auth.SignupActivity;
import com.star.pibbledev.dashboard.DashboardActivity;
import com.star.pibbledev.services.global.Constants;
import com.star.pibbledev.services.global.ParseUtility;
import com.star.pibbledev.services.global.Utility;
import com.star.pibbledev.services.network.RequestListener;
import com.star.pibbledev.services.network.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WebAppInterface implements RequestListener {

    private Context mContext;

    private static final String LOGIN_KEY = "login_key";
    private static final String SNS_TYPE = "sns_type";
    private static final String SNS_ID = "sns_id";
    private static final String NICKNAME = "nickname";
    private static final String GENDER = "gender";
    private static final String AGE = "age";

    private static final String NATIVE_SIGNUP = "native_signup";
    private static final String NATIVE_LOGIN = "native_login";

    private static final String ADD_DEVICE_TOKEN = "add_device_token";
    private static final String GET_PROFILE = "get_profile";
    private static final String SIGN_IN = "sign_in";

    private String requestType;
    private String login_key, sns_type, sns_id, nickname, email, gender, age;

    /** Instantiate the interface and set the context */
    public WebAppInterface(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public void signUp(String type) {

        login_key = "";
        sns_type = "";
        sns_id = "";
        nickname = "";
        email = "";
        gender = "";
        age = "";

        if (Utility.isJSONValid(type)) {

            try {

                JSONObject jsonObject = new JSONObject(type);

                login_key = jsonObject.optString(LOGIN_KEY);
                sns_type = jsonObject.optString(SNS_TYPE);
                sns_id = jsonObject.optString(SNS_ID);
                nickname = jsonObject.optString(NICKNAME);
                email = jsonObject.optString(Constants.EMAIL);
                gender = jsonObject.optString(GENDER);
                age = jsonObject.optString(AGE);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (sns_type.equals(NATIVE_SIGNUP)) {

            Intent intent = new Intent(mContext, SignupActivity.class);
            mContext.startActivity(intent);
            ((Activity)mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        } else {

            if (sns_type.length() > 0 && !sns_type.equals("null")) {

                socialLogin(login_key, sns_type, sns_id, nickname, email, gender, age);

            }
        }
    }

    @JavascriptInterface
    public void signIn(String type) {

        if (Utility.isJSONValid(type)) {

            try {
                JSONObject jsonObject = new JSONObject(type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Intent intent = new Intent(mContext, SigninActivity.class);
        mContext.startActivity(intent);
        ((Activity)mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void socialLogin(String login_key, String sns_type, String sns_id, String nickname, String email, String gender, String age) {

        requestType = SIGN_IN;

        String deviceUUID = Utility.makeDeviceId(mContext);

        Utility.saveUUIDToStorage(mContext, deviceUUID);

        ServerRequest.getSharedServerRequest().authSocialSignin(mContext, this, login_key, sns_type, sns_id, nickname, email, gender, age, deviceUUID);
    }

//    private void normalLogin() {
//
//        String strEmail = "elena.ionkina@hotmail.com";
//        String strPassword = "Qqqqqq4$";
//
//        requestType = SIGN_IN;
//
//        Utility.getSavedPref(mContext).saveString(Constants.EMAIL, strEmail);
//        Utility.getSavedPref(mContext).saveString(Constants.PASSWORD, strPassword);
//
//        ServerRequest.getSharedServerRequest().authSignin(mContext, this, strEmail, strPassword, Utility.g_deviceUUID);
//    }

    private void addDeviceToken() {

        requestType = ADD_DEVICE_TOKEN;

        String accessToken = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);

        if (Utility.g_deviceToken != null && Utility.g_deviceToken.length() > 0) {

            ServerRequest.getSharedServerRequest().addDeviceToken(this, mContext, accessToken, Utility.g_deviceToken);

        } else {

            getUserProfile();

        }
    }

    private void getUserProfile() {

        long currentTime = System.currentTimeMillis() / 1000;

        String expires_in = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_EXPIRED_TIME);

        if (expires_in.equals("null") || expires_in.equals("")) expires_in = "0";

        if (Long.parseLong(expires_in) > currentTime) {

            requestType = GET_PROFILE;

            String access_token = Utility.getReadPref(mContext).getStringValue(Constants.AUTH_ACCESS_TOKEN);
            ServerRequest.getSharedServerRequest().getProfile(this, mContext, access_token);
        }

    }

    private void getProfiledata(JSONObject objResult) {

        ParseUtility.parsingProfiledata(mContext, objResult);

        stepNextPage(true, true);
    }

    private void stepNextPage(boolean isEmail, boolean isPhone) {

        if (isEmail) {

            if (isPhone) {

                Utility.g_indexDashboardTab = 1;

                Intent intent = new Intent(mContext, DashboardActivity.class);
                mContext.startActivity(intent);
                ((Activity)mContext).finish();
                ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

            } else {

                Intent intent = new Intent(mContext, PhoneVerificationActivity.class);
                intent.putExtra(PhoneVerificationActivity.ACTIVITY_TYPE, SigninActivity.TAG);
                mContext.startActivity(intent);
                ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

            }

        } else {

            Intent intent = new Intent(mContext, EmailVerifyActivity.class);
            intent.putExtra("email", Utility.getWithoutLastSpace(email));
            intent.putExtra(EmailVerifyActivity.ACTIVITY_TYPE, SigninActivity.TAG);
            mContext.startActivity(intent);
            ((Activity)mContext).finish();
            ((Activity)mContext).overridePendingTransition(R.anim.trans_in, R.anim.trans_out);

        }
    }

    @Override
    public void succeeded(JSONObject objResult) {

        switch (requestType){

            case SIGN_IN:

                String accessToken = objResult.optString(Constants.AUTH_ACCESS_TOKEN);

                if (accessToken.length() > 0 && !accessToken.equals("null")) {

                    Utility.getSavedPref(mContext).saveString(Constants.AUTH_ACCESS_TOKEN, objResult.optString(Constants.AUTH_ACCESS_TOKEN));
                    Utility.getSavedPref(mContext).saveString(Constants.AUTH_REFRESH_TOKEN, objResult.optString(Constants.AUTH_REFRESH_TOKEN));
                    Utility.getSavedPref(mContext).saveString(Constants.AUTH_EXPIRED_TIME, objResult.optString(Constants.AUTH_EXPIRED_TIME));

                    addDeviceToken();

                } else {

                    Utility.getSavedPref(mContext).saveString(Constants.SIGN_UP_TOKEN, objResult.optString(Constants.SIGN_UP_TOKEN));
                    Utility.getSavedPref(mContext).saveString(Constants.AUTH_REFRESH_TOKEN, objResult.optString(Constants.AUTH_REFRESH_TOKEN));
                    Utility.getSavedPref(mContext).saveString(Constants.AUTH_EXPIRED_TIME, objResult.optString(Constants.AUTH_EXPIRED_TIME));

                    JSONObject userObj = objResult.optJSONObject("user");

                    if (userObj == null) return;

                    boolean is_mail_verified = userObj.optBoolean("is_mail_verified");
                    boolean is_phone_verified = userObj.optBoolean("is_phone_verified");

                    Utility.getSavedPref(mContext).saveBoolean(Constants.EMAIL_VERIFIED, is_mail_verified);
                    Utility.getSavedPref(mContext).saveBoolean(Constants.PHONE_VERIFIED, is_phone_verified);

                    stepNextPage(is_mail_verified, is_phone_verified);
                }

                break;

            case GET_PROFILE:

                getProfiledata(objResult);

                break;

            case ADD_DEVICE_TOKEN:

                getUserProfile();

                break;

        }
    }

    @Override
    public void failed(String strError, int errorCode) {

    }

    @Override
    public void succeedGetArray(JSONArray objResult) {

    }
}
