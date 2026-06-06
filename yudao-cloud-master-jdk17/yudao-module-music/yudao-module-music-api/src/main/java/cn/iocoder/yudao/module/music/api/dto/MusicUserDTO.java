package cn.iocoder.yudao.module.music.api.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MusicUserDTO {

    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String mobile;
    private Integer status;
    private LocalDateTime createTime;

}