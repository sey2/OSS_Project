# OSS_Project 

채팅 앱 오픈소스를 분석하고 개선합니다.

---

|단위|크기|
|------|---|
|1Byte|8bit|
|1KB|1024byte|
|1MB|1024KB|
|1GB|1024MB|
|1TB|1024GB|

----

## ADD Function (MessageAdapterList.java)

```java
   public MESSAGE getMessage(int idx) throws Exception {
        if(idx >= 0)
            return (MESSAGE) items.get(idx);
        else
            throw new Exception("Wrong Index.");

    }

    public MESSAGE getMessage(String content) throws Exception{
        for(Wrapper item : items){
            MESSAGE message = (MESSAGE) item;

            if(message.equals(message)){
                return (MESSAGE) items.get(0);
            }
        }

        return null;
    }

```

---

### ADD Swipe Interface (Utility Expension)

```java
public interface SetOnClickItemListener {

    public void onDeleteClick(DialogsListAdapter.BaseDialogViewHolder holder, View view, String itemId, int getAdapterPosition);
}

```

### DialogViewHolder.class ADD method
```java (
        public void setOnItemClickListener(SetOnClickItemListener listener) {
            this.listener = listener;
        }

```

###  DialogViewHolder.class Listener MVC Pattern

```java
 deleteLayout.setOnClickListener(view -> {
                if(listener != null){
                    listener.onDeleteClick((BaseDialogViewHolder)DialogViewHolder.this, view, dialog.getId(), getAdapterPosition());
                }
            });
```

###  DefaultDialogActivity.java Attach Listener 

```java
     dialogsAdapter.setOnItemClickListener(new SetOnClickItemListener() {
            @Override
            public void onDeleteClick(DialogsListAdapter.BaseDialogViewHolder holder, View view, String itemId, int getAdapterPosition) {
                dialogsAdapter.deleteById(itemId);
            }
        });
```

---

### DialogsListAdapter - deleteById() 시간 복잡도 개선 

#### Before O(n)
```java
    public void deleteById(String id) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(id)) {
                items.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

```

<br><br>
<p align="center"> Modify DTO and related methods </p>
<p align="center"> <img width="389" src="https://user-images.githubusercontent.com/54762273/211431826-5d14b3cb-8d64-4bca-897e-68a0f9def123.png"> </p>

#### After O(logn)

```java
 public void deleteById(int id) {
        int idx = binarySerach(id, 0, items.size());
        items.remove(idx);
        notifyItemRemoved(idx);
    }


    public int binarySerach(int key, int low, int high){
        int mid;

        if(low <= high) {
            mid = (low + high) / 2;
            int cur = items.get(mid).getId();

            if(key == cur) {
                return mid;
            } else if(key < cur) {
                return binarySerach(key ,low, mid-1);
            } else {
                return binarySerach(key, mid+1, high);
            }
        }

        throw new IndexOutOfBoundsException("Invalidate Key");

    }
```

----
#### Fix to load avatar image bug

The demo app has a bug that does not load avatar images. <br>
Because avatar image use http url. <br>
Therefore, it should be specified in the manifest file as follows.

``` xml

    <application
        ...
        android:usesCleartextTraffic="true">
```
----
### Security

Information exposure through an error message

```java
e.printStackTrace(); -> System.out.println(e);
```



