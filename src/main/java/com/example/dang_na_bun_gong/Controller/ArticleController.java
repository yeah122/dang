package com.example.dang_na_bun_gong.Controller;

import com.example.dang_na_bun_gong.DTO.ArticleDto;
import com.example.dang_na_bun_gong.DTO.ArticleWriteDto;
import com.example.dang_na_bun_gong.DTO.MainDto;
import com.example.dang_na_bun_gong.Entity.ArticleEntity;
import com.example.dang_na_bun_gong.Service.ArticleService;
import com.example.dang_na_bun_gong.Service.BookMarkSerivce;
import com.example.dang_na_bun_gong.Vo.ResultVo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private BookMarkSerivce bookMarkSerivce;

    //main 페이지
    @GetMapping("/main")
    public @ResponseBody ResultVo main(){
        List<ArticleDto> mainCurrent = articleService.mainPage_current();
        List<ArticleDto> mainPopular = articleService.mainPage_popular();

        JSONObject jsonObject = new JSONObject();
        List<MainDto> mainDto = new ArrayList<>();

        for (int i=0; i<mainCurrent.size(); i++){
            MainDto data = new MainDto(mainCurrent.get(i));
            mainDto.add(data);
        }
        jsonObject.put("current", mainDto);
        mainDto.clear();

        for (int i=0; i<mainPopular.size(); i++){
            MainDto data = new MainDto(mainPopular.get(i));
            mainDto.add(data);
        }
        jsonObject.put("popular", mainDto);
        mainDto.clear();

        return new ResultVo(0, "true", "불러오기 성공", jsonObject.toString());
    }

    //게시물 리스트 보기 (실패)
    @GetMapping("/articleList")
    public @ResponseBody ResultVo articleList(
            @PageableDefault(page = 0, size = 5) Pageable pageable, HttpSession httpSession) {

        Integer regionid = 23080;
        Page<ArticleEntity> articlelist = articleService.articleListAll(pageable, regionid);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ArticleList", articlelist.toString());

        return new ResultVo(0, "ture", "이게 안돼?", jsonObject.toString());
    }

@GetMapping("/articleWrite")
    public String article(){
return "board";
    }

@PostMapping("/articleWriteDo")
    public @ResponseBody ResultVo articleWritedo(ArticleWriteDto article, List<MultipartFile> file, HttpSession session) throws IOException {
    String userid = session.getAttribute("memberid").toString();

    article.setSellmemberid(userid);

    /*
    // Null값 체크
    System.out.println("내용 : "+ article);
    String null_title = article.getArticletitle();
    String null_content = article.getArticlecontent();
    String null_prince = article.getPrice();
    Integer null_categoryid = article.getProductcategoryid();
    Integer null_regionid = article.getRegionid();

    if(null_title.isEmpty() || null_content.isEmpty() || null_prince.isEmpty() || null_categoryid == null || null_regionid == null){
        return new ResultVo(301, "false", "필수 입력 정보를 모두 입력해주십시오.");
    }

*/

    articleService.write(article,file);


    return new ResultVo(0, "true", "글쓰기 성공" ,null);
}

    @GetMapping("/articleView") // localhost:8080/article/View?id=1
    public @ResponseBody ResultVo articleView(Model model, Integer id, HttpSession session) {
        int likecheck;
    model.addAttribute("article",articleService.articleview(id));

        String memberId = (String)session.getAttribute("memberid");

       /* if(memberId == null) {
            System.out.println("세션에 로그인값이 존재 하지 않음");
            likecheck = 3;

            ArticleEntity jsonObject = articleService.articleview(id);
            System.out.println(jsonObject.toString());


            return new ResultVo(0, "true", "불러오기 성공", jsonObject.toString()+ ", likecheck: " + likecheck);
        }

        */

            likecheck = bookMarkSerivce.Likecheck(id, memberId);


            ArticleEntity jsonObject = articleService.articleview(id);
            System.out.println(jsonObject.toString());


            return new ResultVo(0, "true", "불러오기 성공", jsonObject.toString()+ ", likecheck: " + likecheck);




    }







}