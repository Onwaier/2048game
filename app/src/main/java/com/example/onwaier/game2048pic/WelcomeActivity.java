package com.example.onwaier.game2048pic;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends AppCompatActivity {


    private TextView countdown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_welcome);
        countdown = (TextView) findViewById(R.id.TView_countdown);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        MyCountdownTimer countdowntimer = new MyCountdownTimer(6000, 1000);
        countdowntimer.start();
        skip();
    }

    // 5秒之后跳转到第二个页面
    // time计时器
    public void skip() {
        final Intent it = new Intent(WelcomeActivity.this, ChooseActivity.class); // 你要转向的Activity
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                startActivity(it); // 执行
                finish();
            }
        };

        timer.schedule(task, 1000 * 5);
    }

    //欢迎界面倒计时
    protected class MyCountdownTimer extends CountDownTimer {

        public MyCountdownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            countdown.setText("Close(" + millisUntilFinished / 1000 + ")");
        }

        @Override
        public void onFinish() {
            //countdown.setTextColor(Color.rgb(255, 255, 255));
            countdown.setVisibility(View.INVISIBLE);
        }

    }
}


