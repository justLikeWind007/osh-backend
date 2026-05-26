package com.backstage.system.service.impl.seckill;

import com.backstage.common.enums.ResourceCodePrefixEnum;
import com.backstage.common.exception.ServiceException;
import com.backstage.common.utils.generate.GenerateUtil;
import com.backstage.system.domain.book.BookDO;
import com.backstage.system.domain.course.OshCourse;
import com.backstage.system.domain.dto.seckill.SeckillGoodsAddDTO;
import com.backstage.system.domain.dto.seckill.SeckillGoodsUpdateDTO;
import com.backstage.system.domain.seckill.OshSeckillGoods;
import com.backstage.system.domain.vo.seckill.SeckillGoodsPreviewVO;
import com.backstage.system.domain.vo.seckill.SeckillGoodsVO;
import com.backstage.system.mapper.book.BookMapper;
import com.backstage.system.mapper.course.OshCourseMapper;
import com.backstage.system.mapper.seckill.OshSeckillGoodsMapper;
import com.backstage.system.service.seckill.IOshSeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 秒杀商品池 Service 实现
 *
 * @author backstage
 * @date 2026-04-28
 */
@Service
public class OshSeckillGoodsServiceImpl implements IOshSeckillGoodsService {

    @Autowired
    private OshSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private OshCourseMapper courseMapper;

    @Autowired
    private BookMapper bookMapper;

    // ----------------------------------------------------------------
    // 内部工具方法
    // ----------------------------------------------------------------

    /**
     * 实体转 VO（过滤 deleteFlag、createBy 等内部字段）
     */
    private SeckillGoodsVO toVO(OshSeckillGoods goods) {
        if (goods == null) return null;
        SeckillGoodsVO vo = new SeckillGoodsVO();
        vo.setId(goods.getId());
        vo.setNo(goods.getNo());
        vo.setGoodsId(goods.getGoodsId());
        vo.setGoodsType(goods.getGoodsType());
        vo.setGoodsName(goods.getGoodsName());
        vo.setGoodsCover(goods.getGoodsCover());
        vo.setOriginPrice(goods.getOriginPrice());
        vo.setMinSeckillPrice(goods.getMinSeckillPrice());
        vo.setStatus(goods.getStatus());
        vo.setSort(goods.getSort());
        vo.setCreateTime(goods.getCreateTime());
        vo.setUpdateTime(goods.getUpdateTime());
        return vo;
    }

    /**
     * 根据 goodsType 路由到对应表，查询商品预览信息（name / cover / originPrice）
     * 供预览接口和入池自动填充共用，避免重复逻辑
     *
     * goodsType=1 -> osh_course  : title / cover / t_price
     * goodsType=2 -> osh_book    : title / cover / original_price
     */
    private SeckillGoodsPreviewVO fetchGoodsPreview(Integer goodsType, Long goodsId) {
        SeckillGoodsPreviewVO vo = new SeckillGoodsPreviewVO();
        if (goodsType == 1) {
            OshCourse course = courseMapper.selectCourseById(goodsId);
            if (course == null) {
                throw new ServiceException("课程不存在，ID：" + goodsId);
            }
            vo.setGoodsName(course.getTitle());
            vo.setGoodsCover(course.getCover());
            vo.setOriginPrice(course.getTPrice());
        } else if (goodsType == 2) {
            BookDO book = bookMapper.selectById(goodsId);
            if (book == null) {
                throw new ServiceException("电子书不存在，ID：" + goodsId);
            }
            vo.setGoodsName(book.getTitle());
            vo.setGoodsCover(book.getCover());
            vo.setOriginPrice(book.getOriginalPrice());
        } else {
            throw new ServiceException("暂不支持的商品类型：" + goodsType);
        }
        return vo;
    }

    // ----------------------------------------------------------------
    // 接口实现
    // ----------------------------------------------------------------

    @Override
    public SeckillGoodsVO selectSeckillGoodsById(Long id) {
        return toVO(seckillGoodsMapper.selectSeckillGoodsById(id));
    }

    @Override
    public List<SeckillGoodsVO> selectSeckillGoodsList(OshSeckillGoods goods) {
        return seckillGoodsMapper.selectSeckillGoodsList(goods)
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    /**
     * 预览商品信息，供前端表单自动回填
     */
    @Override
    public SeckillGoodsPreviewVO previewGoods(Integer goodsType, Long goodsId) {
        return fetchGoodsPreview(goodsType, goodsId);
    }

    /**
     * 新增秒杀商品（入池）
     * goodsName / goodsCover / originPrice 由后端自动从对应表查询填充，前端无需传入
     */
    @Override
    public int insertSeckillGoods(SeckillGoodsAddDTO dto) {
        // 校验同一商品是否已入池
        OshSeckillGoods exist = seckillGoodsMapper.selectByGoodsIdAndType(
                dto.getGoodsId(), dto.getGoodsType());
        if (exist != null) {
            throw new ServiceException("该商品已在秒杀商品池中，请勿重复添加");
        }
        // 从对应表自动查询商品信息
        SeckillGoodsPreviewVO preview = fetchGoodsPreview(dto.getGoodsType(), dto.getGoodsId());
        // 组装 Entity
        OshSeckillGoods goods = new OshSeckillGoods();
        // 根据 goodsType 选对应的资源类型前缀生成资源编号
        ResourceCodePrefixEnum prefix = dto.getGoodsType() == 1
                ? ResourceCodePrefixEnum.COURSE
                : ResourceCodePrefixEnum.BOOK;
        goods.setNo(GenerateUtil.generateResourceCode(prefix));
        goods.setGoodsId(dto.getGoodsId());
        goods.setGoodsType(dto.getGoodsType());
        goods.setGoodsName(preview.getGoodsName());
        goods.setGoodsCover(preview.getGoodsCover());
        goods.setOriginPrice(preview.getOriginPrice());
        goods.setMinSeckillPrice(dto.getMinSeckillPrice());
        goods.setSort(dto.getSort() != null ? dto.getSort() : 0);
        goods.setStatus(0); // 默认待审核，不允许前端传入
        return seckillGoodsMapper.insertSeckillGoods(goods);
    }

    /**
     * 修改秒杀商品信息（只允许修改 minSeckillPrice / sort）
     */
    @Override
    public int updateSeckillGoods(SeckillGoodsUpdateDTO dto) {
        OshSeckillGoods exist = seckillGoodsMapper.selectSeckillGoodsById(dto.getId());
        if (exist == null) {
            throw new ServiceException("秒杀商品不存在");
        }
        OshSeckillGoods goods = new OshSeckillGoods();
        goods.setId(dto.getId());
        goods.setGoodsName(dto.getGoodsName());
        goods.setGoodsCover(dto.getGoodsCover());
        goods.setOriginPrice(dto.getOriginPrice());
        goods.setMinSeckillPrice(dto.getMinSeckillPrice());
        goods.setSort(dto.getSort());
        return seckillGoodsMapper.updateSeckillGoods(goods);
    }

    /**
     * 批量修改秒杀商品状态（上架/下架）
     */
    @Override
    public int updateSeckillGoodsStatus(List<Long> ids, Integer status) {
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException("商品ID列表不能为空");
        }
        return seckillGoodsMapper.updateSeckillGoodsStatusByIds(ids, status);
    }

    /**
     * 批量逻辑删除秒杀商品
     */
    @Override
    public int deleteSeckillGoodsByIds(Long[] ids) {
        return seckillGoodsMapper.deleteSeckillGoodsByIds(ids);
    }
}
