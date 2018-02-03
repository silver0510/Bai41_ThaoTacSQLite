package com.example.sinki.bai41_thaotacsqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String DATABASE_NAME="dbContact.sqlite";
    private static final String DB_PATH_SUFFIX = "/databases/";
    SQLiteDatabase database=null;

    ListView lvDanhBa;
    ArrayList<String>dsDanhBa;
    ArrayAdapter<String>adapterDanhBa;

    Button btnThem,btnSua,btnXoa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xuLySaoChepCSDLTuAssetsVaoHeThongMobile();
        addControls();
        addEvents();

        showAllContactOnListView();
    }

    private void showAllContactOnListView() {
        //Bước 1:Mở CDSL
        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor = database.query("Contact",null,null,null,null,null,null);
        //Cursor cursor1 = database.rawQuery("Select * from Contact",null);
        dsDanhBa.clear();
        while (cursor.moveToNext())
        {
            int ma = cursor.getInt(0);
            String ten = cursor.getString(1);
            String phone = cursor.getString(2);
            dsDanhBa.add(ma+"-"+ten+"\n"+phone);
        }
        cursor.close();
        adapterDanhBa.notifyDataSetChanged();
    }

    private void addEvents() {
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xuLyThemDanhBa();
            }
        });
        btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xuLySua();
            }
        });
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xuLyXoa();
            }
        });
    }

    private void xuLyXoa() {
        database.delete("Contact","ma=?",new String[]{"1"});
        showAllContactOnListView();
    }

    private void xuLySua() {
        ContentValues row = new ContentValues();
        row.put("Ten","Trần Duy Hưng");
        database.update("Contact",row,"Ma=?",new String[]{"3"});
        showAllContactOnListView();
    }

    private void xuLyThemDanhBa() {
        //Lưu ý: vì lần trước đã thêm rồi nên khi xem lại code
        //nhấn lại nút này có thể bị lỗi do trùng khóa chính
        ContentValues row = new ContentValues();
        row.put("Ten","Phạm Phú Hiếu");
        row.put("Phone","0905123587");
        long ret = database.insert("Contact",null,row);
        if(ret>0)
        {
            Toast.makeText(MainActivity.this,"Thêm thành công, kết quả ret = "+ret,Toast.LENGTH_SHORT).show();
        }
        showAllContactOnListView();
    }

    private void addControls() {
        lvDanhBa = (ListView) findViewById(R.id.lvDanhBa);
        dsDanhBa = new ArrayList<>();
        adapterDanhBa = new ArrayAdapter<String>(
                MainActivity.this,
                android.R.layout.simple_list_item_1,
                dsDanhBa);
        lvDanhBa.setAdapter(adapterDanhBa);

        btnThem = (Button) findViewById(R.id.btnThem);
        btnSua = (Button) findViewById(R.id.btnSua);
        btnXoa = (Button) findViewById(R.id.btnXoa);
    }

    private void xuLySaoChepCSDLTuAssetsVaoHeThongMobile() {
        //private app
        File dbFile = getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists())    {
            try
            {
                CopyDataBaseFromAsset();
                Toast.makeText(this, "Copying sucess from Assets folder", Toast.LENGTH_LONG).show();
            }
            catch (Exception e)
            {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void CopyDataBaseFromAsset() {
        try
        {
            InputStream myInput;
            myInput = getAssets().open(DATABASE_NAME);

            // Path to the just created empty db
            String outFileName = layDuongDanLuuTru();

            // if the path doesn't exist first, create it
            File f = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if (!f.exists())
            {
                f.mkdir();
            }
            // Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);

            // transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            // Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }
        catch (Exception ex)
        {
            Log.e("Loi sao chep",ex.toString());
        }
    }

    private String layDuongDanLuuTru()
    {
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX+ DATABASE_NAME;
    }
}
