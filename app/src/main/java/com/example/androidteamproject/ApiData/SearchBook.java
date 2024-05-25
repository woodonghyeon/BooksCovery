package com.example.androidteamproject.ApiData;

public class SearchBook {
    private String bookName;
    private String bookImageUrl;
    private String authors;
    private String class_nm;
    private String isbn13;

    public SearchBook() {
    }

    public SearchBook(String class_nm, String bookName, String authors, String bookImageUrl, String isbn13) {
        this.bookName = bookName;
        this.bookImageUrl = bookImageUrl;
        this.authors = authors;
        this.class_nm = class_nm;
        this.isbn13 = isbn13;
    }
    public String getClass_nm() {
        return class_nm;
    }

    public void setClass_nm(String class_nm) {
        this.class_nm = class_nm;
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

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }
}

