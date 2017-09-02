package imageloader.libin.com.imageloaderdemo;

import android.app.Application;

import imageloader.libin.com.images.loader.ImageLoader;

/**
 * @author JeremyHwc;
 * @date 2017/9/2/002 10:53;
 * @email jeremy_hwc@163.com ;
 * @desc
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoader.init(getApplicationContext());
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        // 程序在内存清理的时候执行
        ImageLoader.trimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // 低内存的时候执行
        ImageLoader.clearAllMemoryCaches();
    }
}
