package com.cafuc.novelapp.service.Impl;

import com.cafuc.novelapp.bean.Book;
import com.cafuc.novelapp.mapper.BookMapper;
import com.cafuc.novelapp.service.IBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 书籍表 服务实现类
 * </p>
 *
 * @author wh460
 * @since 2026-07-14
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements IBookService {

}
