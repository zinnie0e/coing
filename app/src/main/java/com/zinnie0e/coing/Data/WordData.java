package com.zinnie0e.coing.Data;

public class WordData {
    private String word_en;
    private String word_ko;

    public WordData(String word_en, String word_ko){
        this.word_en = word_en;
        this.word_ko = word_ko;
    }
    
    public String getWord_en()
    {
        return this.word_en;
    }
    public String getWord_ko()
    {
        return this.word_ko;
    }
}
