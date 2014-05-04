package info.srihawong.thailandonly;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.callback.ImageOptions;

import java.util.ArrayList;

/**
 * Created by Banpot.S on 10/1/2557.
 */
public class ListAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<ListItem> listData = new ArrayList<ListItem>();
    private LayoutInflater mInflater;

    public ListAdapter(Context context, ArrayList<ListItem> listData) {
        this.listData = listData;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public ListItem getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
           ListItemView listItemView;
           if(view==null){
               view = mInflater.inflate(R.layout.item_list,null);
               listItemView = new ListItemView();
               listItemView.textTitle = (TextView) view.findViewById(R.id.textTitle);
               listItemView.imageItem = (ImageView) view.findViewById(R.id.imageItem);
               listItemView.imageUser = (ImageView) view.findViewById(R.id.imageUser);
               listItemView.imageItemProgress = (ProgressBar) view.findViewById(R.id.imageItemProgress);
               view.setTag(listItemView);
           }else{
               listItemView = (ListItemView) view.getTag();
           }
        listItemView.textTitle.setText(listData.get(position).getTitle());

        AQuery aq = new AQuery(view.getContext());
        BitmapAjaxCallback bitmapAjaxCallback = new BitmapAjaxCallback();
        bitmapAjaxCallback.url(listData.get(position).getImageUser())
                .animation(AQuery.FADE_IN_NETWORK)
                .fileCache(true)
                .memCache(true)
                .round(listItemView.imageUser.getMaxWidth()/2);

        aq.id(listItemView.imageUser).image(bitmapAjaxCallback);

        aq.id(listItemView.imageItem)
                .progress(listItemView.imageItemProgress)
                .image(listData.get(position).getImageItem(), true, true, 0, 0, null, AQuery.FADE_IN_NETWORK, AQuery.ANCHOR_DYNAMIC);


        //listItemView.imageUser.setImageURI(Uri.parse(listData.get(position).getImageUser()));
        //listItemView.imageItem.setImageURI(Uri.parse(listData.get(position).getImageItem()));

        //Picasso.with(view.getContext()).load(listData.get(position).getImageUser()).into(listItemView.imageUser);
        //Picasso.with(view.getContext()).load(listData.get(position).getImageItem()).into(listItemView.imageItem);
        //Picasso.with(view.getContext()).load(R.drawable.default_bg).into(listItemView.imageItem);

        return view;
    }
}
