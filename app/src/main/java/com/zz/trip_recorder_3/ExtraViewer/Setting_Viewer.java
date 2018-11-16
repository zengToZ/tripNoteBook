package com.zz.trip_recorder_3.ExtraViewer;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.zz.trip_recorder_3.R;
import com.zz.trip_recorder_3.staticGlobal;

public class Setting_Viewer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("Back");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setContentView(R.layout.activity_setting__viewer);

        final SharedPreferences settings = getSharedPreferences(staticGlobal.APP_NAME, 0);
        String checked = settings.getString("langSet", "EN");
        final RadioButton r1 = findViewById(R.id.Lang_en);
        final RadioButton r2 = findViewById(R.id.Lang_ch);
        if(checked == "EN"){
            r1.setChecked(true);
            r2.setChecked(false);
        }
        else if(checked == "CH"){
            r1.setChecked(false);
            r2.setChecked(true);
        }

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.Lang_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.Lang_en) {
                    settings.edit().putString("langSet","EN").apply();
                    Toast.makeText(Setting_Viewer.this, "change to English", Toast.LENGTH_SHORT).show();
                } else if(checkedId == R.id.Lang_ch) {
                    settings.edit().putString("langSet","CH").apply();
                    Toast.makeText(Setting_Viewer.this, "切换成中文", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
