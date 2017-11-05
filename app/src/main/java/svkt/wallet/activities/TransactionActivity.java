package svkt.wallet.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import svkt.wallet.R;
import svkt.wallet.models.Transaction;
import svkt.wallet.models.User;

public class TransactionActivity extends AppCompatActivity {

    private static final String TAG = "TransactionActivity";
    private TextView totalBalanceText;
    private TextInputEditText contactNoEdit,amountEdit;
    private Button transferBtn;
    private String phoneNo, amount;
    private ProgressDialog progressDialog;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private User currentUser,destUser;
    private String hashKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        getSupportActionBar().setTitle("Transfer Funds");

        totalBalanceText = findViewById(R.id.totalBalance);
        contactNoEdit = findViewById(R.id.contactNo);
        amountEdit = findViewById(R.id.amount);
        transferBtn = findViewById(R.id.transferBtn);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String phoneNo = bundle.getString("PHONE_NO");
            String amount = bundle.getString("AMOUNT");
            contactNoEdit.setText(phoneNo.substring(1,phoneNo.length()-2));
            amountEdit.setText(amount.substring(1,amount.length()-2));
        }
        getSelfUser();

        transferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNo = contactNoEdit.getText().toString();
                amount = amountEdit.getText().toString();
                if(isValid()){
                    showProgressDialog("Checking if user exists...");
                    userExists(phoneNo);
                }
            }
        });
    }

    private boolean isValid(){
        if(phoneNo.isEmpty()){
            contactNoEdit.setError("Enter existng phone No on Wallet");
            contactNoEdit.setFocusable(true);
            return false;
        }
        else if(amount.isEmpty()){
            amountEdit.setError("Please enter amount");
            amountEdit.setFocusable(true);
            return false;
        }
        else if(Float.parseFloat(amount) > currentUser.balance){
            Toast.makeText(TransactionActivity.this,"You have unsufficient balance in our account",Toast.LENGTH_SHORT).show();
            amountEdit.setFocusable(true);
            return false;
        }
        return true;
    }

    private void doTransaction(String hashKey,long destAmount){
        destAmount += Long.parseLong(amount);
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUser.balance -= Long.parseLong(amount);

        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Calendar calobj = Calendar.getInstance();
        String date = df.format(calobj.getTime());
        Transaction destTransaction = new Transaction(hashKey,fUser.getUid(),amount,date,"received");
        Transaction srcTransaction = new Transaction(hashKey,fUser.getUid(),amount,date,"paid");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        reference.child(hashKey).child("balance").setValue(destAmount);
        reference.child(fUser.getUid()).child("balance").setValue(currentUser.balance);

        DatabaseReference transRefer = FirebaseDatabase.getInstance().getReference().child("transaction");
        transRefer.child(fUser.getUid()).push().setValue(srcTransaction);
        transRefer.child(hashKey).push().setValue(destTransaction);
    }

    private void getSelfUser(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                totalBalanceText.setText(getString(R.string.Rs) + currentUser.balance);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void userExists(String phoneNo){
        Query query = FirebaseDatabase.getInstance().getReference().child("users")
                .orderByChild("contactNo").equalTo(phoneNo);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG,"Value = " + dataSnapshot.getValue());

                if(dataSnapshot.getValue() == null){
                    hideProgressDialog();
                    Toast.makeText(TransactionActivity.this,R.string.no_user,Toast.LENGTH_SHORT).show();
                }
                else{
                    hideProgressDialog();
                    HashMap map = (HashMap) dataSnapshot.getValue();
                    for ( Object key : map.keySet() ) {
                        hashKey = (String) key;
                    }
                    Log.e(TAG,"key" + map.keySet());
                    HashMap map2 = (HashMap) map.get(hashKey);
                    long destAmount = (long) map2.get("balance");
                    Log.e(TAG,"Dest user balance = " + destAmount);
                    doTransaction(hashKey,destAmount);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG,"Database error = " + databaseError);
            }
        });
    }

    public void showProgressDialog(String message) {
        progressDialog=new ProgressDialog(TransactionActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    public void hideProgressDialog()
    {
        progressDialog.dismiss();
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
                startActivity(new Intent(TransactionActivity.this,ChatActivity.class));
                break;
            }
            case R.id.action_passbook :
            {
                startActivity(new Intent(TransactionActivity.this,PassbookActivity.class));
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
