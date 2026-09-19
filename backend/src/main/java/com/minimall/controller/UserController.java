package com.minimall.controller;

import com.minimall.common.BizException;
import com.minimall.common.R;
import com.minimall.common.enums.MemberLevel;
import com.minimall.dto.user.UserVO;
import com.minimall.entity.User;
import com.minimall.service.UserService;
import com.minimall.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台用户接口
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/info")
    public R<UserVO> info() {
        Long userId = UserContext.getUserId();
        User user = userService.getById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setMemberLevel(user.getMemberLevel());
        vo.setMemberLevelLabel(MemberLevel.fromCode(user.getMemberLevel()).getLabel());
        vo.setTotalSpent(user.getTotalSpent());
        return R.ok(vo);
    }
}
