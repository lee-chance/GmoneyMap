package com.chance.gmoneymap;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.chance.gmoneymap.Adapters.NoticeAdapter;
import com.chance.gmoneymap.Models.NoticeModel;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import static android.R.drawable.stat_notify_error;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;

public class NoticeActivity extends AppCompatActivity {

    private static final int REQUEST_OK = 100;
    private static final String password = "asdf123";
    RecyclerView rv_notice;

    List<NoticeModel> noticeModels;
    String[] noticeArray;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        rv_notice = findViewById(R.id.rv_notice);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("공지사항");
        actionBar.setDisplayHomeAsUpEnabled(true);

        setRecyclerView();

    }

    private void setRecyclerView() {
        noticeModels = new ArrayList<>();
        db.collection("notices")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            String title = (String) dc.getDocument().getData().get("title");
                            String content = (String) dc.getDocument().getData().get("content");

                            if (content.contains("!@#")) {
                                noticeArray = content.split("!@#");
                                NoticeModel data = new NoticeModel(title, noticeArray);
                                noticeModels.add(0, data);
                            } else {
                                NoticeModel data = new NoticeModel(title, content);
                                noticeModels.add(0, data);
                            }
                        }
                        NoticeAdapter noticeAdapter = new NoticeAdapter(noticeModels);
                        if (noticeModels.size() == 0) {
                            rv_notice.setBackgroundResource(stat_notify_error);
                        } else {
                            rv_notice.setAdapter(noticeAdapter);
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
//            case R.id.add:
//                checkPassword();
//                break;
            case R.id.report:
                noticeBug();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkPassword() {
        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.topMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        final EditText editText = new EditText(this);
        editText.setLayoutParams(params);
        editText.setTextColor(getResources().getColor(android.R.color.white));
        editText.setInputType(TYPE_TEXT_VARIATION_PASSWORD);

        container.addView(editText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle("비밀번호를 입력해 주세요");
        builder.setView(container).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editText.getText().toString().trim().equals(password)) {
                    startActivityForResult(new Intent(getApplicationContext(), NewNoticeActivity.class), REQUEST_OK);
                } else {
                    Toast.makeText(NoticeActivity.this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void noticeBug() {
        //버전정보얻기
        PackageInfo pInfo;
        String versionName = "";
        try {
            pInfo = getPackageManager().getPackageInfo(
                    getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Chance", "" + e.getMessage());
        }
        //이메일 인텐트
        Intent email = new Intent(Intent.ACTION_SENDTO);
        email.setType("plain/Text");
        email.setData(Uri.parse("mailto:"));
        String[] address = {"729mail2@gmail.com"};
        email.putExtra(Intent.EXTRA_EMAIL, address);
        email.putExtra(Intent.EXTRA_SUBJECT, "(" + getString(R.string.app_name) + " 오류제보)");
        email.putExtra(Intent.EXTRA_TEXT, "앱 버전 : " + versionName + "" +
                "\n\n기기명 : " +
                "\n안드로이드 OS : " +
                "\n내용 (Content) : \n" +
                "\n이미지를 첨부하면 더욱 좋습니다.\n");
        startActivity(email);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_OK) {
            setRecyclerView();
        }
    }
}
