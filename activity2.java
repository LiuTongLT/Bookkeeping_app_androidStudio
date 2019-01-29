package be.kuleuven.softdev.liutong.javabean;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class activity2 extends AppCompatActivity {
    //private float amounts[]; ////"food","education","medical","house","shopping",
    // "hotel","car","mobile&internet","sports","travel"
    private int expenseID;
    private String cat; // indicate which category the amount belongs to and save to database
    EditText amount;
    TextView category;
    ImageButton ok;
    String samount;
    private String username;
    float amo;
    SharedPreferences userData;
    private int id;
    private LocalDate date;

    private String[] x = new String[]{"food", "education", "medical", "house", "shopping", "hotel",
            "car", "mobileInternet", "sports", "travel", "other"};
    private float[] y = new float[11];

    EditText dateSet;
    DatePickerDialog datePickerDialog;

    private List<String> monthList = new ArrayList<>();
    private List<String> optionYears = new ArrayList<>();
    private List<List<String>> optionMonths = new ArrayList<>();
    private List<List<List<String>>> optionDays = new ArrayList<>();

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity2);
        userData = getSharedPreferences("userData", 0);
        if(userData.getBoolean("isToModify",false))
            expenseID = userData.getInt("idToModify",0);
        else
            getID();
        username = userData.getString("username", "null");
