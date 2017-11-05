package svkt.wallet.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

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
            {
                startActivity(new Intent(WalletStatement.this,ChatActivity.class));
                break;
            }
            case R.id.action_passbook :
            {
                startActivity(new Intent(WalletStatement.this,PassbookActivity.class));
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
