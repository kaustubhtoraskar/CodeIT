package svkt.wallet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    public TransactionListAdapter(Context context, ArrayList<Transaction> transactions){
        this.context = context;
        this.transactions = transactions;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_transaction,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        Log.e("TransactionListAdapter",transaction.from);

        holder.amount.setText(context.getString(R.string.Rs) +transaction.amount);
        holder.transactionDate.setText(transaction.date);
        if(transaction.type.equals("paid")){
            holder.arrowView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_upward_orange_24dp));
            holder.transactionName.setText(transaction.toName);
        }
        else if(transaction.type.equals("received")){
            holder.arrowView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_downward_orange_24dp));
            holder.transactionName.setText(transaction.fromName);
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView transactionName , transactionDate ,amount ;
        ImageView arrowView;
        ViewHolder(View itemView) {
            super(itemView);
            transactionName = itemView.findViewById(R.id.transactionName);
            transactionDate = itemView.findViewById(R.id.transactionDate);
            amount = itemView.findViewById(R.id.amount);
            arrowView = itemView.findViewById(R.id.arrow);
        }
    }
}
