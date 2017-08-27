package com.yangxiaoguang.www.studydemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ResultActivity extends AppCompatActivity {

    private EditText addr, nowpages, pages, bookbame;
    private ImageView imageView;
    private String code;
    private String imguuid = "";
    private Button btnsave;
    private static int REQUEST_THUMBNAIL = 1;// 请求缩略图信号标识
    private static int REQUEST_ORIGINAL = 2;// 请求原图信号标识

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        bookbame = (EditText) findViewById(R.id.bookname);
        pages = (EditText) findViewById(R.id.pages);
        nowpages = (EditText) findViewById(R.id.nowpages);
        addr = (EditText) findViewById(R.id.addr);
        imageView = (ImageView) findViewById(R.id.p1);
        btnsave = (Button) findViewById(R.id.btnsave);
        code = getIntent().getStringExtra("code");

        File file = new File(getCacheDir() + "/" + code + ".json");
        if (file.exists()) {
            try {

                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] bytes = new byte[fileInputStream.available()];
                fileInputStream.read(bytes);
                fileInputStream.close();

                String s = new String(bytes);
                Log.i("内容", s);
                JSONObject jsonObject = new JSONObject(s);
                bookbame.setText(jsonObject.getString("bookbame"));
                pages.setText(jsonObject.getString("pages"));
                nowpages.setText(jsonObject.getString("nowpages"));
                addr.setText(jsonObject.getString("addr"));
                imguuid = jsonObject.getString("imguuid");

                Bitmap bitmap = BitmapFactory.decodeFile(getCacheDir() + "/" + imguuid + ".jpg");
                imageView.setBackground(null);
                imageView.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }


        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bookbame.getText().toString().equals("")) {
                    Toast.makeText(ResultActivity.this, "不要忘记输入书名哦~~", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("bookbame", bookbame.getText().toString());
                    jsonObject.put("pages", pages.getText().toString());
                    jsonObject.put("nowpages", nowpages.getText().toString());
                    jsonObject.put("addr", addr.getText().toString());
                    jsonObject.put("imguuid", code);

                    File file = new File(getCacheDir() + "/" + code + ".json");
                    file.delete();

                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(jsonObject.toString().getBytes());
                    fileOutputStream.close();

                    Toast.makeText(ResultActivity.this, "收藏完成，记得有空回来看看哦~~", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(ResultActivity.this, "出了点问题，不能收藏这本书", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(getCacheDir() + "/" + code + ".jpg");
                file.delete();
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                Uri uri = Uri.fromFile(file);
//                //为拍摄的图片指定一个存储的路径
//                intent2.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent2, REQUEST_THUMBNAIL);
            }
        });


    }



    /**
     * 返回应用时回调方法
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_THUMBNAIL) {//对应第一种方法
                /**
                 * 通过这种方法取出的拍摄会默认压缩，因为如果相机的像素比较高拍摄出来的图会比较高清，
                 * 如果图太大会造成内存溢出（OOM），因此此种方法会默认给图片尽心压缩
                 */
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");
                File file = new File(getCacheDir() + "/" + code + ".jpg");
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                }
                catch (Exception e)
                {e.printStackTrace();}
                imageView.setBackground(null);
                imageView.setImageBitmap(bitmap);
            } else if (resultCode == REQUEST_ORIGINAL) {//对应第二种方法
                /**
                 * 这种方法是通过内存卡的路径进行读取图片，所以的到的图片是拍摄的原图
                 */
                FileInputStream fis = null;
                try {
                    File file = new File(getCacheDir() + "/" + code + ".jpg");
                    //把图片转化为字节流
                    fis = new FileInputStream(file);
                    //把流转化图片
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    imageView.setBackground(null);
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
