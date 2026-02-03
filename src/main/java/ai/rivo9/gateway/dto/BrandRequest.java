package ai.rivo9.gateway.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BrandRequest {
    @NotBlank(message = "URL is required")
    private String url;
    private Boolean linkedin;
    private Boolean facebook;
    private Boolean youtube;
    private Boolean instagram;
    private Boolean x;
}
