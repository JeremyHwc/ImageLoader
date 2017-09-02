package imageloader.libin.com.images.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import imageloader.libin.com.images.config.AnimationMode;
import imageloader.libin.com.images.config.GlobalConfig;
import imageloader.libin.com.images.config.PriorityMode;
import imageloader.libin.com.images.config.ScaleMode;
import imageloader.libin.com.images.config.ShapeMode;
import imageloader.libin.com.images.config.SingleConfig;
import imageloader.libin.com.images.utils.DownLoadImageService;
import imageloader.libin.com.images.utils.ImageUtil;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import jp.wasabeef.glide.transformations.gpu.BrightnessFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.ContrastFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.InvertFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.PixelationFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.SepiaFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.SketchFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.SwirlFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.ToonFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.VignetteFilterTransformation;

/**
 * @author JeremyHwc;
 * @date 2017/9/2/002 10:45;
 * @email jeremy_hwc@163.com ;
 * @desc GlideLoader GlideLoader实现ILoader接口;
 */
public class GlideLoader implements ILoader {

    /**
     * @param context        上下文
     * @param cacheSizeInM   Glide默认磁盘缓存最大容量250MB
     * @param memoryCategory 调整内存缓存的大小 LOW(0.5f) ／ NORMAL(1f) ／ HIGH(1.5f);
     * @param isInternalCD   true 磁盘缓存到应用的内部目录 / false 磁盘缓存到外部存
     */
    @Override
    public void init(Context context, int cacheSizeInM, MemoryCategory memoryCategory, boolean isInternalCD) {
        Glide.get(context).setMemoryCategory(memoryCategory);
        //如果在应用当中想要调整内存缓存的大小，开发者可以通过如下方式：
        GlideBuilder builder = new GlideBuilder(context);
        if (isInternalCD) {
            builder.setDiskCache(new InternalCacheDiskCacheFactory(context, cacheSizeInM * 1024 * 1024));
        } else {
            builder.setDiskCache(new ExternalCacheDiskCacheFactory(context, cacheSizeInM * 1024 * 1024));
        }
    }

