package com.example.androidteamproject.ApiData;

import java.util.List;

public class SearchBookDetail {
    private String bookName, authors, publisher, bookImageUrl, description, isbn13, vol, class_no, class_nm;
    private int publication_year, loanCnt, book_count;
    private List<String> month, loanHistoryCnt, ranking;
    private List<String> age, gender, loanGrpsCnt, loanGrpsRanking;
    private List<String> word, weight;
    private List<String> maniaIsbn13List, readerIsbn13List;
    private String maniaBookName, maniaAuthor, maniaImageUrl, maniaIsbn13;
    private String readerBookName, readerAuthor, readerImageUrl, readerIsbn13;

    public SearchBookDetail() {
    }

    public SearchBookDetail(String isbn13) {
        this.isbn13 = isbn13;
    }

    public SearchBookDetail(List<String> maniaIsbn13, List<String> readerIsbn13) {
        this.maniaIsbn13List = maniaIsbn13;
        this.readerIsbn13List = readerIsbn13;
    }

    public SearchBookDetail(String maniaBookName, String maniaAuthor, String maniaImageUrl, String maniaIsbn13) {
        this.maniaBookName = maniaBookName;
        this.maniaAuthor = maniaAuthor;
        this.maniaImageUrl = maniaImageUrl;
        this.maniaIsbn13 = maniaIsbn13;
    }

    public SearchBookDetail(String readerBookName, String readerImageUrl, String readerIsbn13) {
        this.readerBookName = readerBookName;
        this.readerImageUrl = readerImageUrl;
        this.readerIsbn13 = readerIsbn13;
    }

    public SearchBookDetail(String bookName, String authors, String bookImageUrl, String publisher, int publication_year, String isbn13, String class_no, int loanCnt, int book_count) {
        this.bookName = bookName;
        this.authors = authors;
        this.bookImageUrl = bookImageUrl;
        this.publisher = publisher;
        this.publication_year = publication_year;
        this.isbn13 = isbn13;
        this.class_no = class_no;
        this.loanCnt = loanCnt;
        this.book_count = book_count;
    }

    public SearchBookDetail(String bookName, String authors, String publisher, String bookImageUrl, String description, int publication_year, String isbn13, String vol, String class_no, String class_nm, int loanCnt,
                            List<String> month, List<String> loanHistoryCnt, List<String> ranking, List<String> age, List<String> gender, List<String> loanGrpsCnt, List<String> loanGrpsRanking, List<String> word, List<String> weight) {
        this.bookName = bookName;
        this.authors = authors;
        this.publisher = publisher;
        this.bookImageUrl = bookImageUrl;
        this.description = description;
        this.publication_year = publication_year;
        this.isbn13 = isbn13;
        this.vol = vol;
        this.class_no = class_no;
        this.class_nm = class_nm;
        this.loanCnt = loanCnt;
        this.month = month;
        this.loanHistoryCnt = loanHistoryCnt;
        this.ranking = ranking;
        this.age = age;
        this.gender = gender;
        this.loanGrpsCnt = loanGrpsCnt;
        this.loanGrpsRanking = loanGrpsRanking;
        this.word = word;
        this.weight = weight;
    }

    // Getter 및 Setter 메소드
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

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getBookImageUrl() {
        return bookImageUrl;
    }

