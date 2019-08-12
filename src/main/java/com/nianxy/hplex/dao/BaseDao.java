package com.nianxy.hplex.dao;

import app.nianxy.commonlib.exceptionutils.ExceptionUtils;
import com.nianxy.hplex.*;
import com.nianxy.hplex.cond.Cond;
import com.nianxy.hplex.cond.CondCompare;
import com.nianxy.hplex.cond.CondList;
import com.nianxy.hplex.limit.ILimit;
import com.nianxy.hplex.limit.Limit;
import com.nianxy.hplex.order.Order;
import com.nianxy.hplex.order.OrderList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class BaseDao<T> {
    private static Logger logger = LogManager.getLogger(BaseDao.class);

    private Class tableClass;

    private void setupTableClass() {
        tableClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    private HPlexTransaction transaction = null;

    protected BaseDao() {
        setupTableClass();
    }

    protected BaseDao(HPlexTransaction transaction) {
        setupTableClass();
        this.transaction = transaction;
    }

    /**
     * 返回用于标识一条记录的Table类属性名
     * @return
     */
    protected abstract String getKeyAttrName();

    protected HPlexTable createHPlexTable() throws Exception {
        return transaction==null?new HPlexTable(tableClass)
                :transaction.hPlexTable(tableClass);
    }

    /**
     * 按条件获取记录总数
     * @param conds 可以为null
     * @return
     */
    public long total(CondList conds) {
        try {
            Query query = createHPlexTable().query();
            if (conds!=null) {
                query.addCondList(conds);
            }
            return query.count();
        } catch (Exception e) {
            logger.error("total exception:" + ExceptionUtils.getTraceInfo(e));
            return -1;
        }
    }

    public long total() {
        return total(null);
    }

    public T get(Object id) {
        try {
            return (T)createHPlexTable().query()
                    .addCond(Cond.compare(CondCompare.Compare.EQ, getKeyAttrName(), id))
                    .fetchOne();
        } catch (Exception e) {
            logger.error("get exception:" + ExceptionUtils.getTraceInfo(e));
            return null;
        }
    }

    /**
     * 返回不分页数据
     * @param conds 可以为null
     * @param orders 如果为null，则默认按getKeyAttrName()返回的列升序排列
     * @return
     */
    public List<T> list(CondList conds, OrderList orders) {
        try {
            Query query = createHPlexTable().query();
            if (conds!=null) {
                query.addCondList(conds);
            }
            if (orders!=null) {
                query.addOrderList(orders);
            } else {
                query.addOrder(Order.ASC(getKeyAttrName()));
            }
            return query.execute();
        } catch (Exception e) {
            logger.error("list exception:" + ExceptionUtils.getTraceInfo(e));
            return null;
        }
    }

    /**
     *
     * @param page
     * @param pageSize
     * @param conds 如果没有条件可以为null
     * @param orders 如果为null，则默认按getKeyAttrName()返回的列升序排列
     * @return
     */
    public List<T> list(int page, int pageSize, CondList conds, OrderList orders) {
        try {
            Query query = createHPlexTable().query();
            if (conds!=null) {
                query.addCondList(conds);
            }
            if (orders!=null) {
                query.addOrderList(orders);
            } else {
                query.addOrder(Order.ASC(getKeyAttrName()));
            }
            return query.setLimit(Limit.limit()
                        .setOffset(page*pageSize)
                        .setMaxSize(pageSize))
                    .execute();
        } catch (Exception e) {
            logger.error("list exception:" + ExceptionUtils.getTraceInfo(e));
            return null;
        }
    }

    public boolean add(T data) {
        try {
            return createHPlexTable().insert().add(data).execute()==1;
        } catch (Exception e) {
            logger.error("add exception:" + ExceptionUtils.getTraceInfo(e));
            return false;
        }
    }

    public int update(T data, CondList conds, String field) {
        return update(data, conds, new String[]{field});
    }

    public int update(T data, Object id, String field) {
        return update(data, new CondList().addCond(Cond.compare(CondCompare.Compare.EQ, getKeyAttrName(), id)),
                new String[]{field});
    }

    public int update(T data, Object id, String[] fields) {
        return update(data, new CondList().addCond(Cond.compare(CondCompare.Compare.EQ, getKeyAttrName(), id)),
                fields);
    }

    public int update(T data, CondList conds, String[] fields) {
        try {
            Update update = createHPlexTable().update(data).addCondList(conds);
            if (fields!=null) {
                update.addFields(fields);
            }
            return update.execute();
        } catch (Exception e) {
            logger.error("update exception:" + ExceptionUtils.getTraceInfo(e));
            return -1;
        }
    }

    /**
     *
     * @param conds 不允许为null
     * @param orders 可以为null
     * @param limit 可以为null
     * @return
     */
    public int delete(CondList conds, OrderList orders, ILimit limit) {
        try {
            Delete delete = createHPlexTable().delete().addCondList(conds);
            if (orders!=null) {
                delete.addOrderList(orders);
            }
            if (limit!=null) {
                delete.setLimit(limit);
            }
            return delete.execute();
        } catch (Exception e) {
            logger.error("delete exception:" + ExceptionUtils.getTraceInfo(e));
            return -1;
        }
    }
}
