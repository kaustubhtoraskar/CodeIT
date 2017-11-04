package svkt.wallet;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class ChatActivity extends AppCompatActivity implements AIListener{

    private static final String TAG = "ChatActivity";
    private static final String CLIENT_ACCESS_TOKEN = "03ceb8109b9a4e99b6eda56dec586c40";
    private Button listenButton;
    private TextView resultTextView;
    private TextInputEditText requestEdit;
    private AIService aiService;
    private AIConfiguration configuration;
    private AIDataService dataService;
    private AIRequest aiRequest;
    private String request;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        listenButton = findViewById(R.id.listenButton);
        resultTextView = findViewById(R.id.resultTextView);
        requestEdit = findViewById(R.id.requestEdit);

        configuration = new AIConfiguration(CLIENT_ACCESS_TOKEN,AIConfiguration.SupportedLanguages.English, AIConfiguration.RecognitionEngine.System);
        dataService = new AIDataService(configuration);
        aiRequest = new AIRequest();

        aiService = AIService.getService(ChatActivity.this,configuration);
        aiService.setListener(ChatActivity.this);

        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                aiService.startListening();
                request = requestEdit.getText().toString();
                aiRequest.setQuery(request);
                new RequestAPIAI().execute(aiRequest);
            }
        });
    }

    @Override
    public void onResult(AIResponse response) {
        Log.e(TAG,"OnResult = " + response);

        Result result = response.getResult();

        /*String parameterString = "";
        if(result.getParameters()!=null && result.getParameters().isEmpty()){
            for(Map.Entry<String, JsonElement> element : result.getParameters().entrySet()){
                parameterString += "(" + element.getKey() + ", " + element.getValue() + ")";
            }
        }*/

        resultTextView.setText("Query: " + result.getResolvedQuery() +
                "\nAction: " + result.getAction() /*+
                "\nParameter: " + parameterString*/);
    }

    @Override
    public void onError(AIError error) {
        Log.e(TAG,"OnError = " + error);
        resultTextView.setText(error.toString());
    }

    @Override
    public void onAudioLevel(float level) {
        Log.e(TAG,"OnAudioLevel = " + level);
    }

    @Override
    public void onListeningStarted() {
        Log.e(TAG,"OnListeningStarted");
    }

    @Override
    public void onListeningCanceled() {
        Log.e(TAG,"OnListeningCancelled");
    }

    @Override
    public void onListeningFinished() {
        Log.e(TAG,"OnListeningFinished");
    }

    public void showProgressDialog()
    {
        progressDialog=new ProgressDialog(ChatActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading Wallet...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    public void hideProgressDialog()
    {
        progressDialog.dismiss();
    }


    private class RequestAPIAI extends AsyncTask<AIRequest,Void,AIResponse>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected AIResponse doInBackground(AIRequest... aiRequests) {
            AIRequest request = aiRequests[0];
            try{
                return dataService.request(request);
            }
            catch (AIServiceException ignored){
            }
            return null;
        }

        @Override
        protected void onPostExecute(AIResponse aiResponse) {
            hideProgressDialog();
            if(aiResponse != null){
                Log.e(TAG,"Task Response = " + aiResponse);
                onResult(aiResponse);
            }
        }
    }
}
