package imageloader.libin.com.images.loader;

import android.content.Context;
import android.view.View;

import com.bumptech.glide.MemoryCategory;

import imageloader.libin.com.images.config.SingleConfig;
import imageloader.libin.com.images.utils.DownLoadImageService;

/**
 * Created by doudou on 2017/4/10.
 */

public interface ILoader {

    void init(Context context, int cacheSizeInM, MemoryCategory memoryCategory, boolean isInternalCD);

    void request(SingleConfig config);

    /**
     * 取消请求
     */
    void pause();

    /**
     * 回复的请求（当列表在滑动的时候，调用pauseRequests()取消请求，滑动停止时，
     * 调用resumeRequests()恢复请求 等等）
     */
    void resume();

    /**
     * 清除磁盘缓存(必须在后台线程中调用)
     */
    void clearDiskCache();

    /**
     * 清除指定view的缓存
     * @param view
     */
    void clearMomoryCache(View view);

    /**
     * 清除内存缓存(必须在UI线程中调用)
     */
    void clearMomory();

    boolean  isCached(String url);

    void trimMemory(int level);

    void clearAllMemoryCaches();

    void saveImageIntoGallery(DownLoadImageService downLoadImageService);
}
