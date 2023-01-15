package com.stfalcon.chatkit.sample.features.demo;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.sample.R;
import com.stfalcon.chatkit.sample.common.data.fixtures.MessagesFixtures;
import com.stfalcon.chatkit.sample.common.data.model.Message;
import com.stfalcon.chatkit.sample.utils.AppUtils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/*
 * Created by troy379 on 04.04.17.
 */
public abstract class DemoMessagesActivity extends AppCompatActivity
        implements MessagesListAdapter.SelectionListener,
        MessagesListAdapter.OnLoadMoreListener {

    private static final int TOTAL_MESSAGES_COUNT = 100;

    protected final int senderId = 0;
    protected ImageLoader imageLoader;
    protected MessagesListAdapter<Message> messagesAdapter;

    private Menu menu;
    private int selectionCount;
    private Date lastLoadedDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload, @Nullable Bitmap bitmap) {
                if(url == null) {
                    imageView.setImageBitmap(bitmap);
                    Log.d("test", bitmap.toString());
                }
                else {
                    Log.d("test", url);
                    Picasso.get().load(url).into(imageView);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        messagesAdapter.addToStart(MessagesFixtures.getTextMessage(), true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.chat_actions_menu, menu);
        onSelectionChanged(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                messagesAdapter.deleteSelectedMessages();
                break;
            case R.id.action_copy:
                messagesAdapter.copySelectedMessagesText(this, getMessageStringFormatter(), true);
                AppUtils.showToast(this, R.string.copied_message, true);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (selectionCount == 0) {
            super.onBackPressed();
        } else {
            messagesAdapter.unselectAllItems();
        }
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        Log.i("TAG", "onLoadMore: " + page + " " + totalItemsCount);
        if (totalItemsCount < TOTAL_MESSAGES_COUNT) {
            loadMessages();
        }
    }

    @Override
    public void onSelectionChanged(int count) {
        this.selectionCount = count;
        menu.findItem(R.id.action_delete).setVisible(count > 0);
        menu.findItem(R.id.action_copy).setVisible(count > 0);
    }

    protected void loadMessages() {
        //imitation of internet connection
        new Handler().postDelayed(() -> {
            ArrayList<Message> messages = MessagesFixtures.getMessages(lastLoadedDate);
            lastLoadedDate = messages.get(messages.size() - 1).getCreatedAt();
            messagesAdapter.addToEnd(messages, false);
        }, 1000);
    }

    private MessagesListAdapter.Formatter<Message> getMessageStringFormatter() {
        return message -> {
            String createdAt = new SimpleDateFormat("MMM d, EEE 'at' h:mm a", Locale.getDefault())
                    .format(message.getCreatedAt());

            String text = message.getText();
            if (text == null) text = "[attachment]";

            return String.format(Locale.getDefault(), "%s: %s (%s)",
                    message.getUser().getName(), text, createdAt);
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case 104:  // 사진을 앨범에서 선택하는 경우
                if(resultCode == this.RESULT_OK) {
                    Uri imgUri = intent.getData();
                    CropImage.activity(imgUri).setGuidelines(CropImageView.Guidelines.ON).start( this);
                }

                break;

            /* 자른 사진을 pictureImageView에 적용 */
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(intent);
                if (result != null) {
                    Uri resultUri = result.getUri();

                    ContentResolver resolver = getApplicationContext().getContentResolver();

                    try {
                        InputStream instream = resolver.openInputStream(resultUri);
                        Bitmap resultPhotoBitmap = BitmapFactory.decodeStream(instream);
                        Log.d("test", "calss Activity reuslt");

                        messagesAdapter.addToStart(
                                MessagesFixtures.getImageMessage(resultPhotoBitmap), true);

                        instream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                }

        }
    }

}
