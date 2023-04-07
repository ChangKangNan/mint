package org.ckn.service.impl;

import org.ckn.entity.StockOrder;
import org.ckn.mapper.StockOrderMapper;
import org.ckn.service.StockOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author ckn
 * @since 2023-04-06
 */
@Service
public class StockOrderServiceImpl extends ServiceImpl<StockOrderMapper, StockOrder> implements StockOrderService {

}
