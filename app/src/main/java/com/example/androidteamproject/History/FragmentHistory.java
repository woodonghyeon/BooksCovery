package com.example.androidteamproject.History;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.androidteamproject.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FragmentHistory extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    protected List<String> bookImageURL = new ArrayList<>();
    private List<String> bookname = new ArrayList<>();
    private List<String> authors = new ArrayList<>();
    private List<String> publisher = new ArrayList<>();
    private List<String> searchDate = new ArrayList<>();

    private ListView list;

    public static FragmentHistory newInstance(String param1, String param2) {
        FragmentHistory fragment = new FragmentHistory();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        list = view.findViewById(R.id.List);

        CustomList adapter = new CustomList(this, bookname, bookImageURL, authors, publisher, searchDate);
        list.setAdapter(adapter);
        return view;
    }

    public class CustomList extends ArrayAdapter<String>{
        private final FragmentHistory context;
        private final List<String> bookNames;
        private final List<String> bookImages;
        private final List<String> bookAuthors;
        private final List<String> bookPublishers;
        private final List<String> bookSearchDates;

        public CustomList(FragmentHistory context, List<String> bookNames, List<String> bookImages, List<String> bookAuthors, List<String> bookPublishers, List<String> bookSearchDates) {
            super(context.getActivity(), R.layout.history_item, bookNames);
            this.context = context;
            this.bookNames = bookNames;
            this.bookImages = bookImages;
            this.bookAuthors = bookAuthors;
            this.bookPublishers = bookPublishers;
            this.bookSearchDates = bookSearchDates;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                view = inflater.inflate(R.layout.history_item, parent, false);
            }

            TextView nameView = view.findViewById(R.id.book_name);
            TextView authorView = view.findViewById(R.id.authors);
            TextView publisherView = view.findViewById(R.id.publisher);
            //TextView dateView = view.findViewById(R.id.search_date);
            ImageView imageView = view.findViewById(R.id.book_image);

            nameView.setText(bookNames.get(position));
            authorView.setText(bookAuthors.get(position));
            publisherView.setText(bookPublishers.get(position));
            //dateView.setText(bookSearchDates.get(position));
            Picasso.get().load(bookImages.get(position)).into(imageView);

            return view;
        }
    }



}
