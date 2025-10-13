package com.cn.filter;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.cn.common.msg.Result;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import com.cn.common.enums.RoleEnum;

/**
 * 路由拦截器
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Configuration
@SuppressWarnings("all")
public class RoutePathFilter {


	@Bean
	public SaServletFilter getSaServletFilter() {
		return new SaServletFilter()
				.addInclude("/**").addExclude("/favicon.ico")
				.setAuth(obj -> {
					// 系统用户模块需管理员角色
					SaRouter.match("/system/**", r -> StpUtil.checkRole(RoleEnum.ADMIN.getDesc()));

				// 其余路径需登录
				SaRouter.match("/**")
						.notMatch(
								"/auth/**",
								"/ws/**",
								"/llm/chat/stream",
								"/llm/get/available-model/page",
								"/llm/get/available-model/list",
								"/llm/get/default-model",
                                "/notice/get",
								"/comfyui/task/get/workflow/page",
								"/comfyui/task/get/workflow/interface",
								"/comfyui/task/get/workflow-category/list"
						)
						.check(r -> StpUtil.checkLogin());
				})
				.setError(e -> {
					return Result.error("登录身份信息已过期,请重新登录", 401);
				})
				.setBeforeAuth(r -> {
					SaHolder.getResponse()
							.setHeader("Access-Control-Allow-Origin", "*")
							.setHeader("Access-Control-Allow-Methods", "*")
							.setHeader("Access-Control-Max-Age", "3600")
							.setHeader("Access-Control-Allow-Headers", "*")
							.setServer("Zeus");
					if (SaHolder.getRequest().getMethod().equals(HttpMethod.OPTIONS.toString())) {
						SaRouter.back();
					}
				});
	}
}
