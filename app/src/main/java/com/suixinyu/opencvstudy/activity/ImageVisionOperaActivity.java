package com.suixinyu.opencvstudy.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.suixinyu.opencvstudy.R;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

/**
 * @author dongdz
 * @desc opencv 图像操作学习
 * @Data 2021-12-25
 * 噪声：图像噪声：对图像成像无用多是图像扣图后残余的星点像素，可以通过滤波过滤抑制噪声
 * 最大最小像素：针对图像的最大最小像素过滤，也是通常滤波过滤
 * Imgproc：
 * blur：均值模糊，源mat/目标mat/卷积核矩阵宽高/卷积核的中心位置/卷积的边缘如何处理
 * GaussianBlur：高斯模糊、居中模糊 源mat/目标mat/卷积核矩阵/x轴模糊系数/y轴模糊系数/模糊边缘类型
 * boxFilter：方框滤波 ：源mat/目标mat/mat深度通常都是源mat深度/滤波核（卷积核）/指定点/是否归一化
 * medianBlur：中值滤波 输入mat、输出mat，ksize
 * dilate：常说的膨胀 最大值滤波
 * erode：常说的腐蚀 最小值滤波
 */
public class ImageVisionOperaActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageViewjj, imageViewGsBlur, imageViewFklb;
    private ImageView imageViewMedian, imageViewMax;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vision);
        initView();
        setListener();
    }

    private void initView() {
        imageViewjj = findViewById(R.id.imageview_jj);
        imageViewGsBlur = findViewById(R.id.imageview_gsblur);
        imageViewFklb = findViewById(R.id.imageview_dst);
        imageViewMedian = findViewById(R.id.imageview_median);
        imageViewMax = findViewById(R.id.imageview_max);
    }

    private void setListener() {
        findViewById(R.id.textview_jj).setOnClickListener(this);
        findViewById(R.id.textview_gsblur).setOnClickListener(this);
        findViewById(R.id.textview).setOnClickListener(this);
        findViewById(R.id.textview_median).setOnClickListener(this);
        findViewById(R.id.textview_max).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.textview_jj:
                pixJjOper();
                break;
            case R.id.textview_gsblur:
                pixGsBlurOper();
                break;
            case R.id.textview:
                pixFklb();
                break;
            case R.id.textview_median:
                pixMedianlb();
                break;
            case R.id.textview_max:
                pixMaxMinlb();
                break;
        }
    }


    /**
     * 名词解释：
     * 滤波：
     * 线性滤波：均值滤波（模糊） 高斯滤波（模糊）方框滤波（线性求和或线性平均值）
     * 基于像素统计排序滤波：中值滤波 最大值、最小值滤波（两个滤波）
     * 噪声
     * 椒盐噪声：图像黑白点 0和255斑点 中值滤波过滤去掉比较好
     * 高斯噪声：模糊噪声 高斯滤波
     * 参考文章：https://blog.csdn.net/qq_35789421/article/details/88987305
     */

    /**
     * 最大值/最小值滤波
     * 最大值、最小值滤波和中值滤波相似不同点是将其矩阵内的排序后利用最大值/最小值
     * 代替中心数输出（中值输出的是中值数）
     * opencv没有提供最大值 最小值函数而是利用dilate和erode来替代
     * dilate：常说的膨胀 erode：常说的腐蚀 源mat/目标mat/矩阵size
     * getStructuringElement：快速获取形状mat：指定形状类型，矩阵大小
     */
    private void pixMaxMinlb() {
        Mat mat = null;
        try {
            mat = Utils.loadResource(getBaseContext(), R.drawable.opencv01800480);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Mat dst = Mat.zeros(mat.size(), mat.type());
        //快速获取形状矩阵 用于后面的最大值 最小值滤波（膨胀/腐蚀）
        Mat matRect = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
//        Imgproc.dilate(mat, dst, matRect);
        Imgproc.erode(mat, dst, matRect);
        Bitmap bitmap = Bitmap.createBitmap(dst.width(), dst.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst, bitmap);
        imageViewMax.setImageBitmap(bitmap);
    }

    /**
     * 中值滤波：
     * 中值：列表中排序后最中间数为中值数，奇数中取单 偶数中取双
     * 中值滤波：逻辑和均值，方框类似取矩阵数进行排序排序后中值数设置为中心数
     * 中值滤波：去噪声 去除最大最小数 平滑图像
     * 中值最好多为奇数：3，5，7等
     * 中值滤波若是设置偶数则取平均值
     * api：medianBlur：输入mat、输出mat，ksize
     * 备注：ksize==3或5时，输入图像可以是浮点型（float）和整数型（int）
     * 若ksize超过5的时候则输入必须为字节型即8UC Android测试不成立 后续待验证
     * 参考文章：https://blog.csdn.net/zhuyong006/article/details/88559018
     * 中值滤波过滤椒盐噪声
     */
    private void pixMedianlb() {
        Mat mat = null;
        try {
            mat = Utils.loadResource(getBaseContext(), R.drawable.opencv01800480);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Mat dst = Mat.zeros(mat.size(), mat.type());
        Imgproc.medianBlur(mat, dst, 3);
        Bitmap bitmap = Bitmap.createBitmap(dst.width(), dst.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst, bitmap);
        imageViewMedian.setImageBitmap(bitmap);
    }

    /**
     * 图像方框滤波
     * 均值滤波：等同与均值模糊，api一致，图像平滑，3*3,5*5矩阵求和平均计算中心值
     * 方框滤波：normalize==1:等同于均值滤波也是矩阵和平均值
     * normalize==0：和均值滤波不同，求矩阵和设置为指定点的值，
     * 矩阵比较大容易超出255 出现全白色的问题矩阵小还可以
     * api：boxFilter：源mat/目标mat/mat深度通常都是源mat深度/滤波核（卷积核）/指定点/是否归一化
     */
    private void pixFklb() {
        Mat mat = null;
        try {
            mat = Utils.loadResource(getBaseContext(), R.drawable.opencv01800480);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Mat dst = Mat.zeros(mat.size(), mat.type());
        Imgproc.boxFilter(mat, dst, mat.depth(), new Size(3, 3), new Point(-1, -1), false);
        Bitmap bitmap = Bitmap.createBitmap(dst.width(), dst.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst, bitmap);
        imageViewFklb.setImageBitmap(bitmap);
    }

    /**
     * 图像高斯模糊：和均值模糊不同，均值模糊是卷积核矩阵的系数都是同一个值，高斯模糊的卷积核的值不同
     * 越靠近中心其系数越大越边缘其系数越小 高斯模糊可以看作是居中模糊
     * api(GaussianBlur):源mat/目标mat/卷积核矩阵/x轴模糊系数/y轴模糊系数/模糊边缘类型
     * 卷积核矩阵可以不同但是必须保持正数和奇数
     * 设置x轴模糊系数不设置y轴模糊系数 系统会自动计算y轴模糊系数
     * 设置x轴模糊系数可以不设置size 会根据x轴y轴系数自动计算size
     * x轴/y轴系数：Android 0到25 opencv可以设置很大 基本全模糊 但是值设置越大性能越低设置非常大的时候
     * 会崩溃
     */
    private void pixGsBlurOper() {
        try {
            //1080*646
            int width = imageViewjj.getMeasuredWidth();
            int height = imageViewjj.getMeasuredHeight();
            Mat mat = Utils.loadResource(getBaseContext(), R.drawable.opencv01800480);
            Mat dst = Mat.zeros(mat.size(), mat.type());
            Imgproc.GaussianBlur(mat, dst, new Size(0, 0), 5, 5, Core.BORDER_DEFAULT);
            Bitmap bitmap = Bitmap.createBitmap(dst.width(), dst.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(dst, bitmap);
            imageViewGsBlur.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 图像卷积操作：
     * 卷积：图像像素矩阵 3*3，5*5 给定矩阵逐步和像素矩阵乘积（两矩阵完全覆盖，后续后移一列继续覆盖求值）求和平均后将值付给给定的像素点（常规是居中）
     * 计算卷积矩阵通常都是奇数矩阵：？？？
     * 卷积通常用于模糊：？？？
     * 卷积核：用于卷积计算的矩阵被称为卷积核 卷积核的矩阵单值统一成为均值模糊 在opencv通过api（blur）实现
     * 均值模糊：源mat/目标mat/卷积核矩阵宽高/卷积核的中心位置/卷积的边缘如何处理
     * 卷积核中心位置：取决与卷积核矩阵，-1，-1代表正中心 可以以矩阵的方式指定位置
     * 卷积核宽高：多为奇数，越大模糊的越厉害
     * 卷积核边缘处理：肉眼没看出太大区别 后续待研究边缘的区别 BORDER_TRANSPARENT会造成崩溃
     * 均值模糊也被常用与均值滤波
     * 最常用的模糊算法是高斯模糊
     */
    private void pixJjOper() {
        try {
            //1080*646
            int width = imageViewjj.getMeasuredWidth();
            int height = imageViewjj.getMeasuredHeight();
            Mat mat = Utils.loadResource(getBaseContext(), R.drawable.opencv01800480);
            Mat dst = Mat.zeros(mat.size(), mat.type());
            //3*3矩阵卷积 轻度模糊
//            Imgproc.blur(mat, dst, new Size(3, 3), new Point(-1, -1), Core.BORDER_DEFAULT);
            //45*45矩阵卷积 重度模糊 即：卷积核越大模糊程度越厉害
            Imgproc.blur(mat, dst, new Size(41, 41), new Point(-1, -1), Core.BORDER_DEFAULT);
            //水平方向卷积 即整行卷积 height=1 width=整行
//            Imgproc.blur(mat, dst, new Size(imageViewjj.getMeasuredWidth(), 1), new Point(-1, -1), Core.BORDER_DEFAULT);
            //竖向卷积 即整列卷积 height=整列 width=1
//            Imgproc.blur(mat, dst, new Size(1, imageViewjj.getMeasuredHeight()), new Point(-1, -1), Core.BORDER_DEFAULT);
            Bitmap bitmap = Bitmap.createBitmap(dst.width(), dst.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(dst, bitmap);
            imageViewjj.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
