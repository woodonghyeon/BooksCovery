package com.example.androidteamproject.Home;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.androidteamproject.ApiData.CompositeSearchBookDetail;
import com.example.androidteamproject.ApiData.DataBase;
import com.example.androidteamproject.ApiData.HttpConnection;
import com.example.androidteamproject.ApiData.SearchBookDetail;
import com.example.androidteamproject.R;
import com.example.androidteamproject.SessionManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Response;

public class FragmentBookDetail extends Fragment {
    private static final String ARG_ISBN13 = "isbn13";
    private static final String ARG_BOOKNAME = "bookName";
    private static final String ARG_AUTHORS = "authors";
    private static final String ARG_IMAGE_URL = "imageUrl";

    private String isbn13;
    private String bookName;
    private String authors;
    private String imageUrl;
    private int bookId;
    private LineChart lineChart;
    private ToggleButton toggle_bookmark;
    private ViewPager2 maniaBookPager, readerBookPager;
    private ManiaBookAdapter maniaBookAdapter;
    private ReaderBookAdapter readerBookAdapter;

    DataBase dataBase = new DataBase();
    SessionManager sessionManager;

    public static FragmentBookDetail newInstance(String isbn13, String bookName, String authors, String imageUrl) {
        FragmentBookDetail fragment = new FragmentBookDetail();
        Bundle args = new Bundle();
        args.putString(ARG_ISBN13, isbn13);
        args.putString(ARG_BOOKNAME, bookName);
        args.putString(ARG_AUTHORS, authors);
        args.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isbn13 = getArguments().getString(ARG_ISBN13);
            bookName = getArguments().getString(ARG_BOOKNAME);
            authors = getArguments().getString(ARG_AUTHORS);
            imageUrl = getArguments().getString(ARG_IMAGE_URL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_detail, container, false);
        Context context = getContext();
        sessionManager = new SessionManager(context);

        ImageView bookImageView = view.findViewById(R.id.iv_detail_book_image);
        TextView bookNameTextView = view.findViewById(R.id.tv_detail_book_name);
        TextView authorsTextView = view.findViewById(R.id.tv_detail_authors);
        TextView descriptionTextView = view.findViewById(R.id.tv_detail_description);
        TextView publisherTextView = view.findViewById(R.id.tv_detail_publisher);
        TextView publicationYearTextView = view.findViewById(R.id.tv_detail_publication_year);
        TextView isbnTextView = view.findViewById(R.id.tv_detail_isbn);
        TextView classNoTextView = view.findViewById(R.id.tv_detail_class_no);
        TextView classNmTextView = view.findViewById(R.id.tv_detail_class_nm);
        TextView loanCntTextView = view.findViewById(R.id.tv_detail_loan_cnt);
        TextView ageTextView = view.findViewById(R.id.tv_detail_age);
        TextView wordTextView = view.findViewById(R.id.tv_detail_word);
        toggle_bookmark = view.findViewById(R.id.toggle_bookmark);
        lineChart = view.findViewById(R.id.line_chart);
        maniaBookPager = view.findViewById(R.id.maniaBooks_viewpager);

        // 로깅 추가
        System.out.println("FragmentBookDetail onCreateView: " + bookName + ", " + authors + ", " + imageUrl);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).placeholder(R.drawable.ic_warring).error(R.drawable.ic_error).into(bookImageView, new Callback() {
                @Override
                public void onSuccess() {
                    System.out.println("Picasso: Image loaded successfully.");
                }

                @Override
                public void onError(Exception e) {
                    System.err.println("Picasso: Failed to load image. " + e.getMessage());
                }
            });
        } else {
            bookImageView.setImageResource(R.drawable.ic_error);
        }

        bookNameTextView.setText(bookName != null ? bookName : "N/A");
        authorsTextView.setText(authors != null ? authors : "N/A");

        fetchBookDetail(isbn13, bookNameTextView, authorsTextView, descriptionTextView, bookImageView, publisherTextView, publicationYearTextView, isbnTextView, classNoTextView, classNmTextView, loanCntTextView, ageTextView, wordTextView);

        return view;
    }

    private void fetchBookDetail(String isbn13, TextView bookNameTextView, TextView authorsTextView, TextView descriptionTextView, ImageView bookImageView, TextView publisherTextView, TextView publicationYearTextView, TextView isbnTextView, TextView classNoTextView, TextView classNmTextView, TextView loanCntTextView, TextView ageTextView, TextView wordTextView) {
        Integer memberId = sessionManager.getMember();
        Integer departmentId = sessionManager.getDepartmentId();

        HttpConnection.getInstance(getContext()).getDetailBook(isbn13, memberId, departmentId, new HttpConnection.HttpResponseCallback<CompositeSearchBookDetail>() {
            @Override
            public void onSuccess(CompositeSearchBookDetail responseData) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        SearchBookDetail bookDetail = responseData.getBookDetail();
                        // set text views ...

                        // 도서 중복 확인 중복시 안하고 없을시 추가
                        dataBase.insertBook(bookDetail, new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e("DataBase", "Error inserting book", e);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    Log.d("DataBase", "Book inserted successfully");
                                } else {
                                    Log.e("DataBase", "Failed to insert book: " + response.code());
                                }
                            }
                        });

                        // 도서 아디 찾기
                        dataBase.selectBookId(bookDetail.getIsbn13(), new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e("DataBase", "Error selecting book id", e);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    String responseBody = response.body().string();
                                    try {
                                        JSONObject jsonObject = new JSONObject(responseBody);
                                        int book_id = jsonObject.getInt("book_id");
                                        Log.e("cha", "" + book_id);

                                        // 검색 기록 도서pk확인 있으면 삭제하고 추가 없으면 추가
                                        dataBase.insertHistory(sessionManager.getMember(), book_id, new okhttp3.Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                Log.e("DataBase", "Error inserting history", e);
                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {
                                                if (response.isSuccessful()) {
                                                    Log.d("DataBase", "History inserted successfully");
                                                } else {
                                                    Log.e("DataBase", "Failed to insert history: " + response.code());
                                                }
                                            }
                                        });

                                        // 학과별 검색횟수 있으면 업데이트 없으면 추가
                                        dataBase.findBookCount(book_id, sessionManager.getDepartmentId(), new okhttp3.Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                Log.e("DataBase", "Error finding book count", e);
                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {
                                                if (response.isSuccessful()) {
                                                    String responseBody = response.body().string();
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(responseBody);
                                                        int book_count_id = jsonObject.getInt("book_count_id");
                                                        Log.e("check", "book_count_id" + book_count_id);

                                                        if (book_count_id != 0) {
                                                            dataBase.updateBookCount(book_count_id, new okhttp3.Callback() {
                                                                @Override
                                                                public void onFailure(Call call, IOException e) {
                                                                    Log.e("DataBase", "Error updating book count", e);
                                                                }

                                                                @Override
                                                                public void onResponse(Call call, Response response) throws IOException {
                                                                    if (response.isSuccessful()) {
                                                                        Log.d("DataBase", "Book count updated successfully");
                                                                    } else {
                                                                        Log.e("DataBase", "Failed to update book count: " + response.code());
                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            dataBase.insertBookCount(sessionManager.getDepartmentId(), book_id, new okhttp3.Callback() {
                                                                @Override
                                                                public void onFailure(Call call, IOException e) {
                                                                    Log.e("DataBase", "Error inserting book count", e);
                                                                }

                                                                @Override
                                                                public void onResponse(Call call, Response response) throws IOException {
                                                                    if (response.isSuccessful()) {
                                                                        Log.d("DataBase", "Book count inserted successfully");
                                                                    } else {
                                                                        Log.e("DataBase", "Failed to insert book count: " + response.code());
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                } else {
                                                    Log.e("DataBase", "Failed to find book count: " + response.code());
                                                }
                                            }
                                        });
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Log.e("DataBase", "Failed to select book id: " + response.code());
                                }
                            }
                        });

                        checkFavoriteStatus();
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        descriptionTextView.setText("도서 상세 정보를 불러오지 못했습니다. 오류: " + e.getMessage());
                    });
                }
            }
        });
    } // end of fetchBookDetail

    private void checkFavoriteStatus() {
        new Thread(() -> {
            dataBase.selectBookId(isbn13, new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("DataBase", "Error selecting book id", e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody);
                            bookId = jsonObject.getInt("book_id");
                            dataBase.isFavorite(sessionManager.getMember(), bookId, new okhttp3.Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.e("DataBase", "Error checking favorite", e);
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    if (response.isSuccessful()) {
                                        String responseBody = response.body().string();
                                        try {
                                            JSONObject jsonObject = new JSONObject(responseBody);
                                            boolean isFavorite = jsonObject.getBoolean("isFavorite");
                                            if (getActivity() != null) {
                                                getActivity().runOnUiThread(() -> {
                                                    toggle_bookmark.setChecked(isFavorite);
                                                    toggle_bookmark.setBackgroundResource(isFavorite ? R.drawable.ic_bookmark_on : R.drawable.ic_bookmark_off);

                                                    toggle_bookmark.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                                        if (isChecked) {
                                                            dataBase.addFavorite(sessionManager.getMember(), bookId, new okhttp3.Callback() {
                                                                @Override
                                                                public void onFailure(Call call, IOException e) {
                                                                    Log.e("DataBase", "Error adding favorite", e);
                                                                }

                                                                @Override
                                                                public void onResponse(Call call, Response response) throws IOException {
                                                                    if (response.isSuccessful()) {
                                                                        Log.d("DataBase", "Favorite added successfully");
                                                                    } else {
                                                                        Log.e("DataBase", "Failed to add favorite: " + response.code());
                                                                    }
                                                                }
                                                            });
                                                            toggle_bookmark.setBackgroundResource(R.drawable.ic_bookmark_on);
                                                        } else {
                                                            dataBase.removeFavorite(sessionManager.getMember(), bookId, new okhttp3.Callback() {
                                                                @Override
                                                                public void onFailure(Call call, IOException e) {
                                                                    Log.e("DataBase", "Error removing favorite", e);
                                                                }

                                                                @Override
                                                                public void onResponse(Call call, Response response) throws IOException {
                                                                    if (response.isSuccessful()) {
                                                                        Log.d("DataBase", "Favorite removed successfully");
                                                                    } else {
                                                                        Log.e("DataBase", "Failed to remove favorite: " + response.code());
                                                                    }
                                                                }
                                                            });
                                                            toggle_bookmark.setBackgroundResource(R.drawable.ic_bookmark_off);
                                                        }
                                                    });
                                                });
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Log.e("DataBase", "Failed to check favorite: " + response.code());
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("DataBase", "Failed to select book id: " + response.code());
                    }
                }
            });
        }).start();
    }

    private void getManiaRecBookItems(List<String> isbn13List) {
        HttpConnection.getInstance(getContext()).getManiaRecBook(isbn13List, new HttpConnection.HttpResponseCallback<List<SearchBookDetail>>() {
            @Override
            public void onSuccess(List<SearchBookDetail> maniaBooks) {
                getActivity().runOnUiThread(() -> {
                    List<String> bookNames = new ArrayList<>();
                    List<String> imageUrls = new ArrayList<>();
                    List<String> isbn13s = new ArrayList<>();
                    List<String> authors = new ArrayList<>();

                    synchronized (maniaBooks) {
                        for (SearchBookDetail book : maniaBooks) {
                            bookNames.add(book.getManiaBookName());
                            authors.add(book.getManiaAuthor());
                            imageUrls.add(book.getManiaImageUrl());
                            isbn13s.add(book.getManiaIsbn13());
                        }
                    }
                    setupManiaViewPager(bookNames, authors, imageUrls, isbn13s);
                });
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    } // end of getManiaRecBookItems

    public void setupManiaViewPager(List<String> bookNames, List<String> authors, List<String> imageUrls, List<String> isbn13s) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            // 이미지 없는 경우 처리
            return;
        }
        // 이미지 URL 있는 경우 설정
        if (getView() == null) {
            return;
        }
        maniaBookPager = getView().findViewById(R.id.maniaBooks_viewpager);
        maniaBookAdapter = new ManiaBookAdapter(requireActivity(), bookNames, authors, imageUrls, isbn13s, this::showBookDetail);
        maniaBookPager.setAdapter(maniaBookAdapter);
        maniaBookPager.setCurrentItem(1000);
        maniaBookPager.setOffscreenPageLimit(10);

        int startPos = imageUrls.size() / 2;
        maniaBookPager.setCurrentItem(startPos);

        int pageMarginPx = getResources().getDimensionPixelOffset(R.dimen.maniaBookPageMargin);
        int pagerWidth = getResources().getDimensionPixelOffset(R.dimen.maniaBookPageWidth);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int offsetPx = screenWidth - pageMarginPx - pagerWidth;

        maniaBookPager.setPageTransformer((page, position) -> page.setTranslationX(position * -offsetPx));
        maniaBookPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    maniaBookPager.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
    } // end of setupViewPager

    private void getReaderRecBookItems(List<String> isbn13List) {
        HttpConnection.getInstance(getContext()).getReaderRecBook(isbn13List, new HttpConnection.HttpResponseCallback<List<SearchBookDetail>>() {
            @Override
            public void onSuccess(List<SearchBookDetail> maniaBooks) {
                getActivity().runOnUiThread(() -> {
                    List<String> bookNames = new ArrayList<>();
                    List<String> imageUrls = new ArrayList<>();
                    List<String> isbn13s = new ArrayList<>();
                    List<String> authors = new ArrayList<>();

                    for (SearchBookDetail book : maniaBooks) {
                        bookNames.add(book.getManiaBookName());
                        authors.add(book.getManiaAuthor());
                        imageUrls.add(book.getManiaImageUrl());
                        isbn13s.add(book.getManiaIsbn13());
                    }
                    setupReaderViewPager(bookNames, authors, imageUrls, isbn13s);
                });
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    } // end of getManiaRecBookItems

    public void setupReaderViewPager(List<String> bookNames, List<String> authors, List<String> imageUrls, List<String> isbn13s) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            // 이미지 없는 경우 처리
            return;
        }
        // 이미지 URL 있는 경우 설정
        if (getView() == null) {
            return;
        }
        readerBookPager = getView().findViewById(R.id.readerBooks_viewpager);
        readerBookAdapter = new ReaderBookAdapter(requireActivity(), bookNames, authors, imageUrls, isbn13s, this::showBookDetail);
        readerBookPager.setAdapter(readerBookAdapter);
        readerBookPager.setCurrentItem(1000);
        readerBookPager.setOffscreenPageLimit(10);

        int startPos = imageUrls.size() / 2;
        readerBookPager.setCurrentItem(startPos);

        int pageMarginPx = getResources().getDimensionPixelOffset(R.dimen.maniaBookPageMargin);
        int pagerWidth = getResources().getDimensionPixelOffset(R.dimen.maniaBookPageWidth);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int offsetPx = screenWidth - pageMarginPx - pagerWidth;

        readerBookPager.setPageTransformer((page, position) -> page.setTranslationX(position * -offsetPx));
        readerBookPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    readerBookPager.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
    } // end of setupViewPager

    private static class KeywordWithWeight {
        String word;
        String weight;

        KeywordWithWeight(String word, String weight) {
            this.word = word;
            this.weight = weight;
        }
    }

    public class XAxisFormatter extends ValueFormatter {
        private final List<String> month;

        public XAxisFormatter(List<String> month) {
            this.month = month;
        }

        @Override
        public String getFormattedValue(float value) {
            int index = (int) value;
            if (index < 0 || index >= month.size()) {
                // 유효하지 않은 인덱스 처리
                return "";
            }
            return month.get(index).substring(5);
        }
    }

    // showBookDetail
    private void showBookDetail(String isbn13, String bookName, String authors, String imageUrl) {
        // 새로운 FragmentBookDetail 인스턴스를 생성하고 필요한 데이터를 전달
        FragmentBookDetail fragment = FragmentBookDetail.newInstance(isbn13, bookName, authors, imageUrl);

        // FragmentTransaction을 통해 프래그먼트를 관리
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

        // 현재 프래그먼트를 가져와서 숨김
        Fragment currentFragment = getParentFragmentManager().findFragmentById(R.id.ly_home);
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }

        // 새로운 프래그먼트를 추가
        transaction.add(R.id.ly_home, fragment);
        transaction.addToBackStack(null); // 백스택에 추가하여 뒤로가기 버튼을 눌렀을 때 이전 프래그먼트로 돌아갈 수 있음
        transaction.commit();
    }
}
