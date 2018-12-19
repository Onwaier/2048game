package com.example.onwaier.game2048pic;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ChooseActivity extends AppCompatActivity {

    private int row = 4, column = 4;
    private int model = 1;//对应的游戏模式
    private int rank = 1;//对应不同的排行榜和最高分
    private ImageView Btn4x4;
    private ImageView Btn5x5;
    private ImageView Btn6x6;
    private ImageView BtnBleach;
    private ImageView BtnMoon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_choose);
        getView();
        setListeners();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    // 用来计算返回键的点击间隔时间
     private long exitTime = 0;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                         && event.getAction() == KeyEvent.ACTION_DOWN) {
                         if ((System.currentTimeMillis() - exitTime) > 2000) {
                                //弹出提示，可以有多种方式
                                 Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                                exitTime = System.currentTimeMillis();
                           } else {
                             //System.exit(0);
                             finish();

                             }
                         return true;
                     }

                 return super.onKeyDown(keyCode, event);
    }


    public void getView() {
        Btn4x4 = (ImageView) findViewById(R.id.Btn4x4);
        Btn5x5 = (ImageView) findViewById(R.id.Btn5x5);
        Btn6x6 = (ImageView) findViewById(R.id.Btn6x6);
        BtnBleach = (ImageView) findViewById(R.id.BleachBtn);
        BtnMoon = (ImageView)findViewById(R.id.MoonBtn);
//        NormalBtn = (Button) findViewById(R.id.NormalBtn);
//        DigitBtn = (Button) findViewById(R.id.DigitBtn);
//        QilongzhuBtn = (Button) findViewById(R.id.QilongzhuBtn);
//        HuoyinBtn = (Button) findViewById(R.id.HuoyinBtn);
    }

    //多个按钮添加监听事件用这种格式简洁明了
    public void setListeners() {
        int button[] = {R.id.Btn4x4, R.id.Btn5x5, R.id.Btn6x6, R.id.BleachBtn, R.id.MoonBtn};

        // 创一个事件处理类
        ButtonListener bl = new ButtonListener();
        for (int i = 0; i < button.length; i++) {
            ImageView btn = (ImageView)findViewById(button[i]);
            btn.setOnClickListener(bl);
        }
    }

    class ButtonListener implements View.OnClickListener {
        //他们都要发生跳转，然后各自传不同的参数
        @Override
        public void onClick(View v) {
            if(v instanceof ImageView){
                //发生跳转
                Intent intent =new Intent(ChooseActivity.this,MainActivity.class);
                //传不同参
                int btnId=v.getId();
                switch (btnId) {
                    case R.id.Btn4x4:
                        model=1;
                        column = 4;
                        row = 4;
                        rank = 1;
                        break;
                    case R.id.Btn5x5:
                        model=1;
                        column = 5;
                        row = 5;
                        rank = 2;
                        break;
                    case R.id.Btn6x6:
                        model=1;
                        column = 6;
                        row = 6;
                        rank = 3;
                        break;
                    case R.id.BleachBtn:
                        column = 4;
                        row = 4;
                        model = 2;
                        rank = 4;
                        break;
                    case R.id.MoonBtn:
                        column = 4;
                        row = 4;
                        model = 3;
                        rank = 5;
                        break;
                    default:
                        break;
                }
                intent.putExtra("column", column);
                intent.putExtra("row", row);
                intent.putExtra("model", model);
                intent.putExtra("music", true);
                intent.putExtra("rank", rank);
                startActivity(intent);
            }
        }
    }

}
