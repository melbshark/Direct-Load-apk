package com.lody.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lody.plugin.PluginLaunch;

import java.io.File;

/**
 * Created by lody  on 2015/3/24.
 */
public class Main extends Activity
{
    Button button;
    EditText editText;

    Activity b;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        button = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.path);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = editText.getText().toString();
                if(path.isEmpty()){
                    Toast.makeText(Main.this,"请输入插件的路径！",Toast.LENGTH_SHORT).show();

                    return;
                }
                File file = new File(path);
                if(!file.exists()){
                    Toast.makeText(Main.this,"所在路径没有文件！！",Toast.LENGTH_SHORT).show();
                    return;
                }
                PluginLaunch.startPlugin(Main.this, path);
            }
        });
    }
}
