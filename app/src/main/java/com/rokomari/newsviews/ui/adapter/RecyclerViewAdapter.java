package com.rokomari.newsviews.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rokomari.newsviews.R;
import com.rokomari.newsviews.repository.entity.Article;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    public List<Article> data;

    public RecyclerViewAdapter(Context context, List<Article> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Article a = data.get(position);
        holder.title.setText(a.title);
        holder.subtitle.setText(a.description);
        holder.date.setText(a.author + " - " + a.publishedAt);
        Glide.with(context).load(a.urlToImage).into(holder.image);

        holder.parent.setOnClickListener(view -> {
//            Intent intent = new Intent(context, DetailsActivity.class);
//            intent.putExtra("image_url", mImages.get(position));
//            intent.putExtra("image_name", a.title);
//            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.tv_title)
        TextView title;
        @BindView(R.id.tv_subtitle)
        TextView subtitle;
        @BindView(R.id.tv_date)
        TextView date;
        @BindView(R.id.parent_layout)
        View parent;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}


