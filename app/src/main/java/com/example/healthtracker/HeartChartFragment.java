package com.example.healthtracker;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeartChartFragment extends Fragment {
    private static final String TAG = "HeartChartFragment";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Set set;
    private Set set2;
    private Set set3;
    Line series1;
    Line series2;
    Line series3;
    FirebaseUser user;
    AnyChartView _chartView;
    Cartesian cartesian;

    public HeartChartFragment() {
        // Required empty public constructor
    }
    public static HeartChartFragment newInstance() {
        HeartChartFragment fragment = new HeartChartFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        Query q = mDatabase.child("users").child(user.getUid()).child("heart").orderByKey();
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Pair<String, String>> data = new ArrayList<>();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Pair<String, String> pair = new Pair<String, String>(postSnapshot.getKey(), postSnapshot.getValue().toString());
                    data.add(pair);
                }
                List<DataEntry> seriesData = new ArrayList<>();
                for(Pair p: data){
                    String dd = p.second.toString();
                    String val = dd.replaceAll("[{}\" ]", "");
                    Map<String, String> myMap = new HashMap<String, String>();
                    String[] pairs = val.split(",");
                    for (String pair : pairs) {
                        String[] keyValue = pair.split("=");
                        myMap.put(keyValue[0], String.valueOf(keyValue[1]));
                    }
                    Log.e(TAG, "dd "+myMap);
                    String r = myMap.get("rate");
                    String s = myMap.get("syst");
                    String d = myMap.get("diast");
                    seriesData.add(
                            new HeartChartFragment.CustomDataEntry(
                                    p.first.toString(),
                                    Float.parseFloat(r),
                                    Float.parseFloat(s),
                                    Float.parseFloat(d)
                            ));
                }
//                cartesian = AnyChart.line();
                cartesian.animation(true);
                cartesian.padding(10d, 20d, 5d, 20d);
                cartesian.crosshair().enabled(true);
                cartesian.crosshair().yLabel(true).yStroke((Stroke) null, null, null, (String) null, (String) null);
                cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
                cartesian.title("Trend of heart params over time");
                cartesian.yAxis(0).title("Value");
                cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);
                cartesian.legend().enabled(true);
                cartesian.legend().fontSize(13d);
                cartesian.legend().padding(0d, 0d, 10d, 0d);

                set = Set.instantiate();
                set.data(seriesData);

                Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
                Mapping series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }");
                Mapping series3Mapping = set.mapAs("{ x: 'x', value: 'value3' }");

                Log.d(TAG, "NewSeries:" + series1Mapping);

                series1 = cartesian.line(series1Mapping);
                series1.name("Heart rate");
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

                series2 = cartesian.line(series2Mapping);
                series2.name("Systolic");
                series2.color("blue");
                series2.markers().enabled(true);
                series2.markers()
                        .type(MarkerType.CIRCLE)
                        .size(4d);
                series2.tooltip()
                        .position("right")
                        .anchor(Anchor.LEFT_CENTER)
                        .offsetX(5d)
                        .offsetY(5d);

                series3 = cartesian.line(series3Mapping);
                series3.name("Diastolic");
                series3.color("green");
                series3.markers().enabled(true);
                series3.markers()
                        .type(MarkerType.CIRCLE)
                        .size(4d);
                series3.tooltip()
                        .position("right")
                        .anchor(Anchor.LEFT_CENTER)
                        .offsetX(5d)
                        .offsetY(5d);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View chartView = inflater.inflate(R.layout.fragment_heart_chart, container, false);
        _chartView = chartView.findViewById(R.id.heart_chart_view);
        cartesian = AnyChart.line();
        _chartView.setChart(cartesian);

        return chartView;
    }
    private class CustomDataEntry extends ValueDataEntry {
        CustomDataEntry(String x, Float value, Float value2, Float value3) {
            super(x, value);
            setValue("value2", value2);
            setValue("value3", value3);
        }
    }
}