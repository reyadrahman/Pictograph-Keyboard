package inc.flide.android.emoji_keyboard.adapter;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

import inc.flide.android.emoji_keyboard.EmojiKeyboardService;
import inc.flide.android.emoji_keyboard.R;
import inc.flide.android.emoji_keyboard.Utility;
import inc.flide.android.emoji_keyboard.constants.CategorizedEmojiList;
import inc.flide.android.emoji_keyboard.constants.Emoji;

public abstract class BaseEmojiAdapter extends BaseAdapter {

    protected EmojiKeyboardService emojiKeyboardService;
    protected List<Emoji> emojiList;
    private static String filePrefix;

    public BaseEmojiAdapter(EmojiKeyboardService emojiKeyboardService ) {
        this.emojiKeyboardService = emojiKeyboardService;
    }

    @Override
    public int getCount() {
        return emojiList.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(emojiKeyboardService);
            int scale = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, emojiKeyboardService.getResources().getDisplayMetrics());
            imageView.setPadding(scale, (int)(scale*1.2), scale, (int)(scale * 1.2));
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
            imageView.setLongClickable(false);
        }

        imageView.setImageResource(getIconIdBasedOnPosition(position));
        imageView.setBackgroundResource(R.drawable.btn_background);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiKeyboardService.sendText(getEmojiUnicodeString(position));
            }
        });

        if (doEmojiSupportDiversity(position)) {
            imageView.setLongClickable(true);
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    diversityEmojiPopup(imageView, position);
                    return true;
                }
            });
        }
        return imageView;
    }

    private void diversityEmojiPopup(ImageView imageView, int position) {
        LayoutInflater layoutInflater = (LayoutInflater)emojiKeyboardService
                                            .getSystemService(emojiKeyboardService.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.popup, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout popupViewLinearLayout = (LinearLayout) popupView.findViewById(R.id.popupWindowLinearLayout);
        List<ImageView> diversityEmojis = getDiversityEmojisImageViewList(position, popupWindow);
        for(ImageView view: diversityEmojis) {
            popupViewLinearLayout.addView(view);
        }

        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);

        popupWindow.showAsDropDown(imageView,0,-imageView.getHeight()*2);
    }

    public boolean doEmojiSupportDiversity(int position) {
        return emojiList.get(position).isDiversityAvailable();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected List<ImageView> getDiversityEmojisImageViewList(final int position, final PopupWindow popupWindow) {

        List<Emoji> diversityEmojiList = CategorizedEmojiList.getInstance().getDiversityEmojisList(emojiList.get(position));

        List<ImageView> diversityEmojiImageViewList = new ArrayList<>();

        for (final Emoji emoji: diversityEmojiList) {
            ImageView imageView = new ImageView(emojiKeyboardService);
            int scale = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, emojiKeyboardService.getResources().getDisplayMetrics());
            imageView.setPadding(scale, (int)(scale*1.2), scale, (int)(scale * 1.2));
            imageView.setAdjustViewBounds(true);
            imageView.setImageResource(getIconIdBasedOnEmoji(emoji));
            imageView.setBackgroundResource(R.drawable.btn_background);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    emojiKeyboardService.sendText(emoji.getUnicodeJavaString());
                    popupWindow.dismiss();
                }
            });
            diversityEmojiImageViewList.add(imageView);
        }
        return diversityEmojiImageViewList;
    }

    protected int getIconIdBasedOnEmoji(Emoji emoji) {
        String resourceString = filePrefix + emoji.getUnicodeHexcode().replace('-','_');
        int resourceId;
        resourceId = emojiKeyboardService.getResources().getIdentifier(resourceString, "drawable", emojiKeyboardService.getPackageName());
        if (resourceId == 0) {
            resourceId = emojiKeyboardService.getResources().getIdentifier("ic_not_available_sign", "drawable", emojiKeyboardService.getPackageName());
        }

        return resourceId;
    }

    public abstract int getIconIdBasedOnPosition(int position);
    public abstract String getEmojiUnicodeString(int position);

    public static void setFilePrefix(String prefix) {
        filePrefix = prefix;
    }
}