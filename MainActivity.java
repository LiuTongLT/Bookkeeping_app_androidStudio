package be.kuleuven.softdev.liutong.javabean;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import be.kuleuven.softdev.liutong.javabean.R;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    private TextView hereCreat;
    private Button submit;
    boolean correct = false;
    String user_name;
    String passWord;
    String user_birth;
    private String username;
    int expenseID = 0;
    SharedPreferences userData;

    private String[] x = new String[] {"food","education","medical","house","shopping","hotel",
            "car","mobileInternet","sports","travel","other"};
    private float[] y = new float[10];

    /*
    //检测用户是否已经登录，如果已经登录，直接跳转到用户主界面，否则什么也不做
    Boolean isLogged = MyTools.isLogged(LoginActivity.this);
if(isLogged){
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();//关闭当前登录界面，否则在主界面按后退键还会回到登录界面
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userData = getSharedPreferences("userData", 0);
        username = userData.getString("username","null");
        if(userData.getString("username", "null").equals("null"))
        {
            hereCreat = findViewById(R.id.here);
            hereCreat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createUserAccount();
                }
            });

            submit = findViewById(R.id.submitBut);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDashboard();

                }
            });
        }
        else
        {
            Intent intent = new Intent(getApplicationContext(),dashboard.class);
            startActivity(intent);
        }

    }

    public void createUserAccount(){
        Intent intent = new Intent(getApplicationContext(),createAccount.class);
        startActivity(intent);

    }

    public void openDashboard() {
        final TextView email = findViewById(R.id.emailEdit);
        final TextView password = findViewById(R.id.passwordEdit);
        final String eMail = email.getText().toString();
        final String Pass = password.getText().toString();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="http://api.a17-sd604.studev.groept.be/getPerson";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONArray jarr = new JSONArray(response);
                            for(int i=0; i<jarr.length(); i++)
                            {
                                JSONObject jobj = jarr.getJSONObject(i);
                                String Email = jobj.getString("email");
                                String pass = jobj.getString("password");
                                String name = jobj.getString("username");
                                String birth =  jobj.getString("birth");
                                //expenseID = jobj.getInt("expenseID");
                                if(eMail.equals(Email) && Pass.equals(pass))
                                {
                                    correct = true;
                                    user_name = name;
                                    user_birth = birth;
                                    passWord=pass;
                                }
                            }
                            if(correct)
                            {
                                Intent intent = new Intent(getApplicationContext(),dashboard.class);
                                startActivity(intent);

                                SharedPreferences.Editor editor = userData.edit();
                                editor.putString("username", user_name);
                                editor.putString("password",passWord);
                                editor.putString("email", eMail);
                                editor.putString("birth",user_birth);
                                editor.commit();
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this, "Please check your email and password.",Toast.LENGTH_SHORT).show();
                            }


                        }
                        catch(JSONException e)
                        {
                            System.out.println(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error...",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        queue.add(stringRequest);
        correct =false;

        //readExpenseID();
    }

    private void readExpenseID()
    {
        RequestQueue queue2 = Volley.newRequestQueue(getApplicationContext());
        String ur2 ="http://api.a17-sd604.studev.groept.be/getExpenseID";

// Request a string response from the provided URL.
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, ur2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONArray jarr = new JSONArray(response);
                            JSONObject jobj = jarr.getJSONObject(0);
                            expenseID = jobj.getInt("max");
                            userData = getSharedPreferences("userData", 0);
                            SharedPreferences.Editor editor = userData.edit();
                            editor.putInt("expenseID", expenseID);
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
                Toast.makeText(MainActivity.this, "Error...",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        queue2.add(stringRequest2);
    }

}
