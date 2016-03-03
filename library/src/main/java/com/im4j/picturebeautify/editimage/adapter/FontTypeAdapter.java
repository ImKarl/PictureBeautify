package com.im4j.picturebeautify.editimage.adapter;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.im4j.picturebeautify.R;

import java.util.List;

/**
 * Created by fostion on 2/23/16.
 */
public class FontTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private AssetManager assetManager;
    private List<String> fontTypes;
    private OnItemClickListener onItemClickListener;

    public FontTypeAdapter(AssetManager _assetManager,List<String> _fontTypes){
        this.assetManager = _assetManager;
        this.fontTypes = _fontTypes;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_font_item, parent,false);
        return new FontHolder(view) ;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof FontHolder){
            ((FontHolder) holder).setData(position);
        }

    }

    @Override
    public int getItemCount() {
        return fontTypes.size();
    }

    public class FontHolder extends RecyclerView.ViewHolder {
        public TextView text;

        public FontHolder(View itemView) {
            super(itemView);
            this.text = (TextView) itemView.findViewById(R.id.text);

        }

        public void setData(final int position){
            final Typeface typeface = Typeface.createFromAsset(assetManager,fontTypes.get(position));
            text.setTypeface(typeface);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null){
                        onItemClickListener.onItemClick(typeface);
                    }
                }
            });
        }

    }// end inner class

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(Typeface typeface);
    }
}
