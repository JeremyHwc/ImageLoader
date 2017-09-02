package imageloader.libin.com.images.imagei;

import android.graphics.Bitmap;

/**
 * @author JeremyHwc;
 * @date 2017/9/2/002 15:07;
 * @email jeremy_hwc@163.com ;
 * @desc 图片下载回调
 */

public interface ImageDownLoadCallBack {

    void onDownLoadSuccess(Bitmap bitmap);

    void onDownLoadFailed();
}
