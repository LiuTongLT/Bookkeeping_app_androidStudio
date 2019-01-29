package be.kuleuven.softdev.liutong.javabean;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentTotal extends Fragment {
    View view;

    private PieChart chart;
    private String[] x = new String[] {"food","education","medical","house","shopping","hotel",
            "car","mobileInternet","sports","travel","other"};
    private float[] y = new float[10];
    private float[] yP = new float[11];

    private String username;
    private int year;
    private int month;
    //select year&month

    SharedPreferences userData ;

    public FragmentTotal() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.total,container,false);

        //because this is fragment,getsharedPreferences() is a method for context object
        userData = this.getActivity().getSharedPreferences("userData", 0);
        username =userData.getString("username","null");

        for(int d = 0; d<11;d++)
        {
            //y[d]=userData.getFloat(x[d],0.0f);
            yP[d]=userData.getFloat(x[d],0.0f);
        }

        // fragment should use getView() method for findViewById() method
        chart = (PieChart) view.findViewById(R.id.total);
        setChart();

        draw(x.length);


        return view;
    }

    private void setChart() {
        chart.setDescription("Total");
        chart.setHoleRadius(50f);
        chart.setDrawCenterText(true);
        chart.setCenterText("Total");
        chart.setCenterTextSize(25);
        chart.setCenterTextColor(Color.rgb(176,196,222));
        Legend mLegend = chart.getLegend();  //设置比例图
        mLegend.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
        mLegend.setXEntrySpace(7f);
        mLegend.setYEntrySpace(5f);
        //chart.animateXY(1000, 1000);
    }

    public void draw(int count) {

        int m =0;
        // 准备x"轴"数据：在i的位置，显示x[i]字符串
        final ArrayList<String> xVals = new ArrayList<String>();
        //xVals.ensureCapacity(15);

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

        PieDataSet yDataSet = new PieDataSet(yVals, "Proportion");

        // 每个百分比占区块绘制的不同颜色
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(Color.rgb(216,191,216)); //thistle
        colors.add(Color.rgb(144,238,144)); //light green
        colors.add(Color.rgb(255,228,225)); //misty rose
        colors.add(Color.rgb(135,206,250)); //light sky blue
        colors.add(Color.rgb(230,230,250)); //lavender
        colors.add(Color.rgb(175,238,238)); //pale Turquoise
        colors.add(Color.rgb(255,218,185)); //peach puff
        colors.add(Color.rgb(3,168,158)); // green blue
        colors.add(Color.rgb(255,192,203)); //pink
        colors.add(Color.rgb(188,143,143)); //rosy brown
        colors.add(Color.rgb(255,222,173)); //rice
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
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                if(e == null)
                    return;
                Toast.makeText(view.getContext(), "Proportion of "+xVals.get(e.getXIndex()) + " = " + e.getVal() + "%", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected() {
            }
        });
    }
}
