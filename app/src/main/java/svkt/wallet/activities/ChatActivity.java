package svkt.wallet.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
import svkt.wallet.models.Message;
import svkt.wallet.models.Transaction;
import svkt.wallet.models.User;

public class ChatActivity extends AppCompatActivity implements AIListener{

    private static final String TAG = "ChatActivity";
    private static final String CLIENT_ACCESS_TOKEN = "03ceb8109b9a4e99b6eda56dec586c40";
    private ImageButton listenButton,sendButton;
    private TextInputEditText requestEdit;
    private AIService aiService;
    private AIConfiguration configuration;
    private AIDataService dataService;
    private AIRequest aiRequest;
    private String request;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private ArrayList<Message> messageList;
    private boolean isListenQuery = false;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        listenButton = findViewById(R.id.listenButton);
        sendButton = findViewById(R.id.sendButton);
        requestEdit = findViewById(R.id.requestEdit);
        recyclerView = findViewById(R.id.recyclerView);

        messageList = new ArrayList<Message>();
        LinearLayoutManager manager = new LinearLayoutManager(ChatActivity.this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(new RequestMessageAdapter(ChatActivity.this,messageList));

        configuration = new AIConfiguration(CLIENT_ACCESS_TOKEN,AIConfiguration.SupportedLanguages.English, AIConfiguration.RecognitionEngine.System);
        dataService = new AIDataService(configuration);
        aiRequest = new AIRequest();

        aiService = AIService.getService(ChatActivity.this,configuration);
        aiService.setListener(ChatActivity.this);

        requestEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                listenButton.setVisibility(View.VISIBLE);
                sendButton.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 0) {
                    listenButton.setVisibility(View.GONE);
                    sendButton.setVisibility(View.VISIBLE);
                }
                else{
                    listenButton.setVisibility(View.VISIBLE);
                    sendButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request = requestEdit.getText().toString();
                if(!request.isEmpty()){
                    Message message = new Message("sent", request);
                    messageList.add(message);
                    recyclerView.setAdapter(new RequestMessageAdapter(ChatActivity.this, messageList));
                    requestEdit.setText("");
                    aiRequest.setQuery(request);
                    new RequestAPIAI().execute(aiRequest);
                }
            }
        });

        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aiService.startListening();
            }
        });
    }

    @Override
    public void onResult(AIResponse response) {
        Result result = response.getResult();
        Log.e(TAG,"OnResult = " + response + " Result parameter = " + result.getParameters());
        if(isListenQuery){
            Message message = new Message("sent",result.getResolvedQuery());
            messageList.add(message);
            recyclerView.setAdapter(new RequestMessageAdapter(ChatActivity.this,messageList));
            isListenQuery = false;
        }

        switch (result.getAction()){
            case "checkBalance":
                showProgressDialog("Retriving your balance...");
                showBalance();
                break;
            case "transferFunds":
                doTranscation();
                Message message = new Message("received",result.getFulfillment().getSpeech());
                messageList.add(message);
                recyclerView.setAdapter(new RequestMessageAdapter(ChatActivity.this,messageList));
                break;
            case "passParams":
                HashMap<String, JsonElement> map = result.getParameters();
                ArrayList<String> params = new ArrayList<String>();
                for(String key : map.keySet()){
                    Log.e(TAG,"Key = " + key);
                    params.add(String.valueOf(map.get(key)));
                }
                Intent intent = new Intent(ChatActivity.this,TransactionActivity.class);
                if(params.size() == 2){
                    intent.putExtra("PHONE_NO",params.get(0));
                    intent.putExtra("AMOUNT",params.get(1));
                }
                startActivity(intent);
                Message message1 = new Message("received",result.getFulfillment().getSpeech());
                messageList.add(message1);
                recyclerView.setAdapter(new RequestMessageAdapter(ChatActivity.this,messageList));
                break;

            case "getWalletStatement":
                startActivity(new Intent(ChatActivity.this , WalletStatement.class));
                Message message2 = new Message("received",result.getFulfillment().getSpeech());
                messageList.add(message2);
                recyclerView.setAdapter(new RequestMessageAdapter(ChatActivity.this,messageList));
                break;
            case "passToPassbook":
                HashMap<String, JsonElement> passMap = result.getParameters();
                Log.e(TAG,"passMap = " + passMap);
                String query = "";
                for(String key : passMap.keySet()){
                    Log.e(TAG,"Key = " + key);
                    query = String.valueOf(passMap.get(key));
                }
                showPassBook(query);
                Message message3 = new Message("received",result.getFulfillment().getSpeech());
                messageList.add(message3);
                recyclerView.setAdapter(new RequestMessageAdapter(ChatActivity.this,messageList));
                break;

            default:
                Log.e(TAG,"Response = " + result.getFulfillment().getSpeech());
                Message message4 = new Message("received",result.getFulfillment().getSpeech());
                messageList.add(message4);
                recyclerView.setAdapter(new RequestMessageAdapter(ChatActivity.this,messageList));
                break;
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finishAffinity();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this,R.string.press_back_to_exit, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private void showBalance(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressDialog();
                User user = dataSnapshot.getValue(User.class);
                Message message = new Message("received","Your current balance is " + user.balance);
                messageList.add(message);
                recyclerView.setAdapter(new RequestMessageAdapter(ChatActivity.this,messageList));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }
        });
    }

    private void doTranscation(){
        //recyclerView.setAdapter(new RequestMessageAdapter(ChatActivity.this,messageList));
        startActivity(new Intent(ChatActivity.this,TransactionActivity.class));
    }

    private void showPassBook(String request){
        FirebaseUser cureUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference().child("transaction").child(cureUser.getUid())
                .orderByChild("date").equalTo(request);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG,"DataSnapShot = " + dataSnapshot);
                if(dataSnapshot.getValue() != null) {
                    Transaction transaction = dataSnapshot.getValue(Transaction.class);
                    Message message4 = new Message("received", transaction.fromName + " " + transaction.amount);
                    messageList.add(message4);
                    recyclerView.setAdapter(new RequestMessageAdapter(ChatActivity.this, messageList));
                }
                else{
                    startActivity(new Intent(ChatActivity.this,PassbookActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        showProgressDialog("Listening started...");
        isListenQuery = true;
    }

    @Override
    public void onListeningCanceled() {
        Log.e(TAG,"OnListeningCancelled");
    }

    @Override
    public void onListeningFinished() {
        Log.e(TAG,"OnListeningFinished");
        hideProgressDialog();
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

                startActivity(new Intent(ChatActivity.this,ChatActivity.class));
                break;

            case R.id.action_passbook :

                startActivity(new Intent(ChatActivity.this,PassbookActivity.class));
                break;

            case R.id.action_statement :

                startActivity(new Intent(ChatActivity.this,WalletStatement.class));
                break;

            case R.id.action_logout :
                signOutDialog();
                break;

            case R.id.action_transfer :
                startActivity(new Intent(ChatActivity.this,TransactionActivity.class));
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void signOutDialog() {
        final AlertDialog.Builder builder=new AlertDialog.Builder(ChatActivity.this);
        builder.setMessage("Do you want to Sign Out").setTitle("Sign Out");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    if(isInternetConnected()) {
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        startActivity(new Intent(ChatActivity.this,LoginActivity.class));
                    }
                }
                catch (Exception e) {
                    Toast.makeText(ChatActivity.this,R.string.error,Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        AlertDialog dialog=builder.create();
        dialog.show();
    }

    private boolean isInternetConnected()
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo==null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
            Toast.makeText(ChatActivity.this,"No Internet Connectivity",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