    public void setBookImageUrl(String bookImageUrl) {
        this.bookImageUrl = bookImageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPublication_year() {
        return publication_year;
    }

    public void setPublication_year(int publication_year) {
        this.publication_year = publication_year;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getVol() {
        return vol;
    }

    public void setVol(String vol) {
        this.vol = vol;
    }

    public String getClass_no() {
        return class_no;
    }

    public void setClass_no(String class_no) {
        this.class_no = class_no;
    }

    public String getClass_nm() {
        return class_nm;
    }

    public void setClass_nm(String class_nm) {
        this.class_nm = class_nm;
    }

    public int getLoanCnt() {
        return loanCnt;
    }

    public void setLoanCnt(int loanCnt) {
        this.loanCnt = loanCnt;
    }

    public List<String> getMonth() {
        return month;
    }

    public void setMonths(List<String> month) {
        this.month = month;
    }

    public List<String> getLoanHistoryCnt() {
        return loanHistoryCnt;
    }

    public void setLoanHistoryCnt(List<String> loanHistoryCnt) {
        this.loanHistoryCnt = loanHistoryCnt;
    }

    public List<String> getRankings() {
        return ranking;
    }

    public void setRanking(List<String> ranking) {
        this.ranking = ranking;
    }

    public List<String> getAge() {
        return age;
    }

    public void setAge(List<String> age) {
        this.age = age;
    }

    public List<String> getGender() {
        return gender;
    }

    public void setGender(List<String> gender) {
        this.gender = gender;
    }

    public List<String> getLoanGrpsCnt() {
        return loanGrpsCnt;
    }

    public void setLoanGrpsCnt(List<String> loanGrpsCnt) {
        this.loanGrpsCnt = loanGrpsCnt;
    }

    public List<String> getLoanGrpsRanking() {
        return loanGrpsRanking;
    }

    public void setLoanGrpsRanking(List<String> loanGrpsRanking) {
        this.loanGrpsRanking = loanGrpsRanking;
    }

    public List<String> getWord() {
        return word;
    }

    public void setWord(List<String> word) {
        this.word = word;
    }

    public List<String> getWeight() {
        return weight;
    }

    public void setWeight(List<String> weight) {
        this.weight = weight;
    }

    public int getBook_count() {
        return book_count;
    }

    public void setBook_count(int book_count) {
        this.book_count = book_count;
    }

    public String getManiaBookName() {
        return maniaBookName;
    }

    public void setManiaBookName(String maniaBookName) {
        this.maniaBookName = maniaBookName;
    }

    public String getManiaAuthor() {
        return maniaAuthor;
    }

    public void setManiaAuthor(String maniaAuthor) {
        this.maniaAuthor = maniaAuthor;
    }

    public String getManiaImageUrl() {
        return maniaImageUrl;
    }

    public void setManiaImageUrl(String maniaImageUrl) {
        this.maniaImageUrl = maniaImageUrl;
    }

    public List<String> getManiaIsbn13List() {
        return maniaIsbn13List;
    }

    public void setManiaIsbn13List(List<String> maniaIsbn13List) {
        this.maniaIsbn13List = maniaIsbn13List;
    }

    public List<String> getReaderIsbn13List() {
        return readerIsbn13List;
    }

    public void setReaderIsbn13List(List<String> readerIsbn13List) {
        this.readerIsbn13List = readerIsbn13List;
    }
    @Override
    public String toString() { // 로그 후에 삭제
        return "SearchBookDetail{" +
                "bookName='" + bookName + '\'' +
                ", authors='" + authors + '\'' +
                ", publisher='" + publisher + '\'' +
                ", bookImageUrl='" + bookImageUrl + '\'' +
                ", description='" + description + '\'' +
                ", publication_year='" + publication_year + '\'' +
                ", isbn13='" + isbn13 + '\'' +
                ", vol='" + vol + '\'' +
                ", class_no='" + class_no + '\'' +
                ", class_nm='" + class_nm + '\'' +
                ", loanCnt='" + loanCnt + '\'' +
                ", month='" + month + '\'' +
                ", loanHistoryCnt='" + loanHistoryCnt + '\'' +
                ", ranking='" + ranking + '\'' +
                ", age='" + age + '\'' +
                ", gender='" + gender + '\'' +
                ", loanGrpsCnt='" + loanGrpsCnt + '\'' +
                ", loanGrpsRanking='" + loanGrpsRanking + '\'' +
                ", word='" + word + '\'' +
                ", weight='" + weight + '\'' +
                '}';
    }

    public String getManiaIsbn13() {
        return maniaIsbn13;
    }

    public void setManiaIsbn13(String maniaIsbn13) {
        this.maniaIsbn13 = maniaIsbn13;
    }

    public String getReaderBookName() {
        return readerBookName;
    }

    public void setReaderBookName(String readerBookName) {
        this.readerBookName = readerBookName;
    }

    public String getReaderAuthor() {
        return readerAuthor;
    }

    public void setReaderAuthor(String readerAuthor) {
        this.readerAuthor = readerAuthor;
    }

    public String getReaderImageUrl() {
        return readerImageUrl;
    }

    public void setReaderImageUrl(String readerImageUrl) {
        this.readerImageUrl = readerImageUrl;
    }

    public String getReaderIsbn13() {
        return readerIsbn13;
    }

    public void setReaderIsbn13(String readerIsbn13) {
        this.readerIsbn13 = readerIsbn13;
    }
}
