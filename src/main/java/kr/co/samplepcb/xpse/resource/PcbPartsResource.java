package kr.co.samplepcb.xpse.resource;

import coolib.common.CCResult;
import kr.co.samplepcb.xpse.service.PcbPartsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/pcbParts")
public class PcbPartsResource {

    private final PcbPartsService pcbPartsService;

    public PcbPartsResource(PcbPartsService pcbPartsService) {
        this.pcbPartsService = pcbPartsService;
    }

    @PostMapping(value = "/_uploadItemFileByEleparts")
    public CCResult uploadItemFileByEleparts(@RequestParam("file") MultipartFile file/*, HttpServletRequest request*/) {
        this.pcbPartsService.indexAllByEleparts(file);
        return CCResult.ok();
    }

}
