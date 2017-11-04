package svkt.wallet.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import svkt.wallet.R;
import svkt.wallet.models.User;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private TextInputEditText emailEdit,passwordEdit;
    private Button loginButton,registerButton;
    private String email,password;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEdit = findViewById(R.id.email);
        passwordEdit = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null)                // User is signed in
                {
                    Log.e(TAG, "User Sign in:" + firebaseUser.getUid());
                    databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
                    startActivity(new Intent(LoginActivity.this,ChatActivity.class));
                } else
                    Log.d(TAG, "No user");
            }
        };

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailEdit.getText().toString();
                password = passwordEdit.getText().toString();
                if(isValid()){
                    showProgressDialog();
                    signInAccount();
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
    }

    private boolean isValid(){
        if(email.isEmpty()){
            emailEdit.setError("Enter valid email");
            emailEdit.setFocusable(true);
            return false;
        }
        else if(password.isEmpty()){
                passwordEdit.setError("Passowrd must be of atleast 8 characters");
                passwordEdit.setFocusable(true);
                return false;
        }
        return true;
    }

    public void signInAccount()
    {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Log.e(TAG, "Sign In complete:" + task.isSuccessful());
                            firebaseUser = firebaseAuth.getCurrentUser();
                            databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
                            retrieveData();
                            hideProgressDialog();
                        }
                        else {
                            Log.e(TAG, "signInWithEmail", task.getException());
                            hideProgressDialog();
                            Toast.makeText(LoginActivity.this, "Invalid User",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void showProgressDialog()
    {
        progressDialog=new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading Wallet...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    public void hideProgressDialog()
    {
        progressDialog.dismiss();
    }

    public void retrieveData()
    {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                User user = dataSnapshot.getValue(User.class);
                Toast.makeText(LoginActivity.this,user.name,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}
