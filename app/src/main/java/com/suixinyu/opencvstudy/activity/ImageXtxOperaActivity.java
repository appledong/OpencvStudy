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
 * @desc opencv 图像形态学操作学习
 * @Data 2021-12-25
 */
public class ImageXtxOperaActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageViewjj, imageViewGsBlur, imageViewFklb;
    private ImageView imageViewMedian, imageViewMax;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xtx);
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
                pixFsOper();
                break;
            case R.id.textview_gsblur:
                pixPzOper();
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
     * 膨胀和腐蚀是相对的，膨胀和腐蚀仅针对与黑白二值图像且仅针对于前景色，膨胀是白色卷积核，
     * 遇边缘将其转为白色若前景色是黑色则为腐蚀，相反腐蚀是黑色卷积核，前景色为黑色则为膨胀
     * 前景色为白色则为腐蚀。即腐蚀还是膨胀取决于卷积核和前景色。
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
     * 膨胀操作：黑白二值图像
     * 膨胀：白色卷积核：边缘参杂变为白色 去除图像黑色噪声
     * dilate：源mat/目标mat/卷积核/核心点（-1,-1）/迭代次数
     */
    private void pixPzOper() {
        try {
            Mat mat = Utils.loadResource(getBaseContext(), R.drawable.xtxtest);
            Mat dst = Mat.zeros(mat.size(), mat.type());
            //快速获取形状矩阵 用于后面的最大值 最小值滤波（膨胀/腐蚀）
            Mat matRect = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
            Imgproc.erode(mat, dst, matRect, new Point(-1, -1), 2);
            Mat pzDst = Mat.zeros(mat.size(), mat.type());
            Imgproc.dilate(dst, pzDst, matRect);
            Bitmap bitmap = Bitmap.createBitmap(pzDst.width(), pzDst.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(pzDst, bitmap);
            imageViewGsBlur.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 腐蚀操作：黑白二值图像
     * 腐蚀：黑色卷积核：边缘参杂变为黑色 去除图像白色噪声
     * erode：源mat/目标mat/卷积核/核心点（-1,-1）/迭代次数
     */
    private void pixFsOper() {
        try {

            Mat mat = Utils.loadResource(getBaseContext(), R.drawable.xtxtest);
            Mat dst = Mat.zeros(mat.size(), mat.type());
            //快速获取形状矩阵 用于后面的最大值 最小值滤波（膨胀/腐蚀）
            Mat matRect = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
            Imgproc.erode(mat, dst, matRect, new Point(-1, -1), 2);
            Bitmap bitmap = Bitmap.createBitmap(dst.width(), dst.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(dst, bitmap);
            imageViewjj.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
