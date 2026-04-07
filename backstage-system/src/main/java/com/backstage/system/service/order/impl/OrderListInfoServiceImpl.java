package com.backstage.system.service.order.impl;

import com.backstage.system.domain.book.BookDO;
import com.backstage.system.domain.course.OshCourse;
import com.backstage.system.domain.group.GroupActivity;
import com.backstage.system.domain.vo.ColumnDetailVo;
import com.backstage.system.domain.vo.FlashColumnVo;
import com.backstage.system.domain.vo.LiveDetailVo;
import com.backstage.system.domain.vo.order.GoodsVo;
import com.backstage.system.mapper.column.ColumnMapper;
import com.backstage.system.mapper.column.SysFlashColumnMapper;
import com.backstage.system.mapper.course.OshCourseMapper;
import com.backstage.system.mapper.group.GroupMapper;
import com.backstage.system.mapper.live.LiveMapper;
import com.backstage.system.mapper.order.OshBookMapper;
import com.backstage.system.service.order.OrderListInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class OrderListInfoServiceImpl implements OrderListInfoService {


    @Autowired
    private OshCourseMapper courseMapper;
    @Autowired
    private SysFlashColumnMapper flashColumnMapper;
    @Autowired
    private ColumnMapper columnMapper;
    @Autowired
    private OshBookMapper oshBookMapper;
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private LiveMapper liveMapper;



    public List findCT(String type, Long  id){
        List<String> ctList = new ArrayList<>();

        if (type.equals("column")) {
            // 专栏
            ColumnDetailVo column = columnMapper.getColumnDetail(id);
            if (column!=null){
                String cover = column.getCover();
                String title = column.getTitle();
                ctList.add(cover);
                ctList.add(title);
            }
        }else if (type.equals("course")) {
            // 课程
            OshCourse course = courseMapper.selectCourseById(id);
            if (course!=null){
                String cover = course.getCover();
                String title = course.getTitle();
                ctList.add(cover);
                ctList.add(title);
            }
        }else if (type.equals("book")) {
            // 电子书
            BookDO bookDO = oshBookMapper.selectById(id);
            if (bookDO !=null){
                String cover = bookDO.getCover();
                String title = bookDO.getTitle();
                ctList.add(cover);
                ctList.add(title);
            }
        }

        return ctList;

    }

    public GoodsVo CreateOrderInfo(Object s){
        GoodsVo a = new GoodsVo();
        if (s!=null) {
            OshCourse course;
            ColumnDetailVo column;
            LiveDetailVo live;
            FlashColumnVo flash;
            GroupActivity group;
            BookDO bookDO;

            if (s instanceof OshCourse) {
                course = (OshCourse) s;
                Long id = course.getId();
                a.setId(id.intValue());
                a.setTitle(course.getTitle());
                a.setCover(course.getCover());
                a.setPrice(course.getPrice().toString());
                a.setType(course.getType());
            } else if (s instanceof BookDO) {
                bookDO = (BookDO) s;
                Long id = bookDO.getId();
                a.setId(id.intValue());
                a.setTitle(bookDO.getTitle());
                a.setCover(bookDO.getCover());
                a.setPrice(bookDO.getPrice().toString());
                a.setType("book");
            } else if (s instanceof ColumnDetailVo) {
                column = (ColumnDetailVo) s;
                Long id = column.getId();
                a.setId(id.intValue());
                a.setTitle(column.getTitle());
                a.setCover(column.getCover());
                a.setPrice(column.getPrice().toString());
                a.setType("column");
            } else if (s instanceof LiveDetailVo) {
                live = (LiveDetailVo) s;
                Long id = live.getId();
                a.setId(id.intValue());
                a.setTitle(live.getTitle());
                a.setCover(live.getCover());
                a.setPrice(live.getPrice().toString());
                a.setType(live.getType());
            }else if (s instanceof FlashColumnVo) {
                flash = (FlashColumnVo) s;
                Long id = flash.getId();
                a.setId(id.intValue());
                a.setTitle(flash.getTitle());
                a.setCover(flash.getCover());
                a.setPrice(flash.getPrice().toString());
                a.setType("book");
            }else if (s instanceof GroupActivity) {
                group = (GroupActivity) s;
                Long id = group.getId();
                a.setId(id.intValue());
                // 根据类型找
                List<String> ct =  findCT(group.getType(),group.getGoodsId());
                a.setTitle(ct.get(1));
                a.setCover(ct.get(0));
                a.setPrice(group.getPrice().toString());
                a.setType(group.getType());
            }
        }

        return a;
    }

    @Override
    public GoodsVo getOrderInfo(String type, Integer id) {

        // 查询对应模块的 id titile cover price type(video 或者其它)
        if (type.equals("course") ){
            OshCourse c = courseMapper.selectCourseById(Long.valueOf(id));
            return CreateOrderInfo(c);
        }else if (type.equals("column")){
            ColumnDetailVo cd = columnMapper.getColumnDetail(Long.valueOf(id));
            return CreateOrderInfo(cd);
        }else if (type.equals("book")){
            BookDO b = oshBookMapper.selectById(Long.valueOf(id));
            return CreateOrderInfo(b);
        }else if (type.equals("live")){
            LiveDetailVo ld =  liveMapper.getLiveInfoById(Long.valueOf(id));
            return CreateOrderInfo(ld);
        }else if (type.equals("group")){
            GroupActivity gp= groupMapper.getGroupActivityById(Long.valueOf(id));
            return CreateOrderInfo(gp);
        }else if (type.equals("flashsale")){
            FlashColumnVo fl = flashColumnMapper.selectOshColumnById(Long.valueOf(id));
            return CreateOrderInfo(fl);
        }


        return null;
    }


}
