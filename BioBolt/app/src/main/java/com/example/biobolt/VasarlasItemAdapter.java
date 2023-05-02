package com.example.biobolt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class VasarlasItemAdapter extends RecyclerView.Adapter<VasarlasItemAdapter.ViewHolder> implements Filterable {
    private ArrayList<VasarlasItem> mVasarlasItemsData;
    private ArrayList<VasarlasItem> mVasarlasItemsDataAll;
    private Context mContext;
    private int lastPosition = -1;

    VasarlasItemAdapter(Context context, ArrayList<VasarlasItem> itemsData){
        this.mVasarlasItemsData = itemsData;
        this.mVasarlasItemsDataAll = itemsData;
        this.mContext = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.lista,parent,false));
    }

    @Override
    public void onBindViewHolder(VasarlasItemAdapter.ViewHolder holder, int position) {
        VasarlasItem currentItem = mVasarlasItemsData.get(position);

        holder.bindTo(currentItem);

        if(holder.getAdapterPosition()>lastPosition){
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }

    }

    @Override
    public int getItemCount() {
        return mVasarlasItemsData.size();
    }

    @Override
    public Filter getFilter() {
        return vasarlasFilter;
    }

    private Filter vasarlasFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<VasarlasItem> filtered = new ArrayList<>();
            FilterResults results = new FilterResults();
            if(charSequence == null || charSequence.length() == 0){
                results.count = mVasarlasItemsDataAll.size();
                results.values = mVasarlasItemsDataAll;
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (VasarlasItem item : mVasarlasItemsDataAll){
                    if(item.getName().toLowerCase().contains(filterPattern)){
                        filtered.add(item);
                    }
                }
                results.count = filtered.size();
                results.values = filtered;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mVasarlasItemsData = (ArrayList)filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView mTitleText;
        private TextView mSubText;
        private TextView mPriceText;
        private ImageView mImg;
        private RatingBar mRating;
        public ViewHolder(View itemView) {
            super(itemView);
            mTitleText=itemView.findViewById(R.id.itemTitle);
            mSubText=itemView.findViewById(R.id.itemsubTitle);
            mPriceText=itemView.findViewById(R.id.price);
            mImg=itemView.findViewById(R.id.itemImage);
            mRating=itemView.findViewById(R.id.ratingBar);

        }

        public void bindTo(VasarlasItem currentItem) {
            mTitleText.setText(currentItem.getName());
            mSubText.setText(currentItem.getInfo());
            mPriceText.setText(currentItem.getPrice());
            mRating.setRating(currentItem.getRatedInfo());

            Glide.with(mContext).load(currentItem.getImageResource()).into(mImg);
            itemView.findViewById(R.id.cart).setOnClickListener(view -> ((TermekListaActivity)mContext).update(currentItem));
            itemView.findViewById(R.id.delete).setOnClickListener(view -> ((TermekListaActivity)mContext).deleteProduct(currentItem));
        }
    };
}

