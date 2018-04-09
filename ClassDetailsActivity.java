package vyshak.sdsu.edu.sdsuclassregistration;

import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import vyshak.sdsu.edu.sdsuclassregistration.sync.VolleyQueue;

public class ClassDetailsActivity extends AppCompatActivity {

    private String TAG = "ClassDetailsActivity";
    private String url;
    private int seatsAvailable;
    private int courseId;
    private Button addCourseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.class_details_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Bundle urlData = getIntent().getExtras();
        url = urlData.getString("url");

        Log.d(TAG, "url as recieved " + url);
        addCourseButton = findViewById(R.id.buttonAddStudentCourse);
        addCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addStudentToClass();
            }
        });
        getCourseInfo();

    }

    private void getCourseInfo() {
        Log.d("rew", "Start");
        Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                //for (int i = 0; i < response.length(); i++) {
                    try {

                        JSONObject responseObject = (JSONObject) response;
                        if (responseObject.has("description")) {
                            TextView descriptionTextView = findViewById(R.id.descriptionTextView);
                            descriptionTextView.setText(responseObject.getString("description"));

                        }

                        if (responseObject.has("description")) {
                            TextView departmentTextView = findViewById(R.id.departmentTextView);
                            departmentTextView.setText(responseObject.getString("department"));
                            Log.d(TAG, "dept " + responseObject.getString("description"));

                        }

                        if (responseObject.has("suffix")) {
                            TextView suffixTextView = findViewById(R.id.suffixTextView);
                            suffixTextView.setText(responseObject.getString("suffix"));
                        }

                        if (responseObject.has("building")) {
                            TextView buildingTextView = findViewById(R.id.buildingTextView);
                            buildingTextView.setText(responseObject.getString("building"));
                        }
                        if (responseObject.has("startTime")) {
                            TextView startTimeTextView = findViewById(R.id.startTimeTextView);
                            startTimeTextView.setText(responseObject.getString("startTime"));
                        }
                        if (responseObject.has("meetingType")) {
                            TextView meetingTypeTextView = findViewById(R.id.meetingTypeTextView);
                            meetingTypeTextView.setText(responseObject.getString("meetingType"));
                        }
                        if (responseObject.has("section")) {
                            TextView sectionTextView = findViewById(R.id.sectionTextView);
                            sectionTextView.setText(responseObject.getString("section"));
                            Log.d(TAG, "sect " + responseObject.getString("section"));
                        }
                        if (responseObject.has("endTime")) {
                            TextView endTimeTextView = findViewById(R.id.endTimeTextView);

                            endTimeTextView.setText(responseObject.getString("endTime"));
                        }
                        if (responseObject.has("enrolled")) {
                            TextView enrolledTextView = findViewById(R.id.enrolledTextView);
                            enrolledTextView.setText(String.valueOf(responseObject.getInt("enrolled")));
                        }
                        if (responseObject.has("days")) {
                            TextView daysTextView = findViewById(R.id.daysTextView);
                            daysTextView.setText(responseObject.getString("days"));
                        }
                        if (responseObject.has("prerequisite")) {
                            TextView prerequisiteTextView = findViewById(R.id.prerequisiteTextView);
                            prerequisiteTextView.setText(responseObject.getString("prerequisite"));
                        }
                        if (responseObject.has("title")) {
                            TextView titleTextView = findViewById(R.id.titleTextView);
                            titleTextView.setText(responseObject.getString("title"));
                        }
                        if (responseObject.has("id")) {
                            TextView idTextView = findViewById(R.id.idTextView);
                            idTextView.setText(String.valueOf(responseObject.getInt("id")));
                            courseId = responseObject.getInt("id");
                        }
                        if (responseObject.has("instructor")) {
                            TextView instructorTextView = findViewById(R.id.instructorTextView);
                            instructorTextView.setText(responseObject.getString("instructor"));
                        }
                        if (responseObject.has("schedule#")) {
                            TextView scheduleNumTextField = findViewById(R.id.scheduleNoTextView);
                            scheduleNumTextField.setText(responseObject.getString("schedule#"));
                        }
                        if (responseObject.has("units")) {
                            TextView unitsTextView = findViewById(R.id.unitsTextField);
                            unitsTextView.setText(responseObject.getString("units"));
                        }
                        if (responseObject.has("room")) {
                            TextView roomTextView = findViewById(R.id.roomTextView);
                            roomTextView.setText(responseObject.getString("room"));
                        }
                        if (responseObject.has("waitlist")) {
                            TextView waitListTextView = findViewById(R.id.waitlistTextView);
                            waitListTextView.setText(String.valueOf(responseObject.getInt("waitlist")));
                        }
                        if (responseObject.has("seats")) {
                            TextView seatsTextView = findViewById(R.id.seatsTextField);
                            seatsAvailable = responseObject.getInt("seats");
                            seatsTextView.setText(String.valueOf(seatsAvailable));
                        }

                        if (!(seatsAvailable > 0))
                            addCourseButton.setText("Add to waitlist");

                        if (responseObject.has("fullTitle")) {
                            TextView fullTitleTextView = findViewById(R.id.fullTitleTextField);
                            fullTitleTextView.setText(responseObject.getString("fullTitle"));
                        }
                        if (responseObject.has("subject")){
                            TextView subjectTextView = findViewById(R.id.subjectTextField);
                            subjectTextView.setText(responseObject.getString("subject"));
                        }
                        if(responseObject.has("course#")) {
                            TextView courseNumTextField = findViewById(R.id.courseNoTextField);
                            courseNumTextField.setText(responseObject.getString("course#"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("rew", error.toString());

            }
        };
        JsonObjectRequest getRequest = new JsonObjectRequest(url, null, success, failure);
        VolleyQueue.instance(this).add(getRequest);
    }

    private void addStudentToClass(){
        String PREFS_NAME="mypre";
        String PREF_REDID="redId";
        String PREF_PASSWORD="password";
        SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        String redid = pref.getString(PREF_REDID, null);
        String password = pref.getString(PREF_PASSWORD, null);
        if( redid!= null && password != null) {
            JSONObject data = new JSONObject();
            try {
                data.put("redid", redid);
                data.put("password", password);
                data.put("courseid", courseId);
            } catch (JSONException error) {
                Log.e(TAG, "JSON error", error);
                return;
            }
            Response.Listener<JSONObject> successRegisterStudent = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(response.has("error")){
                        try {
                            Toast.makeText(getApplicationContext(), response.getString("error") ,
                                    Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        try {
                            Toast.makeText(getApplicationContext(), response.getString("ok") ,
                                    Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SystemClock.sleep(1);
                        finish();
                    }
                }
            };
            Response.ErrorListener failureRegisterStudent = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Volley error", error);
                    Toast.makeText(getApplicationContext(), String.valueOf(error) ,
                            Toast.LENGTH_SHORT).show();
                    //navigateToRegister();
                }
            };
            String urlRegisterStudent = "https://bismarck.sdsu.edu/registration/registerclass";
            String urlWaitlistStudent = "https://bismarck.sdsu.edu/registration/waitlistclass";
            if(seatsAvailable > 0) {
                JsonObjectRequest postRequest = new JsonObjectRequest(urlRegisterStudent, data, successRegisterStudent, failureRegisterStudent);
                VolleyQueue.instance(this).add(postRequest);
            }else{
                JsonObjectRequest postRequest = new JsonObjectRequest(urlWaitlistStudent, data, successRegisterStudent, failureRegisterStudent);
                VolleyQueue.instance(this).add(postRequest);
            }

        }
    }
}
