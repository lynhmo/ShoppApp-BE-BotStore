package com.llu1ts.shopapp.common;

import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Value
public class ImageUpload {
    List<MultipartFile> files;

}
