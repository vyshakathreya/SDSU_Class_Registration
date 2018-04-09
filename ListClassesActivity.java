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
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import vyshak.sdsu.edu.sdsuclassregistration.sync.VolleyQueue;

public class ListClassesActivity extends AppCompatActivity {

    private String urlCourseList = "https://bismarck.sdsu.edu/registration/classidslist?subjectid=";
    private String urlClassDetails = "https://bismarck.sdsu.edu/registration/classdetails?classid=";
    private String url;
    private ListView courseIdList;
    private ArrayList<String> courseIdArrayList = new ArrayList<>();
    private String TAG = "ListClassesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_classes);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.class_id_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Bundle urlData = getIntent().getExtras();
        if (!(urlData == null)) {
            url = urlData.getString("url");
            Log.d(TAG, "url as recieved " + url);
        }

        courseIdList = findViewById(R.id.classIdListView);
        getCourseId();

        courseIdList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG,"list clicked "+courseIdArrayList.get(i));
                Intent go = new Intent(getApplicationContext(),ClassDetailsActivity.class);
                String querySearch = urlClassDetails+courseIdArrayList.get(i);
                    go.putExtra("url",querySearch);
                    startActivity(go);
                }
        });
    }


    private void getCourseId() {
        Log.d("rew", "Start");
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        courseIdArrayList.add(response.get(i).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG,"courseID"+response.toString());
                }
                Log.d(TAG, response.toString());
                courseIdList.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, courseIdArrayList));

            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("rew", error.toString());

            }
        };
        JsonArrayRequest getRequest = new JsonArrayRequest(url, success, failure);
        VolleyQueue.instance(this).add(getRequest);
    }
}