package com.example.androidteamproject.Home;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageScraper {
    private static final String BASE_URL = "http://lib.yjc.ac.kr/WebYJC/Aspx/board/";
    private static final String LIST_URL = BASE_URL + "boardlist.aspx?tbname=1view&SearchType=&searchtext=&startpage=1";

    public List<String> scrapeLatestPostUrls(int numPosts) {
        List<String> postUrls = new ArrayList<>();
        try {
            Connection connection = Jsoup.connect(LIST_URL).userAgent("Mozilla/5.0").timeout(6000);
            Document doc = connection.get();
            Elements postElements = doc.select("a[id*='rptNoticeList_ct'][id$='_hlkLink']");

            for (int i = 0; i < Math.min(numPosts, postElements.size()); i++) {
                String href = postElements.get(i).attr("href");
                String absoluteUrl = BASE_URL + href;
                postUrls.add(absoluteUrl);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return postUrls;
    }

    public List<String> scrapeImagesFromPosts(List<String> postUrls) {
        List<String> imageUrls = new ArrayList<>();
        for (String postUrl : postUrls) {
            try {
                Connection connection = Jsoup.connect(postUrl).userAgent("Mozilla/5.0").timeout(6000);
                Document doc = connection.get();
                Elements images = doc.select("div.substance img"); // 'substance' div 안의 img 태그 선택

                for (Element img : images) {
                    String src = img.attr("src");
                    if (!src.startsWith("http")) {
                        src = src.startsWith("/") ? "http://lib.yjc.ac.kr" + src : BASE_URL + src;
                    }
                    imageUrls.add(src);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imageUrls;
    }

    public static void main(String[] args) {
        ImageScraper scraper = new ImageScraper();
        List<String> latestPostUrls = scraper.scrapeLatestPostUrls(10); // 최신 10개의 게시글 URL 가져오기

        // 결과 출력
        if (latestPostUrls.isEmpty()) {
            System.out.println("최신 게시글 URL을 가져오지 못했습니다. 셀렉터나 URL을 확인하세요.");
        } else {
            System.out.println("최신 게시글 URL:");
            for (String url : latestPostUrls) {
                System.out.println(url);
            }
        }

        List<String> imageUrls = scraper.scrapeImagesFromPosts(latestPostUrls);

        // 결과 출력
        if (imageUrls.isEmpty()) {
            System.out.println("이미지를 가져오지 못했습니다. 셀렉터나 URL을 확인하세요.");
        } else {
            System.out.println("이미지 URL:");
            for (String url : imageUrls) {
                System.out.println(url);
            }
        }
    }
}
