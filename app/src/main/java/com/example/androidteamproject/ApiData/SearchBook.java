package com.example.androidteamproject.ApiData;

public class SearchBook {
    private String bookName;
    private String bookImageUrl;
    private String authors;

    public SearchBook() {
    }

    public SearchBook(String bookName, String authors, String bookImageUrl) {
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

