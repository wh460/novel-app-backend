package com.cafuc.novelapp.service.Impl;

import com.cafuc.novelapp.bean.Admin;
import com.cafuc.novelapp.mapper.AdminMapper;
import com.cafuc.novelapp.service.IAdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 管理员表 服务实现类
 * </p>
 *
 * @author wh460
 * @since 2026-07-17
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {

}
