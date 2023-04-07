package org.ckn.service.impl;

import org.ckn.entity.Stock;
import org.ckn.mapper.StockMapper;
import org.ckn.service.StockService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 库存表 服务实现类
 * </p>
 *
 * @author ckn
 * @since 2023-04-06
 */
@Service
public class StockServiceImpl extends ServiceImpl<StockMapper, Stock> implements StockService {

}
