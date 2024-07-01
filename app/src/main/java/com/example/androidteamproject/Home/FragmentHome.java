package com.example.androidteamproject.Home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.androidteamproject.ApiData.DataBase;
import com.example.androidteamproject.ApiData.HttpConnection;
import com.example.androidteamproject.ApiData.SearchBookDetail;
import com.example.androidteamproject.R;
import com.example.androidteamproject.ApiData.SearchBook;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FragmentHome extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private final int currentEventNum = 9;
    private static final float MIN_SCALE = 0.6f; // WeekBook scale
    long now = System.currentTimeMillis();
    private Date mDate = new Date(now);
    public static SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd"); //딴 곳에도 쓸거라 public으로 바꿧음
    private ViewPager2 currentEventPager, weekBookPager, monthBookPager, hotTrendBookPager, popularBookPager;
    private FragmentStateAdapter homePagerAdapter, weekBookAdapter, monthBookAdapter, hotTrendBookAdapter, popularBookAdapter;
    private TextView tv_popular_book_week, tv_popular_book_month, tv_hotTrend_title, tv_department;
    private Animation anime_left_to_right, anime_right_to_left;
    private Spinner departmentSpinner;

    public FragmentHome() {
    }

    public static FragmentHome newInstance(String param1, String param2) {
        FragmentHome fragment = new FragmentHome();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    } // end of FragmentHome

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    } // end of onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // 데이터베이스 객체 생성
        DataBase db = new DataBase();
        setupSpinner(view); // 스피너 설정 메서드 호출
        startAnimation(view);
        CurrentEventSettingImg(view);
        getResponseApiWeekLoanItems();
        getResponseApiMonthLoanItems();
        getResponseApiHotTrend();

        return view;
    } // end of onCreateView

    private void CurrentEventSettingImg(View view) {
        // 가로 슬라이드 뷰 Fragment

        // 첫 번째 ViewPager2
        currentEventPager = view.findViewById(R.id.event_viewpager);
        homePagerAdapter = new CurrentEventAdapter(requireActivity(), currentEventNum);
        currentEventPager.setAdapter(homePagerAdapter);
        currentEventPager.setCurrentItem(1000);
        currentEventPager.setOffscreenPageLimit(3);

        // viewpager2 간격 변환을 위함 -> res.values.dimes.xml에서 확인
        int pageMarginPx = getResources().getDimensionPixelOffset(R.dimen.eventPageMargin);
        int pagerWidth = getResources().getDimensionPixelOffset(R.dimen.eventPageWidth);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int offsetPx = screenWidth - pageMarginPx - pagerWidth;

        // viewpager2 간격 변환
        currentEventPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                page.setTranslationX(position * -offsetPx);
            }
        });

        // 자동 스크롤 설정
        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            public void run() {
                int currentPage = currentEventPager.getCurrentItem();
                int nextPage = currentPage + 1;
                if (nextPage >= homePagerAdapter.getItemCount()) {
                    nextPage = 0; // 마지막 페이지 이후에는 첫 페이지로 돌아가기
                }
                currentEventPager.setCurrentItem(nextPage, true);
                handler.postDelayed(this, 5000); // 5초마다 페이지 변경
            }
        };

        // 초기 지연 후 자동 스크롤 시작
        handler.postDelayed(update, 5000);

        // 사용자가 수동으로 스크롤할 때 자동 스크롤 중지 및 재시작
        currentEventPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            private boolean isUserScrolling = false;

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    handler.removeCallbacks(update);
                    isUserScrolling = true;
                } else if (state == ViewPager2.SCROLL_STATE_IDLE && isUserScrolling) {
                    isUserScrolling = false;
                    handler.postDelayed(update, 5000);
                }
            }
        });
    }

    private void fetchPopularBooks(int departmentId) {
        new Thread(() -> {
            DataBase db = new DataBase();
            try {
                List<SearchBookDetail> popularBooks = db.getPopularBooks(departmentId);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        List<String> bookName = new ArrayList<>();
                        List<String> authors = new ArrayList<>();
                        List<String> imageUrl = new ArrayList<>();
                        List<String> isbn13 = new ArrayList<>();
                        List<String> publisher = new ArrayList<>();
                        List<Integer> publicationYear = new ArrayList<>();
                        List<String> classNo = new ArrayList<>();
                        List<Integer> loanCount = new ArrayList<>();
                        List<Integer> bookCount = new ArrayList<>();

                        for (SearchBookDetail book : popularBooks) {
                            bookName.add(book.getBookName());
                            authors.add(book.getAuthors());
                            imageUrl.add(book.getBookImageUrl());
                            isbn13.add(book.getIsbn13());
                            publisher.add(book.getPublisher());
                            publicationYear.add(book.getPublication_year());
                            classNo.add(book.getClass_no());
                            loanCount.add(book.getLoanCnt());
                            bookCount.add(book.getBook_count());
                        }

                        setupPopularBooksViewPager(bookName, authors, imageUrl, isbn13);
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setupPopularBooksViewPager(List<String> bookNames, List<String> authors, List<String> imageUrls, List<String> isbn13s) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            Log.d("ImageURL Check", "Image URLs are null or empty.");
            return;
        }
        Log.d("ImageURL Check", "Image URLs: " + imageUrls.toString());

        if (getView() == null) {
            return;
        }

        popularBookPager = getView().findViewById(R.id.popular_book_viewpager);
        popularBookAdapter = new PopularBooksAdapter(requireActivity(), bookNames, authors, imageUrls, isbn13s, this::showBookDetail);
        popularBookPager.setAdapter(popularBookAdapter);
        popularBookPager.setCurrentItem(1000);
        popularBookPager.setOffscreenPageLimit(10);

        int startPos = imageUrls.size() / 2;
        popularBookPager.setCurrentItem(startPos);

        int pageMarginPx = getResources().getDimensionPixelOffset(R.dimen.popularBookPageMargin);
        int pagerWidth = getResources().getDimensionPixelOffset(R.dimen.popularBookPageWidth);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int offsetPx = screenWidth - pageMarginPx - pagerWidth;

        popularBookPager.setPageTransformer((page, position) -> page.setTranslationX(position * -offsetPx));

        popularBookPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    popularBookPager.setCurrentItem(position);
                }
            }
        });
    }

    private void setupSpinner(View view) {
        departmentSpinner = view.findViewById(R.id.department_spinner);
        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDepartment = parent.getItemAtPosition(position).toString();
                tv_department.setText(selectedDepartment + " 인기도서");

                // 학과 이름을 학과 ID로 매핑하는 방법
                int departmentId = getDepartmentIdByName(selectedDepartment);
                fetchPopularBooks(departmentId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 선택되지 않았을 때 처리
            }
        });
    } // end of setupSpinner

    private int getDepartmentIdByName(String departmentName) {
        switch (departmentName) {
            case "전체 학과": return 0;
            case "컴퓨터정보계열": return 1;
            case "IT온라인창업과": return 2;
            case "AI융합기계계열": return 3;
            case "반도체전자계열": return 4;
            case "무인항공드론과": return 5;
            case "신재생에너지전기계열": return 6;
            case "건축과": return 7;
            case "인테리어디자인과": return 8;
            case "DIY실내장식과": return 9;
            case "항공정비부사관과": return 10;
            case "국방정보통신과": return 11;
            case "의무/전투부사관과": return 12;
            case "글로벌시스템융합과": return 13;
            case "경영회계서비스계열": return 14;
            case "호텔항공관광과": return 15;
            case "사회복지과": return 16;
            case "유아교육과": return 17;
            case "간호학과": return 18;
            case "보건의료행정과": return 19;
            case "응급구조과": return 20;
            case "동물보건과": return 21;
            case "반려동물과": return 22;
            case "조리제과제빵과": return 23;
            case "콘텐츠디자인과": return 24;
            case "만화애니메이션과": return 25;
            default: return -1;  // 또는 기본값
        }
    }

    //     최근 많이 검색된 도서 출력 (주간)
    private void getResponseApiWeekLoanItems() {
        String getTime = mFormat.format(mDate); // 현재 날짜 가져오기
        Calendar calendar = Calendar.getInstance(); // 1주일 전 날짜 가져오기
        calendar.setTime(mDate);
        calendar.add(Calendar.DAY_OF_YEAR, -7);

        String startDt = mFormat.format(calendar.getTime()); // 시작 날짜
        String endDt = getTime; // 종료 날짜
        String from_age = "20"; // 최소 나이
        String to_age = "40"; // 최대 나이
        int pageNo = 1; // 페이지 번호
        int pageSize = 10; // 페이지 크기
        String format = "json"; // 응답 형식

        HttpConnection.getInstance(getContext()).getLoanItems(pageNo, pageSize, startDt, endDt, from_age, to_age, new HttpConnection.HttpResponseCallback<List<SearchBook>>() {
            @Override
            public void onSuccess(List<SearchBook> books) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        List<String> imageUrls = new ArrayList<>();
                        List<String> bookName = new ArrayList<>();
                        List<String> authors = new ArrayList<>();
                        List<String> class_nm = new ArrayList<>();
                        List<String> isbn13 = new ArrayList<>();
                        for (SearchBook book : books) {
                            imageUrls.add(book.getBookImageUrl());
                            bookName.add(book.getBookName());
                            authors.add(book.getAuthors());
                            class_nm.add(book.getClass_nm());
                            isbn13.add(book.getIsbn13());
                        }
                        // ViewPager2에 이미지 추가
                        setupWeekViewPager(class_nm, bookName, authors, imageUrls, isbn13);
                        Log.d("API Response(Week)", "Image URLs: " + imageUrls.toString() + ", BookName: " + bookName.toString());
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // 에러 처리 로직 추가
                        Log.e("API Failure(Week)", "Error: " + e.getMessage());
                    });
                }
            }
        });
    } // end of getResponseApiWeekLoanItems

    // 주간 인기 도서 ViewPager2 setup
    private void setupWeekViewPager(List<String> class_nm, List<String> bookName, List<String> authors, List<String> imageUrls, List<String> isbn13) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            // 이미지 URL이 없는 경우 처리
            return;
        }
        // 이미지 URL이 있을 경우 뷰페이저 설정
        if (getView() == null) {
            return;
        }
        weekBookPager = getView().findViewById(R.id.week_book_viewpager);
        weekBookAdapter = new WeekBookAdapter(requireActivity(), class_nm, bookName, authors, imageUrls, isbn13, this::showBookDetail);
        weekBookPager.setAdapter(weekBookAdapter);
        weekBookPager.setCurrentItem(1000);
        weekBookPager.setOffscreenPageLimit(10);

        int startPos = imageUrls.size() / 2;
        weekBookPager.setCurrentItem(startPos);

        // viewpager2 간격 변환을 위함 -> res.values.dimes.xml에서 확인
        int pageMarginPx = getResources().getDimensionPixelOffset(R.dimen.weekBookPageMargin);
        int pagerWidth = getResources().getDimensionPixelOffset(R.dimen.weekBookPageWidth);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int offsetPx = screenWidth - pageMarginPx - pagerWidth;

        // WeekBookViewPager2 간격 변환
        weekBookPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                View weekBookImg = page.findViewById(R.id.iv_weekBookImg);
                View weekBookTitle = page.findViewById(R.id.tv_weekBookTitle);
                View weekBookAuthor = page.findViewById(R.id.tv_weekBookAuthor);
                View weekBookClassNM = page.findViewById(R.id.tv_weekBookClassName);
                if(weekBookImg != null) {
                    page.setTranslationX(position * -offsetPx);
                    if (position < -1) return;
                    if (position <= 1) {
                        float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position * getEase(Math.abs(position))));
                        weekBookImg.setScaleX(scaleFactor);
                        weekBookImg.setScaleY(scaleFactor);
                        weekBookTitle.setScaleX(scaleFactor);
                        weekBookTitle.setScaleY(scaleFactor);
                        weekBookAuthor.setScaleX(scaleFactor);
                        weekBookAuthor.setScaleY(scaleFactor);
                        weekBookClassNM.setScaleX(scaleFactor);
                        weekBookClassNM.setScaleY(scaleFactor);
                    } else {
                        weekBookImg.setScaleX(MIN_SCALE);
                        weekBookImg.setScaleY(MIN_SCALE);
                        weekBookTitle.setScaleX(MIN_SCALE);
                        weekBookTitle.setScaleY(MIN_SCALE);
                        weekBookAuthor.setScaleX(MIN_SCALE);
                        weekBookAuthor.setScaleY(MIN_SCALE);
                        weekBookClassNM.setScaleX(MIN_SCALE);
                        weekBookClassNM.setScaleY(MIN_SCALE);
                    }
                }
            }

            private float getEase(float position) {
                float sqt = position * position;
                return sqt / (2.0f * (sqt - position) + 1.0f);
            }
        });

        weekBookPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    weekBookPager.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
    } // end of setupWeekViewPager

    // 최근 많이 검색된 도서 출력 (월간)
    private void getResponseApiMonthLoanItems() {
        String getTime = mFormat.format(mDate); // 현재 날짜 가져오기
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        calendar.set(Calendar.DAY_OF_MONTH, 1); // 1일을 의미

        String startDt = mFormat.format(calendar.getTime()); // 시작 날짜
        String endDt = getTime; // 종료 날짜 (예시)
        String from_age = "20"; // 최소 나이
        String to_age = "40"; // 최대 나이
        int pageNo = 1; // 페이지 번호 (예시)
        int pageSize = 10; // 페이지 크기 (예시)
        String format = "json"; // 응답 형식 (예시)

        HttpConnection.getInstance(getContext()).getLoanItems(pageNo, pageSize, startDt, endDt, from_age, to_age, new HttpConnection.HttpResponseCallback<List<SearchBook>>() {
            @Override
            public void onSuccess(List<SearchBook> books) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        List<String> imageUrls = new ArrayList<>();
                        List<String> bookName = new ArrayList<>();
                        List<String> authors = new ArrayList<>();
                        List<String> class_nm = new ArrayList<>();
                        List<String> isbn13 = new ArrayList<>();
                        for (SearchBook book : books) {
                            imageUrls.add(book.getBookImageUrl());
                            bookName.add(book.getBookName());
                            authors.add(book.getAuthors());
                            class_nm.add(book.getClass_nm());
                            isbn13.add(book.getIsbn13());
                        }
                        // ViewPager2에 이미지 추가
                        setupMonthViewPager(class_nm, bookName, authors, imageUrls, isbn13);
                        Log.d("API Response(Month)", "Image URLs: " + imageUrls.toString() + ", BookName: " + bookName.toString());
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // 에러 처리 로직 추가
                        Log.e("API Failure(Month)", "Error: " + e.getMessage());
                    });
                }
            }
        });
    } // end of getResponseApiMonthLoanItems

    // 월간 인기 도서 ViewPager2 setup
    private void setupMonthViewPager(List<String> class_nm, List<String> bookName, List<String> authors, List<String> imageUrls, List<String> isbn13) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            // 이미지 URL이 없는 경우 처리
            return;
        }
        // 이미지 URL이 있을 경우 뷰페이저 설정
        if (getView() == null) {
            return;
        }
        monthBookPager = getView().findViewById(R.id.month_book_viewpager);
        monthBookAdapter = new MonthBookAdapter(requireActivity(), class_nm, bookName, authors, imageUrls, isbn13, this::showBookDetail);
        monthBookPager.setAdapter(monthBookAdapter);
        monthBookPager.setCurrentItem(1000);
        monthBookPager.setOffscreenPageLimit(10);

        int startPos = imageUrls.size() / 2;
        monthBookPager.setCurrentItem(startPos);

        // viewpager2 간격 변환을 위함 -> res.values.dimes.xml에서 확인
        int pageMarginPx = getResources().getDimensionPixelOffset(R.dimen.monthBookPageMargin);
        int pagerWidth = getResources().getDimensionPixelOffset(R.dimen.monthBookPageWidth);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int offsetPx = screenWidth - pageMarginPx - pagerWidth;

        // viewpager2 간격 변환
        monthBookPager.setPageTransformer((page, position) -> page.setTranslationX(position * -offsetPx));

        monthBookPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    monthBookPager.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
    } // end of setupMonthViewPager

    // 대출 급상승(hotTrend) 도서 (최근 3일)
    private void getResponseApiHotTrend() {
        String getTime = mFormat.format(mDate); // 현재 날짜 가져오기

        String searchDt = getTime; // 시작 날짜
        String format = "json"; // 응답 형식 (예시)

        HttpConnection.getInstance(getContext()).getHotTrend(searchDt, new HttpConnection.HttpResponseCallback<List<SearchBook>>() {
            @Override
            public void onSuccess(List<SearchBook> books) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        List<String> imageUrls = new ArrayList<>();
                        List<String> bookName = new ArrayList<>();
                        List<String> authors = new ArrayList<>();
                        List<String> class_nm = new ArrayList<>();
                        List<String> isbn13 = new ArrayList<>();
                        for (SearchBook book : books) {
                            imageUrls.add(book.getBookImageUrl());
                            bookName.add(book.getBookName());
                            authors.add(book.getAuthors());
                            class_nm.add(book.getClass_nm());
                            isbn13.add(book.getIsbn13());
                        }
                        // ViewPager2에 이미지 추가
                        setupHotTrendViewPager(class_nm, bookName, authors, imageUrls, isbn13);
                        Log.d("API Response(HotTrend)", "Image URLs: " + imageUrls.toString() + ", BookName: " + bookName.toString());
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // 에러 처리 로직 추가
                        Log.e("API Failure(HotTrend)", "Error: " + e.getMessage());
                    });
                }
            }
        });
    } // end of getResponseApiHotTrend

    // 대출 급상승(hotTrend) 도서 ViewPager2 setup
    private void setupHotTrendViewPager(List<String> class_nm, List<String> bookName, List<String> authors, List<String> imageUrls, List<String> isbn13) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            // 이미지 URL이 없는 경우 처리
            return;
        }
        // 이미지 URL이 있을 경우 뷰페이저 설정
        if (getView() == null) {
            return;
        }
        hotTrendBookPager = getView().findViewById(R.id.hotTrend_book_viewpager);
        hotTrendBookAdapter = new HotTrendBookAdapter(requireActivity(), class_nm, bookName, authors, imageUrls, isbn13, this::showBookDetail);
        hotTrendBookPager.setAdapter(hotTrendBookAdapter);
        hotTrendBookPager.setCurrentItem(1000);
        hotTrendBookPager.setOffscreenPageLimit(10);

        int startPos = imageUrls.size() / 2;
        hotTrendBookPager.setCurrentItem(startPos);

        // viewpager2 간격 변환을 위함 -> res.values.dimes.xml에서 확인
        int pageMarginPx = getResources().getDimensionPixelOffset(R.dimen.hotTrendPageMargin);
        int pagerWidth = getResources().getDimensionPixelOffset(R.dimen.hotTrendPageWidth);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int offsetPx = screenWidth - pageMarginPx - pagerWidth;

        // viewpager2 간격 변환
        hotTrendBookPager.setPageTransformer((page, position) -> page.setTranslationX(position * -offsetPx));

        hotTrendBookPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    hotTrendBookPager.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
    } // end of setupHotTrendViewPager

    // 애니메이션
    private void startAnimation(View view) {
        tv_department = view.findViewById(R.id.tv_department);
        tv_popular_book_week = view.findViewById(R.id.tv_popular_book_week);
        tv_popular_book_month = view.findViewById(R.id.tv_popular_book_month);
        tv_hotTrend_title = view.findViewById(R.id.tv_hotTrend_book_title);

        anime_left_to_right = AnimationUtils.loadAnimation(getContext(), R.anim.anime_left_to_right);
        anime_right_to_left = AnimationUtils.loadAnimation(getContext(), R.anim.anime_right_to_left);

        tv_department.startAnimation(anime_right_to_left);
        tv_popular_book_week.startAnimation(anime_left_to_right);
        tv_popular_book_month.startAnimation(anime_right_to_left);
        tv_hotTrend_title.startAnimation(anime_left_to_right);
    } // end of startAnimation

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
