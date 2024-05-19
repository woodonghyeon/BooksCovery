package com.example.androidteamproject.Search;

public class LatelySearchBook {
    private String bookName;
    private String bookImageUrl;

    public LatelySearchBook() {
    }

    public LatelySearchBook(String bookName, String bookImageUrl) {
        this.bookName = bookName;
        this.bookImageUrl = bookImageUrl;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookImageUrl() {
        return bookImageUrl;
    }

    public void setBookImageUrl(String bookImageUrl) {
        this.bookImageUrl = bookImageUrl;
    }
}

