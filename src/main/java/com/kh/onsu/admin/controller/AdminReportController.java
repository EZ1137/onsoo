package com.kh.onsu.admin.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kh.onsu.admin.model.biz.AdminReportBiz;
import com.kh.onsu.admin.model.biz.AuthBiz;
import com.kh.onsu.admin.model.dto.AdminDto;
import com.kh.onsu.admin.model.dto.AdminReportDto;

@Controller
public class AdminReportController {

	@Autowired
	private AdminReportBiz biz;
	
	@Autowired
	private AuthBiz authBiz;
	
	private Logger logger = LoggerFactory.getLogger(AdminReportController.class);
	
	@RequestMapping("admin/reportlist")
	public String selectList(Model model) {
		logger.info("ReportController selectList");
		
		model.addAttribute("reportlist", biz.selectList());
		
		return "admin/report";
	}
	
	@RequestMapping("/reportdetail")
	public String detail(Model model, String report_id) {
		logger.info("ReportController detail - report_id : " + report_id);
		
		AdminReportDto dto = biz.selectOne(report_id);
		model.addAttribute("rdto", dto);
		return "admin/reportdetail";
	}
	
	@RequestMapping("reportupdate")
	public String update(Model model, String member_id, String member_role,@DateTimeFormat(pattern = "yyyy-MM-dd") Date member_bdate) {
		logger.info("ReportController update - member_role : " + member_role);
		
		//강사 자격 정지
		if (member_role.equals("S")) {
			logger.info("강사 정지상태");
			AdminDto dto = new AdminDto();
			dto.setMember_id(member_id);
			dto.setMember_role(member_role);
			dto.setMember_bdate(member_bdate);
			
			logger.info("member_id : " + member_id);
			logger.info("member_role : " + member_role);
			logger.info("member_bdate : " + member_bdate);
			
			int res = biz.update(dto);
			if (res > 0) {
				logger.info(" S 회원 권한 성공 ");
				model.addAttribute("res", res);
				model.addAttribute("msg", "회원 등급 변경 성공");
				model.addAttribute("url", "/admin/reportlist");
				return "redirect";
			} else {
				model.addAttribute("res", res);
				model.addAttribute("msg", "s 권한 변경에 실패했습니다.");
				model.addAttribute("url", "/admin/reportlist");
				return "redirect";
			}
			
		} else if(member_role.equals("B")) {
			logger.info("회원 정지");
			AdminDto dto = new AdminDto();	
			dto.setMember_id(member_id);
			dto.setMember_role(member_role);
			dto.setMember_bdate(member_bdate);
			
			int res = biz.update(dto);
			int authrity = authBiz.updateb(member_id);
			if (res > 0 && authrity > 0) {
				model.addAttribute("msg", "회원 계정이 정지 되었습니다.");
				model.addAttribute("url", "/admin/reportlist");
				return "redirect";
			} else {
				model.addAttribute("msg", "다시 입력해주세요");
				model.addAttribute("url", "/admin/reportdetail?report_id=" + member_id);
				return "redirect";
			}
		
		} else {
			logger.info("회원 등급 저장실패");
			model.addAttribute("msg", "다시 입력해주세요");
			model.addAttribute("url", "/admin/reportdetail");
			return "redirect";
		}
	}
}