package cc.tachi.jlpt;

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
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
import java.util.Arrays;
import java.util.Objects;

import cc.tachi.jlpt.Fragment.FirstScreen;
import cc.tachi.jlpt.Fragment.Setting;
import cc.tachi.jlpt.Fragment.Vocab;
import cc.tachi.jlpt.Function.CrashHandler;
import cc.tachi.jlpt.Function.DBOperate;
import cc.tachi.jlpt.Widget.ChangeWord;
import cc.tachi.jlpt.Widget.MyWidget;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private SharedPreferences preferences;
    private FragmentManager fm;
    private FirstScreen firstScreen;
    private Setting setting;
    private Vocab vocab;
    private String[] lmenu = {"n2", "n3", "n4", "n5"};
    private int[] databese = {R.raw.n2, R.raw.n3, R.raw.n4, R.raw.n5};


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

        init();
    }

    private void init() {
        //导入数据库
        importDatabase();

        //权限申请
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        //注册异常监听器
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());

        //注册锁屏监听器
        register();

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
        if (id == R.id.action_showinwidget) {
            String level = (String) this.getTitle();
            if ((Arrays.asList(lmenu).contains(level))) {
                preferences = getSharedPreferences("ScrollPos", MODE_PRIVATE);
                String pos = preferences.getString(level, "0|0");
                String [] temp = null;
                temp = pos.split("\\|");
                SharedPreferences.Editor editor = getSharedPreferences("ShowPos", MODE_PRIVATE).edit();
                //数据库位置需要+1
                editor.putString("pos", String.valueOf(Integer.parseInt(temp[0]) + 1));
                editor.putString("level", level);
                editor.apply();
                Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "不允许", Toast.LENGTH_SHORT).show();
            }
            return true;
        }else if(id == R.id.action_showall){
            DBOperate dbo = new DBOperate(this);
            if (dbo.setAllChecked()){
                Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "不允许", Toast.LENGTH_SHORT).show();
            }
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
        } else if (id == R.id.recent) {
            bundle.putString("level", "recent");
            vocab = new Vocab();
            vocab.setArguments(bundle);
            fm.beginTransaction().replace(R.id.id_content, vocab).commit();
        } else if (id == R.id.nav_setting) {
            setting = new Setting();
            fm.beginTransaction().replace(R.id.id_content, setting).commit();
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
                    new ChangeWord(context);
                }
            }
        };
        registerReceiver(mBatInfoReceiver, filter);
    }

    public void importDatabase() {
        // 导入已有数据库
        String dirPath = "/data/data/cc.tachi.jlpt/databases";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        // 数据库文件
        File file;
        for(int i = 0;i<lmenu.length;i++) {
            file = new File(dir, lmenu[i]+".db");
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                // 加载需要导入的数据库
                InputStream is = this.getApplicationContext().getResources().openRawResource(databese[i]);
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

        //建立新的数据库
        SQLiteDatabase db = openOrCreateDatabase("recent.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE if not exists \"jlpt\" (\"_id\" INTEGER PRIMARY KEY NOT NULL ,\"level\" INTEGER,\"kanji\" TEXT NOT NULL ,\"hiragana\" TEXT,\"simplified_chinese\" TEXT NOT NULL ,\"traditional_chinese\" TEXT NOT NULL ,\"english\" TEXT NOT NULL , \"checked\" INTEGER NOT NULL )");
        db.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "拒绝存储权限将无法记录错误日志！", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
