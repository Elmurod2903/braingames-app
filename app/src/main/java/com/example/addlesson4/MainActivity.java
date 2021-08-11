package com.example.addlesson4;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private GridLayout gridLayout;//bu layoutda berilgan kvadrat tomonni yasaydi
    private final int TIME_OUT = 300;
    private int count = 3;//kvadrat tomoni bildiradi
    private Random random;//ixtiyoriy sonni belgilashni amalga owiradi
    private int errorCount = 0; //xato belgilaniwlar sonini aniqlash uchun
    private TextView tvLivels;
    private int selectCount = 2;// to'g'ri belgilaniwlar soni
    private int levelCount = 1;//level sanagich
    private ImageView[] loves;//Bu jonni korsatib turadi
    private int selectColor, errorColor, defaultColor;//ranglar majmui
    private Handler handler;//qandaydir vaqt ichida amalni bajarish (time out)
    private int drawedCount = 0;  //random elementlarini togri topsa iwlaydi
    private int gameCounter = 1;// o'yinlar sonini aniqlash un iwlatiladi
    private int limit = 7;
    private AlertDialog.Builder builder;//ekranda o'yin tugagandan keyin chiquvchi dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initItems();//Bu metodda malumotlarni init qiliw yani kiritish,rang berish va bowqa dastlabki amallar
        generateItems();//Bu  metodda oyinni yig'ish ishlari
        randomItems();//bu metodda ixtiyoriy random belgilnadi
    }

    private void initItems() {
        gridLayout = findViewById(R.id.grid_layout);
        tvLivels = findViewById(R.id.tvLevels);
        loves = new ImageView[3];
        loves[0] = findViewById(R.id.love1);
        loves[1] = findViewById(R.id.love2);
        loves[2] = findViewById(R.id.love3);
        tvLivels.setText(levelCount + "");
        random = new Random();
        selectColor = getResources().getColor(R.color.selectColor);
        defaultColor = getResources().getColor(R.color.defaultColor);
        errorColor = getResources().getColor(R.color.errorColor);
        handler = new Handler();
    }

    private void randomItems() {
        int randomNumber;
        View item;
        for (int i = 0; i < selectCount; i++) {
            randomNumber = random.nextInt(count * count);
            item = gridLayout.getChildAt(randomNumber);
            if (item.getTag() == null) {
                item.setBackgroundColor(selectColor);
                item.setTag(new Object());
                handler.postDelayed(new MyRunnable(item), TIME_OUT);
            } else {
                i--;
            }
        }
    }

    private void generateItems() {
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(count);
        gridLayout.setRowCount(count);
        View view;
        for (int i = 0; i < count * count; i++) {
            view = getLayoutInflater().inflate(R.layout.layout, gridLayout, false);
            view.setOnClickListener(this);
            gridLayout.addView(view);
        }
    }

    @Override
    public void onClick(View v) {
        int items;
        final Object object = v.getTag();
        boolean back = false;
        if (object != null) {
            items = selectColor;
            drawedCount++;

        } else {
            back = true;
            items = errorColor;
            loves[errorCount].setImageResource(R.drawable.action_border_heart);
            errorCount++;
            if (errorCount == 3) {
                count = 3;
                selectCount = 2;
                gameCounter=0;
                levelCount = 1;
                tvLivels.setText(levelCount + "");
                levelUp();
                gameCounter++;
                for (ImageView love : loves) {
                    love.setImageResource(R.drawable.ic_action_name);
                }
            }
        }
        v.setBackgroundColor(items);
        if (back) handler.postDelayed(new MyRunnable(v), TIME_OUT);
        if (drawedCount == selectCount) {
            drawedCount = 0;
           // gameCounter++;
            handler.postDelayed(new IncreaseLevelRunnable(), TIME_OUT);
        }
    }

    private class MyRunnable implements Runnable {
        private View view;

        public MyRunnable(View view) {
            this.view = view;
        }

        @Override
        public void run() {
            view.setBackgroundColor(defaultColor);
        }
    }

    private class IncreaseLevelRunnable implements Runnable {
        @Override
        public void run() {
            if ((gameCounter != 0 && gameCounter % 3 == 0)) {
                count++;
                selectCount++;
            }
            generateItems();
            randomItems();
            gameCounter++;
            if (count == 4) {
                levelCount = 2;
                tvLivels.setText(levelCount + "");
            }
            if (count == 5) {
                levelCount = 3;
                tvLivels.setText(levelCount + "");
            }
            if (count == 6) {
                levelCount = 4;
                tvLivels.setText(levelCount + "");
            }
            if (limit == count) {
                builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getResources().getString(R.string.winner));
                builder.setMessage(getResources().getString(R.string.davomEtiw));
                builder.setCancelable(false);
                builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        count = 3;
                        gameCounter = 0;
                        selectCount = 2;
                        levelCount = 1;
                        tvLivels.setText(levelCount + "");
                        levelUp();
                        gameCounter++;
                        for (ImageView love : loves) {
                            love.setImageResource(R.drawable.ic_action_name);
                        }
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                });
                gridLayout.removeAllViews();
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    private void levelUp() {
        generateItems();
        randomItems();
        errorCount = 0;
    }
}
