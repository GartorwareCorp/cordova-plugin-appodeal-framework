package com.appodeal.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.provider.Settings;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.view.ViewGroup;
import android.view.Gravity;

import androidx.annotation.NonNull;

import java.util.List;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.InterstitialCallbacks;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.appodeal.ads.BannerView;
import com.appodeal.ads.regulator.CCPAUserConsent;
import com.appodeal.ads.regulator.GDPRUserConsent;
import com.appodeal.ads.rewarded.Reward;
import com.appodeal.ads.utils.Log;
import com.appodeal.ads.initializing.ApdInitializationCallback;
import com.appodeal.ads.initializing.ApdInitializationError;
import com.appodeal.consent.Consent;
import com.appodeal.consent.ConsentForm;
import com.appodeal.consent.ConsentFormListener;
import com.appodeal.consent.ConsentInfoUpdateListener;
import com.appodeal.consent.ConsentManager;
import com.appodeal.consent.ConsentManagerError;
import com.appodeal.consent.IConsentFormListener;

import org.json.JSONObject;

public class AppodealPlugin extends CordovaPlugin {

    private static final String TAG = "com.appodeal.plugin";

    private static final String ACTION_IS_INITIALIZED = "isInitalized";

    private static final String ACTION_INITIALIZE = "initialize";

    private static final String ACTION_SHOW = "show";
    private static final String ACTION_SHOW_WITH_PLACEMENT = "showWithPlacement";
    private static final String ACTION_SHOW_BANNER_VIEW = "showBannerView";
    private static final String ACTION_IS_LOADED = "isLoaded";
    private static final String ACTION_CACHE = "cache";
    private static final String ACTION_HIDE = "hide";
    private static final String ACTION_DESTROY = "destroy";
    private static final String ACTION_SET_AUTO_CACHE = "setAutoCache";
    private static final String ACTION_IS_PRECACHE = "isPrecache";

    private static final String ACTION_BANNER_ANIMATION = "setBannerAnimation";
    private static final String ACTION_SMART_BANNERS = "setSmartBanners";
    private static final String ACTION_728X90_BANNERS = "set728x90Banners";
    private static final String ACTION_BANNERS_OVERLAP = "setBannerOverLap";

    private static final String ACTION_SET_TESTING = "setTesting";
    private static final String ACTION_SET_LOGGING = "setLogLevel";
    private static final String ACTION_SET_CHILD_TREATMENT = "setChildDirectedTreatment";
    private static final String ACTION_DISABLE_NETWORK = "disableNetwork";
    private static final String ACTION_DISABLE_NETWORK_FOR_TYPE = "disableNetworkType";
    private static final String ACTION_SET_ON_LOADED_TRIGGER_BOTH = "setTriggerOnLoadedOnPrecache";
    private static final String ACTION_MUTE_VIDEOS_IF_CALLS_MUTED = "muteVideosIfCallsMuted";
    private static final String ACTION_START_TEST_ACTIVITY = "showTestScreen";
    private static final String ACTION_SET_PLUGIN_VERSION = "setPluginVersion";
    private static final String ACTION_GET_VERSION = "getVersion";

    private static final String ACTION_CAN_SHOW = "canShow";
    private static final String ACTION_CAN_SHOW_WITH_PLACEMENT = "canShowWithPlacement";
    private static final String ACTION_SET_CUSTOM_INTEGER_RULE = "setCustomIntegerRule";
    private static final String ACTION_SET_CUSTOM_BOOLEAN_RULE = "setCustomBooleanRule";
    private static final String ACTION_SET_CUSTOM_DOUBLE_RULE = "setCustomDoubleRule";
    private static final String ACTION_SET_CUSTOM_STRING_RULE = "setCustomStringRule";
    private static final String ACTION_GET_REWARD_PARAMETERS = "getRewardParameters";
    private static final String ACTION_GET_REWARD_PARAMETERS_FOR_PLACEMENT = "getRewardParametersForPlacement";
    private static final String ACTION_SET_CUSTOM_FILTER = "setCustomFilter";
    private static final String ACTION_SET_EXTRA_DATA = "setExtraData";
    private static final String ACTION_GET_PREDICTED_ECPM = "getPredictedEcpm";

