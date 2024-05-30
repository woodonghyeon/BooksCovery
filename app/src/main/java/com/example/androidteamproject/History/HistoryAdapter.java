// 분리 시도 못하겠음


//package com.example.androidteamproject.History;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.example.androidteamproject.ApiData.SearchBookDetail;
//import com.example.androidteamproject.R;
//import com.squareup.picasso.Picasso;
//
//import java.util.List;
//
//public class HistoryAdapter extends BaseAdapter {
//    private Context context;
//    private List<SearchBookDetail> books;
//
//    public HistoryAdapter(Context context, List<SearchBookDetail> books) {
//        this.context = context;
//        this.books = books;
//    }
//
//    @Override
//    public int getCount() {
//        return books.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return books.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false);
//        }
//
//        SearchBookDetail book = books.get(position);
//
//        TextView bookName = convertView.findViewById(R.id.bookname);
//        TextView bookAuthor = convertView.findViewById(R.id.authors);
//        TextView bookPublisher = convertView.findViewById(R.id.publisher);
//        ImageView bookImage = convertView.findViewById(R.id.image);
//
//        bookName.setText(book.getBookName());
//        bookAuthor.setText(book.getAuthors());
//        bookPublisher.setText(book.getPublisher());
//
//        String imageUrl = book.getBookImageUrl();
//        if (imageUrl == null || imageUrl.isEmpty()) {
//            Picasso.get().load(R.drawable.ic_error).into(bookImage);
//        } else {
//            Picasso.get().load(imageUrl).into(bookImage);
//        }
//
//        return convertView;
//    }
//}
