package be.kuleuven.softdev.liutong.javabean;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import java.lang.*;

public class createAccount extends AppCompatActivity {
    private Button create;
    EditText name;
    EditText userEmail;
    EditText passwordInput;
    ImageButton femaleuser, maleuser;
    EditText userBirthday;
    String username, email, password, gender;
    LocalDate birth;
    private int id;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        create = findViewById(R.id.creatAccount);
        //getID();
        getGender();
        create.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.O)
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                saveInformation();

            }
        });
    }

    private void getGender(){
        femaleuser = findViewById(R.id.female);
        maleuser = findViewById(R.id.male);
        femaleuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "female";
                femaleuser.setBackgroundColor(Color.GRAY);
            }
        });
        maleuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "male";
                maleuser.setBackgroundColor(Color.GRAY);
            }
        });
    }

    private void openMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

//    private void getID(){
//        //get id number
//        RequestQueue queue1 = Volley.newRequestQueue(getApplicationContext());
//        String url ="http://api.a17-sd604.studev.groept.be/getMaxID";
//
//// Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try
//                        {
//                            JSONArray jarr = new JSONArray(response);
//                            JSONObject jobj = jarr.getJSONObject(0);
//                            id=jobj.getInt("maxID")+1;
//
//                        }
//                        catch(JSONException e)
//                        {
//                            System.out.println(e);
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(createAccount.this, "Error...",Toast.LENGTH_SHORT).show();
//                error.printStackTrace();
//            }
//        });
//        queue1.add(stringRequest);
//    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveInformation() {

        builder = new AlertDialog.Builder(createAccount.this);
        name = findViewById(R.id.usernameEdit);
        userEmail = findViewById(R.id.emailEdit);
        passwordInput = findViewById(R.id.passwordEdit);
        userBirthday=findViewById(R.id.birthEdit);
        birth=birth.parse(userBirthday.getText().toString());
        username = name.getText().toString();
        email = userEmail.getText().toString();
        password = passwordInput.getText().toString();



        String serve_URL = "http://api.a17-sd604.studev.groept.be/registerPerson/"+username+"/"+email+"/"+gender+"/"+birth+"/"+password+"/"+0;
        //String serve_URL_2 = "http://api.a17-sd604.studev.groept.be/creatNew";
        // create new table, not done yet!!

        RequestQueue queue2 = Volley.newRequestQueue(this);
        //queue2.start();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, serve_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        builder.setTitle("Dear, ");
                        builder.setMessage("Create an account successfully~ Please remember your email and password. They are important for the later login.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                name.setText("");
                                userEmail.setText("");
                                passwordInput.setText("");
                               openMain();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        System.out.println(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(createAccount.this, "Error...",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        queue2.add(stringRequest);
    }
}