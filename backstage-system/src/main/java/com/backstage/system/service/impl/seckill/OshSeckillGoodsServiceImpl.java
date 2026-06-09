package com.backstage.system.service.impl.seckill;

import com.backstage.common.enums.ResourceCodePrefixEnum;
import com.backstage.common.exception.ServiceException;
import com.backstage.common.utils.generate.GenerateUtil;
import com.backstage.system.domain.book.BookDO;
import com.backstage.system.domain.course.OshCourse;
import com.backstage.system.domain.dto.seckill.SeckillGoodsAddDTO;
import com.backstage.system.domain.dto.seckill.SeckillGoodsUpdateDTO;
import com.backstage.system.domain.seckill.OshSeckillGoods;
import com.backstage.system.domain.seckill.OshSeckillGoodsTag;
import com.backstage.system.domain.seckill.OshSeckillGoodsTagRel;
import com.backstage.system.domain.vo.seckill.SeckillGoodsPreviewVO;
import com.backstage.system.domain.vo.seckill.SeckillGoodsVO;
import com.backstage.system.mapper.book.BookMapper;
import com.backstage.system.mapper.book.BookTagDOMapper;
import com.backstage.system.mapper.course.OshCourseMapper;
import com.backstage.system.mapper.course.OshCourseTagMapper;
import com.backstage.system.mapper.seckill.OshSeckillGoodsMapper;
import com.backstage.system.mapper.seckill.OshSeckillGoodsTagMapper;
import com.backstage.system.mapper.seckill.OshSeckillGoodsTagRelMapper;
import com.backstage.system.service.seckill.IOshSeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    private OshCourseTagMapper courseTagMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BookTagDOMapper bookTagDOMapper;

    @Autowired
    private OshSeckillGoodsTagMapper seckillGoodsTagMapper;

    @Autowired
    private OshSeckillGoodsTagRelMapper seckillGoodsTagRelMapper;

    // ----------------------------------------------------------------
    // 内部工具方法
    // ----------------------------------------------------------------

    /**
     * 实体转 VO（过滤 deleteFlag、createBy 等内部字段）
     * tagNames 需调用方自行填充（避免在此处单条查询造成 N+1）
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
        SeckillGoodsVO vo = toVO(seckillGoodsMapper.selectSeckillGoodsById(id));
        if (vo != null) {
            vo.setTagNames(seckillGoodsTagMapper.selectTagNamesBySeckillGoodsId(id));
        }
        return vo;
    }

    @Override
    public List<SeckillGoodsVO> selectSeckillGoodsList(OshSeckillGoods goods) {
        List<OshSeckillGoods> list = seckillGoodsMapper.selectSeckillGoodsList(goods);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<SeckillGoodsVO> voList = list.stream().map(this::toVO).collect(Collectors.toList());

        // 批量查标签，避免 N+1
        List<Long> ids = list.stream().map(OshSeckillGoods::getId).collect(Collectors.toList());
        List<OshSeckillGoodsTagMapper.OshSeckillGoodsTagWithGoodsId> allTags =
                seckillGoodsTagMapper.selectTagsBySeckillGoodsIds(ids);
        Map<Long, List<String>> tagMap = allTags.stream()
                .collect(Collectors.groupingBy(
                        OshSeckillGoodsTagMapper.OshSeckillGoodsTagWithGoodsId::getSeckillGoodsId,
                        Collectors.mapping(OshSeckillGoodsTagMapper.OshSeckillGoodsTagWithGoodsId::getTagName,
                                Collectors.toList())));
        voList.forEach(vo -> vo.setTagNames(tagMap.getOrDefault(vo.getId(), Collections.emptyList())));
        return voList;
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
    @Transactional(rollbackFor = Exception.class)
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
        int rows = seckillGoodsMapper.insertSeckillGoods(goods);

        // 自动从原始商品表同步标签
        List<String> tagNames = fetchTagsByGoods(dto.getGoodsType(), dto.getGoodsId());
        saveTagsForGoods(goods.getId(), tagNames, null);
        return rows;
    }

    /**
     * 修改秒杀商品信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
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
        int rows = seckillGoodsMapper.updateSeckillGoods(goods);

        // 标签全量替换（tagNames 不为 null 时才更新，为 null 表示不修改标签）
        if (dto.getTagNames() != null) {
            seckillGoodsTagRelMapper.softDeleteBySeckillGoodsId(dto.getId(), null);
            saveTagsForGoods(dto.getId(), dto.getTagNames(), null);
        }
        return rows;
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

    // ----------------------------------------------------------------
    // 私有工具方法
    // ----------------------------------------------------------------

    /**
     * 根据 goodsType + goodsId 从原始商品标签表查出标签名列表
     * goodsType=1 → osh_course_tag
     * goodsType=2 → osh_book_tag
     */
    private List<String> fetchTagsByGoods(Integer goodsType, Long goodsId) {
        if (goodsType == null || goodsId == null) {
            return Collections.emptyList();
        }
        if (goodsType == 1) {
            List<String> tags = courseTagMapper.selectTagNamesByCourseId(goodsId);
            return tags != null ? tags : Collections.emptyList();
        } else if (goodsType == 2) {
            List<String> tags = bookTagDOMapper.selectBookTagListByBookId(goodsId);
            return tags != null ? tags : Collections.emptyList();
        }
        return Collections.emptyList();
    }

    /**
     * 批量保存标签（多对多）
     * 1. 标签名在字典表中不存在则新建，存在则复用
     * 2. 在关联表中插入 seckillGoodsId + tagId（INSERT IGNORE 幂等）
     */
    private void saveTagsForGoods(Long seckillGoodsId, List<String> tagNames, String operator) {
        if (tagNames == null || tagNames.isEmpty()) {
            return;
        }
        for (int i = 0; i < tagNames.size(); i++) {
            String name = tagNames.get(i);
            if (name == null || name.trim().isEmpty()) {
                continue;
            }
            String trimmed = name.trim();

            // 查字典表，不存在则新建
            OshSeckillGoodsTag tag = seckillGoodsTagMapper.selectByTagName(trimmed);
            if (tag == null) {
                tag = new OshSeckillGoodsTag();
                tag.setTagName(trimmed);
                tag.setSortOrder(i);
                tag.setCreateBy(operator);
                tag.setUpdateBy(operator);
                seckillGoodsTagMapper.insertTag(tag);
            }

            // 插入关联关系（INSERT IGNORE，幂等）
            OshSeckillGoodsTagRel rel = new OshSeckillGoodsTagRel();
            rel.setSeckillGoodsId(seckillGoodsId);
            rel.setTagId(tag.getId());
            rel.setCreateBy(operator);
            rel.setUpdateBy(operator);
            seckillGoodsTagRelMapper.insertRel(rel);
        }
    }
}
