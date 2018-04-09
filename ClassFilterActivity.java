package vyshak.sdsu.edu.sdsuclassregistration;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import vyshak.sdsu.edu.sdsuclassregistration.bean.Major;
import vyshak.sdsu.edu.sdsuclassregistration.sync.VolleyQueue;

public class ClassFilterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner majorSpinner;
    private Spinner levelSpinner;
    private Button resetCriteriaButton;

    private int majorIdChosen;
    private String majorChosen="Select Major";
    private String levelChosen = "Select Level";
    private String startTime = "Select Start Time";
    private String endTime = "Select End Time";


    private String TAG = "ClassFilter";

    private ArrayList<String> majorsArrayList = new ArrayList<>();
    private ArrayList<Integer> majorIdArrayList = new ArrayList<>();
    private final String[] levels={"Select Level","lower", "upper", "graduate"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_filter);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.class_filter_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        majorSpinner = findViewById(R.id.majorSpinner);
        levelSpinner = findViewById(R.id.levelSpinner);
        TimePicker startTimePicker = findViewById(R.id.startTimePicker);
        TimePicker endTimePicker = findViewById(R.id.endTimePicker);
        startTimePicker.setIs24HourView(true);
        endTimePicker.setIs24HourView(true);

        getMajors();
        majorSpinner.setOnItemSelectedListener(this);

        Button findCourseButton = findViewById(R.id.buttonAddCourse);
        findCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCourses();
            }
        });

        resetCriteriaButton = findViewById(R.id.buttonReset);
        resetCriteriaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetSelections();
            }
        });

        ArrayAdapter<String> levelsAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,levels);
        levelSpinner.setAdapter(levelsAdapter);
        levelSpinner.setOnItemSelectedListener(this);

        endTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
            endTime = String.valueOf(i)+String.valueOf(i1);

            }
        });

        startTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
            startTime = String.valueOf(i)+String.valueOf(i1);
            }
        });
    }

    private void showCourses(){
        Intent go = new Intent(getApplicationContext(),ListClassesActivity.class);
        String querySearch = "https://bismarck.sdsu.edu/registration/classidslist?subjectid="+majorIdChosen;

        if(checkValues()) {

            if (!levelChosen.equals("Select Level")) {
                querySearch = querySearch + "&level=" + levelChosen;
            }

            if (!startTime.equals("Select Start Time") )  {
                querySearch = querySearch + "&start-time=" + startTime;
            }
            if (!endTime.equals("Select End Time")){
                querySearch = querySearch + "&end-time=" + endTime;
            }
            Log.d(TAG,"query "+querySearch);
            go.putExtra("url",querySearch);
            startActivity(go);
        }
    }

    private boolean checkValues(){
        if(majorChosen.equals("Select Major")){
            Toast.makeText(getApplicationContext(), "Please choose a major" ,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getMajors() {
        String urlSubject = "https://bismarck.sdsu.edu/registration/subjectlist" ;
        Log.d("rew", "Start");
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                for(int i=0;i<response.length();i++){
                    try {
                        if(! majorsArrayList.isEmpty() && majorsArrayList.get(0) != "Select Major"){
                            majorsArrayList.add(0,"Select Major");
                        }
                        Major major = new Major();
                        JSONObject responseObject = (JSONObject) response.get(i);
                        major.setTitle(responseObject.getString("title"));
                        major.setCollege(responseObject.getString("college"));
                        major.setId(responseObject.getInt("id"));
                        major.setClasses(responseObject.getInt("classes"));
                        majorsArrayList.add(major.getTitle());
                        majorIdArrayList.add(major.getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG,response.toString());
                majorSpinner.setAdapter(new ArrayAdapter<String>(getBaseContext(),
                        android.R.layout.simple_spinner_item, majorsArrayList));
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("rew", error.toString());

            }
        };
        JsonArrayRequest getRequest = new JsonArrayRequest( urlSubject, success, failure);
        VolleyQueue.instance(this).add(getRequest);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView.getId() == majorSpinner.getId()){
            majorIdChosen = majorIdArrayList.get(i);
            majorChosen = majorsArrayList.get(i);
        }
        if(adapterView.getId() == levelSpinner.getId()){
            levelChosen = levels[i];
            Log.d(TAG,"adapt "+adapterView.getItemAtPosition(i));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        resetSelections();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetSelections();
    }

    private void resetSelections(){
        majorSpinner.setSelection(0);
        levelSpinner.setSelection(0);
        majorChosen="Select Major";
        levelChosen = "Select Level";
        startTime = "Select Start Time";
        endTime = "Select End Time";
    }

}
