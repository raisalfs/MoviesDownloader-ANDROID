package com.rafslab.movie.dl.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rafslab.movie.dl.R;
import com.rafslab.movie.dl.model.Account;
import com.rafslab.movie.dl.model.child.Cast;
import com.rafslab.movie.dl.model.user.Report;
import com.rafslab.movie.dl.model.user.Request;
import com.rafslab.movie.dl.services.MySingleton;
import com.rafslab.movie.dl.ui.activity.HomeActivity;
import com.rafslab.movie.dl.ui.activity.SettingsActivity;
import com.rafslab.movie.dl.ui.activity.BookmarkActivity;
import com.rafslab.movie.dl.utils.BaseUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import nl.joery.animatedbottombar.AnimatedBottomBar;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder> {
    private Context mContext;
    private List<Account> accounts;
    private List<Cast.SocialMedia> socialMediaList;
    private LayoutInflater inflater;
    private AnimatedBottomBar contextBottomBar;
    private boolean isAccount;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAUluU7WA:APA91bGbkNWr19S0eTCQrjppxNdV2iCWzhRCfRLz71A-9B8_olNLqlS4BCbwIr91VSLYxlif2fR2yvtGi3JPCrgwH58l1bqa2wxezCzXoncEs6yaNHGZq1ZrOldFsINcr5iQQhqbqld4";
    final private String contentType = "application/json";
    private String TOPIC;
    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    private SimpleAdapterCallBack callBack;
    public SimpleAdapter(Context mContext, List<Account> accounts, boolean isAccount) {
        this.mContext = mContext;
        this.accounts = accounts;
        this.isAccount = isAccount;
        inflater = LayoutInflater.from(mContext);
    }
    public SimpleAdapter(Context mContext, List<Cast.SocialMedia> socialMediaList, SimpleAdapterCallBack callBack) {
        this.mContext = mContext;
        this.socialMediaList = socialMediaList;
        this.callBack = callBack;
        inflater = LayoutInflater.from(mContext);
    }

    public interface SimpleAdapterCallBack {
        void getVisibilityItems(Cast.SocialMedia data);
    }
    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isAccount) {
            return new SimpleViewHolder(inflater.inflate(R.layout.account_item, parent, false));
        } else {
            return new SimpleViewHolder(inflater.inflate(R.layout.social_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleViewHolder holder, int position) {
        if (isAccount) {
            Account data = accounts.get(position);
            holder.icon.setImageResource(data.getIcon());
            holder.title.setText(data.getTitle());
            contextBottomBar = ((HomeActivity)mContext).findViewById(R.id.bottom_navigation);
            switch (position){
                case 0:
                    holder.root.setOnClickListener(v-> mContext.startActivity(new Intent(mContext, SettingsActivity.class)));
                    break;
                case 1:
                    holder.root.setOnClickListener(v-> setRequestMovies());
                    break;
                case 3:
                    holder.root.setOnClickListener(v-> HomeActivity.showDialogUnderDevelopment(mContext));
                    break;
                case 2:
                    holder.root.setOnClickListener(v-> mContext.startActivity(new Intent(mContext, BookmarkActivity.class)));
                    break;
            }
        } else {
            Cast.SocialMedia data = socialMediaList.get(position);
            callBack.getVisibilityItems(data);
            if (data.getType().equals("null")) {
                holder.rootLayout.setVisibility(View.GONE);
            }
            holder.title.setText(data.getName());
            if (data.getType().equals("Instagram")) {
                holder.icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_instagram));
            }
            if (data.getType().equals("Facebook")) {
                holder.icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_facebook));
            }
            if (data.getType().equals("Twitter")) {
                holder.icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_twitter));
            }
            if (data.getType().equals("Website")) {
                holder.icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_web));
            }
            holder.root.setOnClickListener(v-> {
                if (data.getValue() != null) {
                    Uri uri = Uri.parse(data.getValue());
                    Intent i = new Intent(Intent.ACTION_VIEW, uri);
                    mContext.startActivity(i);
                } else {
                    String instagram = "https://www.instagram.com/";
                    String twitter = "https://www.twitter.com/";
                    String facebook = "https://www.facebook.com/";
                    String website = "http://www.";
                    String url = "";
                    if (data.getType().equals("Instagram")) {
                        url = instagram + data.getName();
                    }
                    if (data.getType().equals("Facebook")) {
                        url = facebook + data.getName();

                    }
                    if (data.getType().equals("Twitter")) {
                        url = twitter + data.getName();

                    }
                    if (data.getType().equals("Website")) {
                        url = website + data.getName();

                    }
                    Uri uri = Uri.parse(url);
                    Intent i = new Intent(Intent.ACTION_VIEW, uri);
                    mContext.startActivity(i);
                }
            });
        }
    }
    private void setRequestMovies(){
        View customLayout = LayoutInflater.from(mContext).inflate(R.layout.edit_text, null);
        final TextInputEditText editText = customLayout.findViewById(R.id.edit_text);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialog).setView(customLayout)
                .setPositiveButton("Send", (dialog, which) -> {
                    String title = Objects.requireNonNull(editText.getText()).toString();
                    String devices = Build.MODEL;
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                    Date date = new Date();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference getReference = database.getReference();
                    BaseUtils.showMessage(mContext, "Request sent", Toast.LENGTH_SHORT);
                    getReference.child("Request").child(dateFormat.format(date).replace("/", "-")).push().setValue(new Request(title, devices))
                            .addOnSuccessListener(aVoid -> new Handler().postDelayed(()-> sendNotification("Request Movies", title), 500));
                }).setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show().getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
    }
    private void sendNotification(String title, String message){
        TOPIC = "/topics/superUSER";
        NOTIFICATION_TITLE = title;
        NOTIFICATION_MESSAGE = message;
        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {
            notificationBody.put("title", NOTIFICATION_TITLE);
            notificationBody.put("message", NOTIFICATION_MESSAGE);

            notification.put("to", TOPIC);
            notification.put("data", notificationBody);
        } catch (JSONException e) {
            Log.e("ServerAdapter", "sendNotification: " + e.getMessage() );
        }
        Notification(notification);
    }
    private void Notification(JSONObject notification) {
        JsonObjectRequest request = new JsonObjectRequest(FCM_API, notification, response -> {
            Log.i("ServerAdapter", "onResponse: " + response.toString());
            NOTIFICATION_TITLE = "";
            NOTIFICATION_MESSAGE = "";
        }, error -> BaseUtils.showMessage(mContext, error.getMessage(), Toast.LENGTH_SHORT)){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }


    @Override
    public int getItemCount() {
        if (isAccount) {
            return accounts.size();
        } else return socialMediaList.size();
    }

    static class SimpleViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout root;
        private CardView rootLayout;
        private TextView title;
        private ImageView icon;
        public SimpleViewHolder(@NonNull View itemView) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.root_account);
            root = itemView.findViewById(R.id.test);
            title = itemView.findViewById(R.id.title);
            icon = itemView.findViewById(R.id.icon);
        }
    }
}