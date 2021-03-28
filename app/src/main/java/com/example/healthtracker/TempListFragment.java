package com.example.healthtracker;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TempListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TempListFragment extends Fragment implements RecyclerViewAdapter.ItemClickListener{

    private static final String TAG = "TemperatureListFragment";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    FirebaseUser user;
    RecyclerViewAdapter adapter;

    EditText _temperatureText;
    MaterialButton _addTempButton;
    RecyclerView _recView;

    public TempListFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TempListFragment newInstance() {
        TempListFragment fragment = new TempListFragment();
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
                adapter = new RecyclerViewAdapter(getContext(), data);
                _recView.setAdapter(adapter);
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
        View tempView = inflater.inflate(R.layout.fragment_temp_list, container, false);
        _temperatureText = (EditText)tempView.findViewById(R.id.input_temp);
        _addTempButton = (MaterialButton)tempView.findViewById(R.id.btn_add_temp);
        _recView = (RecyclerView)tempView.findViewById(R.id.temp_list);
        _recView.setLayoutManager(new LinearLayoutManager(getContext()));
        _addTempButton.setOnClickListener(v -> addTemp());

        return tempView;
    }

    public void addTemp(){
        _addTempButton.setEnabled(false);
        String temperature = _temperatureText.getText().toString();
        Date date = Calendar.getInstance().getTime();
        mDatabase.child("users").child(user.getUid()).child("temperatures").child(toISO8601UTC(date)).setValue(temperature);
        _temperatureText.setText(null);
        Toast.makeText(getContext(), "Succesfully added", Toast.LENGTH_LONG).show();
        _addTempButton.setEnabled(true);
    }

    public static String toISO8601UTC(Date date) {
        TimeZone tz = TimeZone.getDefault();
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy', 'HH:mm:ss");
        df.setTimeZone(tz);
        return df.format(date);
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getContext(), "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }
}