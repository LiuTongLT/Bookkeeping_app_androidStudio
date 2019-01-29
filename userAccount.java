package be.kuleuven.softdev.liutong.javabean;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.time.LocalDate;

public class userAccount extends AppCompatActivity {
    TextView userName_this;
    TextView email_this;
    TextView birth_this;
    TextView changePass_this;
    ImageButton logout;
    private ImageButton dashboard;
    private ImageButton report;
    String username;
    String email;
    String birth;
    int expenseID;
    SharedPreferences userData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        userName_this = (TextView) findViewById(R.id.usernameDisplay);
        email_this = (TextView) findViewById(R.id.emailDisplay);
        birth_this = findViewById(R.id.textView3);
        username = userName_this.getText().toString();
        email = email_this.getText().toString();

        changePass_this = findViewById(R.id.changeMyPass);
        changePass_this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePass();
            }
        });

        userData = getSharedPreferences("userData", 0);
        username = userData.getString("username", "null");
        email = userData.getString("email", "null");
        expenseID = userData.getInt("expenseID", 0);
        birth=userData.getString("birth","null");

        userName_this.setText(username);
        email_this.setText(email);
        birth_this.setText(birth);

        logout = (ImageButton) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExpenseID(email, expenseID);
                SharedPreferences.Editor editor = userData.edit();
                editor.clear(); //clear all stored data
                editor.commit();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        dashboard = findViewById(R.id.menuBut);
        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDashboard();
            }
        });

        report=findViewById(R.id.reportBut);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReport();
            }
        });
    }

    private void openDashboard(){
        Intent intent = new Intent(this,dashboard.class);
        startActivity(intent);
    }

    private void changePass(){
        Intent intent = new Intent(this,changePassword.class);
        startActivity(intent);
    }

    private void openReport(){
        Intent intent = new Intent(this,reportNew.class);
        startActivity(intent);
    }

    private void setExpenseID(String email, int expenseID)
    {
        RequestQueue mQueue2 = Volley.newRequestQueue(this);
        String ur2 = "http://api.a17-sd604.studev.groept.be/setExpenseID/" + email + "/" + expenseID;
        mQueue2.start();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (ur2, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        mQueue2.add(jsObjRequest);
    }
}
