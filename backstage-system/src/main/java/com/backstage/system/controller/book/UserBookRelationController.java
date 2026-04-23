package com.backstage.system.controller.book;

import com.backstage.common.core.domain.R;
import com.backstage.system.domain.vo.book.BookRelationActionReqVO;
import com.backstage.system.domain.vo.book.BookRelationReqVO;
import com.backstage.system.domain.vo.book.BookRelationStatusVO;
import com.backstage.system.service.book.IBookService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 用户电子书关联 Controller（收藏/关注/购买）
 *
 * @author backstage
 */
@Validated
@RestController
@RequestMapping("/pc/book/relation")
public class UserBookRelationController {

    @Resource
    private IBookService bookService;

    /**
     * 收藏/取消收藏电子书
     */
    @PostMapping("/favorite")
    public R<String> favorite(@Valid @RequestBody BookRelationActionReqVO reqVO) {
        bookService.favoriteBook(reqVO.getBookId(), reqVO.getStatus());
        return R.ok(reqVO.getStatus() == 1 ? "收藏成功" : "取消收藏成功");
    }

    /**
     * 关注/取消关注电子书
     */
    @PostMapping("/follow")
    public R<String> follow(@Valid @RequestBody BookRelationActionReqVO reqVO) {
        bookService.followBook(reqVO.getBookId(), reqVO.getStatus());
        return R.ok(reqVO.getStatus() == 1 ? "关注成功" : "取消关注成功");
    }

    /**
     * 购买电子书
     */
    @PostMapping("/purchase")
    public R<String> purchase(@Valid @RequestBody BookRelationReqVO reqVO) {
        bookService.purchaseBook(reqVO.getBookId());
        return R.ok("购买成功");
    }

    /**
     * 查询用户与某本电子书的关联状态
     */
    @GetMapping("/status")
    public R<BookRelationStatusVO> status(@NotNull(message = "电子书ID不能为空") @RequestParam Long bookId) {
        return R.ok(bookService.getBookRelationStatus(bookId));
    }
}
