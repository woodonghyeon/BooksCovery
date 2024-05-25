package com.example.androidteamproject.ApiData;

public class SearchBookDetail {
    private String bookName, authors, publisher, bookImageUrl, description, publication_year, isbn13, vol, class_no, class_nm, loanCnt;

    public SearchBookDetail(String bookName, String authors, String publisher, String bookImageUrl, String description, String publication_year, String isbn13, String vol, String class_no, String class_nm, String loanCnt) {
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
    public String toString() { // 로그 후에 삭제
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
