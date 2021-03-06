package com.my.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.JsonObject;
import com.my.service.boardServiceInterface;
import com.my.service.categoryServiceInterface;
import com.my.vo.boardVO;
import com.my.vo.categoryVO;
import com.my.vo.userVO;

@Controller
@RequestMapping(value = "/board")
public class BoardController {

	@Autowired
    private SqlSession sqlSession;
	
	@Inject
	private boardServiceInterface boardService;
	
	@Inject
	private categoryServiceInterface categoryService;
	
	//==========할 일 목록==========
	
	//==========완료 목록==========
	//1. write 완성
	//2. update 완성
	//3. delete 완성
	//4. list에 페이징 기능 추가
	
	//list-get
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public void getList(@RequestParam("category") String category, @RequestParam(value="page",defaultValue="1") int page, Model model) throws Exception {
		System.out.println("start list from board - method : get");
			
		List<boardVO> boardList;
		
		boardList=boardService.selectPost(category);
		
		categoryVO tempCategory=categoryService.selectCategoryInfo(category);
		
		model.addAttribute("category", category);
		model.addAttribute("boardList", boardList);
		model.addAttribute("categoryVO", tempCategory);
		model.addAttribute("currentPage", page);
	}
	
	//view-get
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public void getView(@RequestParam("no") int no, Model model) throws Exception {
		System.out.println("start view from board - method : get");
			
		boardVO vo = boardService.selectOnePost(no);
		model.addAttribute("boardVO", vo);
	}
	
	//write - get, com.my.util.urlInterceptor 적용
	@RequestMapping(value = "/write", method = RequestMethod.GET)
	public void getWrite(HttpServletRequest request, RedirectAttributes rttr) throws Exception {
		System.out.println("start write from board - method : get");
	}
	
	//write - post, sql - insert, com.my.util.urlInterceptor 적용
	@RequestMapping(value = "/write", method = RequestMethod.POST)
	public String postWrite(HttpServletRequest request, RedirectAttributes rttr, boardVO vo) throws Exception {
		System.out.println("start write from board - method : post");
		System.out.println("DB 값 넘기기");
		
		HttpSession session = request.getSession();
		userVO tempUser=(userVO)session.getAttribute("user");//세션에서 id 값 추출
		vo.setUserID(tempUser.getId());
		
		categoryVO tempCategory = new categoryVO();
		tempCategory.setCategoryName(vo.getCategory());
		tempCategory.setUserID(tempUser.getId());
		
		boardService.insertPost(vo);//게시글 추가
		categoryService.countUp(tempCategory);//해당 게시판의 총 게시글 수 증가
		
		return "redirect:/board/list?category="+vo.getCategory();
	}
	
	//update - get, com.my.util.urlInterceptor 적용
	@RequestMapping(value = "/write_{no}", method = RequestMethod.GET)
	public String getUpdate(HttpServletRequest request, RedirectAttributes rttr, @PathVariable int no, Model model) throws Exception {
		System.out.println("start update from board - method : get");

		boardVO vo = boardService.selectOnePost(no);
		model.addAttribute("boardVO", vo);
		
		return "/board/write";
	}
		
	//update - post, sql - update, com.my.util.urlInterceptor 적용
	@RequestMapping(value = "/write_{no}", method = RequestMethod.POST)
	public String postUpdate(boardVO vo, @RequestParam("category") String category) throws Exception {
		System.out.println("start update from board - method : post");
		System.out.println("DB 값 넘기기");
		
		boardService.updatePost(vo);
			
		return "redirect:/board/list?category="+category;
	}
	
	
	//delete, com.my.util.urlInterceptor 적용
	@RequestMapping(value = "/delete_{no}", method = RequestMethod.GET)
	public String getDelete(HttpServletRequest request, RedirectAttributes rttr, @PathVariable int no, @RequestParam("category") String category) throws Exception {
		System.out.println("start delete from board- method : get");
		
		HttpSession session = request.getSession();
		userVO tempUser=(userVO)session.getAttribute("user");//세션에서 id 값 추출
				
		boardVO tempBoard = new boardVO();
		tempBoard.setNo(no);
		tempBoard.setUserID(tempUser.getId());
		
		categoryVO tempCategory = new categoryVO();
		tempCategory.setCategoryName(category);
		tempCategory.setUserID(tempUser.getId());
		
		boardService.deletePost(tempBoard);//게시글 삭제
		categoryService.countDown(tempCategory);//해당 게시판에서 총 게시글 수 감소
			
		return "redirect:/board/list?category="+category;
	}
	
	@RequestMapping(value="/image", produces = "application/json; charset=utf8")
	@ResponseBody
	public String uploadSummernoteImageFile(@RequestParam("file") MultipartFile multipartFile, HttpServletRequest request )  {
		JsonObject jsonObject = new JsonObject();
		
		// 외부경로로 저장을 희망할때.
		//String realFileRoot = "C:\\Users\\you\\Desktop\\My_Space\\GitHub\\practice_spring\\practice_spring\\src\\main\\webapp\\resources\\image\\";
		
		// 내부경로로 저장
		String contextRoot = new HttpServletRequestWrapper(request).getRealPath("/");
		String fileRoot = contextRoot+"resources/image/";
		
		String originalFileName = multipartFile.getOriginalFilename();	//오리지날 파일명
		String extension = originalFileName.substring(originalFileName.lastIndexOf("."));	//파일 확장자
		String savedFileName = UUID.randomUUID() + extension;	//저장될 파일 명
		
		File targetFile = new File(fileRoot + savedFileName);
		//File targetFile = new File(realFileRoot+ savedFileName);//테스트	
		try {
			InputStream fileStream = multipartFile.getInputStream();
			FileUtils.copyInputStreamToFile(fileStream, targetFile);	//파일 저장
			jsonObject.addProperty("url", "/resources/image/"+savedFileName); // contextroot + resources + 저장할 내부 폴더명
			//jsonObject.addProperty("url", fileRoot+savedFileName); 
			System.out.println(fileRoot+savedFileName);//경로 및 파일명 출력
			jsonObject.addProperty("responseCode", "success");
				
		} catch (IOException e) {
			FileUtils.deleteQuietly(targetFile);	//저장된 파일 삭제
			jsonObject.addProperty("responseCode", "error");
			e.printStackTrace();
		}
		String a = jsonObject.toString();
		return a;
	}
}
