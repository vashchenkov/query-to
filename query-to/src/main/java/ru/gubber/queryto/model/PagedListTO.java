package ru.gubber.queryto.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Gubber
 * Date: 01.05.14
 * Time: 16:00
 */
public class PagedListTO {

    private int allCount;
    private int page = 0;
    private int perPage = 10;
    private int pagesCount;
    private List items;
    private List<FilterTO> filters= new ArrayList<>();
    private List<SorterTO> sorters = new ArrayList<>();

    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }

    public int getPagesCount() {
        return pagesCount;
    }

    public void setPagesCount(int pagesCount) {
        this.pagesCount = pagesCount;
    }

    public List<FilterTO> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterTO> filters) {
        this.filters = filters;
    }

    public List<SorterTO> getSorters() {
        return sorters;
    }

    public void setSorters(List<SorterTO> sorters) {
        this.sorters = sorters;
    }
}