package com.sophimp.are.render;

import android.text.Editable;

import com.sophimp.are.inner.Html;

import org.xml.sax.XMLReader;

import java.util.Stack;

public class DRTagHandler implements Html.TagHandler {

    private static Stack OL_STACK = new Stack();

    private static class OL {
        public int level;
    }

    private static class UL {
        public int level;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if (opening) {
            if ("ol".equalsIgnoreCase(tag)) {
                startOL();
            } else if ("li".equalsIgnoreCase(tag)) {
//                startLI();
            } else if ("ul".equalsIgnoreCase(tag)) {
//                startUL();
            }
        } else {
            if ("ol".equalsIgnoreCase(tag)) {
//                endOL();
            } else if ("li".equalsIgnoreCase(tag)) {
//                endLI();
            } else if ("ul".equalsIgnoreCase(tag)) {
//                endUL();
            }
        }
    }

    private static void startOL() {
        OL ol = new OL();

    }

    private static void startLI() {

    }

    private static void startUL() {

    }
}
