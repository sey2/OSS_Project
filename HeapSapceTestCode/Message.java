import java.util.Date;

public class Message implements IMessage {

    String id, text;
    Date createdAt;
    Author author;

    public Message(String id, String text, Date createdAt, Author author){
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
        this.author = author;
    }

    public Message(){};


    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Author getUser() {
        return author;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }
}