    private static final String ACTION_SET_USER_ID = "setUserId";
    private static final String ACTION_TRACK_IN_APP_PURCHASE = "trackInAppPurchase";

    private static final String ACTION_SET_INTERSTITIAL_CALLBACKS = "setInterstitialCallbacks";
    private static final String ACTION_SET_REWARDED_CALLBACKS = "setRewardedVideoCallbacks";
    private static final String ACTION_SET_BANNER_CALLBACKS = "setBannerCallbacks";

    private boolean isInitialized = false;
    private boolean bannerOverlap = true;
    private ViewGroup parentView;
    private BannerView bannerView;

    private static final String CALLBACK_INIT = "onInit";
    private static final String CALLBACK_LOADED = "onLoaded";
    private static final String CALLBACK_FAILED = "onFailedToLoad";
    private static final String CALLBACK_CLICKED = "onClick";
    private static final String CALLBACK_SHOWN = "onShown";
    private static final String CALLBACK_CLOSED = "onClosed";
    private static final String CALLBACK_FINISHED = "onFinished";
    private static final String CALLBACK_EXPIRED = "onExpired";
    private static final String CALLBACK_SHOW_FAILED = "onShowFailed";

    private CallbackContext interstitialCallbacks;
    private CallbackContext bannerCallbacks;
    private CallbackContext rewardedCallbacks;

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callback) throws JSONException {

        if (action.equals(ACTION_INITIALIZE)) {
            final String appKey = args.getString(0);
            final int adType = args.getInt(1);
            final boolean showConsentManager = args.optBoolean(2, true);
            final boolean consentValue = args.optBoolean(3, true);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if("true".equals(Settings.System.getString(cordova.getActivity().getContentResolver(), "firebase.test.lab"))) {
                        Appodeal.setTesting(true);
                    }

                    ConsentManager.requestConsentInfoUpdate(cordova.getActivity(), appKey, new ConsentInfoUpdateListener() {

                        private void updateCcpaConsentValue(boolean wasGranted) {
                            if (wasGranted) {
                                Appodeal.updateCCPAUserConsent(CCPAUserConsent.OptIn);
                            } else {
                                Appodeal.updateCCPAUserConsent(CCPAUserConsent.OptOut);
                            }
                        }

                        private void updateGdprConsentValue(boolean wasGranted) {
                            if (wasGranted) {
                                Appodeal.updateGDPRUserConsent(GDPRUserConsent.Personalized);
                            } else {
                                Appodeal.updateGDPRUserConsent(GDPRUserConsent.NonPersonalized);
                            }
                        }

                        private void showConsent() {
                            IConsentFormListener consentFormListener = new ConsentFormListener() {
                                @Override
                                public void onConsentFormLoaded(@NonNull ConsentForm consentForm) {
                                    // Consent form was loaded. Now you can display consent form
                                    consentForm.show();
                                }

                                @Override
                                public void onConsentFormError(@NonNull ConsentManagerError error) {
                                    // Consent form loading or showing failed. More info can be found in 'error' object
                                    // Initialize the Appodeal SDK here.
                                    initializeAppodeal();
                                }

                                @Override
                                public void onConsentFormOpened() {
                                    // Consent form was shown
                                }

                                @Override
                                public void onConsentFormClosed(@NonNull Consent consent) {
                                    // Consent form was closed. Update consent value here.
                                    Appodeal.updateConsent(consent);
                                    // Consent value was updated.
                                    // Initialize the Appodeal SDK here.
                                    initializeAppodeal();
                                }
                            };

                            // Create new Consent form instance
                            ConsentForm consentForm = new ConsentForm(cordova.getActivity(), consentFormListener);

                            // Show the consent form
                            consentForm.load();
                        }

                        private void initializeAppodeal() {
                            log("Initializing SDK");
                            Appodeal.initialize(cordova.getActivity(), appKey, getAdType(adType), new ApdInitializationCallback() {
                                @Override
                                public void onInitializationFinished(List<ApdInitializationError> list) {
                                    //Appodeal initialization finished
                                    isInitialized = true;
                                    log("SDK initialized");
                                    callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, true));
                                }
                            });
                        }

                        @Override
                        public void onConsentInfoUpdated(@NonNull Consent consent) {
                            if(showConsentManager) {
                                super.onConsentInfoUpdated(consent);
                                if (ConsentManager.getShouldShow()) {
                                    // Show consent window to get a user consent.
                                    showConsent();
                                } else {
                                    // Consent value already set. Initialize.
                                    initializeAppodeal();
                                }
                            } else {
                                if(ConsentManager.getShouldShow()){
                                    // Consent value was provided. Set values and initialize.
                                    updateCcpaConsentValue(consentValue);
                                    updateGdprConsentValue(consentValue);

                                    initializeAppodeal();
                                } else {
                                    // Consent value already set. Initialize.
                                    initializeAppodeal();
                                }
                            }
                        }

                        @Override
                        public void onFailedToUpdateConsentInfo(@NonNull ConsentManagerError consentManagerError) {
                            if(showConsentManager) {
                                super.onFailedToUpdateConsentInfo(consentManagerError);
                            }
                            initializeAppodeal();
                        }
                    });
                }
            });
            return true;
        } else if (action.equals(ACTION_IS_INITIALIZED)) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isInitialized) {
                        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, true));
                    } else {
                        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, false));
                    }
                }
            });
            return true;
        } else if (action.equals(ACTION_SHOW)) {
            final int adType = args.getInt(0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int rAdType = getAdType(adType);
                    boolean res;
                    if (rAdType == Appodeal.BANNER || rAdType == Appodeal.BANNER_BOTTOM
                            || rAdType == Appodeal.BANNER_TOP) {
                        res = showBanner(adType, null);
                    } else {
                        res = Appodeal.show(cordova.getActivity(), getAdType(adType));
                    }
                    callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, res));
                }
            });
            return true;
        } else if (action.equals(ACTION_SHOW_WITH_PLACEMENT)) {
            final int adType = args.getInt(0);
            final String placement = args.getString(1);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int rAdType = getAdType(adType);
                    boolean res = false;
                    if (rAdType == Appodeal.BANNER || rAdType == Appodeal.BANNER_BOTTOM
                            || rAdType == Appodeal.BANNER_TOP) {
                        res = showBanner(adType, placement);
                    } else {
                        res = Appodeal.show(cordova.getActivity(), getAdType(adType), placement);
                    }
                    callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, res));
                }
            });
            return true;
        } else if (action.equals(ACTION_IS_LOADED)) {
            final int adType = args.getInt(0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (Appodeal.isLoaded(getAdType(adType))) {
                        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, true));
                    } else {
                        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, false));
                    }
                }
            });
            return true;
        } else if (action.equals(ACTION_CACHE)) {
            final int adType = args.getInt(0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Appodeal.cache(cordova.getActivity(), getAdType(adType));
                }
            });
            return true;
        } else if (action.equals(ACTION_HIDE)) {
            final int adType = args.getInt(0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Appodeal.hide(cordova.getActivity(), getAdType(adType));
                }
            });
            return true;
        } else if (action.equals(ACTION_DESTROY)) {
            final int adType = args.getInt(0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Appodeal.destroy(adType);
                }
            });
            return true;
        } else if (action.equals(ACTION_SET_AUTO_CACHE)) {
            final int adType = args.getInt(0);
            final boolean autoCache = args.getBoolean(1);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Appodeal.setAutoCache(getAdType(adType), autoCache);
                }
            });
            return true;
        } else if (action.equals(ACTION_IS_PRECACHE)) {
            final int adType = args.getInt(0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (Appodeal.isPrecache(getAdType(adType))) {
                        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, true));
                    } else {
                        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, false));
                    }
                }
            });
            return true;
        } else if (action.equals(ACTION_BANNER_ANIMATION)) {
            final boolean value = args.getBoolean(0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Appodeal.setBannerAnimation(value);
                }
            });
            return true;
        } else if (action.equals(ACTION_SMART_BANNERS)) {
            final boolean value = args.getBoolean(0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Appodeal.setSmartBanners(value);
                }
            });
            return true;
        } else if (action.equals(ACTION_728X90_BANNERS)) {
            final boolean value = args.getBoolean(0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Appodeal.set728x90Banners(value);
                }
            });
            return true;
        } else if (action.equals(ACTION_BANNERS_OVERLAP)) {
            final boolean value = args.getBoolean(0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bannerOverlap = value;
                }
            });
            return true;
        } else if (action.equals(ACTION_SET_TESTING)) {
            final boolean testing = args.getBoolean(0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Appodeal.setTesting(testing);
                }
            });
            return true;
        } else if (action.equals(ACTION_SET_LOGGING)) {
            final int logLevel = args.getInt(0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (logLevel) {
                        case 0:
                            Appodeal.setLogLevel(Log.LogLevel.none);
                            break;
                        case 1:
                            Appodeal.setLogLevel(Log.LogLevel.debug);
                            break;
                        case 2:
                            Appodeal.setLogLevel(Log.LogLevel.verbose);
                            break;
                        default:
                            Appodeal.setLogLevel(Log.LogLevel.none);
                            break;
                    }
                }
            });
            return true;
        } else if (action.equals(ACTION_SET_CHILD_TREATMENT)) {
            final boolean value = args.getBoolean(0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Appodeal.setChildDirectedTreatment(value);
                }
            });
            return true;
        } else if (action.equals(ACTION_DISABLE_NETWORK)) {
            final String network = args.getString(0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Appodeal.disableNetwork(network);
                }
            });
            return true;
        } else if (action.equals(ACTION_DISABLE_NETWORK_FOR_TYPE)) {
            final String network = args.getString(0);
            final int adType = args.getInt(1);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Appodeal.disableNetwork(network, getAdType(adType));
                }
            });
            return true;
        } else if (action.equals(ACTION_SET_ON_LOADED_TRIGGER_BOTH)) {
            final int adType = args.getInt(0);
            final boolean setOnTriggerBoth = args.getBoolean(1);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Appodeal.setTriggerOnLoadedOnPrecache(getAdType(adType), setOnTriggerBoth);
                }
            });
            return true;
        } else if (action.equals(ACTION_MUTE_VIDEOS_IF_CALLS_MUTED)) {
            final boolean value = args.getBoolean(0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Appodeal.muteVideosIfCallsMuted(value);
                }
            });
            return true;
        } else if (action.equals(ACTION_START_TEST_ACTIVITY)) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Appodeal.startTestActivity(cordova.getActivity());
                }
            });
            return true;
        } else if (action.equals(ACTION_SET_PLUGIN_VERSION)) {
            final String pluginVersion = args.getString(0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Appodeal.setFramework("cordova", pluginVersion);
                }
            });
            return true;
        } else if (action.equals(ACTION_GET_VERSION)) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.success(Appodeal.getVersion());
                }
            });
            return true;
        } else if (action.equals(ACTION_CAN_SHOW)) {
            final int adType = args.getInt(0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (Appodeal.canShow(getAdType(adType))) {
                        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, true));
                    } else {
                        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, false));
                    }
                }
            });
            return true;
        } else if (action.equals(ACTION_CAN_SHOW_WITH_PLACEMENT)) {
            final int adType = args.getInt(0);
            final String placement = args.getString(1);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (Appodeal.canShow(getAdType(adType), placement)) {
                        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, true));
                    } else {
                        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, false));
                    }
                }
            });
            return true;
        } else if (action.equals(ACTION_SET_CUSTOM_INTEGER_RULE)) {
            final String name = args.getString(0);
            final int value = args.getInt(1);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Appodeal.setExtraData(name, value);
                }
            });
            return true;
        } else if (action.equals(ACTION_SET_CUSTOM_BOOLEAN_RULE)) {
            final String name = args.getString(0);
            final boolean value = args.getBoolean(1);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Appodeal.setExtraData(name, value);
                }
            });
            return true;
        } else if (action.equals(ACTION_SET_CUSTOM_DOUBLE_RULE)) {
            final String name = args.getString(0);
            final double value = args.getDouble(1);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Appodeal.setExtraData(name, value);
                }
            });
            return true;
        } else if (action.equals(ACTION_SET_CUSTOM_STRING_RULE)) {
            final String name = args.getString(0);
            final String value = args.getString(1);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Appodeal.setExtraData(name, value);
                }
            });
            return true;
        } else if (action.equals(ACTION_SET_CUSTOM_FILTER)) {
            final String name = args.getString(0);
            final String value = args.getString(1);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Appodeal.setCustomFilter(name, value);
                }
            });
            return true;
        } else if (action.equals(ACTION_SET_EXTRA_DATA)) {
            final String name = args.getString(0);
            final String value = args.getString(1);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Appodeal.setExtraData(name, value);
                }
            });
            return true;
        } else if (action.equals(ACTION_GET_PREDICTED_ECPM)) {
            final int adType = args.getInt(0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, (float) Appodeal.getPredictedEcpm(adType)));
                }
            });
            return true;
        } else if (action.equals(ACTION_GET_REWARD_PARAMETERS)) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject vals = new JSONObject();
                        Reward reward = Appodeal.getReward();
                        vals.put("amount", reward.getAmount());
                        vals.put("currency", reward.getCurrency());
                        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, vals));
                    } catch (JSONException e) {
                    }
                }
            });
            return true;
        } else if (action.equals(ACTION_GET_REWARD_PARAMETERS_FOR_PLACEMENT)) {
            final String placement = args.getString(0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject vals = new JSONObject();
                        Reward reward = Appodeal.getReward(placement);
                        vals.put("amount", reward.getAmount());
                        vals.put("currency", reward.getCurrency());
                        callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, vals));
                    } catch (JSONException e) {
                    }
                }
            });
            return true;
        } else if (action.equals(ACTION_SET_USER_ID)) {
            final String userId = args.getString(0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Appodeal.setUserId(userId);
                }
            });
            return true;
        } else if (action.equals(ACTION_SET_INTERSTITIAL_CALLBACKS)) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        interstitialCallbacks = callback;
                        Appodeal.setInterstitialCallbacks(interstitialListener);
                        JSONObject vals = new JSONObject();
                        vals.put("event", CALLBACK_INIT);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, vals);
                        result.setKeepCallback(true);
                        callback.sendPluginResult(result);
                    } catch (JSONException e) {
                    }
                }
            });
            return true;
        } else if (action.equals(ACTION_SET_REWARDED_CALLBACKS)) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        rewardedCallbacks = callback;
                        Appodeal.setRewardedVideoCallbacks(rewardedVideoListener);
                        JSONObject vals = new JSONObject();
                        vals.put("event", CALLBACK_INIT);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, vals);
                        result.setKeepCallback(true);
                        callback.sendPluginResult(result);
                    } catch (JSONException e) {
                    }
                }
            });
            return true;
        } else if (action.equals(ACTION_SET_BANNER_CALLBACKS)) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        bannerCallbacks = callback;
                        Appodeal.setBannerCallbacks(bannerListener);
                        JSONObject vals = new JSONObject();
                        vals.put("event", CALLBACK_INIT);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, vals);
                        result.setKeepCallback(true);
                        callback.sendPluginResult(result);
                    } catch (JSONException e) {
                    }
                }
            });
            return true;
        }
        return false;
    }

    private int getAdType(int adtype) {
        int type = 0;
        if ((adtype & 3) > 0) {
            type |= Appodeal.INTERSTITIAL;
        }
        if ((adtype & 4) > 0) {
            type |= Appodeal.BANNER;
        }
        if ((adtype & 8) > 0) {
            type |= Appodeal.BANNER_BOTTOM;
        }
        if ((adtype & 16) > 0) {
            type |= Appodeal.BANNER_TOP;
        }
        if ((adtype & 128) > 0) {
            type |= Appodeal.REWARDED_VIDEO;
        }
        return type;
    }

    private InterstitialCallbacks interstitialListener = new InterstitialCallbacks() {

        @Override
        public void onInterstitialShown() {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject vals = new JSONObject();
                        vals.put("event", CALLBACK_SHOWN);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, vals);
                        result.setKeepCallback(true);
                        interstitialCallbacks.sendPluginResult(result);
                    } catch (JSONException e) {
                    }
                }
            });
        }

        @Override
        public void onInterstitialLoaded(final boolean arg0) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject vals = new JSONObject();
                        vals.put("event", CALLBACK_LOADED);
                        vals.put("isPrecache", arg0);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, vals);
                        result.setKeepCallback(true);
                        interstitialCallbacks.sendPluginResult(result);
                    } catch (JSONException e) {
                    }
                }
            });
        }

        @Override
        public void onInterstitialFailedToLoad() {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject vals = new JSONObject();
                        vals.put("event", CALLBACK_FAILED);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, vals);
                        result.setKeepCallback(true);
                        interstitialCallbacks.sendPluginResult(result);
                    } catch (JSONException e) {
                    }
                }
            });
        }

        @Override
        public void onInterstitialClosed() {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject vals = new JSONObject();
                        vals.put("event", CALLBACK_CLOSED);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, vals);
                        result.setKeepCallback(true);
                        interstitialCallbacks.sendPluginResult(result);
                    } catch (JSONException e) {
                    }
                }
            });
        }

        @Override
        public void onInterstitialExpired() {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject vals = new JSONObject();
                        vals.put("event", CALLBACK_EXPIRED);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, vals);
                        result.setKeepCallback(true);
                        interstitialCallbacks.sendPluginResult(result);
                    } catch (JSONException e) {
                    }
                }
            });
        }

        @Override
        public void onInterstitialClicked() {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject vals = new JSONObject();
                        vals.put("event", CALLBACK_CLICKED);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, vals);
                        result.setKeepCallback(true);
                        interstitialCallbacks.sendPluginResult(result);
                    } catch (JSONException e) {
                    }
                }
            });
        }

        @Override
        public void onInterstitialShowFailed() {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject vals = new JSONObject();
                        vals.put("event", CALLBACK_SHOW_FAILED);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, vals);
                        result.setKeepCallback(true);
                        interstitialCallbacks.sendPluginResult(result);
                    } catch (JSONException e) {
                    }
                }
            });
        }
    };

    private RewardedVideoCallbacks rewardedVideoListener = new RewardedVideoCallbacks() {

        @Override
        public void onRewardedVideoClosed(boolean finished) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject vals = new JSONObject();
                        vals.put("event", CALLBACK_CLOSED);
                        vals.put("finished", finished);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, vals);
                        result.setKeepCallback(true);
                        rewardedCallbacks.sendPluginResult(result);
                    } catch (JSONException e) {
                    }
                }
            });
        }

        @Override
        public void onRewardedVideoExpired() {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject vals = new JSONObject();
                        vals.put("event", CALLBACK_EXPIRED);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, vals);
                        result.setKeepCallback(true);
                        rewardedCallbacks.sendPluginResult(result);
                    } catch (JSONException e) {
                    }
                }
            });
        }

        @Override
        public void onRewardedVideoFailedToLoad() {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject vals = new JSONObject();
                        vals.put("event", CALLBACK_FAILED);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, vals);
                        result.setKeepCallback(true);
                        rewardedCallbacks.sendPluginResult(result);
                    } catch (JSONException e) {
                    }
                }
            });
        }

        @Override
        public void onRewardedVideoFinished(double amount, String name) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject vals = new JSONObject();
                        vals.put("event", CALLBACK_FINISHED);
                        vals.put("amount", amount);
                        vals.put("name", name);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, vals);
                        result.setKeepCallback(true);
                        rewardedCallbacks.sendPluginResult(result);
                    } catch (JSONException e) {
                    }
                }
            });
        }

        @Override
        public void onRewardedVideoLoaded(boolean loaded) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject vals = new JSONObject();
                        vals.put("event", CALLBACK_LOADED);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, vals);
                        result.setKeepCallback(true);
                        rewardedCallbacks.sendPluginResult(result);
                    } catch (JSONException e) {
                    }
                }
            });
        }

        @Override
        public void onRewardedVideoShown() {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject vals = new JSONObject();
                        vals.put("event", CALLBACK_SHOWN);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, vals);
                        result.setKeepCallback(true);
                        rewardedCallbacks.sendPluginResult(result);
                    } catch (JSONException e) {
                    }
                }
            });
        }

        @Override
        public void onRewardedVideoClicked() {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject vals = new JSONObject();
                        vals.put("event", CALLBACK_CLICKED);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, vals);
                        result.setKeepCallback(true);
                        rewardedCallbacks.sendPluginResult(result);
                    } catch (JSONException e) {
                    }
                }
            });
        }

        @Override
        public void onRewardedVideoShowFailed() {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject vals = new JSONObject();
                        vals.put("event", CALLBACK_SHOW_FAILED);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, vals);
                        result.setKeepCallback(true);
                        rewardedCallbacks.sendPluginResult(result);
                    } catch (JSONException e) {
                    }
                }
            });
        }
    };

    private BannerCallbacks bannerListener = new BannerCallbacks() {

        @Override
        public void onBannerClicked() {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject vals = new JSONObject();
                        vals.put("event", CALLBACK_CLICKED);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, vals);
                        result.setKeepCallback(true);
                        bannerCallbacks.sendPluginResult(result);
                    } catch (JSONException e) {
                    }
                }
            });
        }

        @Override
        public void onBannerExpired() {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject vals = new JSONObject();
                        vals.put("event", CALLBACK_EXPIRED);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, vals);
                        result.setKeepCallback(true);
                        bannerCallbacks.sendPluginResult(result);
                    } catch (JSONException e) {
                    }
                }
            });
        }

        @Override
        public void onBannerFailedToLoad() {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject vals = new JSONObject();
                        vals.put("event", CALLBACK_FAILED);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, vals);
                        result.setKeepCallback(true);
                        bannerCallbacks.sendPluginResult(result);
                    } catch (JSONException e) {
                    }
                }
            });
        }

        @Override
        public void onBannerLoaded(final int height, final boolean isPrecache) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject vals = new JSONObject();
                        vals.put("event", CALLBACK_LOADED);
                        vals.put("height", height);
                        vals.put("isPrecache", isPrecache);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, vals);
                        result.setKeepCallback(true);
                        bannerCallbacks.sendPluginResult(result);
                    } catch (JSONException e) {
                    }
                }
            });
        }

        @Override
        public void onBannerShown() {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject vals = new JSONObject();
                        vals.put("event", CALLBACK_SHOWN);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, vals);
                        result.setKeepCallback(true);
                        bannerCallbacks.sendPluginResult(result);
                    } catch (JSONException e) {
                    }
                }
            });
        }

        @Override
        public void onBannerShowFailed() {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject vals = new JSONObject();
                        vals.put("event", CALLBACK_SHOW_FAILED);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, vals);
                        result.setKeepCallback(true);
                        bannerCallbacks.sendPluginResult(result);
                    } catch (JSONException e) {
                    }
                }
            });
        }
    };

    private ViewGroup getViewGroup(int child) {
        ViewGroup vg = this.cordova.getActivity().getWindow().getDecorView()
                .findViewById(android.R.id.content);
        if (child != -1)
            vg = (ViewGroup) vg.getChildAt(child); // child == 0 is view from setContentView
        return vg;
    }

    private boolean showBanner(int adType, String placement) {
        if (bannerView != null && bannerView.getParent() != null) {
            ((ViewGroup) bannerView.getParent()).removeView(bannerView);
        }
        if (bannerView == null)
            bannerView = Appodeal.getBannerView(cordova.getActivity());

        if (bannerOverlap) {
            ViewGroup rootView = getViewGroup(-1);
            if (rootView == null)
                return false;
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            if (adType == Appodeal.BANNER_TOP)
                params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            else
                params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            rootView.addView(bannerView, params);
            rootView.requestLayout();
        } else {
            ViewGroup rootView = getViewGroup(0);
            if (rootView == null)
                return false;
            if (parentView == null) {
                parentView = new LinearLayout(cordova.getActivity());
            }
            if (rootView != parentView) {
                ((LinearLayout) parentView).setOrientation(LinearLayout.VERTICAL);
                parentView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));
                rootView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0F));
                ViewGroup rootParentView = (ViewGroup) rootView.getParent();
                rootParentView.addView(parentView);
                rootParentView.removeView(rootView);
                parentView.addView(rootView);
                //cordova.getActivity().setContentView(parentView);
            }

            if (adType == Appodeal.BANNER_TOP)
                parentView.addView(bannerView, 0);
            else
                parentView.addView(bannerView);

            parentView.bringToFront();
            parentView.requestLayout();
            parentView.requestFocus();
        }
        boolean res = false;
        if (placement == null)
            res = Appodeal.show(cordova.getActivity(), Appodeal.BANNER_VIEW);
        else
            res = Appodeal.show(cordova.getActivity(), Appodeal.BANNER_VIEW, placement);

        return res;
    }

    private static void log(String message) {
        if(Appodeal.getLogLevel().equals(Log.LogLevel.debug) || Appodeal.getLogLevel().equals(Log.LogLevel.verbose)){
            android.util.Log.d(TAG, message);
        }
    }
}