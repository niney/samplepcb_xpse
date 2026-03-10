package kr.co.samplepcb.xpse.resource;

import coolib.common.CCResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.samplepcb.xpse.service.PcbKindService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "PCB 종류", description = "PCB 종류 파일 업로드/재인덱싱 API")
@RestController
@RequestMapping("/api/pcbKind")
public class PcbKindResource {

    private final PcbKindService pcbKindService;

    public PcbKindResource(PcbKindService pcbKindService) {
        this.pcbKindService = pcbKindService;
    }

    @Operation(summary = "종류 파일 업로드", description = "PCB 종류 파일을 업로드하고 재인덱싱합니다")
    @PostMapping(value = "/_uploadItemFile")
    public CCResult uploadItemFile(@RequestParam("file") MultipartFile file) {
        this.pcbKindService.reindexAllByFile(file);
        return CCResult.ok();
    }
}
