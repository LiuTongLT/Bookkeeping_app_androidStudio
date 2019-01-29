package be.kuleuven.softdev.liutong.javabean;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class report extends AppCompatActivity {
    private ImageButton dashboard;
    private ImageButton mAccount;

    private PieChart chart;
    private String[] x = new String[] {"food","education","medical","house","shopping","hotel",
            "car","mobileInternet","sports","travel","other"};
    private float[] y = new float[10];
    private float[] yP = new float[11];
    private int m =0;

    private String username;
    SharedPreferences userData;

    String res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        userData = getSharedPreferences("userData", 0);
        username =userData.getString("username","null");

        for(int d = 0; d<11;d++)
        {
            //y[d]=userData.getFloat(x[d],0.0f);
            yP[d]=userData.getFloat(x[d],0.0f);
        }

        dashboard = findViewById(R.id.menuBut);
        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDashboard();
            }
        });

        mAccount = findViewById(R.id.myAccountBut);
        mAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMyAccount();
            }
        });

        chart = (PieChart) findViewById(R.id.monthly);
        chart.setDescription("Monthly");

            draw(x.length);


    }

    private void openDashboard(){
        Intent intent = new Intent(this,dashboard.class);
        startActivity(intent);
    }

    private void openMyAccount(){
        Intent intent = new Intent(this,userAccount.class);
        startActivity(intent);
    }
    public void draw(int count) {

        // 准备x"轴"数据：在i的位置，显示x[i]字符串
        final ArrayList<String> xVals = new ArrayList<String>();
        xVals.ensureCapacity(15);

        // 真实的饼状图百分比分区。
        // Entry包含两个重要数据内容：position和该position的数值。
        List<Entry> yVals = new ArrayList<>();

        for (int xi = 0; xi < count; xi++) {
            if(yP[xi] != 0)
            {
                xVals.add(m, x[xi]);
                // y[i]代表在x轴的i位置真实的百分比占
                yVals.add(new Entry(yP[xi], m));
                m++;
            }

        }

        PieDataSet yDataSet = new PieDataSet(yVals, "proportion");

        // 每个百分比占区块绘制的不同颜色
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.BLUE);
        colors.add(Color.GRAY);
        colors.add(Color.YELLOW);
        colors.add(Color.CYAN);
        colors.add(Color.DKGRAY);
        colors.add(Color.rgb(3,168,158));
        colors.add(Color.rgb(255,192,203));
        colors.add(Color.rgb(255,0,255));
        colors.add(Color.rgb(255,222,173));
        yDataSet.setColors(colors);

        // 将x轴和y轴设置给PieData作为数据源
        PieData data = new PieData(xVals,yDataSet);

        // 设置成PercentFormatter将追加%号
        data.setValueFormatter(new PercentFormatter());

        // 文字的颜色
        data.setValueTextColor(Color.BLACK);

        // 最终将全部完整的数据喂给PieChart
        chart.setData(data);
        chart.invalidate();
    }

}
