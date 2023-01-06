package org.techtown.osshango.Data;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements IMessage {

    private String userId;
    private String crt_dt;
    private String content;
    private Author user;

    public Message() {}
    public Message(String userId, String crt_dt, String content) {
        this.userId = userId;
        this.crt_dt = crt_dt;
        this.content = content;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCrt_dt(String crt_dt) {
        this.crt_dt = crt_dt;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public String getCrt_dt() {
        return crt_dt;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String getId() {
        return userId;
    }

    @Override
    public String getText() {
        return content;
    }

    @Override
    public IUser getUser() {
        return user;
    }

    public void setUser(Author author){
        this.user = author;
    }

    @Override
    public Date getCreatedAt() {
        Date dt = null;    
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dt = format.parse(crt_dt);
        }catch (ParseException e) {e.printStackTrace();}
        
         
        return dt;
    }
}