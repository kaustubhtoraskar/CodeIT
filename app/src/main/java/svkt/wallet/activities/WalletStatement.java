package svkt.wallet.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import svkt.wallet.R;
import svkt.wallet.models.User;

public class WalletStatement extends AppCompatActivity {

    TextView totalAmount,name,cardno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_statement);
        totalAmount = findViewById(R.id.totalAmount);
        name = findViewById(R.id.name);
        cardno = findViewById(R.id.cardno);
        passParams();
    }

    public void passParams(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User current = dataSnapshot.getValue(User.class);
                totalAmount.setText(getString(R.string.Rs)+ current.balance);
                name.setText(current.name);
                cardno.setText(current.cardNo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

                startActivity(new Intent(WalletStatement.this,ChatActivity.class));
                break;

            case R.id.action_passbook :

                startActivity(new Intent(WalletStatement.this,PassbookActivity.class));
                break;

            case R.id.action_statement :

                startActivity(new Intent(WalletStatement.this,WalletStatement.class));
                break;

            case R.id.action_logout :
                signOutDialog();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void signOutDialog() {
        final AlertDialog.Builder builder=new AlertDialog.Builder(WalletStatement.this);
        builder.setMessage("Do you want to Sign Out").setTitle("Sign Out");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    if(isInternetConnected()) {
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        startActivity(new Intent(WalletStatement.this,LoginActivity.class));
                    }
                }
                catch (Exception e) {
                    Toast.makeText(WalletStatement.this,R.string.error,Toast.LENGTH_SHORT).show();
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
            Toast.makeText(WalletStatement.this,"No Internet Connectivity",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
