package com.cafuc.novelapp.service.Impl;

import com.cafuc.novelapp.bean.User;
import com.cafuc.novelapp.mapper.UserMapper;
import com.cafuc.novelapp.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author wh460
 * @since 2026-07-14
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
