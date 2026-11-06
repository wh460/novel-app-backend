package com.cafuc.novelapp.service.Impl;

import com.cafuc.novelapp.bean.Category;
import com.cafuc.novelapp.mapper.CategoryMapper;
import com.cafuc.novelapp.service.ICategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 分类表 服务实现类
 * </p>
 *
 * @author wh460
 * @since 2026-07-14
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

}
