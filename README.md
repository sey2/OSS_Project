# OSS_Project 

채팅 앱 오픈소스를 개선하고 개선사항을 기록합니다.

---

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
---
### Send Album image
Before the improvement, the source could only send the image url uploaded to the server, <br>
but after the improvement, the image can be selected from the album and sent immediately.

In addition, the cropper function has been added to edit the selected image in the album.

#### Change ImageLoader interface 

```java
void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload, @Nullable Bitmap bitmap);
```

<br>

####  Change Image class and Add getImageBitmap() Method in Message class

```java
  public Bitmap getImageBitmap() {return image == null ? null : image.bitmap;}

   public static class Image {

        private String url;
        private Bitmap bitmap;

        public Image(String url) {
            this.url = url;
        }
        public Image(Bitmap bitmap) {this.bitmap = bitmap; }
    }

```

<br>

#### Change getContentViewType Method in MessageHoder.class

```java
    private short getContentViewType(IMessage message) {
        if (message instanceof MessageContentType.Image
                && ((MessageContentType.Image) message).getImageUrl() != null
                || ((MessageContentType.Image) message).getImageBitmap() != null) {
            return VIEW_TYPE_IMAGE_MESSAGE;
        }

```

<br>

#### Change to ImageLoader Listener in Activity

```java
        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload, @Nullable Bitmap bitmap) {
                if(url == null)
                    imageView.setImageBitmap(bitmap);
                else
                     Picasso.get().load(url).into(imageView);

            }
        };
```

<br>

#### Change to onBind() Method in MessageHolders.class

``` java
  @Override
        public void onBind(MESSAGE message) {
            super.onBind(message);
            if (image != null && imageLoader != null) {
                imageLoader.loadImage(image, message.getImageUrl(), getPayloadForImageLoader(message), message.getImageBitmap());
            }

            if (imageOverlay != null) {
                imageOverlay.setSelected(isSelected());
            }
        }
```

<br>

####  <a href =" https://github.com/sey2/OSS_Project/commit/a333013f221fee56a125e91eb3d05acdad0e1ab2"> ETC Commit Log  </a>
