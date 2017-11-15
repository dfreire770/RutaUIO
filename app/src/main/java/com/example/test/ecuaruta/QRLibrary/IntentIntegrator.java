package com.example.test.ecuaruta.QRLibrary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class IntentIntegrator {

    public static final int REQUEST_CODE = 0x0000c0de; // Only use bottom 16 bits
    private static final String TAG = IntentIntegrator.class.getSimpleName();

    public static final String DEFAULT_TITLE = "Install Barcode Scanner?";
    public static final String DEFAULT_MESSAGE =
            "This application requires Barcode Scanner. Would you like to install it?";
    public static final String DEFAULT_YES = "Yes";
    public static final String DEFAULT_NO = "No";

    private static final String PACKAGE = "com.google.zxing.client.android";

    // supported barcode formats
    public static final String PRODUCT_CODE_TYPES = "UPC_A,UPC_E,EAN_8,EAN_13";
    public static final String ONE_D_CODE_TYPES = PRODUCT_CODE_TYPES + ",CODE_39,CODE_93,CODE_128";
    public static final String QR_CODE_TYPES = "QR_CODE";
    public static final String ALL_CODE_TYPES = null;

    public static final Method PACKAGE_SETTER;
    static {
        Method temp;
        try {
            temp = Intent.class.getMethod("setPackage", String.class);
        } catch (NoSuchMethodException nsme) {
            temp = null;
        }
        PACKAGE_SETTER = temp;
    }

    public IntentIntegrator() {
    }

    /**
     * See {@link #initiateScan(Activity, CharSequence, CharSequence, CharSequence, CharSequence)} --
     * same, but uses default English labels.
     */
    public static AlertDialog initiateScan(Activity activity) {
        return initiateScan(activity, DEFAULT_TITLE, DEFAULT_MESSAGE, DEFAULT_YES, DEFAULT_NO);
    }

    /**
     * See {@link #initiateScan(Activity, CharSequence, CharSequence, CharSequence, CharSequence)} --
     * same, but takes string IDs which refer
     * to the {@link Activity}'s resource bundle entries.
     */
    public static AlertDialog initiateScan(Activity activity,
                                           int stringTitle,
                                           int stringMessage,
                                           int stringButtonYes,
                                           int stringButtonNo) {
        return initiateScan(activity,
                activity.getString(stringTitle),
                activity.getString(stringMessage),
                activity.getString(stringButtonYes),
                activity.getString(stringButtonNo));
    }

    public static AlertDialog initiateScan(Activity activity,
                                           CharSequence stringTitle,
                                           CharSequence stringMessage,
                                           CharSequence stringButtonYes,
                                           CharSequence stringButtonNo) {

        return initiateScan(activity,
                stringTitle,
                stringMessage,
                stringButtonYes,
                stringButtonNo,
                ALL_CODE_TYPES);
    }
    public static AlertDialog initiateScan(Activity activity,
                                           CharSequence stringTitle,
                                           CharSequence stringMessage,
                                           CharSequence stringButtonYes,
                                           CharSequence stringButtonNo,
                                           CharSequence stringDesiredBarcodeFormats) {
        Intent intentScan = new Intent(PACKAGE + ".SCAN");
        setPackage(intentScan);
        intentScan.addCategory(Intent.CATEGORY_DEFAULT);

        // check which types of codes to scan for
        if (stringDesiredBarcodeFormats != null) {
            // set the desired barcode types
            intentScan.putExtra("SCAN_FORMATS", stringDesiredBarcodeFormats);
        }

        try {
            activity.startActivityForResult(intentScan, REQUEST_CODE);
            return null;
        } catch (ActivityNotFoundException e) {
            return showDownloadDialog(activity, stringTitle, stringMessage, stringButtonYes, stringButtonNo);
        }
    }

    private static AlertDialog showDownloadDialog(final Activity activity,
                                                  CharSequence stringTitle,
                                                  CharSequence stringMessage,
                                                  CharSequence stringButtonYes,
                                                  CharSequence stringButtonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
        downloadDialog.setTitle(stringTitle);
        downloadDialog.setMessage(stringMessage);
        downloadDialog.setPositiveButton(stringButtonYes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + PACKAGE);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    activity.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {
                    // Hmm, market is not installed
                    Log.w(TAG, "Android Market is not installed; cannot install Barcode Scanner");
                }
            }
        });
        downloadDialog.setNegativeButton(stringButtonNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });
        return downloadDialog.show();
    }
    public static IntentResult parseActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String formatName = intent.getStringExtra("SCAN_RESULT_FORMAT");
                return new IntentResult(contents, formatName);
            } else {
                return new IntentResult(null, null);
            }
        }
        return null;
    }

    public static void shareText(Activity activity, CharSequence text) {
        shareText(activity, text, DEFAULT_TITLE, DEFAULT_MESSAGE, DEFAULT_YES, DEFAULT_NO);
    }

    public static void shareText(Activity activity,
                                 CharSequence text,
                                 int stringTitle,
                                 int stringMessage,
                                 int stringButtonYes,
                                 int stringButtonNo) {
        shareText(activity,
                text,
                activity.getString(stringTitle),
                activity.getString(stringMessage),
                activity.getString(stringButtonYes),
                activity.getString(stringButtonNo));
    }
    public static void shareText(Activity activity,
                                 CharSequence text,
                                 CharSequence stringTitle,
                                 CharSequence stringMessage,
                                 CharSequence stringButtonYes,
                                 CharSequence stringButtonNo) {
        Intent intent = new Intent();
        intent.setAction(PACKAGE + ".ENCODE");
        setPackage(intent);
        intent.putExtra("ENCODE_TYPE", "TEXT_TYPE");
        intent.putExtra("ENCODE_DATA", text);
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            showDownloadDialog(activity, stringTitle, stringMessage, stringButtonYes, stringButtonNo);
        }
    }

    private static void setPackage(Intent intent) {
        if (PACKAGE_SETTER != null) {
            try {
                PACKAGE_SETTER.invoke(intent, PACKAGE);
            } catch (InvocationTargetException ite) {
                Log.w(TAG, ite.getTargetException());
            } catch (IllegalAccessException iae) {
                Log.w(TAG, iae);
            }
        }
    }

}