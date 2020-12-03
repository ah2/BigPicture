package com.ah2.BigPicture;

import android.os.AsyncTask;

public class LoadTagsTask extends AsyncTask<Void, Void, String> {

    // you may separate this or combined to caller class.
    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;

    public LoadTagsTask(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String result = Utils.readStringFromUrl("https://bigpicture2.herokuapp.com/api/v1/topics");

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}
