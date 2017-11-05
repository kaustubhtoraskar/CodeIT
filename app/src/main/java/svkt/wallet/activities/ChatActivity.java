package svkt.wallet.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import svkt.wallet.R;
import svkt.wallet.adapter.RequestMessageAdapter;
import svkt.wallet.models.User;

public class ChatActivity extends AppCompatActivity implements AIListener{

    private static final String TAG = "ChatActivity";
    private static final String CLIENT_ACCESS_TOKEN = "03ceb8109b9a4e99b6eda56dec586c40";
    private ImageButton listenButton;
    private TextInputEditText requestEdit;
    private AIService aiService;
    private AIConfiguration configuration;
    private AIDataService dataService;
    private AIRequest aiRequest;
    private String request;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private ArrayList<String> messageList;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        listenButton = findViewById(R.id.listenButton);
        requestEdit = findViewById(R.id.requestEdit);
        recyclerView = findViewById(R.id.recyclerView);

        messageList = new ArrayList<String>();
        LinearLayoutManager manager = new LinearLayoutManager(ChatActivity.this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(new RequestMessageAdapter(ChatActivity.this,messageList));

        configuration = new AIConfiguration(CLIENT_ACCESS_TOKEN,AIConfiguration.SupportedLanguages.English, AIConfiguration.RecognitionEngine.System);
        dataService = new AIDataService(configuration);
        aiRequest = new AIRequest();

        aiService = AIService.getService(ChatActivity.this,configuration);
        aiService.setListener(ChatActivity.this);

        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//              aiService.startListening();
                request = requestEdit.getText().toString();
                messageList.add(request);
                recyclerView.setAdapter(new RequestMessageAdapter(ChatActivity.this,messageList));
                requestEdit.setText("");
                aiRequest.setQuery(request);
                new RequestAPIAI().execute(aiRequest);
            }
        });
    }

    @Override
    public void onResult(AIResponse response) {
        Result result = response.getResult();
        Log.e(TAG,"OnResult = " + response + " Result parameter = " + result.getParameters());

        switch (result.getAction()){
            case "checkBalance":
                showProgressDialog("Retriving your balance...");
                showBalance();
                break;
            case "transferFunds":
                doTranscation();
                //showPassBook();
                break;
            case "passParams":
                HashMap<String, JsonElement> map = result.getParameters();
                ArrayList<String> params = new ArrayList<String>();
                for(String key : map.keySet()){
                    Log.e(TAG,"Key = " + key);
                    params.add(String.valueOf(map.get(key)));
                }

                if(params.size() == 2){
                    Intent intent = new Intent(ChatActivity.this,TransactionActivity.class);
                    intent.putExtra("PHONE_NO",params.get(0));
                    intent.putExtra("AMOUNT",params.get(1));
                    startActivity(intent);
                }
                break;
        }
    }

    private void showBalance(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressDialog();
                User user = dataSnapshot.getValue(User.class);
                messageList.add("Your current balance is " + user.balance);
                recyclerView.setAdapter(new RequestMessageAdapter(ChatActivity.this,messageList));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }
        });
    }

    private void doTranscation(){
        messageList.add("Do some transcation");
        recyclerView.setAdapter(new RequestMessageAdapter(ChatActivity.this,messageList));
        startActivity(new Intent(ChatActivity.this,TransactionActivity.class));
    }

    private void showPassBook(){
        startActivity(new Intent(ChatActivity.this,PassbookActivity.class));
    }

    @Override
    public void onError(AIError error) {
        Log.e(TAG,"OnError = " + error);
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

    public void showProgressDialog(String message)
    {
        progressDialog=new ProgressDialog(ChatActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(message);
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
            showProgressDialog("Please wait for a moment...");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_passbook, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_chat:
            {
                startActivity(new Intent(ChatActivity.this,ChatActivity.class));
                break;
            }
            case R.id.action_passbook :
            {
                startActivity(new Intent(ChatActivity.this,PassbookActivity.class));
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
