package com.example.healthtracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class TemperatureActivity extends AppCompatActivity  implements RecyclerViewAdapter.ItemClickListener {
    private static final String TAG = "TemperatureActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Set set;
    Line series1;
    FirebaseUser user;
    RecyclerViewAdapter adapter;
    ValueEventListener queryValueListener;
    ArrayList<String> itemList;
    AnyChartView _chartView;

    EditText _temperatureText;
    MaterialButton _addTempButton;
    RecyclerView _recView;
    Cartesian cartesian;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        setContentView(R.layout.activity_temperature);

        _chartView = findViewById(R.id.chart_view);

        cartesian = AnyChart.line();
        cartesian.animation(true);
        cartesian.padding(10d, 20d, 5d, 20d);
        cartesian.crosshair().enabled(true);
        cartesian.crosshair().yLabel(true).yStroke((Stroke) null, null, null, (String) null, (String) null);
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.title("Trend of temperature over time");
        cartesian.yAxis(0).title("Temperature (Celsius)");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);
        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);

        _chartView.setChart(cartesian);

        _temperatureText = (EditText)findViewById(R.id.input_temp);
        _addTempButton = (MaterialButton)findViewById(R.id.btn_add_temp);

        _recView = (RecyclerView)findViewById(R.id.temp_list);
        _recView.setLayoutManager(new LinearLayoutManager(this));
//        adapter = new RecyclerViewAdapter(this, get_temp_list());
//        adapter.setClickListener(this);
//        _recView.setAdapter(adapter);

        _addTempButton.setOnClickListener(v -> addTemp());



        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(getBaseContext(), "Failed to load list", Toast.LENGTH_SHORT).show();
            }
        };

        Query q = mDatabase.child("users").child(user.getUid()).child("temperatures").orderByKey();
        q.addChildEventListener(childEventListener);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Pair<String, String>> data = new ArrayList<>();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Pair<String, String> pair = new Pair<String, String>(postSnapshot.getKey(), postSnapshot.getValue().toString());
                    data.add(pair);
                }

                adapter = new RecyclerViewAdapter(getBaseContext(), data);
//                adapter.setClickListener(this);
                _recView.setAdapter(adapter);

                List<DataEntry> seriesData = new ArrayList<>();
                for(Pair p: data){
                    Float f = Float.parseFloat(p.second.toString());
                    seriesData.add(new CustomDataEntry(p.first.toString(), f));
                }
                set = Set.instantiate();
                set.data(seriesData);
                Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");

                Log.d(TAG, "NewSeries:" + series1Mapping);

                series1 = cartesian.line(series1Mapping);
                series1.name("Temperature");
                series1.color("red");
                series1.markers().enabled(true);
                series1.markers()
                        .type(MarkerType.CIRCLE)
                        .size(4d);
                series1.tooltip()
                        .position("right")
                        .anchor(Anchor.LEFT_CENTER)
                        .offsetX(5d)
                        .offsetY(5d);

//                _chartView.setChart(cartesian);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    public static String toISO8601UTC(Date date) {
        TimeZone tz = TimeZone.getDefault();
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy', 'HH:mm:ss");
        df.setTimeZone(tz);
        return df.format(date);
    }

    public static Date fromISO8601UTC(String dateStr) {
        TimeZone tz = TimeZone.getDefault();
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy', 'HH:mm:ss");
        df.setTimeZone(tz);

        try {
            return df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addTemp(){
        _addTempButton.setEnabled(false);
        String temperature = _temperatureText.getText().toString();
        Date date = Calendar.getInstance().getTime();
        mDatabase.child("users").child(user.getUid()).child("temperatures").child(toISO8601UTC(date)).setValue(temperature);
        _temperatureText.setText(null);
        Toast.makeText(getBaseContext(), "Succesfully added", Toast.LENGTH_LONG).show();
        _addTempButton.setEnabled(true);
    }


    private class CustomDataEntry extends ValueDataEntry {

        CustomDataEntry(String x, Float value) {
            super(x, value);
        }

    }
}