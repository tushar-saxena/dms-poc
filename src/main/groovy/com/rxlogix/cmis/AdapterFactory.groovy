package com.rxlogix.cmis


class AdapterFactory {
    public static AdapterInterface getAdapter(def settings) {
        // add other adapter implementations if needed
        DefaultAdapter aa = new DefaultAdapter();
        aa.init(settings)
        return aa;
    }
}
