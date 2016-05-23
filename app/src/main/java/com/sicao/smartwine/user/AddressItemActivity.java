package com.sicao.smartwine.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**地址选择的调整
 * Created by android on 2016/4/22.
 */
public class AddressItemActivity extends BaseActivity{
    private StringBuilder builder = new StringBuilder();
    private StringBuffer privincenimei=new StringBuffer();
    private StringBuilder city=new StringBuilder();
    private ListView lv_address;
   // private SelectAddressAdapter adapter;
    private TextView tv_mata;
    //
    private String CITYSORT = "CityID";
    private String mPorSort = "";
    private String CitySort = "";
    //private ArrayList<CityEntity> province;
    private final static String DATABASE_PATH = android.os.Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/database";
    private static String DATABASE_FILENAME = "chinaprovincecityzone.db";
    private SQLiteDatabase db;
    private TextView tv_private,tv_city,tv_city_lines,tv_privince_lines;

    public SQLiteDatabase getDb() {
        db = openDatabase(AddressItemActivity.this);
        return db;
    }

    // 使用外部数据库
    public SQLiteDatabase openDatabase(Context context) {
        try {
            // Context context=new TestActivity();
            String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
            File dir = new File(DATABASE_PATH);
            if (!dir.exists()) // 如果文件夹不存在创建文件夹
                dir.mkdir();
            if (!(new File(databaseFilename)).exists()) { // 如果文件不存在创建文件
                InputStream is = context.getResources().openRawResource(
                        R.raw.chinaprovincecityzone);
                FileOutputStream fos = new FileOutputStream(databaseFilename);
                byte[] buffer = new byte[8192];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            db = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return db;
    }
    @Override
    protected int setView() {
        return R.layout.activity_address_item;
    }

    @Override
    public String setTitle() {
        return "配送至";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
