package com.microservices.demo.common.util;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtils {
    private CollectionUtils() {
    }
    private static class  CollectionUtilsHolder{
   static final CollectionUtils INSTANCE = new CollectionUtils(); // BUNU DIREKT CollectionUtils CLASSIAN YAZARDIM AMA BU DURUMDA EAGER INITIALIZATION OLUR. BUNU LAZY INITIALIZATION YAPMAK İÇİN HOLDER CLASSINI KULLANIYORUZ.
    }
    public static CollectionUtils getInstance() {
        return CollectionUtilsHolder.INSTANCE;
    }
    public <T> List<T> getListFromIterable(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
}
