package vyshak.sdsu.edu.sdsuclassregistration;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import vyshak.sdsu.edu.sdsuclassregistration.sync.VolleyQueue;

public class RegisterActivity extends AppCompatActivity {

    private ListView myCoursesList;
    private ListView myWaitingList;

    private String TAG = "Register Activity";
    private ArrayList<String> myAddedCourses = new ArrayList<>();
    private ArrayList<String> myWaitlistCourses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button chooseCourseButton = findViewById(R.id.addClassesButton);
        chooseCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go = new Intent(getApplicationContext(), ClassFilterActivity.class);
                startActivity(go);
            }
        });
        Button removeCourseButton = findViewById(R.id.dropClassButton);
        removeCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllClasses();
            }
        });
        myCoursesList = findViewById(R.id.myClassesListView);
        myWaitingList = findViewById(R.id.waitingListView);

        myCoursesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, (String) adapterView.getItemAtPosition(i));
                final String courseIDToBeRemoved = (String) adapterView.getItemAtPosition(i);

                AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
                alertDialog.setTitle("UnRegister");
                alertDialog.setMessage("UnRegister from" + courseIDToBeRemoved);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Unregister", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        unEnrollStudent(courseIDToBeRemoved,false);
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

            }
        });

        myWaitingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String courseIDToBeRemoved = (String) adapterView.getItemAtPosition(i);

                AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
                alertDialog.setTitle("Remove from Waitlist");
                alertDialog.setMessage("Stop waiting for " + courseIDToBeRemoved + "?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Stop Waiting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        unEnrollStudent(courseIDToBeRemoved,true);
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
        getStudentClasses();
    }

    private void getStudentClasses() {
            String urlCheckUser = "https://bismarck.sdsu.edu/registration/studentclasses";

            Log.d(TAG, "url" + urlCheckUser);
            Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response.has("error")) {
                        try {
                            Toast.makeText(getApplicationContext(), response.getString("error"),
                                    Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        JSONObject jsonObject = (JSONObject) response;
                        try {
                            Log.d(TAG, "Got classes" + jsonObject.get("classes"));
                            if(jsonObject.has("classes")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("classes");
                                myAddedCourses.clear();
                                if(jsonArray.length() == 0){
                                    myAddedCourses.add("No classes registered");
                                }else {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        myAddedCourses.add(jsonArray.getString(i));
                                    }
                                }

                                myCoursesList.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, myAddedCourses));
                            }
                            if(jsonObject.has("waitlist")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("waitlist");
                                myWaitlistCourses.clear();
                                if(jsonArray.length() == 0){
                                    myWaitlistCourses.add("No classes waitlisted");
                                }else {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        myWaitlistCourses.add(jsonArray.getString(i));
                                    }
                                }
                                myWaitingList.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, myWaitlistCourses));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            };
            Response.ErrorListener failure = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Volley error", error);
                    Toast.makeText(getApplicationContext(), String.valueOf(error),
                            Toast.LENGTH_SHORT).show();
                }
            };
            JsonObjectRequest postRequest = new JsonObjectRequest(urlCheckUser, getData(null), success, failure);
            VolleyQueue.instance(this).add(postRequest);
    }

    private void unEnrollStudent(String courseId, Boolean isWaitList){
        String urlUnEnrollStudent;
        String PREFS_NAME="mypre";
        String PREF_REDID="redId";
        String PREF_PASSWORD="password";
        SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        String redid = pref.getString(PREF_REDID, null);
        String password = pref.getString(PREF_PASSWORD, null);
        if( redid == null || password == null) {
            finish();
        }
        if(isWaitList){
            urlUnEnrollStudent = "https://bismarck.sdsu.edu/registration/unwaitlistclass?"+
                    "redid="+redid+"&password="+ password+"&courseid="+courseId;
        }else {
            urlUnEnrollStudent = "https://bismarck.sdsu.edu/registration/unregisterclass?"+
                    "redid="+redid+"&password="+ password+"&courseid="+courseId;
        }
        Log.d(TAG, "url" + urlUnEnrollStudent);
        Response.Listener<JSONObject> successUnEnroll = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.has("error")) {
                    try {
                        Toast.makeText(getApplicationContext(), response.getString("error"),
                                Toast.LENGTH_SHORT).show();
                        getStudentClasses();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    JSONObject jsonObject = (JSONObject) response;
                    try {
                        Log.d(TAG, "Got classes " + jsonObject.get("classes"));
                        Toast.makeText(getApplicationContext(), response.toString() ,
                                Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getStudentClasses();
                }
            }
        };
        Response.ErrorListener failureUnEnroll = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Volley error", error);
                Toast.makeText(getApplicationContext(), String.valueOf(error) + R.string.auth_failed,
                        Toast.LENGTH_SHORT).show();
                //navigateToRegister();
            }
        };
        JsonObjectRequest unEnrollStudentRequest = new JsonObjectRequest(urlUnEnrollStudent, null, successUnEnroll, failureUnEnroll);
        VolleyQueue.instance(this).add(unEnrollStudentRequest);

    }

    private void removeAllClasses(){

        String PREFS_NAME="mypre";
        String PREF_REDID="redId";
        String PREF_PASSWORD="password";
        SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        String redid = pref.getString(PREF_REDID, null);
        String password = pref.getString(PREF_PASSWORD, null);
        if( redid == null || password == null) {
            finish();
            }
        String urlResetCourses = "https://bismarck.sdsu.edu/registration/resetstudent?" +
                "redid="+redid+"&password="+ password;
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
                        getStudentClasses();
                        //SystemClock.sleep(1);
                        //finish();
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
            JsonObjectRequest postRequest = new JsonObjectRequest(urlResetCourses, null, successRegisterStudent, failureRegisterStudent);
            VolleyQueue.instance(this).add(postRequest);
        }


    private JSONObject getData(String courseID) {
        String PREFS_NAME="mypre";
        String PREF_REDID="redId";
        String PREF_PASSWORD="password";
        SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        String redid = pref.getString(PREF_REDID, null);
        String password = pref.getString(PREF_PASSWORD, null);
        JSONObject data = new JSONObject();
        if( redid!= null && password != null) {
            try {
                int redIdInt = Integer.valueOf(redid);
                data.put("redid", redid);
                data.put("password", password);
                if (courseID != null){
                    int courseIDInt = Integer.valueOf(courseID);
                    data.put("courseId",courseIDInt);
                }

            } catch (JSONException error) {
                Log.e(TAG, "JSON error", error);

            }
            Log.d(TAG,"Data from data" + data.toString());//return data;
        }
        return data;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getStudentClasses();
    }
}