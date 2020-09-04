package lk.ijse.dep.pos.business.custom.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import lk.ijse.dep.pos.dao.custom.*;
import lk.ijse.dep.pos.entity.CustomEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import lk.ijse.dep.pos.business.custom.OrderBO;
import lk.ijse.dep.pos.dao.DAOFactory;
import lk.ijse.dep.pos.dao.DAOType;
import lk.ijse.dep.pos.db.HibernateUtil;
import lk.ijse.dep.pos.entity.Item;
import lk.ijse.dep.pos.entity.Order;
import lk.ijse.dep.pos.entity.OrderDetail;
import lk.ijse.dep.pos.util.OrderDetailTM;
import lk.ijse.dep.pos.util.OrderTM;

public class OrderBOImpl implements OrderBO { // , Temp

  private final OrderDAO orderDAO = DAOFactory.getInstance().getDAO(DAOType.ORDER);
  private final OrderDetailDAO orderDetailDAO = DAOFactory.getInstance().getDAO(DAOType.ORDER_DETAIL);
  private final ItemDAO itemDAO = DAOFactory.getInstance().getDAO(DAOType.ITEM);
  private final CustomerDAO customerDAO = DAOFactory.getInstance().getDAO(DAOType.CUSTOMER);
  private final QueryDAO queryDAO = DAOFactory.getInstance().getDAO(DAOType.QUERY);

  // Interface through injection
/*    @Override
    public void injection() {
        this.orderDAO = DAOFactory.getInstance().getDAO(DAOType.ORDER);
    }  */

  // Setter method injection
/*    private void setOrderDAO(){
        this.orderDAO = DAOFactory.getInstance().getDAO(DAOType.ORDER);
    }*/

  public String getNewOrderId() throws Exception {

    SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    Session session = sessionFactory.openSession();
    orderDAO.setSession(session);
    String lastOrderId = null;
    Transaction tx = null;
    try {

      tx = session.beginTransaction();

      lastOrderId = orderDAO.getLastOrderId();

      tx.commit();
    } catch (Throwable t) {
      tx.rollback();
      throw t;
    } finally {
      session.close();
    }

    if (lastOrderId == null) {
      return "OD001";
    } else {
      int maxId = Integer.parseInt(lastOrderId.replace("OD", ""));
      maxId = maxId + 1;
      String id = "";
      if (maxId < 10) {
        id = "OD00" + maxId;
      } else if (maxId < 100) {
        id = "OD0" + maxId;
      } else {
        id = "OD" + maxId;
      }
      return id;
    }
  }

  public void placeOrder(OrderTM order, List<OrderDetailTM> orderDetails) throws Exception {
    SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    Session session = sessionFactory.openSession();
    orderDAO.setSession(session);
    customerDAO.setSession(session);
    orderDetailDAO.setSession(session);
    itemDAO.setSession(session);
    Transaction tx = null;
    try {

      tx = session.beginTransaction();
      orderDAO.save(new Order(order.getOrderId(),
              (Date) order.getOrderDate(),
          customerDAO.find(order.getCustomerId())));

      for (OrderDetailTM orderDetail : orderDetails) {
        orderDetailDAO.save(new OrderDetail(
            order.getOrderId(), orderDetail.getCode(),
            orderDetail.getQty(), BigDecimal.valueOf(orderDetail.getUnitPrice())
        ));
        Item item = itemDAO.find(orderDetail.getCode());
        item.setQtyOnHand(item.getQtyOnHand() - orderDetail.getQty());
        itemDAO.update(item);

      }
      tx.commit();
    } catch (Throwable t) {
      tx.rollback();
      throw t;

    } finally {
      session.close();
    }
  }

  @Override
  public List<OrderTM> getAllOrders() throws Exception {
    SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    Session session = sessionFactory.openSession();
    queryDAO.setSession(session);
    List<CustomEntity> odl=null;
    Transaction tx = null;
    try {

      tx = session.beginTransaction();

      odl = queryDAO.getOrderDetail();
      tx.commit();
    } catch (Throwable t) {
      tx.rollback();
      throw t;
    } finally {
      session.close();
    }
    List<OrderTM> orderDetailsList = new ArrayList<>();
    for (CustomEntity orderDetails : odl) {
      BigDecimal total = orderDetails.getTotal();

      orderDetailsList.add(new OrderTM(orderDetails.getOrderId(),orderDetails.getOrderDate(),orderDetails.getCustomerId(),
              orderDetails.getCustomerName(),Double.parseDouble(total.toString())));
    }
    return orderDetailsList;
  }
}
