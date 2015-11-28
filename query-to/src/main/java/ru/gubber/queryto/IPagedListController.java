package ru.gubber.queryto;

import ru.gubber.query.PagedList;
import ru.gubber.queryto.model.PagedListTO;

/**
 * Created by gubber on 28.11.2015.
 */
public interface IPagedListController {

	public PagedListTO fillPagedList(PagedList pagedList);
}