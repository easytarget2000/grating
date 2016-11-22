package org.eztarget.grating;

public interface OnClickButtonListener {

    class RateButton {
        public static final int RATE = 1;
        public static final int DECLINE = 2;
        public static final int REMIND = 3;
    }

    void onClickButton(int which);

}