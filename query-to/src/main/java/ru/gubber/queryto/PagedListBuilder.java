package ru.gubber.queryto;

import org.hibernate.type.Type;
import ru.gubber.query.PagedList;
import ru.gubber.query.filter.CompositeFilter;
import ru.gubber.query.filter.Filter;
import ru.gubber.query.filter.OneValueCompositeFilter;
import ru.gubber.query.sorter.Sorter;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Created by gubber on 28.11.2015.
 */
class PagedListBuilder {

	protected Set<String> obligatoryFilterNames = new HashSet<>();
	protected Set<String> filterNames = new HashSet<>();

	public static final int DEFAULT_PER_PAGE = 10;

	protected PagedList list;

	public PagedListBuilder(Class clazz) {
		this(clazz, 0, DEFAULT_PER_PAGE);
	}

	public PagedListBuilder(Class clazz, int page, int perPage) {
		list = new PagedList(clazz);
		list.setItemsPerPage(perPage);
		list.setPage(page);
	}

	public PagedList generateList() {
		return list;
	}

	@Nonnull
	public PagedListBuilder addSorter(@Nonnull Sorter sorter) {
		list.addSorter(sorter);
		return this;
	}

	public PagedListBuilder addRootFilter(Filter filter) {
		list.setFilter(filter);
		return this;
	}

	public PagedListBuilder addFilter(String parentFilterName, String filterName, Filter filter, boolean obligatory) {
		if (obligatory && filter.isEmpty()) {
			throw new IllegalStateException("Obligatory filter can't be empty");
		}
		Filter parentFilter = getFilterByFullName(parentFilterName);
		if (parentFilter == null)
			throw new IllegalStateException("Parent filter can't be null");
		if (parentFilter instanceof CompositeFilter) {
			String fullFilterName = evaluateFullFilterName(parentFilterName, filterName);
			if (obligatory) {
				if (obligatoryFilterNames.contains(fullFilterName)) {
					throw new IllegalStateException("Can't add second obligatory filter with name ");
				}
				obligatoryFilterNames.add(fullFilterName);
				((CompositeFilter) parentFilter).addObligatoryFilter(filterName, filter);
			} else {
				filterNames.add(fullFilterName);
				((CompositeFilter) parentFilter).addFilter(filterName, filter);
			}
			return this;
		} else throw new IllegalStateException("Root filter must be composite");
	}

	private Filter getFilterByFullName(String parentFilterName) {
		Filter parentFilter = list.getFilter();
		if (parentFilterName != null) {
			String[] filterNames = parentFilterName.split("_");
			parentFilter = findFilterByPath(parentFilter, filterNames, 0);
		}
		return parentFilter;
	}

	protected String evaluateFullFilterName(String parentFilterName, String filterName) {
		return (parentFilterName != null ? (parentFilterName + "_") : "") + filterName;
	}

	public PagedListBuilder addOneValueCompositeFilter(String parentFilterName, String filterName, Type type, String operation,
	                                                   Map<String, Filter> filtersMap) {
		Filter parentFilter = list.getFilter();
		OneValueCompositeFilter addingFilter = new OneValueCompositeFilter(type);
		addingFilter.setOperator(operation);
		for (String name : filtersMap.keySet()) {
			addingFilter.addFilter(name, filtersMap.get(name));
		}
		if (parentFilterName != null) {
			String[] filterNames = parentFilterName.split("_");
			parentFilter = findFilterByPath(parentFilter, filterNames, 0);
		}
		if (parentFilter == null)
			throw new IllegalStateException("Parent filter can't be null");
		if (parentFilter instanceof CompositeFilter) {
			((CompositeFilter) parentFilter).addFilter(filterName, addingFilter);
			String fullFilterName = evaluateFullFilterName(parentFilterName, filterName);
			filterNames.add(fullFilterName);
			return this;
		} else throw new IllegalStateException("Root filter must be composite");
	}

	private static Filter findFilterByPath(Filter parentFilter, String[] filterNames, int i) {
		if (i >= filterNames.length)
			throw new IndexOutOfBoundsException("Filter names length = " + filterNames.length + ", depth = " + i);
		if (parentFilter instanceof CompositeFilter) {
			String filterName = filterNames[i++];
			Filter f = parentFilter.getSubFilter(filterName);
			if (f == null) {
				if ((i - 1) == 0)
					throw new IllegalStateException("There is no filter with name '" + filterName + "' in root filter");
				else
					throw new IllegalStateException("There is no filter with name '" + filterName + "' in filter with name '" + filterNames[i - 2]);

			} else {
				if (i == filterNames.length)
					return f;
				else
					return findFilterByPath(f, filterNames, i);
			}
		} else if (i == 0)
			throw new IllegalStateException("Root filter must be composite");
		else
			throw new IllegalStateException("Filter with name '" + filterNames[i - 1] + "' must be composite");
	}

	public static void setFilterValues(PagedList originalPagedList, String fullFilterName, Collection values) {
		String[] filterNames = fullFilterName.split("_");
		Filter filter = findFilterByPath(originalPagedList.getFilter(), filterNames, 0);
		if (filter != null) {
			filter.clear();
			for (Object value : values) {
				filter.addValue(value);
			}
		}
	}

	/**
	 * Заполняем фильтры в постраничной навигации. Считаем, что обязательные фильтры задаются один раз,
	 * и их повторная инициализация не нужна.
	 *
	 * @param filterName
	 * @param filterValues
	 */
	public void setFilterValues(String filterName, List filterValues) {
		if (!obligatoryFilterNames.contains(filterName))
			setFilterValues(list, filterName, filterValues);
	}

	public Set<String> getFilterNames() {
		return filterNames;
	}

	@Nonnull
	public List getFilterValuesByFullName(String fullFilterName) {
		Filter filter = getFilterByFullName(fullFilterName);
		List result = new ArrayList<>();
		if (filter != null) {
			result.addAll(filter.getValues());
		}
		return result;
	}
}