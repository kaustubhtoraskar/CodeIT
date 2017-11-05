package svkt.wallet.models;

/**
 * Created by Hanumaan on 11/5/2017.
 */

public class Message {
    public String type;
    public String message;

    public Message(){}

    public Message(String type,String message){
        this.message = message;
        this.type = type;
    }
}
