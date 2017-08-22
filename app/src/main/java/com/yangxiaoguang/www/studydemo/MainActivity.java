package com.yangxiaoguang.www.studydemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.yangxiaoguang.www.studydemo.Scan.ScanActivity;

public class MainActivity extends AppCompatActivity {

    private ImageView scan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scan = (ImageView)findViewById(R.id.scan);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);

                startActivityForResult(intent, ScanActivity.SCANRESULTREQUEST);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==ScanActivity.SCANRESULTREQUEST)
        {
            if (resultCode ==1)
            {

                String code =data.getExtras().getString("code");
                Log.i("onActivityResult 回调 " ,code);
                Intent intent=new Intent(MainActivity.this,ResultActivity.class);
                intent.putExtra("code",code);
                startActivity(intent);

            }
            return;
        }



    }
}
