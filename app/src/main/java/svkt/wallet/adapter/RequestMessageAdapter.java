package svkt.wallet.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import svkt.wallet.R;
import svkt.wallet.models.Message;

/**
 * Created by Hanumaan on 11/4/2017.
 */

public class RequestMessageAdapter extends RecyclerView.Adapter<RequestMessageAdapter.ViewHolder>{

    private Activity activity;
    private ArrayList<Message> messageList;

    public RequestMessageAdapter(Activity activity, ArrayList<Message> messageList){
        this.activity = activity;
        this.messageList = messageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(activity).inflate(R.layout.list_item_message,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if(message.type.equals("sent")) {
            holder.messageView.setText(message.message);
            holder.responseTextView.setVisibility(View.GONE);
        }
        else if(message.type.equals("received")){
            holder.responseTextView.setText(message.message);
            holder.messageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView messageView;
        TextView responseTextView;
        ViewHolder(View itemView) {
            super(itemView);
            messageView = itemView.findViewById(R.id.messageTextView);
            responseTextView = itemView.findViewById(R.id.responseTextView);
        }
    }
}
