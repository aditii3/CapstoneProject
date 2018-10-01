package com.example.android.myreddits.remote;

import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by aditi on 9/29/2018.
 */

public class PostService extends JobService {
    private AsyncTask<Void, Void, Void> postTask;

    @Override
    public boolean onStartJob(final JobParameters params) {
        postTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                RemotePost.refreshAllPosts(getApplicationContext());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(params, false);
            }
        }.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (postTask != null) {
            postTask.cancel(true);
        }
        return true;
    }
}
