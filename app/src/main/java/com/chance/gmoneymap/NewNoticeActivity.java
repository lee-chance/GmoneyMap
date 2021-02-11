package com.chance.gmoneymap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewNoticeActivity extends AppCompatActivity implements View.OnClickListener {

//    EditText et_title, et_content;
//    Button btn_apply;
//
//    private String title, content;
//
//    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
//    private String id = mStore.collection("clothes").document().getId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_new_notice);
//
//        et_title = findViewById(R.id.et_title);
//        et_content = findViewById(R.id.et_content);
//        btn_apply = findViewById(R.id.btn_apply);
//
//        btn_apply.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
//        title = et_title.getText().toString().trim();
//        content = et_content.getText().toString().trim();
//        if(title.equals("")){
//            Toast.makeText(this, "Input the title", Toast.LENGTH_SHORT).show();
//        } else if(content.equals("")){
//            Toast.makeText(this, "Input the content", Toast.LENGTH_SHORT).show();
//        } else {
//            uploadDatabase();
//        }
    }


//    private void uploadDatabase() {
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle("저장중입니다...");
//        progressDialog.show();
//        Map<String, Object> data = new HashMap<>();
//        data.put("id", id);
//        data.put("title", title);
//        data.put("content", content);
//        data.put("timestamp", FieldValue.serverTimestamp());
//
//        String document_id = title+"_"+id;
//
//        mStore.collection("notices").document(document_id).set(data)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        progressDialog.dismiss();
//                        Intent intent = new Intent();
//                        intent.putExtra("id", id);
//                        setResult(RESULT_OK, intent);
//                        finish();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                progressDialog.dismiss();
//                Toast.makeText(NewNoticeActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
