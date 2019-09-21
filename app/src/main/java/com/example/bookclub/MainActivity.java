package com.example.bookclub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getBookReviews(View view) {
        String uri = "slice-content://com.example.bookclub/MyBookSliceProvider/bookReview";
        Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    public void getBookRatings(View view) {
        String uri = "slice-content://com.example.bookclub/MyBookSliceProvider/bookRatings";
        Intent intent1 = new Intent (Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent1);
    }
}
