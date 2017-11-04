package svkt.wallet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText nameEdit,emailEdit,contactNoEdit,passwordEdit,confirmPassEdit;
    private TextInputEditText cardNoEdit,expiryDateEdit;
    private Button registerButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReference;
    private String name,email,contactNo,password,confirmPassword;
    private String cardNo,expiryDate;

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

                }
            }
        });

    }

    private boolean isValid(){
        //write some code here
        return true;
    }
}
