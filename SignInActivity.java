package vyshak.sdsu.edu.sdsuclassregistration;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import vyshak.sdsu.edu.sdsuclassregistration.sync.VolleyQueue;

public class SignInActivity extends AppCompatActivity {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText passwordEditText;
    private EditText redIdEditText;
    private EditText emailEditText;

    private String TAG="SignInActivity";
    private static String PREFS_NAME="mypre";
    private static String PREF_REDID="redId";
    private static String PREF_PASSWORD="password";

    private Switch signUpSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        redIdEditText = findViewById(R.id.redIdEditText);
        emailEditText = findViewById(R.id.emailEditText);

        signUpSwitch = findViewById(R.id.signUpSwitch);

        final TextView firstNameTextView = findViewById(R.id.firstNameTextView);
        final TextView lastNameTextView = findViewById(R.id.lastNameTextView);
        final TextView emailTextView = findViewById(R.id.emailTextView);

        SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        String username = pref.getString(PREF_REDID, null);
        String password = pref.getString(PREF_PASSWORD, null);
        if( username!= null && password != null) {
            redIdEditText.setText(username);
            passwordEditText.setText(password);
        }
        firstNameEditText.setVisibility(View.INVISIBLE);
        firstNameTextView.setVisibility(View.INVISIBLE);
        lastNameTextView.setVisibility(View.INVISIBLE);
        lastNameEditText.setVisibility(View.INVISIBLE);
        emailTextView.setVisibility(View.INVISIBLE);
        emailEditText.setVisibility(View.INVISIBLE);

        signUpSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (signUpSwitch.isChecked()) {
                    Log.d(TAG,"check detected");
                    firstNameEditText.setVisibility(View.VISIBLE);
                    firstNameTextView.setVisibility(View.VISIBLE);
                    lastNameTextView.setVisibility(View.VISIBLE);
                    lastNameEditText.setVisibility(View.VISIBLE);
                    emailTextView.setVisibility(View.VISIBLE);
                    emailEditText.setVisibility(View.VISIBLE);
                }else{
                    firstNameEditText.setVisibility(View.INVISIBLE);
                    firstNameTextView.setVisibility(View.INVISIBLE);
                    lastNameTextView.setVisibility(View.INVISIBLE);
                    lastNameEditText.setVisibility(View.INVISIBLE);
                    emailTextView.setVisibility(View.INVISIBLE);
                    emailEditText.setVisibility(View.INVISIBLE);
                }
            }
        });



        final Button logInButton = findViewById(R.id.logInButton);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(signUpSwitch.isChecked()) {
                    signIn();
                }else{
                    checkUser();
                }
            }
        });

    }


    private void signIn() {
        Log.d(TAG,"Sign in");
        if (validateForm()) {
            Log.d(TAG,"form authentic");
            JSONObject data = new JSONObject();
            try {
                data.put("firstname", firstNameEditText.getText());
                data.put("password", passwordEditText.getText());
                data.put("redid", redIdEditText.getText());
                data.put("lastname", lastNameEditText.getText());
                data.put("email", emailEditText.getText());
            } catch (JSONException error) {
                Log.e(TAG, "JSON error", error);
                return;
            }
            Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(response.has("error")){
                        try {
                            Toast.makeText(SignInActivity.this, response.getString("error") + R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Log.d(TAG, "Got some response" + response.toString());
                        navigateToRegister();
                    }
                }
            };
            Response.ErrorListener failure = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Volley error", error);
                    Toast.makeText(SignInActivity.this, String.valueOf(error) + R.string.auth_failed,
                            Toast.LENGTH_SHORT).show();
                    //navigateToRegister();
                }
            };
            String url = "https://bismarck.sdsu.edu/registration/addstudent";
            JsonObjectRequest postRequest = new JsonObjectRequest(url, data, success, failure);
            VolleyQueue.instance(this).add(postRequest);
        }
    }

    private void checkUser(){
        String urlCheckUser="https://bismarck.sdsu.edu/registration/studentclasses?redid="+redIdEditText.getText().toString() + "&password=" +passwordEditText.getText().toString();
        Log.d(TAG,"url"+urlCheckUser);
        Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(response.has("error")){
                    try {
                        Toast.makeText(SignInActivity.this, response.getString("error") + R.string.auth_failed,
                                Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Log.d(TAG, "Got some response by checking"+response.toString());
                    navigateToRegister();
                }
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Volley error", error);
                Toast.makeText(SignInActivity.this, String.valueOf(error) + R.string.auth_failed,
                        Toast.LENGTH_SHORT).show();
                //navigateToRegister();
            }
        };
        JsonObjectRequest postRequest = new JsonObjectRequest(urlCheckUser, null,success, failure);
        VolleyQueue.instance(this).add(postRequest);
    }


    private boolean validateForm(){
        int passwordLength = passwordEditText.getText().length();
        if(firstNameEditText.getText().toString().isEmpty()){
            firstNameEditText.setError("Required");
            return false;
        }
        if(lastNameEditText.getText().toString().isEmpty()){
            lastNameEditText.setError("Required");
            return false;
        }
        if(passwordEditText.getText().toString().isEmpty()){
            passwordEditText.setError("Required");
            return false;
        }
        if(redIdEditText.getText().toString().isEmpty()){
            redIdEditText.setError("Required");
            return false;
        }
        getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
                .edit()
                .putString(PREF_REDID,redIdEditText.getText().toString())
                .putString(PREF_PASSWORD,passwordEditText.getText().toString())
                .apply();
        if(emailEditText.getText().toString().isEmpty()){
            emailEditText.setError("Required");
            return false;
        }
        if(passwordLength < 8){
            passwordEditText.setError("Minimum 8 characters");
            return false;
        }
        if(!emailEditText.getText().toString().contains("@")){
            emailEditText.setError("Invalid email");
            return false;
        }
        //TODO: check if password contains unique characters
        return true;
    }

    private void navigateToRegister(){
        Intent go = new Intent(this,RegisterActivity.class);
        startActivity(go);
    }


}
