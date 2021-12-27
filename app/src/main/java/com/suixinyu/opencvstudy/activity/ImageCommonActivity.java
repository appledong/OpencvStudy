package com.suixinyu.opencvstudy.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.suixinyu.opencvstudy.R;
import com.suixinyu.opencvstudy.permissions.PermissionsUtils;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Range;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * opencv android函数学习：
 * utils：工具类 mat和bitmap的转换 加载res下资源图片 保存图片到目录并返回绝对路径
 * Imgproc：和image有关的操作都封装在这个类中，功能函数如下（待完善和补充）：
 * cvtColor：图片的颜色模式转换
 * applyColorMap: opencv提供一系列颜色值供使用 下面有详细备注
 * line(); 绘制线
 * arrowedLine(); 绘制箭头线
 * circle(); 绘制圆和点
 * ellipse(); 绘制椭圆和扇形
 * rectangle(); 绘制矩形
 * putText(); 绘制文本
 * drawContours(); 绘制轮廓 用于轮廓检查和绘制
 * Imgcodecs：和image的读写操作有关都封装在这个类中，功能函数如下（待完善和补充）：
 * imread：读取图片 图片路径必须是绝对路径（opencv底层不能识别相对路径）
 * imwrite：写图片到给定路径
 * Core: 和image的像素计算所有操作都封装在这个类中，功能函数如下：
 * add：叠加 subtract：减 multiply：乘 divide：除
 * min:取小 max：取大
 * addWeighted: 融合两个mat到目标mat 可以设置其占比 包括可以添加扩展Scalar
 * bitwise_and bitwise_or bitwise_xor bitwise_no: 像素的逻辑运算：与 或 异火 非
 * split: 分离通道：双参：分离目标mat 分离后的通道列表
 * merge：融合通道：双参：通道列表 目标mat
 * mixChanns：复制初始通道到目标通道，可以指定初始mat和目标mat的通道映射
 * MatOfInt：初始mat和目标mat映射对象 接受int变参 参数格式：初始通道+目标通道+初始通道+目标通道.....
 * inRange：通过设定低高区域像素点筛选0，255mask遮罩层（掩码）即在区域间的像素点设置为255（白色）不在的全部设置为0（黑色）
 * 最终获取到的是0和255两种状态的图片 用来当做遮罩
 * minMaxLoc：计算图片的最小像素 最大像素 最小像素点位置 最大像素点位置 针对单通道：灰度或者遍历通道
 * meanStdDev：计算图片均值 图片标准差 对象可以是单通道和多通道 计算是以通道为单位
 * MatOfDouble：double为单位的mat，构造默认对象即可由内部赋值
 */
