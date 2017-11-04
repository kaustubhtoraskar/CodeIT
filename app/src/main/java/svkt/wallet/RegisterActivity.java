package svkt.wallet;

import android.app.ProgressDialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import svkt.wallet.models.User;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private TextInputEditText nameEdit,emailEdit,contactNoEdit,passwordEdit,confirmPassEdit;
    private TextInputEditText cardNoEdit,expiryDateEdit;
    private Button registerButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReference;
    private String name,email,contactNo,password,confirmPassword;
    private String cardNo,expiryDate;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Register");

        nameEdit = findViewById(R.id.name);
        emailEdit = findViewById(R.id.email);
        contactNoEdit = findViewById(R.id.contactno);
        passwordEdit = findViewById(R.id.password);
        confirmPassEdit = findViewById(R.id.confirmPassword);
        cardNoEdit = findViewById(R.id.cardno);
        expiryDateEdit = findViewById(R.id.expiryDate);
        registerButton = findViewById(R.id.registerBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            }
        };

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = nameEdit.getText().toString();
                email = emailEdit.getText().toString();
                contactNo = contactNoEdit.getText().toString();
                password = passwordEdit.getText().toString();
                confirmPassword = confirmPassEdit.getText().toString();
                cardNo = cardNoEdit.getText().toString();
                expiryDate = expiryDateEdit.getText().toString();

                if(isValid()){
                    showProgressDialog();
                    createAccount();
                }
            }
        });

    }

    public void createAccount()
    {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if(task.isSuccessful())
                        {
                            Toast.makeText(RegisterActivity.this,"Registered Successfully",Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                            uploadData();
                        }
                        else if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }
                    }
                });
    }

    private boolean isValid(){
        //write some code here
        if(name.isEmpty()){
            nameEdit.setError("Enter Name");
            nameEdit.setFocusable(true);
            return false;
        }
        return true;
    }

    public void showProgressDialog()
    {
        progressDialog=new ProgressDialog(RegisterActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Registering to Wallet...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    public void hideProgressDialog()
    {
        progressDialog.dismiss();
    }

    public void uploadData() {
        FirebaseUser fuser = firebaseAuth.getCurrentUser();
        if (fuser != null){
            Log.e(TAG, "UserId = " + fuser.getUid());
            User user = new User(name,email,contactNo,cardNo,expiryDate,0);
            databaseReference.child("users").child(fuser.getUid()).setValue(user);
        }
        else
            Log.d(TAG, "No user");
    }
}