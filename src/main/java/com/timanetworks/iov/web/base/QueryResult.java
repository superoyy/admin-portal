package com.timanetworks.iov.web.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dukla on 10/27/16.
 */
public class QueryResult implements Serializable {

    int start = 0;

    int count = 20;

    int total;

    Map<String,String> orders=new HashMap<>();

    List<Map<String,Object>> rows=new ArrayList<>();

    List<Map<String,String>> footer=new ArrayList<>();

    public QueryResult(int page,int pageSize){
        this.count = pageSize;
        this.start = (page - 1) * this.count;
    }

    public QueryResult(){
    }


    public int getStart() {
        return start;
    }

    public int getCount() {
        return count;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Map<String, Object>> getRows() {
        return rows;
    }

    public void setRows(List<Map<String, Object>> rows) {
        this.rows = rows;
    }

    public Map<String, String> getOrders() {
        return orders;
    }

    public List<Map<String, String>> getFooter() {
        return footer;
    }

    public void setFooter(List<Map<String, String>> footer) {
        this.footer = footer;
    }
}
