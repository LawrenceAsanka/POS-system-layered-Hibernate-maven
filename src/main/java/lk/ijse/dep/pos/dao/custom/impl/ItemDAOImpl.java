package lk.ijse.dep.pos.dao.custom.impl;

import java.util.List;

import lk.ijse.dep.pos.dao.CrudDAOImpl;
import lk.ijse.dep.pos.dao.custom.ItemDAO;
import lk.ijse.dep.pos.entity.Item;

public class ItemDAOImpl extends CrudDAOImpl<Item,String> implements ItemDAO {



  public String getLastItemCode() throws Exception {
    List list = session.createQuery("SELECT i.code FROM lk.ijse.dep.pos.lk.ijse.dep.pos.entity.Item i ORDER BY i.code DESC").setMaxResults(1).list();
    return list.size() > 0 ? (String) list.get(0) : null;

  }

}
