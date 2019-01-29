package be.kuleuven.softdev.liutong.javabean;

import android.content.SharedPreferences;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.PieChart;

public class reportNew extends AppCompatActivity {

    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;


    private PieChart chart;
    private String[] x = new String[] {"food","education","medical","house","shopping","hotel",
            "car","mobileInternet","sports","travel","other"};
    private float[] y = new float[10];
    private float[] yP = new float[10];
    private int m =0;

    private String username;
    SharedPreferences userData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_new);

        tabLayout=(TabLayout) findViewById(R.id.tabLayoutId);
        appBarLayout=(AppBarLayout)findViewById(R.id.appbarid);
        viewPager=(ViewPager)findViewById(R.id.viewPagerId);
        viewPagerAdapter viewPagerAdapter = new viewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new FragmentMay(),"month");
        viewPagerAdapter.addFragment(new FragmentYear(),"year");
        viewPagerAdapter.addFragment(new FragmentTotal(),"total");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

}
