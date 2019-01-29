package be.kuleuven.softdev.liutong.javabean;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class changePassword extends AppCompatActivity {

    SharedPreferences userData;
    String password;
    EditText oldPass;
    EditText newPass;
    EditText newPassAgain;
    Button submit;

    String passOld;
    String passNew;
    String passNewAgain;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        //readInfo();
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePass();
                openUserAccount();
            }
        });
    }

    private void openUserAccount(){
        Intent intent = new Intent(this,userAccount.class);
        startActivity(intent);
    }

    private void readInfo(){
        oldPass = findViewById(R.id.oldPass);
        passOld = oldPass.getText().toString();
        newPass = findViewById(R.id.passwordNew);
        passNew = newPass.getText().toString();
        newPassAgain = findViewById(R.id.passwordNewAgain);
        passNewAgain = newPassAgain.getText().toString();
    }

    public void changePass(){
        oldPass = findViewById(R.id.oldPass);
        passOld = oldPass.getText().toString();
        newPass = findViewById(R.id.passwordNew);
        passNew = newPass.getText().toString();
        newPassAgain = findViewById(R.id.passwordNewAgain);
        passNewAgain = newPassAgain.getText().toString();

        userData = getSharedPreferences("userData", 0);
        password=userData.getString("password",null);
        username = userData.getString("username",null);

        String serve_URL = "http://api.a17-sd604.studev.groept.be/changePass/"+passNew+"/"+username+"/"+0;

        RequestQueue queue2 = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, serve_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(passOld.equals(password) && passNew.equals(passNewAgain) )
                        {

                            SharedPreferences.Editor editor = userData.edit();
                            editor.putString("password",passNew);
                            editor.commit();
                            Toast.makeText(changePassword.this, "Password is changed successfully~",Toast.LENGTH_SHORT).show();



                        }
                        else if(passOld.equals(password) && !passNew.equals(passNewAgain)){
                            Toast.makeText(changePassword.this, "Check your new password",Toast.LENGTH_SHORT).show();
                        }
                        else if(!passOld.equals(password) && passNew.equals(passNewAgain) ){
                            Toast.makeText(changePassword.this, "Check your old password",Toast.LENGTH_SHORT).show();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(changePassword.this, "Error...",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        queue2.add(stringRequest);
    }
}
