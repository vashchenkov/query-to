package ru.gubber.queryto;

import ru.gubber.query.PagedList;
import ru.gubber.queryto.model.PagedListTO;
import ru.gubber.utils.trasformutils.transformation.transformator.Domain2TOTransformer;

import java.util.List;

/**
 * Created by gubber on 28.11.2015.
 */
public class PagedListProcessor {
	private IPagedListController controller;

	public PagedListTO getItems(PagedList origin, Class itemsClass) {
		PagedListTO result = controller.fillPagedList(origin);
		if (itemsClass != null) {
			List resultItems = (List) Domain2TOTransformer.transform(result.getItems(), itemsClass);
			result.setItems(resultItems);
		}
		return result;
	}

	public PagedListProcessor setController(IPagedListController controller) {
		this.controller = controller;
		return this;
	}
}