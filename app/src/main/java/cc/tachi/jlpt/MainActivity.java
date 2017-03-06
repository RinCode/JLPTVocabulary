package cc.tachi.jlpt;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import cc.tachi.jlpt.Fragment.FirstScreen;
import cc.tachi.jlpt.Fragment.Vocab;
import cc.tachi.jlpt.Widget.MyWidget;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private SharedPreferences preferences;
    private SQLiteDatabase db;
    private FragmentManager fm;
    private FirstScreen firstScreen;
    private Vocab vocab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstScreen = new FirstScreen();
                fm.beginTransaction().replace(R.id.id_content, firstScreen).commit();
                setTitle("JLPT");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fm = getSupportFragmentManager();

        importDatabase();
        init();
    }

    private void init() {
        register();//注册锁屏监听器
        firstScreen = new FirstScreen();
        fm.beginTransaction().replace(R.id.id_content, firstScreen).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            String level = (String) this.getTitle();
            if (!Objects.equals(level, "JLPT")) {
                preferences = getSharedPreferences("ScrollPos", MODE_PRIVATE);
                String pos = preferences.getString(level, "0");
                SharedPreferences.Editor editor = getSharedPreferences("ShowPos", MODE_PRIVATE).edit();
                //数据库位置需要+1
                editor.putString("pos", String.valueOf(Integer.parseInt(pos) + 1));
                editor.putString("level", level);
                editor.apply();
                Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "不允许", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Bundle bundle = new Bundle();
        if (id == R.id.n2) {
            bundle.putString("level", "n2");
            vocab = new Vocab();
            vocab.setArguments(bundle);
            fm.beginTransaction().replace(R.id.id_content, vocab).commit();
        } else if (id == R.id.n3) {
            bundle.putString("level", "n3");
            vocab = new Vocab();
            vocab.setArguments(bundle);
            fm.beginTransaction().replace(R.id.id_content, vocab).commit();
        } else if (id == R.id.n4) {
            bundle.putString("level", "n4");
            vocab = new Vocab();
            vocab.setArguments(bundle);
            fm.beginTransaction().replace(R.id.id_content, vocab).commit();
        } else if (id == R.id.n5) {
            bundle.putString("level", "n5");
            vocab = new Vocab();
            vocab.setArguments(bundle);
            fm.beginTransaction().replace(R.id.id_content, vocab).commit();
        } else if (id == R.id.nav_setting) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void register() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                final String action = intent.getAction();
                if (Intent.ACTION_SCREEN_ON.equals(action)) {
                    AppWidgetManager manager = AppWidgetManager.getInstance(context);
                    ComponentName provider = new ComponentName(context, MyWidget.class);
                    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

                    preferences = getSharedPreferences("ShowPos", MODE_PRIVATE);
                    String pos = preferences.getString("pos", "1");
                    String level = preferences.getString("level", "n5");
                    SharedPreferences.Editor editor = context.getSharedPreferences("ShowPos", context.MODE_PRIVATE).edit();
                    editor.putString("pos", String.valueOf(Integer.parseInt(pos) + 1));
                    editor.apply();

                    db = openOrCreateDatabase(level + ".db", context.MODE_PRIVATE, null);
                    Cursor c = db.rawQuery("select * from jlpt where _id =" + pos, null);
                    //设置要显示的TextView，及显示的内容'
                    if (c != null) {
                        while (c.moveToNext()) {
                            views.setTextViewText(R.id.kanji, c.getString(c.getColumnIndex("kanji")));
                            views.setTextViewText(R.id.hiragana, c.getString(c.getColumnIndex("hiragana")));
                            views.setTextViewText(R.id.meaning, c.getString(c.getColumnIndex("simplified_chinese")));
                        }
                        // 发送一个系统广播
                        manager.updateAppWidget(provider, views);
                    } else {
                        editor.putString("position", "1");
                        editor.apply();
                    }
                    db.close();
                }
            }
        };
        registerReceiver(mBatInfoReceiver, filter);
    }

    public void importDatabase() {
        // 存放数据库的目录
        String dirPath = "/data/data/cc.tachi.jlpt/databases";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        // 数据库文件
        File file = new File(dir, "n2.db");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            // 加载需要导入的数据库
            InputStream is = this.getApplicationContext().getResources().openRawResource(R.raw.n2);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffere = new byte[is.available()];
            is.read(buffere);
            fos.write(buffere);
            is.close();
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        file = new File(dir, "n3.db");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            // 加载需要导入的数据库
            InputStream is = this.getApplicationContext().getResources().openRawResource(R.raw.n3);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffere = new byte[is.available()];
            is.read(buffere);
            fos.write(buffere);
            is.close();
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        file = new File(dir, "n4.db");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            // 加载需要导入的数据库
            InputStream is = this.getApplicationContext().getResources().openRawResource(R.raw.n4);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffere = new byte[is.available()];
            is.read(buffere);
            fos.write(buffere);
            is.close();
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        file = new File(dir, "n5.db");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            // 加载需要导入的数据库
            InputStream is = this.getApplicationContext().getResources().openRawResource(R.raw.n5);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffere = new byte[is.available()];
            is.read(buffere);
            fos.write(buffere);
            is.close();
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
