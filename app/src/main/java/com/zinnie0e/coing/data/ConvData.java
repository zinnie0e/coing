package com.zinnie0e.coing.data;

public class ConvData {
    private String conv_en;
    private String conv_ko;

    public ConvData(String conv_en, String conv_ko){
        this.conv_en = conv_en;
        this.conv_ko = conv_ko;
    }
    
    public String getConv_en()
    {
        return this.conv_en;
    }
    public String getConv_ko()
    {
        return this.conv_ko;
    }
}
