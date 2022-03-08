package com.technivaaran.services;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.technivaaran.dto.request.StockRequestDto;
import com.technivaaran.entities.ItemMaster;
import com.technivaaran.entities.StockDetails;
import com.technivaaran.entities.StockHeader;
import com.technivaaran.repositories.StockDetailsRepository;
import com.technivaaran.repositories.StockHeaderRepository;
import com.technivaaran.utils.DateUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class StockService {

    @Autowired
    private StockHeaderRepository stockHeaderRepository;

    @Autowired
    private StockDetailsRepository stockDetailsRepository;

    @Autowired
    ItemMasterService itemMasterService;

    public void getMaterialDetailsByMaterialName() {
        stockHeaderRepository.findById(1L);
        stockDetailsRepository.findById(1L);
    }

    public void createStockEntry(StockRequestDto stockRequestDto) {
        List<ItemMaster> itemList = itemMasterService.findByItemName(stockRequestDto.getModel());
        if (!CollectionUtils.isEmpty(itemList)) {
            itemList.stream().forEach(item -> {
                if (item.getPartNo().equalsIgnoreCase(stockRequestDto.getPartNo())) {
                    StockHeader stockHeader = null;
                    Optional<StockHeader> stockHeaderOp = stockHeaderRepository
                            .findByLocationAndItemMaster(stockRequestDto.getBoxNo(), item);
                    if (!stockHeaderOp.isEmpty()) {
                        stockHeader = stockHeaderOp.get();
                    } else {
                        stockHeader = StockHeader.builder()
                                .itemMaster(item)
                                .location(stockRequestDto.getBoxNo())
                                .stockDate(DateUtils.getCurrentDate())
                                .build();
                    }
                    createStockDetail(stockRequestDto, stockHeader);
                    updateStockHeader(stockHeader);
                    return;
                }
            });

        }
    }

    private StockDetails createStockDetail(StockRequestDto stockRequestDto, StockHeader stockHeader) {
        StockDetails stockDetails = StockDetails.builder()
                .inQty(stockRequestDto.getQty())
                .outQty(0)
                .stockHeader(stockHeader)
                .transactionDate(DateUtils.getCurrentDateTime())
                .user(null)
                .build();

        return stockDetailsRepository.save(stockDetails);
    }

    private void updateStockHeader(StockHeader stockHeader) {

        stockDetailsRepository.getByStockHeaderAndTransactionDate(stockHeader.getId(), stockHeader.getStockDate());

    }
}
