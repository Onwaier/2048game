package com.example.onwaier.game2048pic;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Onwaier on 2018/4/7.
 */

public class GameView extends GridLayout {

    private float startX, startY, endX, endY, offX, offY;
    private int row = 4, colunm = 4;// 行row对应y，列colunm对应x,默认开始都为4
    private CardView[][] cardsMap = new CardView[10][10];// 用一个二维数组来存
    private List<Point> emptyPoints = new ArrayList<Point>();// 链表方便增加删除
    private static GameView gview = null;
    private MainActivity my2048;
    private MediaPlayer mp;
    private boolean flag = false, flag1 = false;
    //private CardAnim ca = new CardAnim();
    private int cw;

    // 在xml中能够访问则要添加构造方法
    // 以防万一三个构造方法都要写:对应参分别为上下文，属性，样式
    public GameView(Context context) {
        super(context);
        gview = this;
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gview = this;
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        gview = this;
    }

    public static GameView getGameView() {
        return gview;
    }

    // 由于手机可能不同，我们需要动态地获取卡片的宽高，所以要重写下面这个方法获取当前布局的宽高，
    // 为了让手机不会因倒过来改变宽高，要去mainifest里配置
    // 只会在手机里第一次运行的时候执行，之后不会改变
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int cardWidth = (w - 10) / colunm;
        addCards(cardWidth, cardWidth);// 把参数传过去
        startGame();
    }

    public void startGame() {
        for (int y = 0; y < row; y++) {
            for (int x = 0; x < colunm; x++) {
                cardsMap[x][y].setNum(0);
            }
        }

        my2048.initScore();
        my2048.clearScore();
        addRandomNum();
        addRandomNum();
    }

    private void addCards(int cardWidth, int cardHeigth) {
        CardView c;
        cw = cardWidth;

        for (int y = 0; y < row; y++) {
            for (int x = 0; x < colunm; x++) {

                c = new CardView(getContext());
                // 先都初始画0号图片
                c.setNum(0);
                addView(c, cardWidth, cardHeigth);
                // 把所有的卡片都记录下来
                cardsMap[x][y] = c;
            }
        }
    }

    // 添加随机数的时候要先遍历
    private void addRandomNum() {

        emptyPoints.clear();
        for (int y = 0; y < row; y++) {
            for (int x = 0; x < colunm; x++) {
                if (cardsMap[x][y].getNum() <= 0) {
                    emptyPoints.add(new Point(x, y));// 把空位给emptypoints链表
                }
            }
        }
        // 随机把emptyPoints中的一个赋值，生成2的概率为9,4为1
        Point p = emptyPoints.remove((int) (Math.random() * emptyPoints.size()));
        // 2号图片和4号图片
        cardsMap[p.x][p.y].setNum(Math.random() > 0.1 ? 2 : 4);
    }

    // GameView实现的功能
    public void initGameView() {
        my2048 = (MainActivity) this.getContext();
        colunm = my2048.getColumn();
        row = my2048.getRow();

        setColumnCount(colunm);// 设置表格为4列
        setRowCount(row);
        setBackgroundColor(0xffbbada0);
        setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();// 获取触屏的动作
                switch (action) {
                    // 按下获取起始点
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    // 松开获取终止点，通过比较位移来判断滑动方向
                    // 要处理一下滑动偏的，看offx和offy哪个绝对值大就按照哪个来
                    case MotionEvent.ACTION_UP:
                        endX = event.getX();
                        endY = event.getY();
                        offX = startX - endX;
                        offY = startY - endY;
                        if (Math.abs(offX) >= Math.abs(offY)) {
                            if (offX >= 5)
                                moveLeft();// System.out.println("左");
                            else if (offX < -5)
                                moveRight();// System.out.println("右");
                        } else if (Math.abs(offX) <= Math.abs(offY)) {
                            if (offY >= 5)
                                moveUp();// System.out.println("上");
                            else if (offY < -5)
                                moveDown();// System.out.println("下");
                        }
                        break;
                }
                // !!!要改为true，否则ACTION_UP不会执行
                return true;
            }
        });
    }

    private void moveLeft() {
        boolean merge = false;
        flag = false;
        flag1 = false;
        for (int y = 0; y < row; y++) {
            for (int x = 0; x < colunm; x++) {
                // 遍历当前位置的右边，如果有数字，如果当前位置没有数字，则合并到当前位置
                for (int x1 = x + 1; x1 < colunm; x1++) {
                    // 每个右边的位置只判断执行一次
                    if (cardsMap[x1][y].getNum() > 0) {

                        if (cardsMap[x][y].getNum() <= 0) {
                            cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
                            cardsMap[x1][y].setNum(0);

                            x--;// 填补空位后，还要再次判断有没相同的可以合并的
                            merge = true;
                            if(!flag && !flag1) {
                                mp = MediaPlayer.create(getContext(), R.raw.move);
                                //mp.start();
                                flag = true;
                            }

                            //MyPlayer.playTone(getContext(),
                             //       MyPlayer.INDEX_SONG_MOVE);
                            break;
                        } else if (cardsMap[x][y].equals(cardsMap[x1][y])) {
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
                            cardsMap[x1][y].setNum(0);
                            my2048.addScore(cardsMap[x][y].getNum());
                            merge = true;
                            //MyPlayer.playTone(getContext(),
                            //        MyPlayer.INDEX_SONG_CANCEL);

                            if(!flag1) {
                                mp = MediaPlayer.create(getContext(), R.raw.del);
                                //mp.start();
                                flag1 = true;
                            }

                            break;
                        }
                        break;
                    }
                }
            }
        }
        mp.start();
        if (merge) {
            addRandomNum();
            checkComplete();
        }
    }

    private void moveRight() {
        boolean merge = false;
        flag  = false;
        flag1 = false;
        for (int y = 0; y < row; y++) {
            for (int x = colunm - 1; x >= 0; x--) {
                // 遍历当前位置的右边，如果有数字，如果当前位置没有数字，则合并到当前位置
                for (int x1 = x - 1; x1 >= 0; x1--) {
                    // 每个右边的位置只判断执行一次
                    if (cardsMap[x1][y].getNum() > 0) {

                        if (cardsMap[x][y].getNum() <= 0) {
                            cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
                            cardsMap[x1][y].setNum(0);
                            x++;// 填补空位后，还要再次判断有没相同的可以合并的
                            merge = true;
                            //MyPlayer.playTone(getContext(),
                            //       MyPlayer.INDEX_SONG_MOVE);

                            if(!flag && !flag1) {
                                mp = MediaPlayer.create(getContext(), R.raw.move);
                                //mp.start();
                                flag = true;
                            }
                            break;
                        } else if (cardsMap[x][y].equals(cardsMap[x1][y])) {
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
                            cardsMap[x1][y].setNum(0);
                            my2048.addScore(cardsMap[x][y].getNum());
                            merge = true;
                            //MyPlayer.playTone(getContext(),
                            //        MyPlayer.INDEX_SONG_CANCEL);
                            if(!flag1) {
                                mp = MediaPlayer.create(getContext(), R.raw.del);
                               // mp.start();
                                flag1 = true;
                            }
                            break;
                        }

                        break;
                    }
                }
            }
        }
        mp.start();
        if (merge) {
            addRandomNum();
            checkComplete();
        }

    }

    private void moveUp() {
        boolean merge = false;
        flag = false;
        flag1 = false;
        for (int x = 0; x < colunm; x++) {
            for (int y = 0; y < row; y++) {
                // 遍历当前位置的右边，如果有数字，如果当前位置没有数字，则合并到当前位置
                for (int y1 = y + 1; y1 < row; y1++) {
                    // 每个右边的位置只判断执行一次
                    if (cardsMap[x][y1].getNum() > 0) {

                        if (cardsMap[x][y].getNum() <= 0) {
                            cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
                            cardsMap[x][y1].setNum(0);
                            y--;// 填补空位后，还要再次判断有没相同的可以合并的
                            merge = true;
                            //MyPlayer.playTone(getContext(),
                            //        MyPlayer.INDEX_SONG_MOVE);

                            if(!flag && !flag1) {
                                mp = MediaPlayer.create(getContext(), R.raw.move);
                                //mp.start();
                                flag = true;
                            }
                            break;
                        } else if (cardsMap[x][y].equals(cardsMap[x][y1])) {
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
                            cardsMap[x][y1].setNum(0);
                            my2048.addScore(cardsMap[x][y].getNum());
                            merge = true;
                            //MyPlayer.playTone(getContext(),
                            //        MyPlayer.INDEX_SONG_CANCEL);

                            if(!flag1) {
                                mp = MediaPlayer.create(getContext(), R.raw.del);
                                //mp.start();
                                flag1 = true;
                            }
                            break;
                        }
                        break;
                    }
                }
            }
        }
        mp.start();
        if (merge) {
            addRandomNum();
            checkComplete();
        }

    }

    private void moveDown() {
        boolean merge = false;
        flag = false;
        flag1 = false;
        for (int x = 0; x < colunm; x++) {
            for (int y = row - 1; y >= 0; y--) {
                // 遍历当前位置的右边，如果有数字，如果当前位置没有数字，则合并到当前位置
                for (int y1 = y - 1; y1 >= 0; y1--) {
                    // 每个右边的位置只判断执行一次
                    if (cardsMap[x][y1].getNum() > 0) {

                        if (cardsMap[x][y].getNum() <= 0) {
                            cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
                            cardsMap[x][y1].setNum(0);
                            y++;// 填补空位后，还要再次判断有没相同的可以合并的
                            merge = true;
                            //MyPlayer.playTone(getContext(),
                             //       MyPlayer.INDEX_SONG_MOVE);
                            if(!flag && !flag1) {
                                mp = MediaPlayer.create(getContext(), R.raw.move);
                               // mp.start();
                                flag = true;
                            }
                            break;
                        } else if (cardsMap[x][y].equals(cardsMap[x][y1])) {
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
                            cardsMap[x][y1].setNum(0);
                            my2048.addScore(cardsMap[x][y].getNum());
                            merge = true;
                           // MyPlayer.playTone(getContext(),
                            //        MyPlayer.INDEX_SONG_CANCEL);

                            if(!flag1) {
                                mp = MediaPlayer.create(getContext(), R.raw.del);
                                //mp.start();
                                flag1 = true;
                            }
                            break;
                        }
                        break;
                    }
                }
            }
        }
        mp.start();
        if (merge) {
            addRandomNum();
            checkComplete();
        }

    }

    // 判断结束
    private void checkComplete() {

        boolean complete = true;
        for (int y = 0; y < row; y++) {
            for (int x = 0; x < colunm; x++) {
                if (cardsMap[x][y].getNum() == 2048)
                    new AlertDialog.Builder(getContext())
                            .setTitle("你好")
                            .setMessage("游戏胜利")
                            .setPositiveButton("重来",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            startGame();
                                        }
                                    }).show();
            }
        }

        ALL: for (int y = 0; y < row; y++) {
            for (int x = 0; x < colunm; x++) {
                // 如果还有空位，或者四个方向上还有相同的
                if (cardsMap[x][y].getNum() == 0
                        || (x > 0 && cardsMap[x][y].equals(cardsMap[x - 1][y]))
                        || (x < 3 && cardsMap[x][y].equals(cardsMap[x + 1][y]))
                        || (y > 0 && cardsMap[x][y].equals(cardsMap[x][y - 1]))
                        || (y < 3 && cardsMap[x][y].equals(cardsMap[x][y + 1]))) {

                    complete = false;
                    break ALL;// 如果出现这种情况，跳出双重循环，只写一个break只能跳出当前循环
                }
            }
        }

        if (complete) {
            if(my2048.getMyDBHelper().isOverRank(my2048.getRank(), my2048.getScore(), my2048.getLastTime())){
                new AlertDialog.Builder(getContext())
                        .setTitle("保存成绩")
                        .setMessage("Do you want to store the score?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                my2048.showEditDialog();
                            }
                        })
                        .create().show();

            }
            new AlertDialog.Builder(getContext())
                    .setTitle("你好")
                    .setMessage("游戏结束")
                    .setPositiveButton("重来",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    startGame();
                                }
                            }).show();
        }
    }
}
