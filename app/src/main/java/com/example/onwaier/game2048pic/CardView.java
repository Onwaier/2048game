package com.example.onwaier.game2048pic;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Onwaier on 2018/4/7.
 */

public class CardView extends FrameLayout {
    private int num = 0;
    // 只显示数字的话可以用Textview,要是用图片的话要用imview
    private TextView label;
    private ImageView pic;
    private int[] picArray = new int[3000];
    private MainActivity my2048;
    private int model = 1;
    private LayoutParams lp;

    public CardView(Context context) {
        super(context);
        putPic();
        pic = new ImageView(getContext());
        lp = new LayoutParams(-1, -1);// -1,-1就是填充完父类容器的意思
        lp.setMargins(10, 10, 0, 0);// 用来设置边框很管用
        addView(pic, lp);// 把imageView加到CardView上
        setNum(0);
    }

    //把数字逻辑实现的2048转化为图片逻辑，只需要把数字定位数组序数，数字对应图片，并保持一一对应关系
    public void putPic() {

//        // 数字版+0
        picArray[0] = R.drawable.bleach0;
        picArray[2] = R.drawable.num2;
        picArray[4] = R.drawable.num4;
        picArray[8] = R.drawable.num8;
        picArray[16] = R.drawable.num16;
        picArray[32] = R.drawable.num32;
        picArray[64] = R.drawable.num64;
        picArray[128] = R.drawable.num128;
        picArray[256] = R.drawable.num256;
        picArray[512] = R.drawable.num512;
        picArray[1024] = R.drawable.num1024;
        picArray[2048] = R.drawable.num2048;
//        // 死神版+5
        picArray[5] = R.drawable.bleach0;
        picArray[7] = R.drawable.bleach2;
        picArray[9] = R.drawable.bleach4;
        picArray[13] = R.drawable.bleach8;
        picArray[21] = R.drawable.bleach16;
        picArray[37] = R.drawable.bleach32;
        picArray[69] = R.drawable.bleach64;
        picArray[133] = R.drawable.bleach128;
        picArray[261] = R.drawable.bleach256;
        picArray[517] = R.drawable.bleach512;
        picArray[1029] = R.drawable.bleach1024;
        picArray[2053] = R.drawable.bleach2048;
        //秦时明月+25
        picArray[25] = R.drawable.bleach0;
        picArray[27] = R.drawable.moon2;
        picArray[29] = R.drawable.moon4;
        picArray[33] = R.drawable.moon8;
        picArray[41] = R.drawable.moon16;
        picArray[57] = R.drawable.moon32;
        picArray[89] = R.drawable.moon64;
        picArray[153] = R.drawable.moon128;
        picArray[281] = R.drawable.moon256;
        picArray[537] = R.drawable.moon512;
        picArray[1049] = R.drawable.moon1024;
        picArray[2073] = R.drawable.moon2048;
    }

    // 数字:数字相当于图片id
    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;

        my2048 = (MainActivity) this.getContext();
        model = my2048.getModel();
        switch (model) {
            case 1:// 普通模式
                pic.setBackgroundResource(picArray[num]);
                break;
            case 2:// 死神（bleach）模式
                pic.setBackgroundResource(picArray[num + 5]);
                break;
            case 3:// 秦时明月模式
                pic.setBackgroundResource(picArray[num + 25]);
                break;
            case 4:// **模式
                pic.setBackgroundResource(picArray[num + 17]);
                break;
        }
    }

    // 判断数字是否相同
    public boolean equals(CardView cv) {
        return getNum() == cv.getNum();
    }
}
