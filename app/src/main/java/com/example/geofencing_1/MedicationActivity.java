package com.example.geofencing_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MedicationActivity extends AppCompatActivity {

    private static final String TAG = "MedicationActivity";
    DatabaseReference ref;
    ArrayList<Helper> list;
    RecyclerView recyclerView;
    SearchView searchView;

    ArrayList<String> keyz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);

        ref = FirebaseDatabase.getInstance().getReference().child("medicines");
        recyclerView = findViewById(R.id.rv);
        searchView = findViewById(R.id.searchview);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(ref != null){
            ref.addValueEventListener(new ValueEventListener() {
                @NonNull
                @Override
                protected Object clone() throws CloneNotSupportedException {
                    return super.clone();
                }

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        list = new ArrayList<>();
                        keyz = new ArrayList<>();
                        for(DataSnapshot ds : snapshot.getChildren()){
                            list.add(ds.getValue(Helper.class));
                            keyz.add(ds.getKey());
                        }
                        AdapterClass adapterClass = new AdapterClass(list);
                        recyclerView.setAdapter(adapterClass);

                        adapterClass.setOnItemClickListener(new AdapterClass.OnClickItemListener() {
                            @Override
                            public void onItemClick(int position) {
                                String key = keyz.get(position);
                                Helper data = list.get(position);
                                String heading = data.getDisease();
                                Log.d(TAG, "Key : " + key);
                                Log.d(TAG, "Position : " + position);
                                Log.d(TAG, "Heading : " + heading );
                                Log.d(TAG, "Link : " + data.getImageurl());

                                Intent intent = new Intent(getApplicationContext(), VoteMedicine.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("key", key);
                                bundle.putString("disease", data.getDisease());
                                bundle.putString("medication", data.getMedication());
                                bundle.putString("url", data.getImageurl());
                                bundle.putInt("helpfull", data.getHelpfull());
                                bundle.putInt("nothelpfull", data.getNothelpfull());
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (searchView != null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return true;
                }
            });
        }

    }

    private void search(String str) {
        if (!str.equals("")){
            ArrayList<Helper> myList = new ArrayList<>();
            for (Helper object : list){
                if(object.getDisease().toLowerCase().contains(str.toLowerCase())){
                    myList.add(object);
                }
            }
            AdapterClass adapterClass = new AdapterClass(myList);
            recyclerView.setAdapter(adapterClass);
        }

    }
}