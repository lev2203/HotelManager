package com.example.hotelmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import org.w3c.dom.Document;
import java.util.ArrayList;
import java.util.List;

public class ListActivity2 extends AppCompatActivity {

    List<Model> modelList = new ArrayList<>();
    RecyclerView mRecyclerView;

    //расположению для recyclerview
    RecyclerView.LayoutManager layoutManager;

    FloatingActionButton mAddBtn;

    FirebaseFirestore db;
    CustomAdapter adapter;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list2);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("List Data");

        db = FirebaseFirestore.getInstance();

        mRecyclerView = findViewById(R.id.recycler_view);
        mAddBtn = findViewById(R.id.addBtn);


        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        pd = new ProgressDialog(this);

        // показать данные

        showData();

        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListActivity2.this,MainActivity.class));
                finish();
            }
        });
    }

    private void showData() {

        pd.setTitle("Loading Data");
        pd.show();

        db.collection("Documents")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        modelList.clear();
                        
                        pd.dismiss();
                        // show data
                        for (DocumentSnapshot doc: task.getResult()){
                            Model model = new Model(doc.getString("id"),
                                    doc.getString("title"),
                                    doc.getString("description"));
                            modelList.add(model);
                        }

                        adapter = new CustomAdapter(ListActivity2.this, modelList);

                        mRecyclerView.setAdapter(adapter);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        
                        pd.dismiss();
                        Toast.makeText(ListActivity2.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
    public void deleteData(int index){

        pd.setTitle("Deleting Data");
        pd.show();
        db.collection("Documents").document(modelList.get(index).getId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(ListActivity2.this, "Deleted...", Toast.LENGTH_SHORT).show();
                        showData();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        pd.dismiss();

                        Toast.makeText(ListActivity2.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
}