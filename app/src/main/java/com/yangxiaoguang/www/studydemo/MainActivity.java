package com.yangxiaoguang.www.studydemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yangxiaoguang.www.studydemo.Scan.ScanActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView scan;
    private ListView listView;
    private MyAdapter myAdapter;
    private List<JSONObject> jsonObjectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scan = (ImageView) findViewById(R.id.scan);

        listView = (ListView) findViewById(R.id.list);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);

                startActivityForResult(intent, ScanActivity.SCANRESULTREQUEST);
            }
        });



        myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadJson();
    }

    private void loadJson()
    {
        jsonObjectList.clear();
        File file = new File(getCacheDir().toString());
        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isFile() && file.getName().contains("json"))
                    return true;
                return false;
            }
        });

        for (File file1 : files)
        {
            try {
                Log.i("内容", file1.getName());
                FileInputStream fileInputStream = new FileInputStream(file1);
                byte[] bytes = new byte[fileInputStream.available()];
                fileInputStream.read(bytes);
                fileInputStream.close();

                String s = new String(bytes);
                Log.i("内容", s);
                JSONObject jsonObject = new JSONObject(s);
                jsonObjectList.add(jsonObject);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        myAdapter.notifyDataSetChanged();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ScanActivity.SCANRESULTREQUEST) {
            if (resultCode == 1) {

                String code = data.getExtras().getString("code");
                Log.i("onActivityResult 回调 ", code);
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra("code", code);
                startActivity(intent);

            }
            return;
        }


    }

    private class MyAdapter extends BaseAdapter {
        private ImageView imageView;
        private TextView t1,t2,t3,t4;
        @Override
        public int getCount() {
            return jsonObjectList.size();
        }

        @Override
        public Object getItem(int i) {
            return jsonObjectList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            JSONObject jsonObject = jsonObjectList.get(i);
            view = getLayoutInflater().inflate(R.layout.cell,null);
            imageView = (ImageView)view.findViewById(R.id.img);
            t1 = (TextView)view.findViewById(R.id.t1);
            t2 = (TextView)view.findViewById(R.id.t2);
            t3 = (TextView)view.findViewById(R.id.t3);
            t4 = (TextView)view.findViewById(R.id.t4);

            try {
                t1.setText(jsonObject.getString("bookbame"));
                t2.setText("总页数:" + jsonObject.getString("pages"));
                t3.setText("书签页:" + jsonObject.getString("nowpages"));
                t4.setText("藏书地:" + jsonObject.getString("addr"));
                String imguuid = jsonObject.getString("imguuid");

                Bitmap bitmap = BitmapFactory.decodeFile(getCacheDir() + "/" + imguuid + ".jpg");
                imageView.setBackground(null);
                imageView.setImageBitmap(bitmap);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return view;
        }

        @Override
        public CharSequence[] getAutofillOptions() {
            return new CharSequence[0];
        }
    }
}
