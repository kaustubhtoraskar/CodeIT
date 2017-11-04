package svkt.wallet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import svkt.wallet.R;
import svkt.wallet.models.Transaction;

/**
 * Created by kaustubh on 04-11-2017.
 */

public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.ViewHolder>{

    Context context;
    ArrayList<Transaction> transactions;
    TransactionListAdapter(Context context, ArrayList<Transaction> transactions){

        this.context = context;
        this.transactions = transactions;

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_transaction,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.transactionName.setText(transactions.get(position).from);
        holder.amount.setText(transactions.get(position).amount);
        holder.transactionDate.setText(transactions.get(position).date);

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView transactionName , transactionDate ,amount ;
        public ViewHolder(View itemView) {
            super(itemView);
            transactionName = itemView.findViewById(R.id.transactionName);
            transactionDate = itemView.findViewById(R.id.transactionDate);
            amount = itemView.findViewById(R.id.amount);
        }
    }
}
