package com.my.bbs;

import com.my.bbs.dao.CommentMapper;
import com.my.bbs.dao.UserMapper;
import com.my.bbs.entity.CommentEntity;
import com.my.bbs.entity.PostEntity;
import com.my.bbs.entity.UserEntity;
import com.my.bbs.service.CommentService;
import com.my.bbs.service.PostService;
import com.my.bbs.service.UserService;
import com.my.bbs.util.PatternUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

@SpringBootTest
class MyBBSApplicationTests {

	private UserMapper userMapper;
	@Resource
	private UserService userService;
	@Resource
	private PostService postService;
	@Resource
	private CommentMapper commentMapper;
	@Resource
	private CommentService commentService;
	@Test
	void contextLoads() {
	}

	@Autowired
	private DataSource dataSource;

	@Test
	void testDataSource(){
		System.out.println(dataSource!=null);
		System.out.println(dataSource);
	}

	@Test
	void testRegister(){
		System.out.println(userService.register("1123583391@qq.com", "wbTest1!", "wbTest1", "未知"));
		System.out.println(userService.register("wbtest@qq.com", "wbTest1!", "wbTest2", "未知"));
		System.out.println(userService.register("wbtest@qq.com", "wbTest1!", "wbTest2", "未知"));
	}

	@Test
	void testLogin(){
		System.out.println(userService.login("wbtest@qq.com", "wbTest1!", "wbTest1", null));
		System.out.println(userService.login("wbtest@qq.com", "wbTest1!", "wbTest2", null));
	}

	@Test
	void testUpdateUserInfo(){

		UserEntity normalUser = userMapper.selectByPrimaryKey(0L);		// 正常用户
		UserEntity abnormalUser = userMapper.selectByPrimaryKey(8L);		// 异常用户
		System.out.println(userService.updateUserInfo(abnormalUser, null));
		System.out.println(userService.updateUserInfo(normalUser, null));
		System.out.println(userService.updateUserInfo(normalUser, null));
	}

	@Test
	void testUpdatePassword(){
		System.out.println(userService.updatePassword(5L, "wbTest1!", "wbTest2!"));
		System.out.println(userService.updatePassword(5L, "wbTesasdt2!", "wbTest2!"));
		System.out.println(userService.updatePassword(5L, "wbTest1!", "wbTest2!"));
	}

	@Test
	void testSavePost(){
		PostEntity abnormalPost = postService.getPostById(6L);
		PostEntity post = postService.getPostById(16L);

		System.out.println(postService.savePost(abnormalPost));
		System.out.println(postService.savePost(post));
		post.setPostCategoryId(-1);
		System.out.println(postService.savePost(post));
	}

	@Test
	void testUpdatePost(){
		PostEntity abnormalPost = postService.getPostById(6L);
		PostEntity post = postService.getPostById(16L);

		System.out.println(postService.updatePost(abnormalPost));
		System.out.println(postService.updatePost(post));
		post.setPostCategoryId(-1);
		System.out.println(postService.updatePost(post));
	}

	@Test
	void testAddPostComment(){
		CommentEntity testComment = commentMapper.selectByPrimaryKey(16L);
		testComment.setPostId(-1L);
		System.out.println(commentService.addPostComment(testComment));
		testComment.setPostId(16L);
		testComment.setCommentUserId(-1L);
		System.out.println(commentService.addPostComment(testComment));
		testComment.setCommentUserId(16L);
		System.out.println(commentService.addPostComment(testComment));
	}

	@Test
	void testDeletePostComment(){
		System.out.println(commentService.delPostComment(-1L, 4L));
		System.out.println(commentService.delPostComment(4L, -1L));
		System.out.println(commentService.delPostComment(4L, 4L));
		System.out.println(commentService.delPostComment(4L, 5L));
		System.out.println(commentService.delPostComment(4L, 4L));
	}

	@Test
	void testPatternUtil(){
		System.out.println(PatternUtil.isEmail("1123583391@qq.com"));
		System.out.println(PatternUtil.isEmail("ajksdnkqjuw@163.com"));
		System.out.println(PatternUtil.isEmail("223156112"));
		System.out.println(PatternUtil.isEmail("1034683568@@163"));
		System.out.println(PatternUtil.isEmail("kansldw@mail2.sysu.edu.cn"));
	}



}
