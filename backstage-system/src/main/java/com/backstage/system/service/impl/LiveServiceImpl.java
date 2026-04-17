package com.backstage.system.service.impl;

import com.backstage.common.core.domain.R;
import com.backstage.system.domain.vo.LiveDetailVo;
import com.backstage.system.domain.vo.LiveQueryVo;
import com.backstage.system.domain.vo.LiveUserVo;
import com.backstage.system.mapper.live.LiveMapper;
import com.backstage.system.service.ILiveService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/3/8
 * Time: 14:24
 */
@Service
public class LiveServiceImpl implements ILiveService {

    @Autowired
    private LiveMapper liveMapper;

    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;

    @Override
    public R<LiveDetailVo> read(Long id, String token) {
        LiveDetailVo liveDetailVO = liveMapper.getLiveInfoById(id);
        Claims claims = parseToken(secret, token);
        Long userId = claims.get("user_id", Long.class);
        LiveUserVo liveUserVo = liveMapper.getLiveUserById(id, userId);
        liveDetailVO.setIsbuy(liveUserVo.getIsbuy());
        liveDetailVO.setIsfava(liveUserVo.getIsfava());
        return R.ok(liveDetailVO);
    }

    @Override
    public List<LiveQueryVo> list() {
        return liveMapper.getLiveList();
    }

    public Claims parseToken(String secret, String token) {
        try {
            // 使用相同的secret解析token
            Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
            return claims;
        } catch (SignatureException e) {
            // 签名验证失败
            throw new RuntimeException("Token签名验证失败", e);
        } catch (Exception e) {
            // token过期或其他解析错误
            throw new RuntimeException("Token解析失败", e);
        }
    }
}