package svkt.wallet;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        getSupportActionBar().setTitle("@string/transferFunds");

        totalBalanceText = findViewById(R.id.totalBalance);
        contactNoEdit = findViewById(R.id.contactNo);
        amountEdit = findViewById(R.id.amount);
        transferBtn = findViewById(R.id.transferBtn);

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

    private void doTransaction(User destUser){
        Log.e(TAG,"Dest user balance = " + destUser.balance);
    }

    private void getSelfUser(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressDialog();
                currentUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }
        });
    }

    private void userExists(String phoneNo){
        Query query = FirebaseDatabase.getInstance().getReference().child("users")
                .orderByChild("contactNo").equalTo(phoneNo);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG,"Snapshot = " + dataSnapshot);
                Log.e(TAG,"Value = " + dataSnapshot.getValue());

                if(dataSnapshot.getValue() == null){
                    hideProgressDialog();
                    Toast.makeText(TransactionActivity.this,"User does not exits",Toast.LENGTH_SHORT).show();
                }
                else{
                    hideProgressDialog();
                    Log.e(TAG,"User exists");
                    destUser = dataSnapshot.getValue(User.class);
                    doTransaction(destUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG,"Database error = " + databaseError);
            }
        });
    }

    public void showProgressDialog(String message)
    {
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

}