var Appodeal = exports;

var exec = require('cordova/exec');
var cordova = require('cordova');

Appodeal.INTERSTITIAL = 3;
Appodeal.BANNER = 4;
Appodeal.BANNER_BOTTOM = 8;
Appodeal.BANNER_TOP = 16;
Appodeal.REWARDED_VIDEO = 128;

Appodeal.BANNER_X_SMART = 0;
Appodeal.BANNER_X_CENTER = 1;
Appodeal.BANNER_X_LEFT = 2;
Appodeal.BANNER_X_RIGHT = 3;

Appodeal.LogLevel = {
    NONE: 0,
    DEBUG: 1,
    VERBOSE: 2
};

Appodeal.pluginVersion = '3.2.0-beta.2';

Appodeal.initialize = function(appKey, adType, showConsentManager, consentValue, callback) {
	exec(null, null, "AppodealPlugin", "setPluginVersion", [Appodeal.pluginVersion]);
    exec(callback, null, "AppodealPlugin", "initialize", [appKey, adType, showConsentManager, consentValue]);
};

Appodeal.show = function(adType, callback) {
    exec(callback, null, "AppodealPlugin", "show", [adType]);
};

Appodeal.showWithPlacement = function(adType, placement, callback) {
    exec(callback, null, "AppodealPlugin", "showWithPlacement", [adType, placement]);
};

Appodeal.showBannerView = function(xAxis, yAxis, placement) {
    exec(null, null, "AppodealPlugin", "showBannerView", [xAxis, yAxis, placement]);
};

Appodeal.isLoaded = function(adType, callback) {
    exec(callback, null, "AppodealPlugin", "isLoaded", [adType]);
};

Appodeal.cache = function(adType) {
    exec(null, null, "AppodealPlugin", "cache", [adType]);
};

Appodeal.hide = function(adType) {
    exec(null, null, "AppodealPlugin", "hide", [adType]);
};

Appodeal.destroy = function(adType) {
	exec(null, null, "AppodealPlugin", "destroy", [adType]);
}

Appodeal.setAutoCache = function(adType, autoCache) {
    exec(null, null, "AppodealPlugin", "setAutoCache", [adType, autoCache]);
};

Appodeal.isPrecache = function(adType, callback) {
    exec(callback, null, "AppodealPlugin", "isPrecache", [adType]);
};

Appodeal.setBannerAnimation = function(value) {
    exec(null, null, "AppodealPlugin", "setBannerAnimation", [value]);
};

Appodeal.setSmartBanners = function(value) {
    exec(null, null, "AppodealPlugin", "setSmartBanners", [value]);
};

Appodeal.set728x90Banners = function(value) {
    exec(null, null, "AppodealPlugin", "set728x90Banners", [value]);
};

Appodeal.setBannerOverLap = function(value) {
    exec(null, null, "AppodealPlugin", "setBannerOverLap", [value]);
};

Appodeal.setTesting = function(testing) {
    exec(null, null, "AppodealPlugin", "setTesting", [testing]);
};

Appodeal.setLogLevel = function(loglevel) {
    exec(null, null, "AppodealPlugin", "setLogLevel", [loglevel]);
};

Appodeal.setChildDirectedTreatment = function(value) {
    exec(null, null, "AppodealPlugin", "setChildDirectedTreatment", [value]);
};

Appodeal.setTriggerOnLoadedOnPrecache = function(set) {
    exec(null, null, "AppodealPlugin", "setOnLoadedTriggerBoth", [set]);
};

Appodeal.disableNetwork = function(network, adType) {
    exec(null, null, "AppodealPlugin", "disableNetwork", [network]);
};

Appodeal.disableNetworkType = function(network, adType) {
    exec(null, null, "AppodealPlugin", "disableNetworkType", [network, adType]);
};

Appodeal.muteVideosIfCallsMuted = function(value) {
    exec(null, null, "AppodealPlugin", "muteVideosIfCallsMuted", [value]);
};

Appodeal.showTestScreen = function(value) {
    exec(null, null, "AppodealPlugin", "showTestScreen", []);
};

Appodeal.getVersion = function(callback) {
    exec(callback, null, "AppodealPlugin", "getVersion", []);
};

Appodeal.getPluginVersion = function(){
    return Appodeal.pluginVersion;
};

Appodeal.isInitialized = function(callback) {
    exec(callback, null, "AppodealPlugin", "isInitalized", []);
};

Appodeal.canShow = function(adType, callback) {
    exec(callback, null, "AppodealPlugin", "canShow", [adType]);
};

Appodeal.canShowWithPlacement = function(adType, placement, callback) {
    exec(callback, null, "AppodealPlugin", "canShowWithPlacement", [adType, placement]);
};

Appodeal.getRewardParameters = function(callback) {
    exec(callback, null, "AppodealPlugin", "getRewardParameters", []);
};

Appodeal.getRewardParametersForPlacement = function(placement, callback) {
    exec(callback, null, "AppodealPlugin", "getRewardParametersForPlacement", [placement]);
};

Appodeal.setCustomFilter = function(name, value) {
    exec(null, null, "AppodealPlugin", "setCustomFilter", [name, value]);
};

Appodeal.setExtraData = function(name, value) {
    exec(null, null, "AppodealPlugin", "setExtraData", [name, value]);
};

Appodeal.getPredictedEcpm = function(adType, callback) {
    exec(callback, null, "AppodealPlugin", "getPredictedEcpm", [adType]);
};

Appodeal.setUserId = function(userid){
    exec(null, null, "AppodealPlugin", "setUserId", [userid]);
};

Appodeal.trackInAppPurchase = function(amount, currency){
    exec(null, null, "AppodealPlugin", "trackInAppPurchase", [amount, currency]);
};

Appodeal.setInterstitialCallbacks = function(callback) {
    exec(callback, null, "AppodealPlugin", "setInterstitialCallbacks", [])
};

Appodeal.setRewardedVideoCallbacks = function(callbacks) {
    exec(callbacks, null, "AppodealPlugin", "setRewardedVideoCallbacks", []);
};

Appodeal.setBannerCallbacks = function(callbacks) {
    exec(callbacks, null, "AppodealPlugin", "setBannerCallbacks", []);
};