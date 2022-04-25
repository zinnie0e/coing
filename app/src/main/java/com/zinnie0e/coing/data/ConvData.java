package com.zinnie0e.coing.data;

public class ConvData {
    private String conv_en;
    private String conv_ko;
    private String status;
    private boolean is_bookmark;

    public ConvData(String conv_en, String conv_ko, String status){
        this.conv_en = conv_en;
        this.conv_ko = conv_ko;
        this.status = status;
        this.is_bookmark = false;
    }

    public String getConv_en()
    {
        return this.conv_en;
    }
    public String getConv_ko()
    {
        return this.conv_ko;
    }
    public String getStatus()
    {
        return this.status;
    }
    public boolean getIs_bookmark(){ return this.is_bookmark; }

    public void setIs_bookmark(boolean is_bookmark) {
        this.is_bookmark = is_bookmark;
    }
}
