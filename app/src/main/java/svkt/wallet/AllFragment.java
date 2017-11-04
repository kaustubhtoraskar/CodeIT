package svkt.wallet;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import svkt.wallet.adapter.TransactionListAdapter;
import svkt.wallet.models.Transaction;

public class AllFragment extends Fragment {

    private static final String TAG = "AllFragment";
    RecyclerView allTransactions;
    DatabaseReference dbRef ;
    FirebaseUser user;
    ArrayList<Transaction> transactionList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allTransactions = view.findViewById(R.id.allTransactions);

        transactionList = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        allTransactions.setLayoutManager(manager);
        allTransactions.setAdapter(new TransactionListAdapter(getActivity(),transactionList));

        updateList();
    }

    private void updateList(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference().child("transaction").child(user.getUid());
        transactionList = new ArrayList<>();

        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Transaction transaction = dataSnapshot.getValue(Transaction.class);
                Log.e(TAG,"Transaction from = " + transaction.from);
                transactionList.add(transaction);
                allTransactions.setAdapter(new TransactionListAdapter(getActivity(),transactionList));
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
