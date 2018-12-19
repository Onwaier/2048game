package com.example.onwaier.game2048pic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private int score = 0;
    private TextView tvScore;
    private TextView tvBestScore;
    private ListView listView;
    private int column = 4;
    private int row = 4;
    private int model = 1;
    private int rank = 1;
    private MediaPlayer mp;
    private Chronometer lastTime;
    private boolean music = false;
    private MainActivity mainActivity;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private MyDBHelper myDBHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private ArrayList<Map<String,String>> list=new ArrayList<Map<String,String>>();
    private CreateUserDialog createUserDialog;
    private LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ImageButton resbtn = (ImageButton) findViewById(R.id.restartBtn);
        final ImageButton controlbtn = (ImageButton)findViewById(R.id.controlBtn);
        lastTime = (Chronometer) findViewById(R.id.lastTime);
        tvScore = (TextView) findViewById(R.id.tvScore);
        tvBestScore = (TextView) findViewById(R.id.bestScore);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        listView = (ListView)findViewById(R.id.listView1);
        linearLayout = (LinearLayout)findViewById(R.id.mylinearlaout);
        myDBHelper = new MyDBHelper(this);
        db = myDBHelper.getReadableDatabase();
        cursor = db.rawQuery("select * from " + myDBHelper.getTableName(), null);
        controlbtn.setImageDrawable(getResources().getDrawable(R.drawable.pause));
        Intent intent = this.getIntent();
        row = intent.getIntExtra("row", 4);
        column = intent.getIntExtra("column", 4);
        model = intent.getIntExtra("model", 1);
        rank = intent.getIntExtra("rank", 1);

        //设置背景音乐
        switch (model) {
            case 1:// 数字版
                mp = MediaPlayer.create(this, R.raw.num);
                break;
            case 2:// 死神版
                mp = MediaPlayer.create(this, R.raw.bleach);
                break;
            case 3:// 秦时明月版
                mp = MediaPlayer.create(this, R.raw.moon);
                break;
            case 4:// 火影版
                //mp = MediaPlayer.create(this, R.raw.huoyin);
                break;
        }

        try {
            mp.prepare();// 让mp对象准备
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 一进来就播放音乐
        music = intent.getBooleanExtra("music", false);
        if (music && (null != mp)) {
            mp.start();
            mp.setLooping(true);
        }

        //设置title
        switch (rank){
            case 1:
                toolbar.setTitle(R.string.rank1);
                break;
            case 2:
                toolbar.setTitle(R.string.rank2);
                break;
            case 3:
                toolbar.setTitle(R.string.rank3);
                break;
            case 4:
                toolbar.setTitle(R.string.rank4);
            case 5:
                toolbar.setTitle(R.string.rank5);
        }

        // 开始游戏的逻辑
        GameView.getGameView().initGameView();
        int hour = (int) ((SystemClock.elapsedRealtime() - lastTime.getBase()) / 1000 / 60);
        lastTime.setFormat("0"+String.valueOf(hour)+":%s");
        lastTime.start();//开始计时

        //设置表格的高度
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();//屏幕宽度
        ViewGroup.LayoutParams lp;
        lp= linearLayout.getLayoutParams();
        lp.width=width;
        lp.height=width;
        linearLayout.setLayoutParams(lp);
        //排行榜数据库数据初始化(仅为测试数据库使用)
        if(cursor.getCount() == 0){
            db = myDBHelper.getWritableDatabase();
            //db.beginTransaction();

            //db.execSQL("insert into " + MyDBHelper.TABLE_NAME + "(Rank, Id, Score, LastTime)values(1, 'Onwaier', 1250, '03:20')");
            //db.execSQL("insert into " + MyDBHelper.TABLE_NAME + "(Rank, Id, Score, LastTime)values(1, 'Mary', 3550, '06:45')");
            //db.execSQL("insert into " + MyDBHelper.TABLE_NAME + "(Rank, Id, Score, LastTime)values(1, 'Tom', 1450, '10:20')");
           // db.setTransactionSuccessful();
            db.close();
        }
        //侧边栏
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        toggle.syncState();
        //drawer.addDrawerListener(toggle);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
           public void onDrawerStateChanged(int newState) {
                // 状态发生改变
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // 滑动的过程当中不断地回调 slideOffset：0~1
                View content = drawer.getChildAt(0);
                float scale = 1 - slideOffset;//1~0
                float leftScale = (float) (1 - 0.7 * scale);
                float rightScale = (float) (0.3f + 0.7 * scale);//0.7~1
                drawerView.setScaleX(leftScale);//1~0.7
                drawerView.setScaleY(leftScale);//1~0.7
                content.setScaleX(rightScale);
                content.setScaleY(rightScale);
                content.setTranslationX(drawerView.getMeasuredWidth() * (1 - scale));//0~width

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // 打开
                //Toast.makeText(getApplicationContext(), "已打开Drawer", Toast.LENGTH_SHORT).show();

                //显示排行榜
                int cnt = 1;
                db = myDBHelper.getReadableDatabase();
                cursor = db.rawQuery("select * from " + MyDBHelper.TABLE_NAME + " where Rank = " + rank + " order by Score desc, LastTime", null);
                if (cursor.getCount() > 0) {
                    list.clear();
                    Map map;
                    while (cursor.moveToNext()) {
                        map=new HashMap<String, String>();
                        map.put("RankNum", String.valueOf(cnt));
                        map.put("IdName", cursor.getString(1));
                        map.put("Score", cursor.getString(2));
                        map.put("LastTime", cursor.getString(3));
                        list.add(map);
                        ++cnt;
                        if(cnt > 10)//仅显示前10名
                            break;
                    }
                    SimpleAdapter simpleAdapter =new SimpleAdapter(
                            getApplicationContext(),//第一个参数上下文 当前的Activity
                            list, //第二个参数是一个集合类型的数据源
                            R.layout.simple_list_item_layout, //第三个参数是一个用于展示效果的Layout就是我们设定的布局文件
                            new String[]{"RankNum","IdName", "Score", "LastTime"}, //第四个参数通过源码可以看出需要的是一个K值的字符串数组
                            new int[]{R.id.rankNum, R.id.idName, R.id.score, R.id.lastTime}//第五个参数通过源码看出是一个与K值匹配的的控件对象
                    );
                    listView.setAdapter(simpleAdapter);
                }
                db.close();
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                // 关闭
                //Toast.makeText(getApplicationContext(), "已关闭Drawer", Toast.LENGTH_SHORT).show();
            }
        });


        //重新开始游戏
        resbtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                new AlertDialog.Builder(getMainActivity())
                        .setTitle("Reset Game")
                        .setMessage("Do you reset game?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                GameView.getGameView().startGame();
                                clearScore();
                            }
                        })
                        .create().show();

            }
        });

        //暂停/开始播放背景音乐
        controlbtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Drawable myDrawable = controlbtn.getDrawable();
                Drawable.ConstantState constantState1 =  getResources().getDrawable(R.drawable.start).getConstantState();
                Drawable.ConstantState constantState2 =  getResources().getDrawable(R.drawable.pause).getConstantState();
                if( myDrawable.getConstantState().equals(constantState1)){
                    controlbtn.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                    if (null != mp){
                        mp.start();
                        mp.setLooping(true);
                    }

                }
                else if(myDrawable.getConstantState().equals(constantState2)){
                    if (null != mp)
                        mp.pause();
                    controlbtn.setImageDrawable(getResources().getDrawable(R.drawable.start));
                }
            }
        });

    }

    private long exitTime = 0;

    //点击返回键时
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                //弹出提示，可以有多种方式
                Toast.makeText(getApplicationContext(), "再按一次退出游戏", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                if(myDBHelper.isOverRank(rank, score, lastTime.getText().toString().substring(5))){
                    new AlertDialog.Builder(getMainActivity())
                            .setTitle("保存成绩")
                            .setMessage("Do you want to store the score?")
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showEditDialog();
                                    //finish();
                                }
                            })
                            .create().show();

                }

                else {
                    lastTime.stop();//计时结束
                    finish();
                }

            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected   void onPause() {
        super.onPause();
        mp.pause();
    }

    protected void onResume(){
        super.onResume();
        if(mp != null){
            mp.start();
        }
    }

    public void showEditDialog() {
        createUserDialog = new CreateUserDialog(this);
        createUserDialog.InitDialog();
        createUserDialog.show();
        createUserDialog.setRank(rank);
        createUserDialog.setScore(score);
        createUserDialog.setLastTime(lastTime.getText().toString());
        createUserDialog.setMainActivity(this);
    }




    public MainActivity getMainActivity(){
        return this;
    }
    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public int getModel() {
        return model;
    }

    public MyDBHelper getMyDBHelper(){
        return myDBHelper;
    }
    public int getRank(){
        return rank;
    }
    public int getScore(){
        return score;
    }
    public String getLastTime(){
        return lastTime.getText().toString().substring(5);
    }
    public void clearScore() {//分数清0
        score = 0;
        showScore();
    }

    public void showScore() {//显示当前分数
        tvScore.setText(score + "");
    }

    public void addScore(int s) {//更新增加当前分数
        score += s;
        showScore();
        int maxScore = Math.max(score, getBestScore(rank));
        saveBestScore(maxScore, rank);
        showBestScore(maxScore);
    }

    public void initScore(){//初始化分数
        score = 0;
        showScore();
        db = myDBHelper.getReadableDatabase();
        cursor = db.rawQuery("select max(Score) from " + MyDBHelper.TABLE_NAME + " where Rank = " + rank, null);
        if(cursor.getCount() > 0){
            if(cursor.moveToFirst()){
                if(getBestScore(rank) > cursor.getInt(0)){
                    saveBestScore(cursor.getInt(0), rank);
                }
            }
        }
        showBestScore(getBestScore(rank));
        db.close();
    }

    // 保存分数
    public void saveBestScore(int s, int rank) {
        SharedPreferences.Editor e = getPreferences(MODE_PRIVATE).edit();
        e.putInt("bestScore" + rank, s);
        e.commit();
    }

    public int getBestScore(int rank) {//获取当前最高分数
        return getPreferences(MODE_PRIVATE).getInt("bestScore" + rank, 0);
    }

    public void showBestScore(int s) {//显示最高分数
        tvBestScore.setText(s + "");
    }

    // 销毁Activity时执行的，重写此方法表示Activity的生命周期结束不要一直播放占用资源
    protected void onDestroy() {
        super.onDestroy();
        if (null != mp)
            try {
                mp.release();
            } catch (Exception e) {
                e.getStackTrace();
            }
    }
}
