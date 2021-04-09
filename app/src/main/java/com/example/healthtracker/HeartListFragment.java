package com.example.healthtracker;

import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HeartListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HeartListFragment extends Fragment implements HeartRecyclerViewAdapter.ItemClickListener{
    private static final String TAG = "HeartListFragment";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    FirebaseUser user;
    HeartRecyclerViewAdapter adapter;

    EditText _rateText;
    EditText _systText;
    EditText _diastText;
    MaterialButton _addHeartButton;
    RecyclerView _recView;

    public HeartListFragment() {
        // Required empty public constructor
    }

    public static HeartListFragment newInstance() {
        HeartListFragment fragment = new HeartListFragment();
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
                Collections.reverse(data);
                adapter = new HeartRecyclerViewAdapter(getContext(), data);
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
        View heartView = inflater.inflate(R.layout.fragment_heart_list, container, false);
        _rateText = (EditText)heartView.findViewById(R.id.input_hrate);
        _systText = (EditText)heartView.findViewById(R.id.input_systolic);
        _diastText = (EditText)heartView.findViewById(R.id.input_diastolic);

        _addHeartButton = (MaterialButton)heartView.findViewById(R.id.btn_add_heart);
        _recView = (RecyclerView)heartView.findViewById(R.id.heart_temp_list);
        _recView.setLayoutManager(new LinearLayoutManager(getContext()));
        _addHeartButton.setOnClickListener(v -> addHeart());
        return heartView;
    }

    public void addHeart(){
        _addHeartButton.setEnabled(false);
        String heartRate = _rateText.getText().toString();
        String systolic = _systText.getText().toString();
        String diastolic = _diastText.getText().toString();
        Date date = Calendar.getInstance().getTime();
        Heart heart = new Heart(heartRate, systolic, diastolic);
        mDatabase.child("users").child(user.getUid()).child("heart").child(toISO8601UTC(date)).setValue(heart);
        _rateText.setText(null);
        _systText.setText(null);
        _diastText.setText(null);
        Toast.makeText(getContext(), "Succesfully added", Toast.LENGTH_LONG).show();
        _addHeartButton.setEnabled(true);
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

    @IgnoreExtraProperties
    public class Heart{
        public String rate;
        public String syst;
        public String diast;

        public Heart(){

        }

        public Heart(String rate, String syst, String diast)
        {
            this.rate = rate;
            this.syst = syst;
            this.diast = diast;
        }
    }
}