public class ImageCommonActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String LOG_TAG = "dongdianzhou";

    private TextView textView, textViewRead, textViewMat, textViewPix;
    private ImageView imageView, imageViewRead, imageViewMat, imageViewPix;
    private TextView textViewCp, textViewLight, textViewColor, textViewLjOper;
    private ImageView imageViewCp, imageViewLight, imageViewColor, imageViewLjOper;
    private TextView textViewConvert, textViewCount, textViewCountDesc;
    private ImageView imageViewConvet;
    private TextView textViewPixOne, textViewTwoValue, textViewOneConvert;
    private ImageView imageViewPixOne, imageViewTwoValue, imageViewOneConvert;
    private SeekBar seekBarLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionsUtils.verifyStoragePermissions(this);
        setContentView(R.layout.activity_common);
        textViewLjOper = findViewById(R.id.textview_ljoper);
        imageViewLjOper = findViewById(R.id.imageview_ljoper);
        textViewLight = findViewById(R.id.textview_light);
        textViewColor = findViewById(R.id.textview_color);
        imageViewLight = findViewById(R.id.imageview_light);
        seekBarLight = findViewById(R.id.seekbar_light);
        textView = findViewById(R.id.textview);
        imageView = findViewById(R.id.imageview_dst);
        textViewRead = findViewById(R.id.textview_read);
        imageViewRead = findViewById(R.id.imageview_read);
        textViewMat = findViewById(R.id.textview_mat);
        imageViewMat = findViewById(R.id.imageview_mat);
        textViewPix = findViewById(R.id.textview_pix);
        imageViewPix = findViewById(R.id.imageview_pix);
        textViewCp = findViewById(R.id.textview_pixcomputer);
        imageViewCp = findViewById(R.id.imageview_pixcomputer);
        imageViewColor = findViewById(R.id.imageview_color);
        textViewConvert = findViewById(R.id.textview_colorconvert);
        imageViewConvet = findViewById(R.id.imageview_colorconvert);
        textViewCount = findViewById(R.id.textview_pixcount);
        textViewCountDesc = findViewById(R.id.textview_pixcountdesc);
        textViewPixOne = findViewById(R.id.textview_pixone);
        imageViewPixOne = findViewById(R.id.imageview_pixone);
        textViewTwoValue = findViewById(R.id.textview_twovalue);
        imageViewTwoValue = findViewById(R.id.imageview_twovalue);
        textViewOneConvert = findViewById(R.id.textview_oneconvert);
        imageViewOneConvert = findViewById(R.id.imageview_oneconvert);
        textViewOneConvert.setOnClickListener(this);
        textViewTwoValue.setOnClickListener(this);
        textViewPixOne.setOnClickListener(this);
        textViewCount.setOnClickListener(this);
        textViewRead.setOnClickListener(this);
        textView.setOnClickListener(this);
        textViewMat.setOnClickListener(this);
        textViewPix.setOnClickListener(this);
        textViewCp.setOnClickListener(this);
        textViewColor.setOnClickListener(this);
        textViewLjOper.setOnClickListener(this);
        textViewConvert.setOnClickListener(this);
        seekBarLight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                onLightProgressChange(progress);
                onLightProgressChange2(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * 图片操作
     */
    private void imageOper() {
        try {
            Mat source = Utils.loadResource(getBaseContext(), R.drawable.opencv01800480);
            Mat dst = Mat.zeros(source.size(), source.type());
            /**
             * 缩放图片1：缩放为一半 2：缩放为2倍
             * 参数：源mat/结果mat/缩放倍数/fx：横向参照 fy：竖向参照 0=整行整列 参照= 整行*fx /缩放插值类型：决定了插值后的图片与源图片比对
             *
             */
            Imgproc.resize(source, dst, new Size(new double[]{0.5d, 0.5d}), 0, 0, Imgproc.INTER_LINEAR);
            Imgproc.resize(source, dst, new Size(new double[]{2, 2}), 0, 0, Imgproc.INTER_LINEAR);
            /**
             * 图片翻转：参数：源mat/结果mat/翻转方式：0/-1/1: 图片90度翻转/180度翻转/270度翻转 注意翻转不一一对应
             */
            Core.flip(source, dst, 0);

            /**
             * 图片旋转：计算旋转mat：参数：旋转中心点/旋转角度/旋转中是否缩放
             * warpAffine：图片旋转：源mat/结果mat/旋转mat/size：输出图片的size 通常设置为源图片的size
             */
            int w = source.cols();
            int h = source.rows();
            Mat rotation = Imgproc.getRotationMatrix2D(new Point(w / 2, h / 2), 45, 1.0d);
            Imgproc.warpAffine(source, dst, rotation, source.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * opencv 提供了随机数的工具类 可以产生随机数
     * c：参考文章：RNG：https://www.cnblogs.com/Reyzal/p/5033701.html
     * Android 暂未找到对应的工具类和工具算法 不过就是随机数么 可以参考java和Android
     */
    private void rng() {

    }

    /**
     * opencv 绘制图形：
     * line(); 绘制线
     * arrowedLine(); 绘制箭头线
     * circle(); 绘制圆和点
     * ellipse(); 绘制椭圆和扇形
     * rectangle(); 绘制矩形
     * putText(); 绘制文本
     * drawContours(); 绘制轮廓 用于轮廓检查和绘制
     * 参数备注：
     * thickness：线的宽度 -1表示填充
     * lineType：锯齿情况 Imgproc.LINE_AA 抗锯齿
     * 参考文章：https://blog.csdn.net/qq_42064159/article/details/104531176?spm=1001.2101.3001.6661.1&utm_medium=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7Edefault-1.highlightwordscore&depth_1-utm_source=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7Edefault-1.highlightwordscore
     */
    private void opencvDraw() {
        Mat mat = null, dst = null;
        //像素类型转换
        mat.convertTo(dst, CvType.CV_8UC3);
//        Imgproc.line(); 绘制线
//        Imgproc.arrowedLine(); 绘制箭头线
//        Imgproc.circle(); 绘制圆和点
//        Imgproc.ellipse(); 绘制椭圆和扇形
//        Imgproc.rectangle(); 绘制矩形
//        Imgproc.putText(); 绘制文本
//          Imgproc.polylines(); //提供多组点绘制多边形
//        Imgproc.drawContours(); 绘制轮廓 用于轮廓检查和绘制
    }

    /**
     * 色彩转换：
     * cvtColor：色彩转换 BGR、Gray、HSV
     * inRange：通过设定低高区域像素点筛选0，255mask遮罩层（掩码）即在区域间的像素点设置为255（白色）不在的全部设置为0（黑色）
     * 最终获取到的是0和255两种状态的图片 用来当做遮罩
     * bitwise_not：像素点取反
     * copyTo：将src图片复制到dst中去，添加mask后，mask和src先叠加 即 mask中像素点为0（黑色）对应的src上的像素点设置为透明
     * 最终将叠合后的图片赋值到dst中去。
     * 此业务逻辑可以实现图片的背景替换和图片的局域截取。
     * 参考文章：copyTo:https://blog.csdn.net/u013270326/article/details/72730812
     * inRange:https://blog.csdn.net/coldwindha/article/details/82080176
     */
    private void colorConvert() {
        try {
            Mat mat = Utils.loadResource(getBaseContext(), R.drawable.greenback);
            Mat hsv = Mat.zeros(mat.size(), mat.type());
            // 色彩转换 转为 HSV
            Imgproc.cvtColor(mat, hsv, Imgproc.COLOR_BGR2HSV);

            //二副化取遮罩 二副化：只有0和255两种状态 inRange：在给定的区间内赋值255（白色）不在赋值0（黑色）
            //最终获取到的是黑白二副化图片 多用于取遮罩图
            Scalar lowerb = new Scalar(35, 43, 46);
            Scalar upperb = new Scalar(77, 255, 255);
            Mat mask = Mat.zeros(hsv.size(), hsv.type());
            Core.inRange(hsv, lowerb, upperb, mask);

            //取反 黑白替换
            Core.bitwise_not(mask, mask);

            //换红背景
            Mat dst = Mat.zeros(hsv.size(), hsv.type());
            dst.setTo(new Scalar(255, 0, 0));

            //叠合mat 实现背景替换 mask：遮罩/掩码 添加mask表示mask和mat先进行叠合即将mask为0（黑色）的对应src的像素点设为透明
            // 叠合以后再降叠合后的图片绘制到dst中去
            mat.copyTo(dst, mask);

            Bitmap bitmap = Bitmap.createBitmap(dst.width(), dst.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(dst, bitmap);
            imageViewConvet.setImageBitmap(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分离、融合、复制通道
     * split: 分离通道：双参：分离目标mat 分离后的通道列表
     * merge：融合通道：双参：通道列表 目标mat
     * mixChanns：复制初始通道到目标通道，可以指定初始mat和目标mat的通道映射
     * MatOfInt：初始mat和目标mat映射对象 接受int变参 参数格式：初始通道+目标通道+初始通道+目标通道.....
     * 参考文章： mixChannels ： https://blog.csdn.net/qq_41741344/article/details/104370450
     */
    private void spliteChannels() {
        try {
            List<Mat> list = new ArrayList<>();
            Mat mat = Utils.loadResource(getBaseContext(), R.drawable.contours);
            Mat dst = Mat.zeros(mat.size(), mat.type());
            //将mat的通道分离到mat list中
            Core.split(mat, list);
            //遍历通道列表
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    //将mat的B通道设置为0
                    list.get(0).setTo(new Scalar(0, 0, 0));
                }
            }
            //修改完通道后 将通道列表再融合回去
            Core.merge(list, dst);

            //分离dst 的通道数 到list1
            List<Mat> list1 = new ArrayList<>();
            Core.split(dst, list1);
            //初始通道和目标通道的映射：初始0-1（目标）、初始1-2（目标）、初始2-0（目标）
            MatOfInt matOfInt = new MatOfInt(0, 1, 1, 2, 2, 0);
            Core.mixChannels(list, list1, matOfInt);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过 Core api(addWeighted) 实现图片融合及其亮度变化
     * 亮度转换原理还是先前原理 通过修改像素实现亮度变化 0-255 越接近255 亮度越高 越接近0 亮度越低
     * addWeighted：算法核心：dst = src1*alpha + src2*beta + gamma;
     * 解析： mat1和其百分比加上mat2和百分比加上扩展scalar(会利用gamma创建一个新的scalar)
     * 参数如算法所示：
     * addWeighted还有一个重写api 参数扩展一个：
     * int dtype: 设置输出dst的图片深度，两个输入的图片深度一致 设置-1就好 若不然可以设置输出dst的深度。
     * alpha+beta 通常为1 通过这两个值可以修改图片的对比度
     * gamma 可以修改图片的亮度 还是建议通过像素的加减乘除实现亮度和对比度的转换。
     *
     * @param progress
     */
    private void onLightProgressChange2(int progress) {
        if (matSource == null) {
            try {
                matSource = Utils.loadResource(getBaseContext(), R.drawable.contours);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (matSource != null) {
            if (matLight == null) {
                matLight = Mat.zeros(matSource.size(), matSource.type());
            }
            if (dst == null) {
                dst = Mat.zeros(matSource.size(), matSource.type());
            }
            Core.addWeighted(matSource, 1.0, matLight, 0, progress, dst);
            Imgproc.cvtColor(dst, dst, Imgproc.COLOR_BGR2RGB);
            Bitmap bitmap = Bitmap.createBitmap(dst.width(), dst.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(dst, bitmap);
            imageViewLight.setImageBitmap(bitmap);
        }
    }

    /**
     * 利用进度条修改图片的亮度：
     * 图片亮度：修改图片的亮暗程度 采光原因
     * 原理：通过一个空的像素mat 接受进度变换进行亮暗转换
     * 对于mat经常变换为了节省资源提升性能 可以全局化mat 无需一直创建mat 不全局 使用完就回收也行
     * mat 使用完后建议回收 但是需要结合场景 若全局 看是否其他还有使用 没有使用 则回收
     * dst 必须初始化 不能为空对象 add 对其填充
     * 图片亮度：图片的颜色通道BGR：越接近255亮度越高 越接近0亮度越低 opencv 通常通过加减来操作亮度
     * 图片对比度：图片颜色和亮度之间的差异感知，对比度越大 每个像素与周围的差异性越大 图像细节就越好
     * opencv 通过mat的乘除修改图片的对比度，0-3 float数 <1 降低对比度 >1 增加对比度
     * ???? 在Android中甚至opencv中 修改rgb值都是修改颜色，为何像素的加减乘除却修改的是亮度和对比度
     */
    private Mat matLight, matSource, dst;

    private void onLightProgressChange(int progress) {
        if (matSource == null) {
            try {
                matSource = Utils.loadResource(getBaseContext(), R.drawable.contours);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (matSource != null) {
            if (matLight == null) {
                matLight = Mat.zeros(matSource.size(), CvType.CV_8UC3);
            }
            if (dst == null) {
                dst = Mat.zeros(matSource.size(), CvType.CV_8UC3);
            }
            matLight.setTo(new Scalar(progress, progress, progress));
            Core.add(matSource, matLight, dst);
            Imgproc.cvtColor(dst, dst, Imgproc.COLOR_BGR2RGB);
            Bitmap bitmap = Bitmap.createBitmap(dst.width(), dst.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(dst, bitmap);
            imageViewLight.setImageBitmap(bitmap);
        }
    }

    /**
     * 整体像素翻转
     * 效率是最高的
     * get(0,0,bytes):获取整体像素字节数组
     * get/put是耗时操作 操作像素内存 越少效率性能越高
     * <p>
     * 参考文章：
     * opencv像素算法计算：https://blog.csdn.net/qq_38315348/article/details/108411619
     */
    private void pixAccessAll() {
        try {
            Mat mat = Utils.loadResource(getBaseContext(), R.drawable.opencv01800480);
            int width = mat.width();
            int height = mat.height();
            int channels = mat.channels();
            //创建整个图片字节数组
            byte[] bytes = new byte[channels * width * height];
            mat.get(0, 0, bytes);
            int pixv = 0;
            //字节数组 遍历 翻转
            for (int byteNum = 0; byteNum < bytes.length; byteNum++) {
                pixv = bytes[byteNum] & 0xff;
                pixv = 255 - pixv;
                bytes[byteNum] = (byte) pixv;
            }
            mat.put(0, 0, bytes);
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2RGB);
            Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat, bitmap);
            imageViewPix.setImageBitmap(bitmap);
            mat.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 按行访问像素且修改像素：
     * get(row,0,bytes):不指定列获取整行字节数
     * 行字节数=channel * width 颜色字节数：三通道 单个像素=三个字节（rgb） 通道数*像素宽度
     * 效率上按行比单个像素操作效率高了很多
     * 参考文章：
     * opencv像素算法计算：https://blog.csdn.net/qq_38315348/article/details/108411619
     */
    private void pixAccessByRow() {
        try {
            Mat mat = Utils.loadResource(getBaseContext(), R.drawable.opencv01800480);
            int width = mat.width();
            int height = mat.height();
            int channels = mat.channels();
            //创建一行的字节数组 mat 行字节数=channel * width;
            byte[] bytes = new byte[channels * width];
            int pixv = 0;
            //按行读取像素 行==高 共有多少行
            for (int row = 0; row < height; row++) {
                //col==0表示不设置对应的列 获取单行像素字节数
                mat.get(row, 0, bytes);
                //和列没关系 遍历字节数 获取字节转为十进制并翻转数字
                for (int byteNum = 0; byteNum < bytes.length; byteNum++) {
                    pixv = bytes[byteNum] & 0xff;
                    pixv = 255 - pixv;
                    bytes[byteNum] = (byte) pixv;
                }
                //按行重置像素
                mat.put(row, 0, bytes);
            }
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2RGB);
            Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat, bitmap);
            imageViewPix.setImageBitmap(bitmap);
            mat.release();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 像素访问：反向计算像素
     * 像素计算：像素可以在0---255之间任意计算/算数运算
     * mat::get 获取像素
     * mat::put 存储像素
     * byte[] 标识像素 单字节代表像素颜色的单通道
     * byte[0]&0xff: 将当前通道的字节值转换为10进制数字 0---255
     * 参考文章：
     * opencv像素算法计算：https://blog.csdn.net/qq_38315348/article/details/108411619
     */
    private void pixAccess() {
        try {
            Mat mat = Utils.loadResource(getBaseContext(), R.drawable.opencv01800480);
            // mat 通道数
            int channels = mat.channels();
            //mat 列数 标识宽度
            int cols = mat.cols();
            //mat 行数 标识高度
            int rows = mat.rows();
            //创建字节数组 Android opencv 需要先将像素读取到字节数组中去
            byte[] bytes = new byte[channels];
            //根据行列读取像素并翻转像素
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (channels == 1) {
                        //单通道 将i行j列像素读取到字节数组中
                        mat.get(i, j, bytes);
                        int bgr = bytes[0] & 0xff;
                        //翻转像素
                        bgr = 255 - bgr;
                        //翻转后的像素赋值给字节数组
                        bytes[0] = (byte) bgr;
                        mat.put(i, j, bytes);

                    } else if (channels == 3) {
                        //三通道 将i行j列像素读取到字节数组中
                        mat.get(i, j, bytes);
                        int b = bytes[0] & 0xff;
                        int g = bytes[1] & 0xff;
                        int r = bytes[2] & 0xff;
                        //翻转像素
                        b = 255 - b;
                        g = 255 - g;
                        r = 255 - r;
                        //翻转后的像素赋值给字节数组
                        bytes[0] = (byte) b;
                        bytes[1] = (byte) g;
                        bytes[2] = (byte) r;
                        mat.put(i, j, bytes);
                    }
                }

            }
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2RGB);
            Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat, bitmap);
            imageViewPix.setImageBitmap(bitmap);
            mat.release();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * mat 赋值运算 Scalar ＝赋值 mat::setTo Scalar
     * opencv 行列代表宽高，行=高 列=宽 行在前 列在后
     * Android bitmap 以宽高代表 width在前 height在后
     * mat 和 bitmap转换需要注意
     * 注意： = 等同于 指针 对象修改 clone 是新建对象
     */
    private void matVoluation() {
        Mat mat = new Mat(400, 1080, CvType.CV_8UC3);
//        c++语法 java中不可用
//        mat = Scalar(255,0,0);
        mat.setTo(new Scalar(255, 0, 0));
//        Mat mat1 = mat;
//        mat1.setTo(new Scalar(0,255,0));
        Mat mat1 = mat.clone();
        mat1.setTo(new Scalar(0, 0, 255));
        Bitmap bitmap = Bitmap.createBitmap(1080, 400, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        imageViewMat.setImageBitmap(bitmap);
        mat.release();
        mat1.release();

    }

    /**
     * 像素运算：加减乘除取小取大 Core核心工具类
     * 参考文章：
     * opencv像素算法计算：https://blog.csdn.net/qq_38315348/article/details/108411619
     * 操作对象：行列数相等两个对象
     * 混合后的对象也必须初始化且和混合前的对象行列数相等
     */
    private void pixComputer() {
        //像素的加减乘除运算
//        Core.add();
//        Core.subtract();
//        Core.multiply();
//        Core.divide();
//        Core.min();
//        Core.max();
        try {
            Mat mat = Utils.loadResource(getBaseContext(), R.drawable.contours);
            Mat mat1 = new Mat(mat.rows(), mat.cols(), CvType.CV_8UC3, new Scalar(0, 0, 255));
            Mat dst = new Mat(mat.rows(), mat.cols(), CvType.CV_8UC3);
            Core.add(mat, mat1, dst);
            Imgproc.cvtColor(dst, dst, Imgproc.COLOR_BGR2RGB);
            Bitmap bitmap = Bitmap.createBitmap(dst.width(), dst.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(dst, bitmap);
            imageViewCp.setImageBitmap(bitmap);
            mat.release();
            mat1.release();
            dst.release();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * opencv：存储图片到本地 loadResource/imwrite api
     */
    private void savePicToSd() {
        try {
            Mat mat = Utils.loadResource(getBaseContext(), R.drawable.opencv01800480);
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dcim/camera/opencv01800480.jpeg";
            File file = new File(path);
            if (file.isFile() && !file.exists()) {
                file.createNewFile();
            }
            Imgcodecs.imwrite(path, mat);
            mat.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * opencv mat的构造方法及其构造参数备注
     * ???? ones、zeros、eye的区别
     * mat结构：[ 3000*4000*CV_8UC3, isCont=true, isSubmat=false, nativeObj=0x73e9022e00, dataAddr=0x7385400000 ]
     * header和矩阵数据两部分 32位
     * 0-2标识图片的数据类型即CV_8U
     * 3-11标识了图片的通道数 openv默认最大通道数是512 故9位可以完全表达
     * 12，13 暂定
     * 14位代表Mat的内存是否连续，一般由creat创建的mat均是连续的，如果是连续，将加快对数据的访问。
     * 15位代表该Mat是否为某一个Mat的submatrix，一般通过ROI以及row()、col()、rowRange()、colRange()等得到的mat均为submatrix。
     * 16-31代表magic signature，暂理解为用来区分Mat的类型，如果Mat和SparseMat
     * 参考：
     * opencv中mat详解：https://www.jianshu.com/p/cfc0c1f87bf8
     * opencv中mat类详解及其用法： https://www.jianshu.com/p/d2b933f74107
     */
    private void creatMatType() {
        Mat mat = new Mat();
        //行，列，mat的通道类型 创建一个4行5列BGR三通道的mat 行=高，列=宽
        Mat mat1 = new Mat(4, 5, CvType.CV_8UC3);
        //行，列，mat的通道类型，指定mat的字节内存空间
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        Mat mat2 = new Mat(4, 5, CvType.CV_8UC3, byteBuffer);
        //行，列，mat的通道类型，指定mat的字节内存空间，mat的实际宽度即跳转到下一行的实际步长
        //关于step步长：step 为图象像素行的实际宽度，不一定与width相符，比如 图像为 1024 *768
        //设置了感兴趣区域ROI为 400*200，那么这个感兴趣区域的图象宽度 为 200，要访问这个感兴趣区域的下一行，
        //图像数据指针的步长应该为 1024 而不是 200，这里 width 为 200  而 step为 1024
        Mat mat3 = new Mat(400, 500, CvType.CV_8UC3, byteBuffer, 400);

        //opencv point概念 和Android中的point概念不一样 同样存在x，y 但是 x,y表示宽度和高度
        Point point = new Point();
        // 通过double数组标识point的x，y 0标识x 1 标识y
        double[] points = new double[]{4.0d, 5.0d};
        Point point1 = new Point(points);
        //size 标识了mat的x，y（宽度和高度） 两种构造：通过point和double数组两种方式
        Size size = new Size(point);
        double[] sizes = new double[]{5.0d, 6.0d};
        Size size1 = new Size(sizes);
        // 通过size 指定行和列
        Mat mat4 = new Mat(size, CvType.CV_8UC3);
        //通过size的int[]构建对应的mat ??? 数组的length 和数组内容待后续确认
        Mat mat5 = new Mat(new int[]{5, 6}, CvType.CV_8UC3);

        // 行，列，type，指定像素值 scalar：BGRA 通道对象 针对行列单个像素值赋值
        Mat mat6 = new Mat(4, 5, CvType.CV_8UC3, new Scalar(0, 0, 255, 255));
        // size type 指定像素值
        Mat mat7 = new Mat(size1, CvType.CV_8UC3, new Scalar(0, 0, 255, 255));
        // size[] type 指定像素值
        Mat mat8 = new Mat(new int[]{4, 5}, CvType.CV_8UC3, new Scalar(0, 0, 255, 255));

        //局部截取mat range: start-end 开始像素点---结束像素点 直接构造或者通过double 指定
        Range rangeRow = new Range(4, 6);
        Range rangeCol = new Range(new double[]{5, 6});
        //截取mat 输入mat 行截取 列截取
        Mat mat9 = new Mat(mat8, rangeRow, rangeCol);
        //截取mat 输入mat 直接指定行截取 列不指定 全部
        Mat mat10 = new Mat(mat8, rangeRow);

        Rect rect = new Rect(0, 0, 100, 100);
        // point：xy：opencv 的point 中x，y取决于point的服务对象 针对rect则是point 点 和宽高
        Point point2 = new Point(0, 0);
        Point point3 = new Point(100, 100);
        //point1：指定rect的起始点x,y point2: 指定rect的宽和高 和Android 不太一样
        Rect rect1 = new Rect(point2, point3);
        //通过rect截取mat
        Mat mat11 = new Mat(mat8, rect);

        // 创建单通道mat
        Mat mat12 = Mat.ones(size, CvType.CV_8UC3);
        Mat mat13 = Mat.ones(new int[]{6, 7}, CvType.CV_8UC3);
        Mat mat14 = Mat.ones(6, 7, CvType.CV_8UC3);

        //等同与 mat的构造方法
        Mat mat15 = Mat.zeros(5, 6, CvType.CV_8UC3);
        Mat mat16 = Mat.zeros(size, CvType.CV_8UC3);
        Mat mat17 = Mat.zeros(new int[]{8, 9}, CvType.CV_8UC3);

        //创建 matlab式恒等矩阵初始值设定项，可以使用缩放操作高效的创建缩放的恒等矩阵
        Mat mat18 = Mat.eye(4, 4, CvType.CV_8UC3);
        Mat mat19 = Mat.eye(size, CvType.CV_8UC3);

        //通过clone和copy创建新的mat
        Mat mat20 = mat.clone();
        Mat mat21 = null;
        mat1.copyTo(mat21);
    }

    /**
     * 灰度化图片并展示图片
     * mat的创建/bitmapToMat/cvtColor/matToBitmap
     * cvtColor:图片的色彩转换：输入mat，输出mat，转换方式
     * 常见转换方式：
     * COLOR_BGR2GRAY：BGR转灰度
     * COLOR_BGR2RGBA：BGR转RGBA
     * COLOR_BGR2HSV：BGR转HSV
     * bitmap和opencv mat 的颜色通道转换通常需要此api辅助
     * opencv颜色模型：
     * RGB：bitmap三原色 Android使用
     * BGR：opencv三原色 与RGB相反 取值都是0到255 添加透明通道
     * YUV：电视信号的颜色模型，可以和RGB交换
     * HSV：色度（Hue）、饱和度（Saturation）和亮度（Value）的简写
     * LAB：颜色模型：L标识亮度 AB标识颜色
     * 参考文章：
     * opencv中的颜色模型：https://blog.csdn.net/poorkick/article/details/104192122
     */
    private void setDstBitmapData() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.opencv01800480);
        if (bitmap != null) {
            Mat mat = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC3);
            Utils.bitmapToMat(bitmap, mat);
            //初学者不细心 此处第三参数 灰度参数错误COLOR_BayerBG2GRAY造成崩溃 demosaicing.cpp:1699: error: (-215:Assertion failed) scn == 1 && dcn == 1 in function 'demosaicing'
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
            Utils.matToBitmap(mat, bitmap);
            imageView.setImageBitmap(bitmap);
            mat.release();
        }
    }

    /**
     * 灰度化图片并展示图片(直接加载res下的图片)
     * loadResource/cvtColor/matToBitmap
     */
    private void setDstBitmapData2() {
        Mat mat = null;
        try {
            //opencv 提供工具类 可以直接加载res下资源图片转为mat
            mat = Utils.loadResource(getBaseContext(), R.drawable.opencv01800480);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
        //通过mat生成bitmap mat 封装了对应的width，height api方法
        Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        imageView.setImageBitmap(bitmap);
        mat.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.textview:
                setDstBitmapData();
//                setDstBitmapData2();
                break;
            case R.id.textview_read:
                readPicFromRes();
//                savePicToSd();
                break;
            case R.id.textview_mat:
                matVoluation();
                break;
            case R.id.textview_pix:
//                pixAccess();
//                pixAccessByRow();
                pixAccessAll();
                break;
            case R.id.textview_pixcomputer:
                pixComputer();
                break;
            case R.id.textview_color:
                opencvColor();
                break;
            case R.id.textview_ljoper:
                pixLjOper();
                break;
            case R.id.textview_colorconvert:
                colorConvert();
                break;
            case R.id.textview_pixcount:
                pixCount();
                break;
            case R.id.textview_pixone:
                pixOne();
                break;
            case R.id.textview_twovalue:
                pixTwoValue();
                break;
            case R.id.textview_oneconvert:
                pixOneConvert();
                break;
        }
    }

    /**
     * 通过归一化和转换将浮点数像素转换为三通道
     * 归一化：将图片的像素值归一化到一个区间方便比对和转化
     */
    private void pixOneConvert() {
        int width = imageViewOneConvert.getMeasuredWidth();
        int height = imageViewOneConvert.getMeasuredHeight();
        //创建mat
        Mat mat = new Mat(height, width, CvType.CV_32FC3);

        //创建mat对应的像素点的浮点数数组及其浮点型赋值
        float[] pixs = new float[width * height * 3];
        Random random = new Random();
        for (int i = 0; i < pixs.length; i++) {
            //高斯随机数 随机出的数不一定在0-255之间 所以需要后续的归一化到0-255之间
            pixs[i] = (float) random.nextGaussian();
        }
        //将浮点型数组赋值给mat
        mat.put(0, 0, pixs);

        //将浮点型像素归一化到0-255之间
        Mat dst = Mat.zeros(mat.size(), mat.type());
        Core.normalize(mat, dst, 255, 0, Core.NORM_MINMAX);

        Mat mat8U = Mat.zeros(dst.size(), dst.type());
        // 将浮点型3通道转化为无符号整形3通道
        dst.convertTo(mat8U, CvType.CV_8UC3);
        Bitmap bitmap = Bitmap.createBitmap(mat8U.width(), mat8U.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat8U, bitmap);
        imageViewOneConvert.setImageBitmap(bitmap);
    }

    /**
     * 根据均值分离图片
     * 二值图像：图像中仅有两个值 通常为0和255 即黑色和白色
     * 均值：图像的像素平均值 此Demo为通过均值划分图像为0和255 二值图像
     * 方差：标准差：校验图片像素的复杂度进而筛选图片
     */
    private void pixTwoValue() {
        try {
            Mat mat = Utils.loadResource(getBaseContext(), R.drawable.greenback);
            //灰度图片
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
            //均值+方差
            MatOfDouble mean = new MatOfDouble();
            MatOfDouble stdDev = new MatOfDouble();
            Core.meanStdDev(mat, mean, stdDev);
            //获取像素点均值及其方差
            double[] means = mean.toArray();
            double[] stdDevs = stdDev.toArray();
            double meanNum = (int) means[0];
            double stdDevNum = stdDevs[0];
            Log.e("dongdianzhou", " pixTwoValue:meanNum:" + meanNum + " stdDevNum:" + stdDevNum);

            //获取所有的像素点到字节数组中去
            int width = mat.cols();
            int height = mat.rows();
            byte[] bytes = new byte[width * height];
            mat.get(0, 0, bytes);

            int pixv = 0;
            //字节数组 遍历 校验和均值对比
            for (int byteNum = 0; byteNum < bytes.length; byteNum++) {
                pixv = bytes[byteNum] & 0xff;
                if (pixv < meanNum) {
                    bytes[byteNum] = 0;
                } else {
                    bytes[byteNum] = (byte) 255;
                }
            }
            mat.put(0, 0, bytes);
            Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat, bitmap);
            imageViewTwoValue.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 像素格式转换及其归一化
     * 像素格式解析：
     * Cv_8UC3:16 8U:8位无符号整形数 C3：三通道
     * Cv_32F: 21 32F： 32位单精度浮点数 float
     * Cv_32S: 20 32S: 32位有符号整形数
     * U：无符号整形数 S：有符号整形数 F：单精度浮点数 C：通道数 1：灰度 3：BGR 4：BGRA 四通道
     * 32F = 32FC1 8S = 8SC1
     * 参考文章：像素格式：https://blog.csdn.net/Young__Fan/article/details/81868666
     * 像素归一化：将像素转换到0和1之间 方便计算和转换 多重转换方式
     * 参考文章：像素归一化：https://blog.csdn.net/qq_41498261/article/details/100727309
     * 像素归一化：https://blog.csdn.net/weixin_41709536/article/details/100619476
     * 归一化参数：源mat/结果mat/归一化后的像素上限/归一化后像素下限/按照那种方式归一化 使用比较多的就是Core.NORM_MINMAX
     */
    private void pixOne() {
        try {
            Mat mat = Utils.loadResource(getBaseContext(), R.drawable.greenback);
//            Mat convert = Mat.zeros(mat.size(), mat.type());
//            mat.convertTo(convert, CvType.CV_32F);
//            Log.e("dongdianzhou", "mat的像素格式：" + mat.type() + " 转换后的像素格式：" + convert.type());
            Mat dst = Mat.zeros(mat.size(), mat.type());
            Core.normalize(mat, dst, 1.0, 0, Core.NORM_MINMAX);
            //error: (-215:Assertion failed) src.type() == CV_8UC1 || src.type() == CV_8UC3 ||
            // src.type() == CV_8UC4 in function 'Java_org_opencv_android_Utils_nMatToBitmap2'
            // 通道不一样不能直接转换bitmap 此处可以先转换为3通道再转换 若直接转换mat
            Bitmap bitmap = Bitmap.createBitmap(dst.width(), dst.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(dst, bitmap);
            imageViewPixOne.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 像素统计：计算图片的最小像素 最大像素 最小像素点位置 最大像素点位置 图片均值 图片标准差
     * minMaxLoc：计算图片的最小像素 最大像素 最小像素点位置 最大像素点位置 针对单通道：灰度或者遍历通道
     * meanStdDev：计算图片均值 图片标准差 对象可以是单通道和多通道 计算是以通道为单位
     * MatOfDouble：double为单位的mat，构造默认对象即可由内部赋值
     * 后续需要研究的是：均值和标准差有何用处？
     * 均值：图片单通道的像素平均值 标准方差=标准差：计算像素的差值即差值越小表明当前图片的像素越不丰富，像素简单
     * 差值越大说明图片像素越复杂 在图片过滤的过程中 均值和方差可以用来过滤图片 通常方差小于5（接近于纯色图片）可以将其淘汰
     * 参考文章： meanStdDev：https://www.freesion.com/article/14091393921/
     */
    private void pixCount() {
        try {
            /***
             *     CvException [org.opencv.core.CvException: OpenCV(4.5.4) /build/master_pack-android/opencv/modules/core
             *     /src/minmax.cpp:1495: error: (-215:Assertion failed) (cn == 1 && (_mask.empty() || _mask.type() == CV_8U))
             *     || (cn > 1 && _mask.empty() && !minIdx && !maxIdx) in function 'minMaxIdx'
             *     minMaxLoc 针对单个通道的 若将多通道设置货出现上面异常
             *     解决方案：将其灰度成为单通道 或者遍历其通道
             */
            Mat mat = Utils.loadResource(getBaseContext(), R.drawable.greenback);
            Mat gray = Mat.zeros(mat.size(), mat.type());
            Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);
            Core.MinMaxLocResult result = Core.minMaxLoc(gray);
            String resultStr = "图片最小像素：" + result.minVal + "最大像素：" + result.maxVal
                    + " 最小像素点位置：" + result.minLoc.toString()
                    + " 最大像素点位置：" + result.maxLoc.toString() + "\n";

            //计算图片的均值及其标准差 对象可以是单通道也可以是多通道 MatOfDouble创建一个默认对象由内部赋值即可
            MatOfDouble means = new MatOfDouble();
            MatOfDouble stddev = new MatOfDouble();
            Core.meanStdDev(mat, means, stddev);
            resultStr = resultStr + " 图片均值：" + means.toList().toString() + "\n"
                    + " 图片标准差：" + stddev.toList().toString();
            textViewCountDesc.setText(resultStr);

        } catch (IOException e) {
            e.printStackTrace();
        }

//        Core.meanStdDev();
    }

    /**
     * 像素的逻辑操作
     */
    private void pixLjOper() {
        Mat mat = new Mat(788, 1080, CvType.CV_8UC3);
        mat.setTo(new Scalar(255, 0, 0));
        Mat mat1 = new Mat(788, 1080, CvType.CV_8UC3);
        mat1.setTo(new Scalar(0, 0, 255));
        Mat dst = new Mat(788, 1080, CvType.CV_8UC3);
        //像素与运算：同为1则为1 有0则为0
//        Core.bitwise_and(mat, mat1, dst);
        //像素或运算：有1则为1 同为0则为0
        Core.bitwise_or(mat, mat1, dst);
        //像素非运算：取反运算
//        Core.bitwise_not(mat,dst);
        //像素异火运算：相同则为1 不同则为0
//        Core.bitwise_xor(mat,mat1,dst);
        Bitmap bitmap = Bitmap.createBitmap(dst.width(), dst.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst, bitmap);
        imageViewLjOper.setImageBitmap(bitmap);
        mat.release();
        mat1.release();
        dst.release();
    }

    /**
     * opencv 颜色表：api(applyColorMap)
     * COLORMAP_AUTUMN = 0, 蓝色
     * COLORMAP_BONE = 1, 黑色
     * COLORMAP_JET = 2, 深红
     * COLORMAP_WINTER = 3, 亮红
     * COLORMAP_RAINBOW = 4, 蓝色
     * COLORMAP_OCEAN = 5, 黑色
     * COLORMAP_SUMMER = 6, 深绿
     * COLORMAP_SPRING = 7, 浅红
     * COLORMAP_COOL = 8, 亮黄
     * COLORMAP_HSV = 9, 蓝色
     * COLORMAP_PINK = 10, 黑色
     * COLORMAP_HOT = 11, 黑色
     * COLORMAP_PARULA = 12,
     * COLORMAP_MAGMA = 13,
     * COLORMAP_INFERNO = 14,
     * COLORMAP_PLASMA = 15,
     * COLORMAP_VIRIDIS = 16,
     * COLORMAP_CIVIDIS = 17,
     * COLORMAP_TWILIGHT = 18,
     * COLORMAP_TWILIGHT_SHIFTED = 19,
     * COLORMAP_TURBO = 20,
     * COLORMAP_DEEPGREEN = 21;
     */
    private void opencvColor() {
        Mat mat = new Mat(788, 1080, CvType.CV_8UC3);
        Mat dst = new Mat(788, 1080, CvType.CV_8UC3);
        Imgproc.applyColorMap(mat, dst, Imgproc.COLORMAP_DEEPGREEN);
        Bitmap bitmap = Bitmap.createBitmap(dst.width(), dst.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst, bitmap);
        imageViewColor.setImageBitmap(bitmap);
        mat.release();
        dst.release();
    }

    private byte[] InputStreamToByte(InputStream is) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        while ((ch = is.read()) != -1) {
            bytestream.write(ch);
        }
        byte imgdata[] = bytestream.toByteArray();
        bytestream.close();
        return imgdata;
    }


    /**
     * opencv 读取sd卡图片到view 熟悉opencv api：imread/cvtColor windows api： imshow/namewindow
     * imread：api：读取指定图片到mat，
     * path：图片路径，注意是绝对路径，即使res，assets也需要是绝对路径，opencv的实现是底层c++实现，C++不认识Android路径
     * flag：读取图片的格式：单通道/3通道/灰度等即：
     * imread flag	意义
     * IMREAD_UNCHANGED = -1	无改动
     * IMREAD_GRAYSCALE = 0	单通道灰色图像
     * IMREAD_COLOR = 1	三通道BGR图像
     * IMREAD_ANYDEPTH = 2	不改变图像深度
     * IMREAD_ANYCOLOR = 4	以任何可能的颜色格式读取图像
     * IMREAD_LOAD_GDAL = 8	使用Gdal驱动程序加载图像
     * IMREAD_REDUCED_GRAYSCALE_2 = 16	单通道灰色图像，宽高减半
     * IMREAD_REDUCED_COLOR_2 = 17	三通单BGR图像，宽高减半
     * IMREAD_REDUCED_GRAYSCALE_4 = 32	单通道灰色图像，宽高为原图1/4
     * IMREAD_REDUCED_COLOR_4 = 33	三通单BGR图像，宽高为原图1/4
     * IMREAD_REDUCED_GRAYSCALE_8 = 64	单通道灰色图像，宽高为原图1/8
     * IMREAD_REDUCED_COLOR_8 = 65	三通单BGR图像，宽高为原图1/8
     * IMREAD_IGNORE_ORIENTATION = 128	忽略EXIF的方向标志
     * opencv针对图片的通道顺序是BGR 而Android 中bitmap的通道顺序是RGB 所以不能够直接将imread的图片转换bitmap赋值，
     * 需要先进行通道color替换 才能转换bitmap赋值
     * windows 扩展两个api：
     * imshow：显示窗体，将图片显示出来，参数一：指定窗体名称 参数2：需要显示的mat
     * namewindow：opencv在windows上直接加载图片原图加载 在windows上显示显示不开会直接截屏，使用此api可以重置输出窗口
     * 并设置窗口的适配范围（类似与Android的imageview的scaltype）参数一：输出窗口名字，参数二：窗口适配flag：WINDOW_FREERATIO:自适应窗口
     */
    private void readPicFromRes() {
//        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
//                + getResources().getResourcePackageName(R.drawable.opencv01800480) + "/"
//                + getResources().getResourceTypeName(R.drawable.opencv01800480) + "/"
//                + getResources().getResourceEntryName(R.drawable.opencv01800480));
//        String path = uri.getPath();
//        Log.e(LOG_TAG, path);
        //assets 路径没错 只是c++ 不认识 且不能够访问这个文件夹
//        String path = "file:///android_asset/pics/opencv01800480.jpeg";
//        String path = Uri.parse("assets/pics/opencv01800480.jpeg").getPath();
//        try {
//            String[] paths = getResources().getAssets().list("pics");
//            for (String path : paths) {
//                if (TextUtils.equals(path, "opencv01800480.jpeg")) {
//                    File file = new File(path);
//                    Log.e(LOG_TAG, file.getAbsolutePath());
//                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                    imageViewRead.setImageBitmap(bitmap);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dcim/camera/IMG_20200330_210809.jpg";
//        File file = new File(path);
//        if (file.isFile() && file.exists()) {
//            Log.e(LOG_TAG, "XXXXXXXXXXXXXXXXXXX");
//        }
//        Bitmap bitmap = BitmapFactory.decodeFile(path);
//        imageViewRead.setImageBitmap(bitmap);
        //Mat [ 3000*4000*CV_8UC3, isCont=true, isSubmat=false, nativeObj=0x73e9022e00, dataAddr=0x7385400000 ]
        Mat mat = Imgcodecs.imread(path, Imgcodecs.IMREAD_UNCHANGED);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2RGBA);//COLOR_BGR2RGB
        if (mat != null) {

            int width = mat.width();
            int height = mat.height();
            if (width > 0 && height > 0) {
                Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat, bitmap);
                imageViewRead.setImageBitmap(bitmap);
            }
        }
        mat.release();
    }
}