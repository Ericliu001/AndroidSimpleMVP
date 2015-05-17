package com.ericliudeveloper.mvpevent;

/**
 * Created by eric.liu on 18/05/15.
 */
public final class MyEvents {
    private MyEvents(){}

    public static class NameSetEvent{
        public final String name;

        public NameSetEvent(String name){
            this.name = name;
        }
    }
}
