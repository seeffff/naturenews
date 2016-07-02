package com.joedephillipo.naturenews;

//Object used to store the articles title and url
public class Article {

    //Variables used
    String mArticleTitle;
    String mArticleUrl;

    //Constructor
    public Article(String articleTitle, String articleUrl){
        this.mArticleTitle = articleTitle;
        this.mArticleUrl = articleUrl;
    }

    //Getters
    public String getArticleTitle(){
        return mArticleTitle;
    }

    public String getArticleUrl(){
        return mArticleUrl;
    }

}
