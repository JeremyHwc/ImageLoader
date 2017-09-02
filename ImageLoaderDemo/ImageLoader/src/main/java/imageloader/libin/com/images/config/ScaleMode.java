package imageloader.libin.com.images.config;

/**
 * @author JeremyHwc;
 * @date 2017/9/2/002 15:06;
 * @email jeremy_hwc@163.com ;
 * @desc
 */

public interface ScaleMode {
    /**
     * 等比例缩放图片，直到图片的宽高都大于等于ImageView的宽度，然后截取中间的显示
     */
    int CENTER_CROP = 1;
    /**
     * 等比例缩放图片，宽或者是高等于ImageView的宽或者是高
     */
    int FIT_CENTER = 2;
}
