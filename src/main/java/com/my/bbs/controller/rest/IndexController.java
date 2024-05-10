package com.my.bbs.controller.rest;

import com.my.bbs.entity.CategoryEntity;
import com.my.bbs.service.CategoryService;
import com.my.bbs.service.PostService;
import com.my.bbs.util.PageQueryUtil;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController {
    @Resource
    private CategoryService categoryService;
    @Resource
    private PostService postService;

    @GetMapping({"", "/", "/index", "/index.html"})
    public String indexPage(HttpServletRequest request,
                            @RequestParam(value = "categoryId", required = false) Long categoryId,
                            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                            @RequestParam(value = "keyword", required = false) String keyword,
                            @RequestParam(value = "period", required = false) String period,
                            @RequestParam(value = "orderBy", required = false) String orderBy) {
        List<CategoryEntity> categoryEntities = categoryService.getBBSPostCategories();
        if (CollectionUtils.isEmpty(categoryEntities)) {
            return "error/error_404";
        }

        /* 将不同数据设置到请求属性中，以便在Thymeleaf模板中访问 */

        // 设置类别数据
        request.setAttribute("bbsPostCategories", categoryEntities);

        // 设置热门帖子数据
        request.setAttribute("hotTopicBBSPostList", postService.getHotPosts());

        // 设置页码参数
        Map params = new HashMap();
        params.put("page", page);
        params.put("limit", 10);    // 默认每页10条

        // 设置选择的类别Id
        if (categoryId != null && categoryId > 0) {
            request.setAttribute("categoryId", categoryId);
            params.put("categoryId", categoryId);
        }

        // 设置搜索关键字
        if (StringUtils.hasLength(keyword)) {
            request.setAttribute("keyword", keyword);
            params.put("keyword", keyword);
        }

        // 设置榜单选择(周榜、月榜、全部)
        if (StringUtils.hasLength(period)) {
            request.setAttribute("period", period);
            params.put("period", period);
        }
        // 设置排序选项(按时间、按浏览量)
        if (StringUtils.hasLength(orderBy)) {
            request.setAttribute("orderBy", orderBy);
            params.put("orderBy", orderBy);
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);

        // 设置当前页的帖子数据
        request.setAttribute("pageResult", postService.getPagePosts(pageUtil));
        return "index";
    }
}
