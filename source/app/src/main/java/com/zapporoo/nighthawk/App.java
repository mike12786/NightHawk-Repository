package com.zapporoo.nighthawk;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.quickblox.core.QBSettings;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.zapporoo.nighthawk.model.ModCheckIn;
import com.zapporoo.nighthawk.model.ModGroup;
import com.zapporoo.nighthawk.model.ModMyClub;
import com.zapporoo.nighthawk.model.ModRating;
import com.zapporoo.nighthawk.model.ModRelationship;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.quickblox.Consts;

import io.fabric.sdk.android.Fabric;


/**
 * Created by Pile on 12/29/2015.
 */
public class App extends MultiDexApplication {
    public static final String YELP_CONSUMER_KEY = "-JwBUPo-0fUHeRkAyKW8LA";
    public static final String YELP_CONSUMER_SECRET = "0LuG-Q1SOtIvB9fOJKphssQJv8g";
    public static final String YELP_TOKEN = "QEc11lu9xJPEuTCkbIoOVO5fdJACcn_d";
    public static final String YELP_TOKEN_SECRET = "UK4WT_ViHdX8JZTMFX8XWesydcY";

    public static final boolean TEST_SERVER = false;//false
    public static final boolean DEBUG = false;//false
    public com.quickblox.core.LogLevel LogLevel = com.quickblox.core.LogLevel.NOTHING;

    public static YelpAPI yelpAPI;

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        initApplication();
    }

    private void initApplication() {
        instance = this;
        if (!BuildConfig.DEBUG)
            Fabric.with(this, new Crashlytics());

        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(ModUser.class);
        ParseObject.registerSubclass(ModCheckIn.class);
        ParseObject.registerSubclass(ModMyClub.class);
        ParseObject.registerSubclass(ModGroup.class);
        ParseObject.registerSubclass(ModRating.class);
        ParseObject.registerSubclass(ModRelationship.class);
        Parse.initialize(this, "CDePugZmL3pbcTGnP2cVgFkRUp3MwplZKghASTv2", "5TFRwcHNiQpEOHwsLUTQVK3FfbGZlgRJTbBhmyak");
        ParseFacebookUtils.initialize(this);
        LogLevel = com.quickblox.core.LogLevel.NOTHING;

        QBSettings.getInstance().init(getApplicationContext(), Consts.QUICK_BLOX_APP_ID, Consts.QUICK_BLOX_APP_AUTH_KEY, Consts.QUICK_BLOX_APP_AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(Consts.QUICK_BLOX_APP_ACCOUNT_KEY);

        YelpAPIFactory apiFactory = new YelpAPIFactory(YELP_CONSUMER_KEY, YELP_CONSUMER_SECRET, YELP_TOKEN, YELP_TOKEN_SECRET);
        yelpAPI = apiFactory.createAPI();
        initImageLoader();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public com.quickblox.core.LogLevel getLogLevel() {
        return LogLevel;
    }

    private void initImageLoader() {
        int memoryCacheSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            int memClass = ((ActivityManager)getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
            memoryCacheSize = (memClass / 8) * 1024 * 1024;
        } else {
            memoryCacheSize = 2 * 1024 * 1024;
        }

        final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).threadPoolSize(5)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCacheSize(memoryCacheSize)
                .memoryCache(new FIFOLimitedMemoryCache(memoryCacheSize-1000000))
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs()
                .build();

        ImageLoader.getInstance().init(config);
    }

    public static App getInstance() {
        return instance;
    }

    public int getAppVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}
