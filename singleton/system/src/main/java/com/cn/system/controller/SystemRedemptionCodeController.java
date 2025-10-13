package com.cn.system.controller;

import com.cn.common.msg.Result;
import com.cn.common.vo.PageVo;
import com.cn.system.dto.CreateRedemptionCodeDto;
import com.cn.system.dto.DeleteRedemptionCodeDto;
import com.cn.system.dto.UpdateRedemptionCodeCreditsDto;
import com.cn.system.dto.UpdateRedemptionCodeDto;
import com.cn.system.service.SystemRedemptionCodeService;
import com.cn.system.vo.SystemRedemptionCodeVo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 系统兑换码控制器
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/system/redemption-code")
@Validated
@RequiredArgsConstructor
public class SystemRedemptionCodeController {

	private final SystemRedemptionCodeService systemRedemptionCodeService;

	@GetMapping(value = "/page", produces = MediaType.APPLICATION_JSON_VALUE)
	public Result page(@RequestParam(defaultValue = "1") @Min(1) Integer page,
	                  @RequestParam(defaultValue = "10") @Min(1) Integer size,
	                  @RequestParam(required = false) String keyword,
	                  @RequestParam(required = false) Integer status) {
		PageVo<SystemRedemptionCodeVo> data = systemRedemptionCodeService.page(page, size, keyword, status);
		return Result.data(data);
	}

	@PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Result create(@RequestBody @Valid CreateRedemptionCodeDto dto) {
		Long id = systemRedemptionCodeService.create(dto);
		return Result.data(id);
	}

	@PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Result delete(@RequestBody @Valid DeleteRedemptionCodeDto dto) {
		systemRedemptionCodeService.delete(dto);
		return Result.ok();
	}

	@PostMapping(value = "/update-credits", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Result updateCredits(@RequestBody @Valid UpdateRedemptionCodeCreditsDto dto) {
		systemRedemptionCodeService.updateCredits(dto);
		return Result.ok();
	}

	@PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Result update(@RequestBody @Valid UpdateRedemptionCodeDto dto) {
		systemRedemptionCodeService.update(dto);
		return Result.ok();
	}
}


