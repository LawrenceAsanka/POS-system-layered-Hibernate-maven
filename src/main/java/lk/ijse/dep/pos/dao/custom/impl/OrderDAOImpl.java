package lk.ijse.dep.pos.dao.custom.impl;

import java.util.List;

import lk.ijse.dep.pos.dao.CrudDAOImpl;
import lk.ijse.dep.pos.dao.custom.OrderDAO;
import lk.ijse.dep.pos.entity.Order;

public class OrderDAOImpl extends CrudDAOImpl<Order,String> implements OrderDAO {

  public String getLastOrderId() throws Exception {
    List list = session.createQuery("SELECT o.id FROM lk.ijse.dep.pos.lk.ijse.dep.pos.entity.Order o ORDER BY id DESC").setMaxResults(1).list();
    return list.size() > 0 ? (String) list.get(0) : null;
  }

}