//        SharedPreferences.Editor editor = userData.edit();
//        editor.putInt("minYear", 1997);
//        editor.commit();
        ok = (ImageButton) findViewById(R.id.okBut);
        amount = (EditText) findViewById(R.id.numText);
        date = LocalDate.now();

        dateSet = (EditText) findViewById(R.id.dateSet);
        if (userData.getBoolean("isToModify", false))
        {
            LocalDate dateToModify = LocalDate.parse(userData.getString("dateToModify",null));
            dateSet.setText(dateToModify.getDayOfMonth() + "/"
                    + dateToModify.getMonthValue() + "/" + dateToModify.getYear());
        }
        else
            dateSet.setText(date.getDayOfMonth() + "/"
                    + date.getMonthValue() + "/" + date.getYear());
        dateSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(activity2.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int yearValue,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                dateSet.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + yearValue);
                                /*year = yearValue;
                                month = monthOfYear;
                                day = dayOfMonth;*/
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                if (monthOfYear < 9) {
                                    if (dayOfMonth < 9)
                                        date = LocalDate.parse(yearValue + "-"
                                                + 0 + (monthOfYear + 1) + "-" + 0 + dayOfMonth, formatter);
                                    else
                                        date = LocalDate.parse(yearValue + "-"
                                                + 0 + (monthOfYear + 1) + "-" + dayOfMonth, formatter);
                                } else {
                                    if (dayOfMonth < 9)
                                        date = LocalDate.parse(yearValue + "-"
                                                + (monthOfYear + 1) + "-" + 0 + dayOfMonth, formatter);
                                    else
                                        date = LocalDate.parse(yearValue + "-"
                                                + (monthOfYear + 1) + "-" + dayOfMonth, formatter);
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        chooseCate();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                samount = amount.getText().toString();  //read number after clicking the ok button
                amo = Float.parseFloat(samount);
                if(!userData.getBoolean("isToModify",false))
                {
                    expenseID++;
                    addExpense(username, cat, amo);
                }
                else
                    updateExpense(username,cat,amo);
                SharedPreferences.Editor editor = userData.edit();
                //if(!userData.getBoolean("isToModify",false))
                //    editor.putInt("expenseID", expenseID);
                editor.putBoolean("isToModify", false);
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), dashboard.class);
                startActivity(intent);
            }
        });
    }

    private void chooseCate() {
        category = (TextView) findViewById(R.id.categoryText);
        if (userData.getBoolean("isToModify", false)) {
            category.setText(userData.getString("cateToModify", null));
            amount.setText("" + userData.getFloat("amountToModify", 0));
            cat = userData.getString("cateToModify", null);
            amo = userData.getFloat("amountToModify", 0);
        }

        ImageButton food = (ImageButton) findViewById(R.id.foodBut);
        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category.setText("Food");
                cat = "food";
            }
        });
        ImageButton education = findViewById(R.id.educationBut);
        education.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category.setText("Education");
                cat = "education";
            }
        });
        ImageButton medical = findViewById(R.id.medicalBut);
        medical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category.setText("Medical");
                cat = "medical";
            }
        });
        ImageButton house = findViewById(R.id.houseBut);
        house.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category.setText("House");
                cat = "house";
            }
        });
        ImageButton shopping = findViewById(R.id.shoppingBut);
        shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category.setText("Shopping");
                cat = "shopping";
            }
        });
        ImageButton hotel = findViewById(R.id.hotelBut);
        hotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category.setText("Hotel");
                cat = "hotel";
            }
        });
        ImageButton car = findViewById(R.id.carBut);
        car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category.setText("Car");
                cat = "car";
            }
        });
        ImageButton mobileInternet = findViewById(R.id.mobileInternetBut);
        mobileInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category.setText("Mobile&Internet");
                cat = "mobileInternet";
            }
        });
        ImageButton sports = findViewById(R.id.sportsBut);
        sports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category.setText("Sports");
                cat = "sports";
            }
        });
        ImageButton travel = findViewById(R.id.travelBut);
        travel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category.setText("Travel");
                cat = "travel";
            }
        });

        ImageButton other = findViewById(R.id.othersBut);
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category.setText("other");
                cat = "other";
            }
        });
    }

    private void getID() {
        //get id number
        RequestQueue queue1 = Volley.newRequestQueue(getApplicationContext());
        String url = "http://api.a17-sd604.studev.groept.be/getExpenseID";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jarr = new JSONArray(response);
                            JSONObject jobj = jarr.getJSONObject(0);
                            expenseID = jobj.getInt("max") + 1;

                        } catch (JSONException e) {
                            System.out.println(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity2.this, "Error...", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        queue1.add(stringRequest);
    }

    private void addExpense(String username, final String cate, final float amo) {
        RequestQueue mQueue2 = Volley.newRequestQueue(this);
        String ur2 = "http://api.a17-sd604.studev.groept.be/addExpense/" + username + "/" + date + "/" + cate + "/" + amo + "/" + expenseID;
        mQueue2.start();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (ur2, null, new Response.Listener<JSONObject>() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        //no response here
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        mQueue2.add(jsObjRequest);
    }

    private void updateExpense(String username, final String cate, final float amo) {
        RequestQueue mQueue2 = Volley.newRequestQueue(this);
        String ur2 = "http://api.a17-sd604.studev.groept.be/updateExpense/" + amo + "/" + date + "/" + cate  + "/" + expenseID;
        mQueue2.start();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (ur2, null, new Response.Listener<JSONObject>() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        /*float am = userData.getFloat(cate,0.0f);
                        am+=amo;
                        SharedPreferences.Editor editor = userData.edit();
                        editor.putFloat(cate, am);
                        editor.commit();*/
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        mQueue2.add(jsObjRequest);
    }

    public void refreshExpense() {
        System.out.println("startget");
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://api.a17-sd604.studev.groept.be/getAllExpense";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //String res = null;
                        try {
                            JSONArray jarr = new JSONArray(response);
                            for (int i = 0; i < jarr.length(); i++) {
                                JSONObject jobj = jarr.getJSONObject(i);
                                String name = jobj.getString("username");
                                if (name.equals(username)) {
                                    String category = jobj.getString("category");
                                    float number = (float) jobj.getDouble("amount");
                                    float am = userData.getFloat(category, 0.0f);
                                    am += number;
                                    SharedPreferences.Editor editor = userData.edit();
                                    editor.putFloat(category, am);
                                    editor.commit();
                                }
                            }
                            for (int j = 0; j < 10; j++) {
                                System.out.println(x[j] + y[j]);
                            }
                        } catch (JSONException e) {
                            System.out.println(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity2.this, "Error...", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);

    }


}