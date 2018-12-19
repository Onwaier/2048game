package com.example.onwaier.game2048pic;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import static java.lang.Integer.parseInt;

/**
 * Created by Onwaier on 2018/4/14.
 */

public class CreateUserDialog extends Dialog {

    /**
     * 上下文对象 *
     */
    Activity context;

    private Button btnSave, btnCancel;
    private MyDBHelper myDBHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private int rank = 1;
    private EditText idName;
    private TextView lastTime;
    public TextView score;
    private CreateUserDialog myDialog;
    private MainActivity mainActivity;
    private View.OnClickListener mClickListener;

    public CreateUserDialog(Activity context) {
        super(context);
        this.context = context;
    }



    public CreateUserDialog getMyDialog(){
        return myDialog;
    }

    public void InitDialog(){
        this.setContentView(R.layout.create_user_dialog);
        idName = (EditText) findViewById(R.id.idName);
        lastTime = (TextView) findViewById(R.id.lastTime);
        score = (TextView) findViewById(R.id.score);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        myDialog = this;
        myDBHelper = new MyDBHelper(context);

         /*
         * 获取对话框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window
         * 对象,这样这可以以同样的方式改变这个Activity的属性.
         */
        Window dialogWindow = this.getWindow();

        WindowManager m = context.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        // p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.8
        dialogWindow.setAttributes(p);


        // 为按钮绑定点击事件监听器
        btnSave.setOnClickListener(onClickListener);
        btnCancel.setOnClickListener(onClickListener);
        this.setCancelable(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 指定布局
        this.setContentView(R.layout.create_user_dialog);
        idName = (EditText) findViewById(R.id.idName);
        lastTime = (TextView) findViewById(R.id.lastTime);
        score = (TextView) findViewById(R.id.score);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        myDialog = this;
        myDBHelper = new MyDBHelper(context);
         /*
         * 获取对话框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window
         * 对象,这样这可以以同样的方式改变这个Activity的属性.
         */
        Window dialogWindow = this.getWindow();

        WindowManager m = context.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        // p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.8
        dialogWindow.setAttributes(p);


        // 为按钮绑定点击事件监听器
        btnSave.setOnClickListener(onClickListener);
        btnCancel.setOnClickListener(onClickListener);
        this.setCancelable(true);

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btnSave:

                    String idName = myDialog.idName.getText().toString().trim();
                    String lastTime = myDialog.lastTime.getText().toString().trim().substring(5);
                    int score = parseInt(myDialog.score.getText().toString().trim().substring(4));

                    if(idName.equals("")){
                        Toast.makeText(context, "昵称不能为空!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        db = myDBHelper.getReadableDatabase();
                        cursor = db.rawQuery("select * from " + myDBHelper.getTableName() + " where Id = '" + idName + "' and Rank = " + rank, null);
                        if(cursor.getCount() > 0){
                            Toast.makeText(context, "该昵称已被使用！", Toast.LENGTH_SHORT).show();
                            db.close();
                        }
                        else{
                            db = myDBHelper.getWritableDatabase();
                            //db.beginTransaction();
                            //Toast.makeText(context, "insert into " + MyDBHelper.TABLE_NAME + "(Rank, Id, Score, LastTime)values(" + rank + ",'" + idName + "'," + score + ",'" + lastTime + "')", Toast.LENGTH_SHORT).show();
                            db.execSQL("insert into " + MyDBHelper.TABLE_NAME + "(Rank, Id, Score, LastTime)values(" + rank + ",'" + idName + "'," + score + ",'" + lastTime + "')");

                            //不区分大小写
                            // db.setTransactionSuccessful();
                            db.close();
                            Toast.makeText(context, "保存成功!", Toast.LENGTH_SHORT).show();
                            myDialog.dismiss();
                            mainActivity.finish();

                        }
                    }
                    break;
                case R.id.btnCancel:
                    myDialog.dismiss();
                    mainActivity.finish();
                    break;

            }
        }
    };

    public void setRank(int rank){
        this.rank = rank;
    }

    public void setScore(int score){
        this.score.setText("分数: " + String.valueOf(score));
    }

    public void setLastTime(String lastTime){
        this.lastTime.setText("游戏时长：" + lastTime);
    }

    public void setMainActivity(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }
}