    @Override
    public void request(final SingleConfig config) {
        RequestManager requestManager = Glide.with(config.getContext());
        DrawableTypeRequest request = getDrawableTypeRequest(config, requestManager);
        if (config.isAsBitmap()) {// 作为bitmap
            SimpleTarget target = new SimpleTarget<Bitmap>(config.getWidth(), config.getHeight()) {
                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                    config.getBitmapListener().onSuccess(bitmap);//返回bitmap
                }
            };
            //设置图片滤镜和形状
            setShapeModeAndBlur(config, request);

            // 是否跳过磁盘存储
            if (config.getDiskCacheStrategy() != null) {
                request.diskCacheStrategy(config.getDiskCacheStrategy());
            }

            request.asBitmap().into(target);

        } else {

            if (request == null) {
                return;
            }

            // 占位图
            //只有在图片源为网络图片,并且图片没有缓存到本地时,才给显示placeholder
            if (ImageUtil.shouldSetPlaceHolder(config)) {
                request.placeholder(config.getPlaceHolderResId());
            }
            // 缩放模式
            int scaleMode = config.getScaleMode();
            switch (scaleMode) {
                case ScaleMode.CENTER_CROP:
                    request.centerCrop();
                    break;
                case ScaleMode.FIT_CENTER:
                    request.fitCenter();
                    break;
                default:
                    request.fitCenter();
                    break;
            }
            // 设置图片滤镜和形状
            setShapeModeAndBlur(config, request);

            // 设置缩略图
            if (config.getThumbnail() != 0) {
                request.thumbnail(config.getThumbnail());
            }

            // 设置图片加载的分辨 sp
            if (config.getoWidth() != 0 && config.getoHeight() != 0) {
                request.override(config.getoWidth(), config.getoHeight());
            }

            // 是否跳过磁盘存储
            if (config.getDiskCacheStrategy() != null) {
                request.diskCacheStrategy(config.getDiskCacheStrategy());
            }

            // 设置图片加载动画
            setAnimator(config, request);

            // 设置图片加载优先级
            setPriority(config, request);

            // 加载失败图
            if (config.getErrorResId() > 0) {
                request.error(config.getErrorResId());
            }

            // 是否是gif格式
            if (config.isGif()) {
                request.asGif();
            }
            
            // 显示到ImageView
            if (config.getTarget() instanceof ImageView) {
                request.into((ImageView) config.getTarget());
            }

        }

    }

    /**
     * 设置加载优先级
     *
     * @param config
     * @param request
     */
    private void setPriority(SingleConfig config, DrawableTypeRequest request) {
        switch (config.getPriority()) {
            //低
            case PriorityMode.PRIORITY_LOW:
                request.priority(Priority.LOW);
                break;
            //正常
            case PriorityMode.PRIORITY_NORMAL:
                request.priority(Priority.NORMAL);
                break;
            //高
            case PriorityMode.PRIORITY_HIGH:
                request.priority(Priority.HIGH);
                break;
            //立即
            case PriorityMode.PRIORITY_IMMEDIATE:
                request.priority(Priority.IMMEDIATE);
                break;
            default:
                request.priority(Priority.IMMEDIATE);
                break;
        }
    }

    /**
     * 设置加载进入动画
     *
     * @param config
     * @param request
     */
    private void setAnimator(SingleConfig config, DrawableTypeRequest request) {
        if (config.getAnimationType() == AnimationMode.ANIMATIONID) {
            request.animate(config.getAnimationId());
        } else if (config.getAnimationType() == AnimationMode.ANIMATOR) {
            request.animate(config.getAnimator());
        } else if (config.getAnimationType() == AnimationMode.ANIMATION) {
            request.animate(config.getAnimation());
        }
    }

    /**
     * @desc 根据相应的config配置返回 DrawableTypeRequest
     * @param config
     * @param requestManager
     * @return
     */
    @Nullable
    private DrawableTypeRequest getDrawableTypeRequest(SingleConfig config, RequestManager requestManager) {
        DrawableTypeRequest request = null;
        if (!TextUtils.isEmpty(config.getUrl())) {//网络图
            request = requestManager.load(ImageUtil.appendUrl(config.getUrl()));
            Log.i("GlideLoader", "getUrl : " + config.getUrl());
        } else if (!TextUtils.isEmpty(config.getFilePath())) {//文件路径
            request = requestManager.load(ImageUtil.appendUrl(config.getFilePath()));
            Log.i("GlideLoader", "getFilePath : " + config.getFilePath());
        } else if (!TextUtils.isEmpty(config.getContentProvider())) {//内容提供者
            request = requestManager.loadFromMediaStore(Uri.parse(config.getContentProvider()));
            Log.i("GlideLoader", "getContentProvider : " + config.getContentProvider());
        } else if (config.getResId() > 0) {//资源id
            request = requestManager.load(config.getResId());
            Log.i("GlideLoader", "getResId : " + config.getResId());
        } else if (config.getFile() != null) {//文件
            request = requestManager.load(config.getFile());
            Log.i("GlideLoader", "getFile : " + config.getFile());
        } else if (!TextUtils.isEmpty(config.getAssertspath())) {
            request = requestManager.load(config.getAssertspath());
            Log.i("GlideLoader", "getAssertspath : " + config.getAssertspath());
        } else if (!TextUtils.isEmpty(config.getRawPath())) {
            request = requestManager.load(config.getRawPath());
            Log.i("GlideLoader", "getRawPath : " + config.getRawPath());
        }
        return request;
    }

    /**
     * 设置图片滤镜和形状
     *
     * @param config
     * @param request
     */
    private void setShapeModeAndBlur(SingleConfig config, DrawableTypeRequest request) {

        int count = 0;

        Transformation[] transformation = new Transformation[statisticsCount(config)];

        //高斯模糊
        if (config.isNeedBlur()) {
            transformation[count] = new BlurTransformation(config.getContext(), config.getBlurRadius());
            count++;
        }

        //调节图片亮度
        if (config.isNeedBrightness()) {
            transformation[count] = new BrightnessFilterTransformation(config.getContext(), config.getBrightnessLeve()); //亮度
            count++;
        }

        //黑白效果
        if (config.isNeedGrayscale()) {
            transformation[count] = new GrayscaleTransformation(config.getContext()); //黑白效果
            count++;
        }

        if (config.isNeedFilteColor()) {
            transformation[count] = new ColorFilterTransformation(config.getContext(), config.getFilteColor());
            count++;
        }

        //漩涡效果
        if (config.isNeedSwirl()) {
            transformation[count] = new SwirlFilterTransformation(config.getContext(), 0.5f, 1.0f, new PointF(0.5f, 0.5f)); //漩涡
            count++;
        }

        //油画效果
        if (config.isNeedToon()) {
            transformation[count] = new ToonFilterTransformation(config.getContext());
            count++;
        }

        //水墨画效果
        if (config.isNeedSepia()) {
            transformation[count] = new SepiaFilterTransformation(config.getContext());
            count++;
        }

        //锐化效果
        if (config.isNeedContrast()) {
            transformation[count] = new ContrastFilterTransformation(config.getContext(), config.getContrastLevel()); //锐化
            count++;
        }

        //胶片效果
        if (config.isNeedInvert()) {
            transformation[count] = new InvertFilterTransformation(config.getContext()); //胶片
            count++;
        }

        //马赛克效果
        if (config.isNeedPixelation()) {
            transformation[count] = new PixelationFilterTransformation(config.getContext(), config.getPixelationLevel());
            count++;
        }

        //素描效果
        if (config.isNeedSketch()) {
            transformation[count] = new SketchFilterTransformation(config.getContext());
            count++;
        }

        //晕映效果
        if (config.isNeedVignette()) {
            transformation[count] = new VignetteFilterTransformation(config.getContext(), new PointF(0.5f, 0.5f),
                    new float[]{0.0f, 0.0f, 0.0f}, 0f, 0.75f);
            count++;
        }

        switch (config.getShapeMode()) {
            case ShapeMode.RECT://矩形

                break;
            case ShapeMode.RECT_ROUND://圆角矩形
                transformation[count] = new RoundedCornersTransformation
                        (config.getContext(), config.getRectRoundRadius(), 0, RoundedCornersTransformation.CornerType.ALL);
                count++;
                break;
            case ShapeMode.OVAL://椭圆
                transformation[count] = new CropCircleTransformation(config.getContext());
                count++;
                break;

            case ShapeMode.SQUARE://正方形
                transformation[count] = new CropSquareTransformation(config.getContext());
                count++;
                break;
        }

        if (transformation.length != 0) {
            request.bitmapTransform(transformation);

        }
    }

    /**
     * 统计图片的被设置了哪些状态数
     * @param config
     * @return
     */
    private int statisticsCount(SingleConfig config) {
        int count = 0;
        if (config.getShapeMode() == ShapeMode.OVAL || config.getShapeMode() == ShapeMode.RECT_ROUND || config.getShapeMode() == ShapeMode.SQUARE) {
            count++;
        }

        if (config.isNeedBlur()) {
            count++;
        }

        if (config.isNeedFilteColor()) {
            count++;
        }

        if (config.isNeedBrightness()) {
            count++;
        }

        if (config.isNeedGrayscale()) {
            count++;
        }

        if (config.isNeedSwirl()) {
            count++;
        }

        if (config.isNeedToon()) {
            count++;
        }

        if (config.isNeedSepia()) {
            count++;
        }

        if (config.isNeedContrast()) {
            count++;
        }

        if (config.isNeedInvert()) {
            count++;
        }

        if (config.isNeedPixelation()) {
            count++;
        }

        if (config.isNeedSketch()) {
            count++;
        }

        if (config.isNeedVignette()) {
            count++;
        }

        return count;
    }

    /**
     * 取消请求
     */
    @Override
    public void pause() {
        Glide.with(GlobalConfig.context).pauseRequestsRecursive();
    }

    /**
     * 恢复请求（当列表在滑动的时候，调用pauseRequests()取消请求，滑动停止时，
     * 调用resumeRequests()恢复请求 等等）
     */
    @Override
    public void resume() {
        Glide.with(GlobalConfig.context).resumeRequestsRecursive();
    }

    /**
     * 清除磁盘缓存(必须在后台线程中调用)
     */
    @Override
    public void clearDiskCache() {
        Glide.get(GlobalConfig.context).clearDiskCache();
    }

    /**
     * 清除指定view的缓存
     *
     * @param view
     */
    @Override
    public void clearMomoryCache(View view) {
        Glide.clear(view);
    }

    /**
     * 清除内存缓存(必须在UI线程中调用)
     */
    @Override
    public void clearMomory() {
        Glide.get(GlobalConfig.context).clearMemory();
    }

    @Override
    public boolean isCached(String url) {
        return false;
    }

    @Override
    public void trimMemory(int level) {
        Glide.with(GlobalConfig.context).onTrimMemory(level);
    }

    @Override
    public void clearAllMemoryCaches() {
        Glide.with(GlobalConfig.context).onLowMemory();
    }

    /**
     * 保存图片在相册
     * @param downLoadImageService
     */
    @Override
    public void saveImageIntoGallery(DownLoadImageService downLoadImageService) {
        new Thread(downLoadImageService).start();
    }

}
