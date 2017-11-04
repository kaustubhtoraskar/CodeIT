package svkt.wallet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonElement;

import java.util.Map;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class ChatActivity extends AppCompatActivity implements AIListener{

    private static final String TAG = "ChatActivity";
    private static final String CLIENT_ACCESS_TOKEN = "03ceb8109b9a4e99b6eda56dec586c40";
    private Button listenButton;
    private TextView resultTextView;
    private AIService aiService;
    private AIConfiguration configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        listenButton = (Button) findViewById(R.id.listenButton);
        resultTextView = (TextView) findViewById(R.id.resultTextView);

        configuration = new AIConfiguration(CLIENT_ACCESS_TOKEN,AIConfiguration.SupportedLanguages.English, AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(ChatActivity.this,configuration);
        aiService.setListener(ChatActivity.this);

        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aiService.startListening();
            }
        });
    }

    @Override
    public void onResult(AIResponse response) {
        Log.e(TAG,"OnResult = " + response);

        Result result = response.getResult();

        String parameterString = "";
        if(result.getParameters()!=null && result.getParameters().isEmpty()){
            for(Map.Entry<String, JsonElement> element : result.getParameters().entrySet()){
                parameterString += "(" + element.getKey() + ", " + element.getValue() + ")";
            }
        }

        resultTextView.setText("Query: " + result.getResolvedQuery() +
                "\nAction: " + result.getAction() +
                "\nParameter: " + parameterString);
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
}
