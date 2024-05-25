package com.example.androidteamproject.ApiData;

public class SearchBookDetail {
    private String bookName, authors, publisher, bookImageUrl, description, publication_year, isbn13, vol, class_no, class_nm, loanCnt,
            month, loanHistoryCnt, ranking, age, gender, loanGrpsCnt, loanGrpsRanking, word, weight;

    public SearchBookDetail(String bookName, String authors, String publisher, String bookImageUrl, String description, String publication_year, String isbn13, String vol, String class_no, String class_nm, String loanCnt,
                            String month, String loanHistoryCnt, String ranking, String age, String gender, String loanGrpsCnt, String loanGrpsRanking, String word, String weight) {
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

    public String getPublication_year() {
        return publication_year;
    }

    public void setPublication_year(String publication_year) {
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

    public String getLoanCnt() {
        return loanCnt;
    }

    public void setLoanCnt(String loanCnt) {
        this.loanCnt = loanCnt;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getLoanHistoryCnt() {
        return loanHistoryCnt;
    }

    public void setLoanHistoryCnt(String loanHistoryCnt) {
        this.loanHistoryCnt = loanHistoryCnt;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLoanGrpsCnt() {
        return loanGrpsCnt;
    }

    public void setLoanGrpsCnt(String loanGrpsCnt) {
        this.loanGrpsCnt = loanGrpsCnt;
    }

    public String getLoanGrpsRanking() {
        return loanGrpsRanking;
    }

    public void setLoanGrpsRanking(String loanGrpsRanking) {
        this.loanGrpsRanking = loanGrpsRanking;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
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
}
