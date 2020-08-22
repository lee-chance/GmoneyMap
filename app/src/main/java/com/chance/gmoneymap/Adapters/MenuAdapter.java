package com.chance.gmoneymap.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chance.gmoneymap.Dialogs.InformationDialog;
import com.chance.gmoneymap.DownloadActivity;
import com.chance.gmoneymap.Models.MenuModel;
import com.chance.gmoneymap.NoticeActivity;
import com.chance.gmoneymap.R;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.TemplateParams;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.mViewHolder> {

    private Context context;
    private List<MenuModel> list;
    private static final int NOTICE_POSITION = 0;
    private static final int DOWNLOAD_POSITION = 1;
    private static final int REPORT_POSITION = 2;
    private static final int GRADE_POSITION = 3;
    private static final int SHARE_POSITION = 4;
    private static final int INFO_POSITION = 5;

    public MenuAdapter(Context context, List<MenuModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new mViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, final int position) {
        holder.item_tv_content.setText(list.get(position).getContent());
        holder.item_tv_content.setCompoundDrawablesWithIntrinsicBounds(list.get(position).getIcon(), null, null, null);

        holder.item_ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position) {
                    case NOTICE_POSITION:
                        //공지사항
                        context.startActivity(new Intent(context, NoticeActivity.class));
                        break;
                    case REPORT_POSITION:
                        //오류제보
                        noticeBug();
                        break;
                    case GRADE_POSITION:
                        //평점주기
                        final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                        break;
                    case SHARE_POSITION:
                        //공유하기
                        kakaoLink();
                        break;
                    case INFO_POSITION:
                        //정보
                        context.startActivity(new Intent(context, InformationDialog.class));
                        break;
                    case DOWNLOAD_POSITION:
                        //다운로드
                        context.startActivity(new Intent(context, DownloadActivity.class));
                        break;
                }
            }
        });
    }

    private String linktoApp() {
        final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
        try {
            return "market://details?id=" + appPackageName;
        } catch (android.content.ActivityNotFoundException anfe) {
            return "https://play.google.com/store/apps/details?id=" + appPackageName;
        }
    }

    private void kakaoLink() {
        //템플릿 만들기 (피드)
        TemplateParams params = FeedTemplate
                .newBuilder(ContentObject.newBuilder(
                        "경기지역화폐지도",
                        "https://ifh.cc/g/ynfCDx.png",
                        LinkObject.newBuilder()
                                .setWebUrl(linktoApp())
                                .setMobileWebUrl(linktoApp())
                                .build())
                        .setDescrption("경기도민 필수어플!\n지역화폐 가맹점을 쉽게 찾아보세요.")
                        .build())
                .addButton(new ButtonObject(
                        "앱다운로드",
                        LinkObject.newBuilder()
                                .setMobileWebUrl(linktoApp())
                                .setWebUrl(linktoApp())
                                .setAndroidExecutionParams("key1=value1")
                                .setIosExecutionParams("key1=value1")
                                .build()))
                .build();

        // 기본 템플릿으로 카카오링크 보내기
        KakaoLinkService.getInstance()
                .sendDefault(context, params, new ResponseCallback<KakaoLinkResponse>() {
                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Log.e("Chance", "카카오링크 공유 실패: " + errorResult);
                    }

                    @Override
                    public void onSuccess(KakaoLinkResponse result) {
                        Log.i("Chance", "카카오링크 공유 성공");

                        // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                        Log.w("Chance", "warning messages: " + result.getWarningMsg());
                        Log.w("Chance", "argument messages: " + result.getArgumentMsg());
                    }
                });

    }

    private void noticeBug() {
        //버전정보얻기
        PackageInfo pInfo;
        String versionName = "";
        try {
            pInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
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
        email.putExtra(Intent.EXTRA_SUBJECT, "(" + context.getString(R.string.app_name) + " 오류제보)");
        email.putExtra(Intent.EXTRA_TEXT, "앱 버전 : " + versionName + "" +
                "\n\n기기명 : " +
                "\n안드로이드 OS : " +
                "\n내용 (Content) : \n");
        context.startActivity(email);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout item_ll_main;
        private TextView item_tv_content;

        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            item_tv_content = itemView.findViewById(R.id.item_tv_content);
            item_ll_main = itemView.findViewById(R.id.item_ll_main);
        }
    }
}
