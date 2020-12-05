package com.ah2.BigPicture;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

public class LoadTagsTask extends AsyncTask<Void, Void, String> {

    WeakReference<Context> conRef;

    public LoadTagsTask(WeakReference<Context> conRef){ this.conRef = conRef; }

    @Override
    protected String doInBackground(Void... voids) {
        String result = Utils.readStringFromUrl("https://bigpicture2.herokuapp.com/api/v1/topics");

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        //delegate.processFinish(result);
        conRef.get();

    }
}
