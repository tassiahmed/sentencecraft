package sentencecraft.sentencecraft;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zqiu on 4/27/16
 * Used to asynchronously get incomplete sentences for the ContinueSentence Activity
 */

public class ContinueSentenceGetTask extends DownloadInfoTask {

    Handler mainUIHandler;
    private int tagsId;
    private String key = "";

    public ContinueSentenceGetTask(View rootView, Context context, int editId, int tagsId, Handler mainUIHandler) {
        super(rootView, context, editId);
        this.tagsId = tagsId;
        this.mainUIHandler = mainUIHandler;
    }

    protected void onPostExecute(String result) {
        String operationName = "DownloadTask";
        if (getResponseCode() == 200) {
            ArrayList<String> data = interpretContinue(result);
            TextView sentence = (TextView) rootView.findViewById(editId);
            sentence.setText(context.getString(R.string.continue_lexeme_part, data.get(0)));
            TextView tags = (TextView) rootView.findViewById(tagsId);
            tags.setText(context.getString(R.string.continue_tags_part, data.get(1)));
        } else {
            Snackbar mySnackBar;
            mySnackBar = Snackbar.make(rootView, context.getString(R.string.error_operation_not_complete, operationName), Snackbar.LENGTH_LONG);
            mySnackBar.show();
            mySnackBar.setText(result);
            mySnackBar.show();
        }
        Message msg = Message.obtain();
        msg.obj= key;
        mainUIHandler.sendMessage(msg);
    }

    private ArrayList<String> interpretContinue(String data) {
        ArrayList<String> toReturn = new ArrayList<>();
        String userData = "";
        String tagData = "";
        try {
            JSONObject reader = new JSONObject(data);
            key = reader.getString("key");
            JSONObject lexemeCollection = reader.getJSONObject("lexemecollection");
            JSONArray lexemes = lexemeCollection.getJSONArray("lexemes");
            for (int i = 0; i < lexemes.length(); ++i) {
                userData += lexemes.getString(i) + " ";
            }
            try {
                JSONArray tags = lexemeCollection.getJSONArray("tags");
                for (int i = 0; i < tags.length(); ++i) {
                    if (!tagData.equals("")) {
                        tagData += ",";
                    }
                    tagData += tags.getString(i);
                }
            } catch (JSONException e) {
                tagData = "none";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        toReturn.add(userData);
        toReturn.add(tagData);
        return toReturn;
    }

    @Override
    protected String doInBackground(String... urls) {
        if(urls.length == 2){
            //assuming getting incomplete sentence
            key = "";
            return super.doInBackground(urls);
        }else{
            return "arguments not recognized";
        }
    }
}