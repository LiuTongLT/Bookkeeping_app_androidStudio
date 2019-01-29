package be.kuleuven.softdev.liutong.javabean;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FragmentYear extends Fragment{
    View view;

    private PieChart chart;
    private String[] x = new String[] {"food","education","medical","house","shopping","hotel",
            "car","mobileInternet","sports","travel","other"};
    private float[] yP = new float[11];

    private int year;

    SharedPreferences userData;

    private TextView tvTime;
    private Button btn_show;
    private List<String> optionYears = new ArrayList<>();

    public FragmentYear() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.year,container,false);

        userData = this.getActivity().getSharedPreferences("userData", 0);

        // fragment should use getView() method for findViewById() method
        chart = (PieChart) view.findViewById(R.id.yearly);
        setChart();

        year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();;
        /*for(int d = 0; d<11;d++)
        {
            int total = 0;
            for(int i = 1; i<=12; i++)
            {
                total += userData.getFloat(x[d]+year+i,0.0f);
            }
            yP[d] = total;
        }*/
        for(int d=0; d<11; d++)
        {
            yP[d] = userData.getFloat(x[d]+year,0.0f);
        }
        draw(x.length);

        tvTime = (TextView) view.findViewById(R.id.tv_time_year);
        tvTime.setText(new StringBuilder(""+year));
        btn_show = (Button) view.findViewById(R.id.btn_show_year);
        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickerView(view);
            }
        });
        initData();

        return view;
    }

    private void setChart() {
        chart.setDescription("Yearly");
        chart.setHoleRadius(50f);
        chart.setDrawCenterText(true);
        chart.setCenterText("Yearly");
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

        List<Entry> yVals = new ArrayList<>();

        for (int xi = 0; xi < count; xi++) {
            if(yP[xi] != 0)
            {
                xVals.add(m, x[xi]);
                yVals.add(new Entry(yP[xi], m));
                m++;
            }

        }

        PieDataSet yDataSet = new PieDataSet(yVals, "Amount");

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
        //data.setValueFormatter(new PercentFormatter());

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
                Toast.makeText(view.getContext(), "Amount of "+xVals.get(e.getXIndex()) + " = " + e.getVal(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected() {
            }
        });
    }

    private void initData() {
        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        for (int i = curYear + 1; i >= 1989; i--) {
            //对应年份的月份数据集合
            List<String> tempMonths = new ArrayList<>();
            if (i == curYear + 1) {
                //设置最新时间“至今”
                optionYears.add("Until now");
            } else if (i == curYear) {
                //设置当前年份及其对应的月份
                optionYears.add(String.valueOf(i));
            } else if (i == 1989) {
                //设置最早时间“1900以前”
                optionYears.add("Before 1990");
            } else {
                //设置常规时间
                optionYears.add(String.valueOf(i));
            }
        }
    }


    /**
     * 显示滚轮
     *
     * @param view
     */
    public void showPickerView(View view) {
        OptionsPickerView multipleOp = new OptionsPickerView.Builder(this.getActivity(), new OptionsPickerView.OnOptionsSelectListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View view) {
                if (options1 == 0 || options1 == optionYears.size() - 1) {
                    //选中最新和最早时间时直接显示文字，不需要拼接月份
                    //tvTime.setText(optionYears.get(options1));
                    year = LocalDate.now().getYear();
                    tvTime.setText(new StringBuilder(""+year));
                    for(int d = 0; d<11;d++)
                    {
                        int total = 0;
                        for(int i = 1; i<=12; i++)
                        {
                            total += userData.getFloat(x[d]+year+i,0.0f);
                        }
                        yP[d] = total;
                    }
                    draw(x.length);
                } else {
                    //常规的时间，需要拼接年份和月份
                    tvTime.setText(new StringBuffer(optionYears.get(options1)));
                    year = Integer.parseInt(optionYears.get(options1));
                    for(int d = 0; d<11;d++)
                    {
                        int total = 0;
                        for(int i = 1; i<=12; i++)
                        {
                            total += userData.getFloat(x[d]+year+i,0.0f);
                        }
                        yP[d] = total;
                    }
                    draw(x.length);
                }
            }
        }).setTitleText("Please select year")
                .build();
        multipleOp.setPicker(optionYears);
        multipleOp.show();

       /* OptionsPickerView op = createBuilder().build();
        op.setPicker(数据1,数据2);
        op.show();*/
    }

    /**
     * 滚轮的监听事件
     *
     * @param options1
     * @param options2
     * @param options3
     * @param v
     */
    //@Override
    public void onOptionsSelect(int options1, int options2, int options3, View v) {
        /*switch (v.getId()) {
            //根据所点击的控件Id来区分点击事件
            case R.id.btn_show:
                break;
            default:
                break;
        }*/
    }
}