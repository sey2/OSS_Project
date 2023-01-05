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

    public Message(){}

    public Message(String userId, String crt_dt, String content) {
        this.userId = userId;
        // 포맷터
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");

        // 문자열 -> Date
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
        return new Date("2022-02");
    }
}