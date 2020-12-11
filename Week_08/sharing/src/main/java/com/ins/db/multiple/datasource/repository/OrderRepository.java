package com.ins.db.multiple.datasource.repository;

import com.ins.db.multiple.datasource.domain.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order,Long> {
}
