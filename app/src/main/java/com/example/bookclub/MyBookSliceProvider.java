package com.example.bookclub;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceProvider;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.ListBuilder.RowBuilder;
import androidx.slice.builders.SliceAction;

import static android.app.slice.Slice.EXTRA_TOGGLE_STATE;

public class MyBookSliceProvider extends SliceProvider {
    //variable to hold the value of context.
    Context mcontext;
    /**
     * Instantiate any required objects. Return true if the provider was successfully created,
     * false otherwise.
     */
    @Override
    public boolean onCreateSliceProvider() {
        mcontext = getContext();
        if (mcontext == null){
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Converts URL to content URI (i.e. content://com.example.bookclub...)
     */
    @Override
    @NonNull
    public Uri onMapIntentToUri(@Nullable Intent intent) {
        // Note: implementing this is only required if you plan on catching URL requests.
        // This is an example solution.
        Uri.Builder uriBuilder = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT);
        if (intent == null) return uriBuilder.build();
        Uri data = intent.getData();
        if (data != null && data.getPath() != null) {
            String path = data.getPath().replace("/", "");
            uriBuilder = uriBuilder.path(path);
        }
        Context context = getContext();
        if (context != null) {
            uriBuilder = uriBuilder.authority(context.getPackageName());
        }
        return uriBuilder.build();
    }

    /**
     * Construct the Slice and bind data if available.
     */
    public Slice onBindSlice(Uri sliceUri) {

        if (getContext() == null ) {
            return null;
        }
//we build the slices dynamically depending on the uri path that gets passed to the slice provider
switch (sliceUri.getPath()){
    case "/MyBookSliceProvider/bookReview" : //slice to display the reviews

        return createReviewSlice(sliceUri); // create a slice of reviews


    case "/MyBookSliceProvider/bookRatings" : //slice to display the ratings

        return createRatingSlice(sliceUri); //create a slice of ratings

default: //default creates a slice and displays that there is no slice for the path
    ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY);
    listBuilder.addRow(new RowBuilder().setTitle("No URI found").setPrimaryAction(createActivityAction()));
   return listBuilder.build();
}

    }
    //***********************************Review slice***********************************
    /* this method creates a slice for reviews*/
private Slice createReviewSlice(Uri sliceUri){

    //Increase action associated with the slice
    SliceAction increaseAction = increaseReviews();

    //decrease action associated with the slice
    SliceAction decreaseAction = decreaseReviews();

    //The slice is built here with three dummy rows and two images with actions
    ListBuilder reviewlistBuilder = new ListBuilder(mcontext, sliceUri, ListBuilder.INFINITY);
    SliceAction activityAction = createActivityAction();
    reviewlistBuilder.setHeader(new ListBuilder.HeaderBuilder().setTitle("Book Reviews")
            .setSubtitle("Review Count :" + MyBroadcastReceiver.currentValue)
            .setTitle("Temperature :" + MyBroadcastReceiver.temperature)
            .setSummary("Temperature :" + MyBroadcastReceiver.temperature));
    reviewlistBuilder.addRow(new RowBuilder().setTitle("Book one Reviews").setPrimaryAction(activityAction));
    reviewlistBuilder.addRow(new RowBuilder().setTitle("Book two Reviews"));
    reviewlistBuilder.addRow(new RowBuilder().setTitle("Book three Reviews"))
            .addAction(increaseAction)
            .addAction(decreaseAction);
    return reviewlistBuilder.build();
}

    //slice action associated with upward arrow
private SliceAction increaseReviews(){

    //create a pending intend to trigger the broadcast receiver and send the count value to be updated.
    // we are getting connecting to BR because some one will dump the changes to it...web services amy pump the data and broadcast reciever will get the latest data...
   // PendingIntent.getService()//BR--> mode of transport
                                  //service--> it is a daemon it can have uri notification or broadcast reciever...
    //broadcast every change in weather and then catch my in my broadcast receiver and display in your app.--version 1
    //weather app third line from top....chigago,usa..it will give its weather...have loop every 5 sec..show the weather of 5 cities...version 2...
    //getting connected to uri--version 3....
    PendingIntent increaseReviewIntent = PendingIntent.getBroadcast(mcontext,
            0,
            new Intent(mcontext, MyBroadcastReceiver.class)
                    .setAction(MyBroadcastReceiver.INCREMENT_COUNTER_ACTION)
                    .putExtra(MyBroadcastReceiver.EXTRA_VALUE_KEY,
            MyBroadcastReceiver.currentValue + 1), PendingIntent.FLAG_UPDATE_CURRENT);

//create a slice action which is connected to the upward arrow image
   return SliceAction.create( increaseReviewIntent,
            IconCompat.createWithResource(mcontext, R.drawable.upward_arrow),
            ListBuilder.ICON_IMAGE, "Increase reviews");
}
//slice action associated with downward arrow
    private SliceAction decreaseReviews(){

        //create a pending intend to trigger the broadcast receiver and send the count value to be updated
        PendingIntent decreaseReviewIntent = PendingIntent.getBroadcast(mcontext,
                0,
                new Intent(mcontext, MyBroadcastReceiver.class)
                        .setAction(MyBroadcastReceiver.DECREMENT_COUNTER_ACTION)
                        .putExtra(MyBroadcastReceiver.EXTRA_VALUE_KEY,
                                MyBroadcastReceiver.currentValue - 1), PendingIntent.FLAG_UPDATE_CURRENT);

        //create a slice action which is connected to the upward arrow image
        return SliceAction.create( decreaseReviewIntent,
                IconCompat.createWithResource(mcontext, R.drawable.downward_arrow),
                ListBuilder.ICON_IMAGE, "Decrease reviews");
    }
//****************review slice ends*****************************

    //*****************************Rating slice****************************************
    //this method creates a rating slice
    private Slice createRatingSlice(Uri sliceUri){



   PendingIntent RatingChangedIntent = PendingIntent.getBroadcast(mcontext,0,
           new Intent(mcontext,MyBroadcastReceiver.class)
   .setAction(MyBroadcastReceiver.RATING_INCREASED)

   .putExtra(MyBroadcastReceiver.RATING_MESSAGE,"Rating Changed"),PendingIntent.FLAG_UPDATE_CURRENT);


        ListBuilder ratinglistBuilder = new ListBuilder(mcontext, sliceUri, ListBuilder.INFINITY);
        SliceAction activityAction2 = createActivityAction();
        ratinglistBuilder.setHeader(new ListBuilder.HeaderBuilder().setTitle("Book Ratings")
                .setSubtitle("want to rate?").setSummary("Rated by most users"))
                .addInputRange(new ListBuilder.InputRangeBuilder().setTitle("Rating range").setInputAction(RatingChangedIntent)
                        .setMax(5)
                        .setMin(0));
        ratinglistBuilder.addRow(new RowBuilder().setTitle("Book one Rating"));
        ratinglistBuilder.addRow(new RowBuilder().setTitle("Book two Rating"));
        ratinglistBuilder.addRow(new RowBuilder().setTitle("Book three Rating").setPrimaryAction(activityAction2));

        return ratinglistBuilder.build();
    }

    private SliceAction createActivityAction() {
       // return null;
        //Instead of returning null, you should create a SliceAction. Here is an example:

        return SliceAction.create(
                //calling activity
            PendingIntent.getActivity(
                getContext(), 0, new Intent(getContext(), MainActivity.class), 0
            ),
            IconCompat.createWithResource(mcontext, R.drawable.ic_launcher_foreground),
            ListBuilder.ICON_IMAGE,
            "Open App"
        );

    }

    /**
     * Slice has been pinned to external process. Subscribe to data source if necessary.
     */
    @Override
    public void onSlicePinned(Uri sliceUri) {
        // When data is received, call context.contentResolver.notifyChange(sliceUri, null) to
        // trigger MyBookSliceProvider#onBindSlice(Uri) again.

       // mcontext.getContentResolver().query(sliceUri,values);

     //   mcontext.getContentResolver().notifyChange(sliceUri, null);
//        SliceBroadcastRelay.registerReceiver (getContext(), sliceUri, SliceRelayReceiver.class,
//                intentFilter);
    }

    /**
     * Unsubscribe from data source if necessary.
     */
    @Override
    public void onSliceUnpinned(Uri sliceUri) {
        // Remove any observers if necessary to avoid memory leaks.
    }


}

