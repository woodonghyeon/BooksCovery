package com.example.androidteamproject.Search;

public class LatelySearchBook {
    private String bookName;
    private String bookImageUrl;
    private String authors;

    public LatelySearchBook() {
    }

    public LatelySearchBook(String bookName, String authors, String bookImageUrl) {
        this.bookName = bookName;
        this.bookImageUrl = bookImageUrl;
        this.authors = authors;
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

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }
}

