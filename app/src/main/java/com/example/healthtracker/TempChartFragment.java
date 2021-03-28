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
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TempChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TempChartFragment extends Fragment {
    private static final String TAG = "TemperatureChartFragment";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Set set;
    Line series1;
    FirebaseUser user;
    AnyChartView _chartView;
    Cartesian cartesian;


    public TempChartFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TempChartFragment newInstance() {
        TempChartFragment fragment = new TempChartFragment();
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

        Query q = mDatabase.child("users").child(user.getUid()).child("temperatures").orderByKey();
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
                    Float f = Float.parseFloat(p.second.toString());
                    seriesData.add(new TempChartFragment.CustomDataEntry(p.first.toString(), f));
                }
//                cartesian = AnyChart.line();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        View chartView = inflater.inflate(R.layout.fragment_temp_chart, container, false);
        _chartView = chartView.findViewById(R.id.chart_view);
        cartesian = AnyChart.line();
        _chartView.setChart(cartesian);

        return chartView;
    }

    private class CustomDataEntry extends ValueDataEntry {
        CustomDataEntry(String x, Float value) {
            super(x, value);
        }
    }
}