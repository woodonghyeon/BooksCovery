package com.example.androidteamproject.ApiData;

import java.io.Serializable;

public class SearchBookAuthor implements Serializable {
    private String isbn13, bookName, bookImageUrl, authors, publisher, publication_year;

    public SearchBookAuthor() {
    }

    public SearchBookAuthor(String isbn13, String bookName, String authors, String bookImageUrl, String publisher, String publication_year) {
        this.isbn13 = isbn13;
        this.bookName = bookName;
        this.bookImageUrl = bookImageUrl;
        this.authors = authors;
        this.publisher = publisher;
        this.publication_year = publication_year;
    }

    public String getIsbn13() {
        return isbn13;
    }
    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
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
    public String getPublisher() {
        return publisher;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    public String getPublication_year() {
        return publication_year;
    }
    public void setPublication_year(String publication_year) { this.publication_year = publication_year; }
}

