package com.fm.commons.rx;

import rx.Subscriber;

/**
 * Created by admin on 2016/10/21.
 */

public class RxUtils {

    public static Subscriber createSubscriber(final OnNext onNext) {
        return new Subscriber() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {
                onNext.onNext(o);
            }
        };
    }


}
