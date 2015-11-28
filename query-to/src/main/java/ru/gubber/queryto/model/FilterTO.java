package ru.gubber.queryto.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gubber on 29.04.2015.
 */
public class FilterTO {
    private String filterName;
    private List filterValues;

    public FilterTO() {
    }

    public FilterTO(String filterName, List filterValues) {
        this.filterName = filterName;
        this.filterValues = filterValues;
    }

    public FilterTO(String filterName, Object... values) {
        this.filterName = filterName;
        filterValues = new ArrayList<>();
        for (Object value : values) {
            filterValues.add(value);
        }
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public List getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(List filterValues) {
        this.filterValues = filterValues;
    }
}