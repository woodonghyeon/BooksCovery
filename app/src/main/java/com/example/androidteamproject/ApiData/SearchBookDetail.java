package com.example.androidteamproject.ApiData;

public class SearchBookDetail {
    private String class_nm;
    private String bookName;
    private String authors;
    private String bookImageUrl;
    private String isbn13;
    private String description;

    public SearchBookDetail(String class_nm, String bookName, String authors, String bookImageUrl, String isbn13, String description) {
        this.class_nm = class_nm;
        this.bookName = bookName;
        this.authors = authors;
        this.bookImageUrl = bookImageUrl;
        this.isbn13 = isbn13;
        this.description = description;
    }

    // Getter 및 Setter 메소드
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

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getBookImageUrl() {
        return bookImageUrl;
    }

    public void setBookImageUrl(String bookImageUrl) {
        this.bookImageUrl = bookImageUrl;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "SearchBookDetail{" +
                "class_nm='" + class_nm + '\'' +
                ", bookName='" + bookName + '\'' +
                ", authors='" + authors + '\'' +
                ", bookImageUrl='" + bookImageUrl + '\'' +
                ", isbn13='" + isbn13 + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
