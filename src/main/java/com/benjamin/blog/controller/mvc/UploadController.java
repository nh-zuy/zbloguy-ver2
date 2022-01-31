package com.benjamin.blog.controller.mvc;

import com.benjamin.blog.utils.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@Controller
public class UploadController {

    @PostMapping("/upload/images")
    public String upload(@RequestPart MultipartFile upload, HttpServletRequest request) throws IOException {
        String sourceName = upload.getOriginalFilename();
        String sourceExt = Utils.getExtensionByStringHandling(sourceName).orElse(null);
        if (sourceExt == null) {
            throw new RuntimeException(String.format("%s not found with %s : %s", sourceExt, "file extension", sourceExt));
        }

        File destFile;
        String destFilename;

        do {
            destFilename = Utils.getAlphaNumericString(8).concat(".").concat(sourceExt);
            destFile = new File("src/main/resources/static/upload/".concat(destFilename));
        } while (destFile.exists());
        //destFile.getParentFile().mkdirs();
        upload.transferTo(destFile);

        return request.getScheme().concat("://").concat(request.getServerName()).concat("/upload/").concat(destFilename);
    }
}
