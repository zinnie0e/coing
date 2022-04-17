package com.zinnie0e.coing.data;

public class WordData {
    private String word_en;
    private String word_ko;
    private boolean is_save;

    public WordData(String word_en, String word_ko){
        this.word_en = word_en;
        this.word_ko = word_ko;
        this.is_save = false;
    }
    
    public String getWord_en()
    {
        return this.word_en;
    }
    public String getWord_ko()
    {
        return this.word_ko;
    }
    public boolean getIs_save(){ return this.is_save; }

    public void setIs_save(boolean is_save) {
        this.is_save = is_save;
    }
}
