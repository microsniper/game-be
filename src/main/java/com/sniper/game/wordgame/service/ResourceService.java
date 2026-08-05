package com.sniper.game.wordgame.service;

import com.aliyun.oss.OSS;
import com.sniper.game.wordgame.config.OssProperties;
import com.sniper.game.wordgame.dto.ResourceUploadResponse;
import com.sniper.game.wordgame.dto.SignInRewardItem;
import com.sniper.game.wordgame.exception.BusinessException;
import com.sniper.game.wordgame.mapper.SignInRewardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * 资源与签到配置：
 * - upload：公共上传，图片传阿里云 OSS 返回 CDN 地址（不落库，登记由使用方自行处理）
 * - getSignInRewards：7 天签到奖励（配置表 JOIN 资源表）
 * 签到状态全在前端 localStorage，后端无用户签到记录。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024L;
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");

    private final OSS ossClient;
    private final OssProperties ossProperties;
    private final SignInRewardMapper signInRewardMapper;

    /** 公共上传：校验格式大小 -> 传 OSS -> 返回 CDN 地址（不插库） */
    public ResourceUploadResponse upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw BusinessException.badRequest("文件为空");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw BusinessException.badRequest("图片不能超过 2MB");
        }
        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String ext = getExtension(original);
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw BusinessException.badRequest("仅支持 jpg/png 图片");
        }

        // 对象 key：game/fruit-pop/时间戳_短随机.ext，避免重名覆盖
        String objectKey = "game/fruit-pop/" + System.currentTimeMillis() + "_"
                + UUID.randomUUID().toString().substring(0, 8) + "." + ext;

        try (InputStream in = file.getInputStream()) {
            ossClient.putObject(ossProperties.getBucket(), objectKey, in);
        } catch (Exception e) {
            log.error("OSS 上传失败: key={}", objectKey, e);
            throw new BusinessException("图片上传失败，请稍后重试");
        }

        String url = ossProperties.getPublicDomain() + "/" + objectKey;
        log.info("资源上传成功: url={}", url);
        return new ResourceUploadResponse(url);
    }

    /** 7 天签到奖励配置（含奖励图 URL），按天升序 */
    public List<SignInRewardItem> getSignInRewards() {
        return signInRewardMapper.findAllWithResource();
    }

    private String getExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1) {
            return "";
        }
        return filename.substring(idx + 1).toLowerCase(Locale.ROOT);
    }
}
