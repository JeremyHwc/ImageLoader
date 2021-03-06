package imageloader.libin.com.images.loader;

import android.content.Context;
import android.view.View;

import com.bumptech.glide.MemoryCategory;

import imageloader.libin.com.images.config.GlobalConfig;
import imageloader.libin.com.images.config.SingleConfig;
import imageloader.libin.com.images.utils.DownLoadImageService;

/**
 * @author JeremyHwc;
 * @date 2017/9/2/002 10:02;
 * @desc ImageLoader是封装好所有的方法供用户使用的;
 * @email jeremy_hwc@163.com .
 */
public class ImageLoader {
    public static Context context;
    /**
     * 默认最大缓存
     */
    public static int CACHE_IMAGE_SIZE = 250;


    public static void init(final Context context) {
        init(context, CACHE_IMAGE_SIZE);
    }

    public static void init(final Context context, int cacheSizeInM) {
        init(context, cacheSizeInM, MemoryCategory.NORMAL);
    }

    public static void init(final Context context, int cacheSizeInM, MemoryCategory memoryCategory) {
        init(context, cacheSizeInM, memoryCategory, true);
    }

    /**
     * @param context        上下文
     * @param cacheSizeInM   Glide默认磁盘缓存最大容量250MB
     * @param memoryCategory 调整内存缓存的大小 LOW(0.5f) ／ NORMAL(1f) ／ HIGH(1.5f);
     * @param isInternalCD   true 磁盘缓存到应用的内部目录 / false 磁盘缓存到外部存
     */
    public static void init(final Context context, int cacheSizeInM, MemoryCategory memoryCategory, boolean isInternalCD) {
        ImageLoader.context = context;
        GlobalConfig.init(context, cacheSizeInM, memoryCategory, isInternalCD);
    }

    /**
     * 获取当前的Loader
     *
     * @return ILoader
     */
    public static ILoader getActualLoader() {
        return GlobalConfig.getLoader();
    }

    /**
     * 加载普通图片
     *
     * @param context
     * @return
     */
    public static SingleConfig.ConfigBuilder with(Context context) {
        return new SingleConfig.ConfigBuilder(context);
    }

    /**
     * 程序在内存清理的时候执行
     *
     * @param level
     */
    public static void trimMemory(int level) {
        getActualLoader().trimMemory(level);
    }

    /**
     * 低内存的时候执行
     */
    public static void clearAllMemoryCaches() {
        getActualLoader().clearAllMemoryCaches();
    }

    /**
     * 取消请求
     */
    public static void pauseRequests() {
        getActualLoader().pause();
    }

    /**
     * 恢复请求
     */
    public static void resumeRequests() {
        getActualLoader().resume();
    }

    /**
     * Cancel any pending loads Glide may have for the view and free any resources that may have been loaded for the view.
     *
     * @param view
     */
    public static void clearMomoryCache(View view) {
        getActualLoader().clearMomoryCache(view);
    }


    /**
     * Clears disk cache.
     * This method should always be called on a background thread, since it is a blocking call.
     */
    public static void clearDiskCache() {
        getActualLoader().clearDiskCache();
    }

    /**
     * Clears as much memory as possible.
     */
    public static void clearMomory() {
        getActualLoader().clearMomory();
    }

    /**
     * 图片保存到相册
     *
     * @param downLoadImageService
     */
    public static void saveImageIntoGallery(DownLoadImageService downLoadImageService) {
        getActualLoader().saveImageIntoGallery(downLoadImageService);
    }
}
