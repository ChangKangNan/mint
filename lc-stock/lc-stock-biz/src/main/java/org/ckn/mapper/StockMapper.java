package org.ckn.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ckn.entity.Stock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 库存表 Mapper 接口
 * </p>
 *
 * @author ckn
 * @since 2023-04-06
 */
@Mapper
public interface StockMapper extends BaseMapper<Stock> {

}
