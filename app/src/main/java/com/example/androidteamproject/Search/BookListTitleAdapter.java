package com.example.androidteamproject.Search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.androidteamproject.ApiData.SearchBookTitle;
import com.example.androidteamproject.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookListTitleAdapter extends BaseAdapter {
    private Context context;
    private List<SearchBookTitle> books;

    public BookListTitleAdapter(Context context, List<SearchBookTitle> books) {
        this.context = context;
        this.books = books;
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Object getItem(int position) {
        return books.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_book, parent, false);
        }

        SearchBookTitle book = books.get(position);

        TextView bookName = convertView.findViewById(R.id.book_name);
        TextView bookAuthor = convertView.findViewById(R.id.book_author);
        TextView bookPublisher = convertView.findViewById(R.id.book_publisher);
        TextView bookPublicationYear = convertView.findViewById(R.id.book_publication_year);
        ImageView bookImage = convertView.findViewById(R.id.book_image);

        bookName.setText(book.getBookName());
        bookAuthor.setText(book.getAuthors());
        bookPublisher.setText(book.getPublisher());
        bookPublicationYear.setText(book.getPublication_year());

        // 이미지를 로드할 때 Picasso 라이브러리를 사용
        Glide.with(context).load(book.getBookImageUrl()).into(bookImage);

        return convertView;
    }
}
