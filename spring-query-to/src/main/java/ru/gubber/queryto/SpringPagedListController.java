package ru.gubber.queryto;

import org.hibernate.Session;
import org.hibernate.jpa.HibernateEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.gubber.query.PagedList;
import ru.gubber.queryto.model.PagedListTO;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gubber on 28.11.2015.
 */
@Component("queryTOPagedListController")
public class SpringPagedListController implements IPagedListController {

	@PersistenceContext
	protected EntityManager entityManager;

	@Autowired
	protected EntityManagerFactory emf;

	@PersistenceUnit
	public void setEntityManagerFactory(EntityManagerFactory emf) {
		this.emf = emf;
	}

	@Transactional
	public PagedListTO fillPagedList(PagedList pagedList){
		List resultList = new ArrayList();
		synchronized (pagedList) {
			HibernateEntityManager hem = entityManager.unwrap(HibernateEntityManager.class);
			Session session = hem.getSession();
			resultList.addAll(pagedList.getItems(session));
		}
		PagedListTO result = new PagedListTO();
		result.setPage(pagedList.getPage());
		result.setAllCount(pagedList.getItemCount());
		result.setPerPage(pagedList.getItemsPerPage());
		result.setPagesCount(pagedList.getPageCount());
		result.setItems(resultList);
		return result;
	}
}