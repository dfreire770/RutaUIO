package com.example.test.ecuaruta.QRLibrary;

public final class IntentResult {

    private final String contents;
    private final String formatName;

    IntentResult(String contents, String formatName) {
        this.contents = contents;
        this.formatName = formatName;
    }

    /**
     * @return raw content of barcode
     */
    public String getContents() {
        return contents;
    }

    public String getFormatName() {
        return formatName;
    }

}