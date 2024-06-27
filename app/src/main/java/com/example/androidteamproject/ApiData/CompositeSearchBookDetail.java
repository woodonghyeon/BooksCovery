package com.example.androidteamproject.ApiData;

public class CompositeSearchBookDetail {
    private final SearchBookDetail bookDetail;
    private final SearchBookDetail maniaReaderBookDetail;

    public CompositeSearchBookDetail(SearchBookDetail bookDetail, SearchBookDetail maniaReaderBookDetail) {
        this.bookDetail = bookDetail;
        this.maniaReaderBookDetail = maniaReaderBookDetail;
    }

    public SearchBookDetail getBookDetail() {
        return bookDetail;
    }

    public SearchBookDetail getManiaReaderBookDetail() {
        return maniaReaderBookDetail;
    }
}
