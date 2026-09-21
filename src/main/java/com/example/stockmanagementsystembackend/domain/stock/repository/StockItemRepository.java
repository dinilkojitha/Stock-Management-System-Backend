package com.example.stockmanagementsystembackend.domain.stock.repository;


import com.example.stockmanagementsystembackend.domain.stock.entity.StockItem;
import com.example.stockmanagementsystembackend.domain.stock.entity.StockItemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface StockItemRepository  extends JpaRepository<StockItem, StockItemId> {

    List<StockItem> findByItem_Id(Integer itemId);



}
