package svkt.wallet.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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

import svkt.wallet.R;
import svkt.wallet.adapter.TransactionListAdapter;
import svkt.wallet.models.Transaction;

public class AllFragment extends Fragment {

    private static final String TAG = "AllFragment";
    private RecyclerView recyclerView;
    private DatabaseReference dbRef ;
    private FirebaseUser user;
    private ArrayList<Transaction> transactionList, searchResult;
    private TextInputEditText searchBar;
    private TransactionListAdapter transactionListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.allTransactions);
        searchBar = view.findViewById(R.id.searchBar);

        transactionList = new ArrayList<Transaction>();
        searchResult = new ArrayList<Transaction>();

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        transactionListAdapter = new TransactionListAdapter(getActivity(),transactionList);
        recyclerView.setAdapter(transactionListAdapter);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchResult.clear();
                transactionListAdapter = new TransactionListAdapter(getActivity(),transactionList);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e(TAG,"Query = " + charSequence);
                doSearch(charSequence.toString());
                transactionListAdapter = new TransactionListAdapter(getActivity(),searchResult);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                recyclerView.setAdapter(transactionListAdapter);
            }
        });

        updateList();
    }

    private void doSearch(String query){
        searchResult.clear();

        String lowerCaseQuery = query.toLowerCase();

        for(int i = 0; i < transactionList.size(); i++){
            Transaction transaction = transactionList.get(i);
            if(transaction.amount.equals(query) || transaction.amount.toLowerCase().contains(lowerCaseQuery)) {
                Log.e(TAG,"Amount = " + transaction.amount);
                searchResult.add(transaction);
            }
        }

        for(int i = 0; i < transactionList.size(); i++){
            Transaction transaction = transactionList.get(i);
            if(transaction.date.toLowerCase().equals(lowerCaseQuery) || transaction.date.toLowerCase().contains(lowerCaseQuery)) {
                searchResult.add(transaction);
            }
        }

        for(int i = 0; i < transactionList.size(); i++){
            Transaction transaction = transactionList.get(i);
            if(transaction.from.toLowerCase().equals(lowerCaseQuery) || transaction.from.toLowerCase().contains(lowerCaseQuery)) {
                searchResult.add(transaction);
            }
        }

        for(int i = 0; i < transactionList.size(); i++){
            Transaction transaction = transactionList.get(i);
            if(transaction.to.toLowerCase().equals(lowerCaseQuery) || transaction.to.toLowerCase().contains(lowerCaseQuery)) {
                searchResult.add(transaction);
            }
        }
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
                transactionListAdapter = new TransactionListAdapter(getActivity(),transactionList);
                recyclerView.setAdapter(transactionListAdapter);
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
