package com.ericliudeveloper.withmvp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by liu on 14/05/15.
 */
public class FirstModel implements Parcelable{

    public static final String KEY_DIRECTION = "direction";
    private Direction direction;
    enum Direction{
        LEFT, RIGHT;
    }


    public static final String KEY_PROGRESS = "progress";
    private int progress;

    public static final String KEY_NAME = "name";
    private String name;

    // empty constructor
    public FirstModel(){}

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(direction);
        dest.writeInt(progress);
        dest.writeString(name);
    }

    public static final Parcelable.Creator<FirstModel> CREATOR
            = new Parcelable.Creator<FirstModel>() {
        public FirstModel createFromParcel(Parcel in) {
            return new FirstModel(in);
        }

        public FirstModel[] newArray(int size) {
            return new FirstModel[size];
        }
    };

    private FirstModel(Parcel in) {
        direction = (Direction) in.readSerializable();
        progress = in.readInt();
        name = in.readString();
    }


}
