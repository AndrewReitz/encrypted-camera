package com.andrewreitz.encryptedcamera.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.andrewreitz.encryptedcamera.R;

import org.jetbrains.annotations.NotNull;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class FirstRunActivity extends Activity {

    public static void navigateTo(@NotNull Context context) {
        Intent intent = new Intent(context, FirstRunActivity.class);
        context.startActivity(intent);
    }

    @InjectView(R.id.first_run_web_links) TextView linksTextView;

    @OnClick(R.id.first_run_got_it) void gotIt() {
        finish();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run);
        ButterKnife.inject(this);

        // Buried in an Sample from google
        // http://stackoverflow.com/questions/2734270/how-do-i-make-links-in-a-textview-clickable/2746708#2746708
        linksTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.first_run, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
