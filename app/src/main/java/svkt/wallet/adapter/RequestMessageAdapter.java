package svkt.wallet.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import svkt.wallet.R;

/**
 * Created by Hanumaan on 11/4/2017.
 */

public class RequestMessageAdapter extends RecyclerView.Adapter<RequestMessageAdapter.ViewHolder>{

    private Activity activity;
    private ArrayList<String> messageList;

    public RequestMessageAdapter(Activity activity, ArrayList<String> messageList){
        this.activity = activity;
        this.messageList = messageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(activity).inflate(R.layout.list_item_message,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.messageView.setText(messageList.get(position));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView messageView;
        public ViewHolder(View itemView) {
            super(itemView);
//            messageView = itemView.findViewById(R.id.messageTextView);
        }
    }
}
