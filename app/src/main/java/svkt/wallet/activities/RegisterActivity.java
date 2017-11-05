package svkt.wallet.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import svkt.wallet.R;
import svkt.wallet.models.User;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private TextInputEditText nameEdit,emailEdit,contactNoEdit,passwordEdit,confirmPassEdit;
    private TextInputEditText cardNoEdit;
    private Button registerButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReference;
    private String name,email,contactNo,password,confirmPassword,day,year;
    private String cardNo,expiryDate;
    private ProgressDialog progressDialog;
    private Spinner daySpinner,yearSpinner;

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
        registerButton = findViewById(R.id.registerBtn);
        daySpinner = findViewById(R.id.daySpinner);
        yearSpinner = findViewById(R.id.yearSpinner);

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
                day = daySpinner.getSelectedItem().toString();
                year = yearSpinner.getSelectedItem().toString();
                expiryDate = day + "/" + year;

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
                            finish();
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
            nameEdit.setError(getString(R.string.name_error));
            nameEdit.setFocusable(true);
            return false;
        }
        else if(email.isEmpty() || !isEmailValid(email)){
            emailEdit.setError(getString(R.string.email_error));
            emailEdit.setFocusable(true);
            return false;
        }
        else if(contactNo.isEmpty() || contactNo.length() != 10){
            contactNoEdit.setError(getString(R.string.contact_no_error));
            contactNoEdit.setFocusable(true);
            return false;
        }
        else if(password.isEmpty() || password.length() < 8){
            passwordEdit.setError(getString(R.string.password_error));
            passwordEdit.setFocusable(true);
            return false;
        }
        else if(!confirmPassword.equals(password)){
            Toast.makeText(RegisterActivity.this,R.string.password_match_error,Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(cardNo.isEmpty() || cardNo.length() !=16){
            cardNoEdit.setError(getString(R.string.card_no_error));
            cardNoEdit.setFocusable(true);
            return false;
        }
        else if(expiryDate.isEmpty()){
            Toast.makeText(RegisterActivity.this,R.string.expiry_date_error,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isEmailValid(String email){
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
            User user = new User(name,email,contactNo,cardNo,expiryDate,5000);
            Toast.makeText(RegisterActivity.this,"You have been initially credited with Rs 5000",Toast.LENGTH_SHORT).show();
            databaseReference.child("users").child(fuser.getUid()).setValue(user);
        }
        else
            Log.d(TAG, "No user");
    }
}