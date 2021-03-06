package com.bn.thread;

import static com.bn.constant.SourceConstant.*;

public class HoleThread extends Thread {
    boolean ismin = false;
    boolean iscolor = false;
    boolean isST = false;

    public HoleThread() {
        super("HoleThread");
    }

    private boolean mIsStop = false;
    public void quitSync(){
        mIsStop = true ;
        try {
            interrupt();
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (!mIsStop) {//循环定时移动炮弹
            try {
                if (!iscolor) {
                    ColorCS += 1;
                    if (ColorCS > 75) {
                        iscolor = true;
                    }
                }
                if (iscolor) {
                    ColorCS -= 1;
                    if (ColorCS < 5) {
                        iscolor = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
