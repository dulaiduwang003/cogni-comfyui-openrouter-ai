package com.cn.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cn.common.entity.RedemptionCode;
import com.cn.common.enums.RedemptionCodeStatus;
import com.cn.common.mapper.RedemptionCodeMapper;
import com.cn.common.utils.RedemptionCodeGenerator;
import com.cn.common.vo.PageVo;
import com.cn.system.dto.CreateRedemptionCodeDto;
import com.cn.system.dto.DeleteRedemptionCodeDto;
import com.cn.system.dto.UpdateRedemptionCodeCreditsDto;
import com.cn.system.dto.UpdateRedemptionCodeDto;
import com.cn.system.service.SystemRedemptionCodeService;
import com.cn.system.vo.SystemRedemptionCodeVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统兑换码服务实现类
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemRedemptionCodeServiceImpl implements SystemRedemptionCodeService {

	private final RedemptionCodeMapper redemptionCodeMapper;

	@Override
	public PageVo<SystemRedemptionCodeVo> page(Integer page, Integer size, String keyword, Integer status) {
		Page<RedemptionCode> mpPage = new Page<>(page == null ? 1 : page, size == null ? 10 : size);
		LambdaQueryWrapper<RedemptionCode> wrapper = new LambdaQueryWrapper<>();
		if (StringUtils.isNotBlank(keyword)) {
			wrapper.like(RedemptionCode::getCode, keyword);
		}
		if (status != null) {
			wrapper.eq(RedemptionCode::getStatus, status);
		}
		wrapper.orderByDesc(RedemptionCode::getId);
		IPage<RedemptionCode> result = redemptionCodeMapper.selectPage(mpPage, wrapper);
		List<SystemRedemptionCodeVo> items = result.getRecords().stream().map(this::toVo).collect(Collectors.toList());
		return new PageVo<SystemRedemptionCodeVo>().setTotal(result.getTotal()).setItems(items);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long create(CreateRedemptionCodeDto dto) {
		String prefix = StringUtils.trimToEmpty(dto.getPrefix());
		int len = dto.getLength() == null ? 8 : Math.max(4, dto.getLength());
		String code = StringUtils.isBlank(prefix) ? RedemptionCodeGenerator.generateCode(len) : RedemptionCodeGenerator.generateCodeWithPrefix(prefix, len);

		RedemptionCode entity = new RedemptionCode()
				.setCode(code.toUpperCase())
				.setCreditsAmount(dto.getCreditsAmount())
				.setStatus(RedemptionCodeStatus.ACTIVE.getCode())
				.setExpireTime(dto.getExpireTime())
				.setDescription(dto.getDescription());
		redemptionCodeMapper.insert(entity);
		return entity.getId();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(DeleteRedemptionCodeDto dto) {
		redemptionCodeMapper.deleteById(dto.getId());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateCredits(UpdateRedemptionCodeCreditsDto dto) {
		RedemptionCode rc = redemptionCodeMapper.selectById(dto.getId());
		if (rc == null) {
			throw new IllegalArgumentException("兑换码不存在");
		}
		// 已使用的不允许改积分
		if (RedemptionCodeStatus.USED.getCode().equals(rc.getStatus())) {
			throw new IllegalStateException("已使用的兑换码不允许修改积分");
		}
		rc.setCreditsAmount(dto.getCreditsAmount());
		redemptionCodeMapper.updateById(rc);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(UpdateRedemptionCodeDto dto) {
		RedemptionCode rc = redemptionCodeMapper.selectById(dto.getId());
		if (rc == null) {
			throw new IllegalArgumentException("兑换码不存在");
		}
		// 不允许修改code，本方法仅修改其余可编辑字段
		if (dto.getCreditsAmount() != null) {
			// 已使用的不允许改积分
			if (RedemptionCodeStatus.USED.getCode().equals(rc.getStatus())) {
				throw new IllegalStateException("已使用的兑换码不允许修改积分");
			}
			rc.setCreditsAmount(dto.getCreditsAmount());
		}
		if (dto.getStatus() != null) {
			rc.setStatus(dto.getStatus());
		}
		if (dto.getExpireTime() != null) {
			rc.setExpireTime(dto.getExpireTime());
		}
		if (dto.getDescription() != null) {
			rc.setDescription(dto.getDescription());
		}
		redemptionCodeMapper.updateById(rc);
	}

	private SystemRedemptionCodeVo toVo(RedemptionCode rc) {
		return new SystemRedemptionCodeVo()
				.setId(rc.getId())
				.setCode(rc.getCode())
				.setCreditsAmount(rc.getCreditsAmount())
				.setStatus(rc.getStatus())
				.setUsedByUserId(rc.getUsedByUserId())
				.setUsedTime(rc.getUsedTime())
				.setExpireTime(rc.getExpireTime())
				.setDescription(rc.getDescription())
				.setCreateTime(rc.getCreateTime());
	}
}


