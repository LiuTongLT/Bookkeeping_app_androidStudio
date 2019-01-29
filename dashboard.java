package be.kuleuven.softdev.liutong.javabean;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bigkoo.pickerview.OptionsPickerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class dashboard extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ImageButton pen;
    private ImageButton myAccount;
    private ImageButton report;

    private ListView listView;
    List<String> data = new ArrayList<String>();
    private List<Map<String, Object>> datas=new ArrayList<Map<String, Object>>();
    ArrayAdapter<String> adapter;
    private SimpleAdapter simpleAdapter;

    SharedPreferences userData;

    private String username;
    private String[] x = new String[]{"food", "education", "medical", "house", "shopping", "hotel",
            "car", "mobileInternet", "sports", "travel", "other"};

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        userData = getSharedPreferences("userData", 0);
        username = userData.getString("username", "null");

        for (int i = 0; i < 10; i++) {
            SharedPreferences.Editor editor = userData.edit();
            editor.putFloat(x[i], 0.0f);
            editor.commit();
        }

        pen = findViewById(R.id.penBut);
        pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity2();
            }
        });

        myAccount = findViewById(R.id.myAccountBut);
        myAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityUserAccount();
            }
        });

        report = findViewById(R.id.reportBut);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReport2();
            }
        });

        listView = (ListView) findViewById(R.id.lv);

        //list = info.keySet().stream().collect(Collectors.toList());
        //list.addAll(info.keySet());
        /*if (info.values() instanceof List)
            list = (List)info.keySet();
        else
            list = new ArrayList<String>(info.keySet());*/
        simpleAdapter=new SimpleAdapter(this,datas,R.layout.list_layout,new String[]{"info","id"},new int[]{R.id.info,R.id.id});
        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
        //        data);//(List)info.values()
        listView.setAdapter(simpleAdapter);//设置适配器
        listView.setOnItemClickListener(this);

        getHistoryExpenseTotal();

    }

    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        final String[] Items = {"Edit", "Delete"};
        System.out.println("item = "+parent.getItemAtPosition(position));
        final Map map =  (Map)parent.getItemAtPosition(position);
        final Integer expenseID = (Integer) map.get("id");
        System.out.println("expense id "+expenseID);
        readExpense(expenseID);

        new AlertDialog.Builder(this)
                .setIcon(R.drawable.heart)
                .setTitle("Options")
                //.setMessage("" + parent.getItemAtPosition(position))
                .setItems(Items, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0)
                        {
                            editExpense(expenseID);
                        }

                        else
                        {
                            deleteExpense(expenseID);
                            datas.remove(map);
                            listView.setAdapter(simpleAdapter);
                            }
                    }
                })
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                })
                .setCancelable(true)
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void deleteExpense(int id)
    {
        RequestQueue mQueue2 = Volley.newRequestQueue(this);
        String ur2 = "http://api.a17-sd604.studev.groept.be/deleteExpense/"+id;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ur2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jarr = new JSONArray(response);
                            Toast.makeText(getApplicationContext(), "Deleted successfully", Toast.LENGTH_SHORT).show();
                            getHistoryExpenseTotal();

                        } catch (JSONException e) {
                            System.out.println(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(dashboard.this, "Error...", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        mQueue2.add(stringRequest);
    }

    public void editExpense(int id)
    {
        SharedPreferences.Editor editor = userData.edit();
        editor.putInt("idToModify",id);
        editor.putBoolean("isToModify",true);
        editor.commit();
        Intent intent = new Intent(this, activity2.class);
        startActivity(intent);
    }

    public void readExpense(int id)
    {
        RequestQueue queue1 = Volley.newRequestQueue(getApplicationContext());
        String url ="http://api.a17-sd604.studev.groept.be/getModifiedExpense/"+id;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONArray jarr = new JSONArray(response);
                            JSONObject jobj = jarr.getJSONObject(0);
                            String cate = jobj.getString("category");
                            String date = jobj.getString("date");
                            float amount = (float)jobj.getDouble("amount");
                            SharedPreferences.Editor editor = userData.edit();
                            editor.putString("cateToModify",cate);
                            editor.putString("dateToModify",date);
                            editor.putFloat("amountToModify",amount);
                            editor.commit();
                        }
                        catch(JSONException e)
                        {
                            System.out.println(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(dashboard.this, "Error...",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        queue1.add(stringRequest);
    }

    private void openReport2() {
        Intent intent = new Intent(this, reportNew.class);
        startActivity(intent);
    }

    private void openActivity2() {
        Intent intent = new Intent(this, activity2.class);
        startActivity(intent);
    }

    private void openActivityUserAccount() {
        Intent intent = new Intent(this, userAccount.class);
        startActivity(intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getHistoryExpenseTotal() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://api.a17-sd604.studev.groept.be/getExpenseTotal/" + username + "/" + username;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        //String res = null;
                        try {
                            JSONArray jarr = new JSONArray(response);
                            System.out.println(jarr.length());
                            boolean y[] = new boolean[11];
                            for(int i=0; i<11; i++)
                            {
                                y[i] = false;
                            }
                            for (int i = 0; i < jarr.length(); i++) {
                                JSONObject jobj = jarr.getJSONObject(i);
                                //String name = jobj.getString("username");

                                String category = jobj.getString("category");
                                float prop = (float) jobj.getDouble("prop");
                                for(int d=0; d<11; d++)
                                {
                                    if(x[d].equals(category))
                                        y[d] = true;
                                }
                                SharedPreferences.Editor editor = userData.edit();
                                editor.putFloat(category, prop);
                                //also: min&max year, month!!!
                                //editor.putInt("year",year);
                                //editor.putInt("month", month);
                                editor.commit();
                            }
                            for(int d=0; d<11; d++)
                            {
                                System.out.println("y"+d+" is "+y[d]);
                                if(y[d]==false) //no expense for this category, must put 0
                                {
                                    SharedPreferences.Editor editor = userData.edit();
                                    editor.putFloat(x[d], 0);
                                    editor.commit();
                                    System.out.println("no such "+x[d]);
                                }
                            }
                        } catch (JSONException e) {
                            System.out.println(e);
                        }
                        //result.setText("before: "+res);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(dashboard.this, "Error...", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        String ur2 = "http://api.a17-sd604.studev.groept.be/getExpenseYearly/" + username;
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, ur2,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        //String res = null;
                        try {
                            JSONArray jarr = new JSONArray(response);
                            System.out.println(jarr.length());
                            boolean y[] = new boolean[11];
                            for(int i=0; i<11; i++)
                            {
                                y[i] = true;
                            }
                            for (int i = 0; i < jarr.length(); i++) {
                                JSONObject jobj = jarr.getJSONObject(i);
                                //String name = jobj.getString("username");

                                String category = jobj.getString("category");
                                float number = (float) jobj.getDouble("cateAmount");
                                int year = jobj.getInt("year");
                                for(int d=0; d<11; d++)
                                {
                                    if(x[d].equals(category))
                                        y[d] = false;
                                }
                                SharedPreferences.Editor editor = userData.edit();
                                editor.putFloat(category + year, number);
                                for(int d=0; d<11; d++)
                                {
                                    if(y[d]) //no expense for this category, must put 0
                                        editor.putFloat(x[d]+year, 0);
                                }
                                editor.commit();

                            }
                        } catch (JSONException e) {
                            System.out.println(e);
                        }
                        //result.setText("before: "+res);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(dashboard.this, "Error...", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
// Request a string response from the provided URL.

        String ur3 = "http://api.a17-sd604.studev.groept.be/getExpenseMonthly/" + username;
        StringRequest stringRequest3 = new StringRequest(Request.Method.GET, ur3,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        //String res = null;
                        try {
                            JSONArray jarr = new JSONArray(response);
                            System.out.println(jarr.length());
                            boolean y[] = new boolean[11];
                            for(int i=0; i<11; i++)
                            {
                                y[i] = true;
                            }
                            for (int i = 0; i < jarr.length(); i++) {
                                JSONObject jobj = jarr.getJSONObject(i);
                                String category = jobj.getString("category");
                                float number = (float) jobj.getDouble("cateAmount");
                                int year = jobj.getInt("year");
                                int month = jobj.getInt("month");
                                for(int d=0; d<11; d++)
                                {
                                    if(x[d].equals(category))
                                        y[d] = false;
                                }
                                SharedPreferences.Editor editor = userData.edit();
                                editor.putFloat(category + year + month, number);
                                for(int d=0; d<11; d++)
                                {
                                    if(y[d]) //no expense for this category, must put 0
                                        editor.putFloat(x[d]+year+month, 0);
                                }
                                editor.commit();

                            }
                        } catch (JSONException e) {
                            System.out.println(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(dashboard.this, "Error...", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        String ur4 = "http://api.a17-sd604.studev.groept.be/getEachExpense/" + username;
        StringRequest stringRequest4 = new StringRequest(Request.Method.GET, ur4,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        //String res = null;
                        try {
                            JSONArray jarr = new JSONArray(response);
                            System.out.println(jarr.length());
                            for (int i = 0; i < jarr.length(); i++) {
                                JSONObject jobj = jarr.getJSONObject(i);
                                String category = jobj.getString("category");
                                float number = (float) jobj.getDouble("amount");
                                String date = (String) jobj.getString("date");
                                int id = jobj.getInt("id");
                                //LocalDate day = LocalDate.parse(date);

//                                SharedPreferences.Editor editor = userData.edit();
//                                editor.putFloat(date+category+id,number);
//
//                                editor.commit();
                                Map map=new HashMap();
                                data.add(date + " " + category + " " + number);//+" NO."+id
                                map.put("info",date + " " + category + " " + number);
                                map.put("id", id);
                                datas.add(map);
                            }
                            listView.setAdapter(simpleAdapter);
                        } catch (JSONException e) {
                            System.out.println(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(dashboard.this, "Error...", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        queue.add(stringRequest);
        queue.add(stringRequest2);
        queue.add(stringRequest3);
        queue.add(stringRequest4);

    }

}
