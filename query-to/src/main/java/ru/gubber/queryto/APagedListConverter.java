package ru.gubber.queryto;

import ru.gubber.query.PagedList;
import ru.gubber.query.sorter.FieldSorter;
import ru.gubber.query.sorter.Sorter;
import ru.gubber.queryto.model.FilterTO;
import ru.gubber.queryto.model.PagedListTO;
import ru.gubber.queryto.model.SorterTO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Класс, преобразующий входящий PagedListTO в PagedList, производит формирование списка в соответствии с параметрами поиска
 * и возвращающий заполненный PagedListTO, для отображения на клиенте.
 * Created by gubber on 28.11.2015.
 */
public abstract class APagedListConverter<O, T> {

	protected final int ORIGIN_CLASS_INDX = 0;
	protected final int TRANSPORT_CLASS_INDX = 1;

	private PagedListProcessor pagedListProcessor;

	@Nonnull
	public PagedListTO fillPagedList(@Nullable PagedListTO originPagedList) {

		if (originPagedList == null) {
			originPagedList = new PagedListTO();
		}

		PagedListBuilder pagedListBuilder = generateOriginPagedList(originPagedList);
		PagedList realPagedList = pagedListBuilder.generateList();
		Class transportEntityClass = getParametrizedClass(TRANSPORT_CLASS_INDX);
		PagedListTO resultPagedList = pagedListProcessor.getItems(realPagedList, transportEntityClass);
//        заполнить фильтры и сортировки в результирующем списке постраничной навигации
		transformFilters(resultPagedList, pagedListBuilder);
		return resultPagedList;
	}

	protected void transformFilters(PagedListTO resultList, PagedListBuilder builder){
		resultList.getFilters().clear();
		builder.getFilterNames().stream().forEach(fullFilterName -> {
			String filterName = getTOFilterName(fullFilterName);
			List values = builder.getFilterValuesByFullName(fullFilterName);
			resultList.getFilters().add(new FilterTO(filterName, values));
		});
	}

	private String getTOFilterName(String fullFilterName) {
		return fullFilterName;
	}

	/**
	 * Формируем объект постраничной навигации, заполняем его фильтрами и сортировкой
	 *
	 * @param listTO
	 * @return
	 */
	@Nonnull
	private PagedListBuilder generateOriginPagedList(@Nonnull PagedListTO listTO) {

		final PagedListBuilder pagedListBuilder = generatePagedListBuilder(listTO.getPage(), listTO.getPerPage());

		createListFilters(pagedListBuilder);

		listTO.getFilters().stream().forEach(filterTO ->
				pagedListBuilder.setFilterValues(makeFullFilterName(filterTO.getFilterName()),
						convertFilterValues(filterTO.getFilterName(), filterTO.getFilterValues())));

		if (listTO.getSorters().size() > 0) {
			listTO.getSorters().stream().forEach(sorter -> pagedListBuilder.addSorter(generateSorterFromTO(sorter)));
		} else {
			addDefaultSorters(pagedListBuilder);
		}

		return pagedListBuilder;
	}

	/**
	 * Формирует построитель постраничной навигации. Для большинства реализаций, этого метода достаточно.
	 * Этот метод надо переписывать, если надо использовать PagedListByTree для фильтрации по списку дочерних объектов.
	 * @return
	 */
	private PagedListBuilder generatePagedListBuilder(int page, int perPage) {
		Class originEntityClass = getParametrizedClass(ORIGIN_CLASS_INDX);
		return new PagedListBuilder(originEntityClass, page, perPage);
	}

	protected Class getParametrizedClass(int index) {
		if ( (index < 0 ) || (index > 1) )
			throw new IllegalStateException("Wrong index");
		return (Class) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[index];
	}

	protected List convertFilterValues(String filterName, List filterValues) {
		return filterValues;
	}

	/**
	 * Формирует сортировщики пол умолчанию.
	 * @param pagedListBuilder
	 */
	protected abstract void addDefaultSorters(PagedListBuilder pagedListBuilder);

	/**
	 * Формирует исходную структуру объекта постраничной навигации, чтоб потом задать фильтры и сортировуку
	 * @param pagedListBuilder
	 */
	protected abstract void initPagedList(@Nonnull PagedListBuilder pagedListBuilder);

	@Nonnull
	private Sorter generateSorterFromTO(SorterTO sorterTO) {
		return new FieldSorter(convertSorterNameToField(sorterTO.getSorterName()), sorterTO.isAscending() ? 0 : 1);
	}

	/**
	 * Метод преобразующий имя сортировщика, используемого на транспортном уровне, в имя поля объекта по которому
	 * должна производиться сортировка. Сделан с целью сокрытия внутренней структуры.
	 *
	 * @param sorterName
	 * @return
	 */
	protected abstract String convertSorterNameToField(String sorterName);

	/**
	 * Конвертирует название фильтра, используемое в транспортных целях, в имя фильтра используемого для фильтрации в HQL
	 *
	 * @param filterName
	 * @return
	 */
	protected String makeFullFilterName(String filterName) {
		return filterName;
	}

	/**
	 * Формирует список фитров для списка, которые используются для фильтрации в HQL
	 *
	 * @param pagedListBuilder
	 * @return
	 */
	protected abstract PagedListBuilder createListFilters(PagedListBuilder pagedListBuilder);

	public void setPagedListProcessor(PagedListProcessor pagedListProcessor) {
		this.pagedListProcessor = pagedListProcessor;
	}
}