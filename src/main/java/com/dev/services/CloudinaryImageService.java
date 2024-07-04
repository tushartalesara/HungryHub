package com.dev.services;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;

@Service
public class CloudinaryImageService implements CloudinaryService{

    @Autowired
    Cloudinary cloudinary;

    @Override
    public Map upload(MultipartFile file) {
        try {
            // Map<String, Object> uploadOptions = ObjectUtils.asMap(
            //     "transformation", new Transformation().width(width).height(height).crop("fit")
            // );
            // Map<String, Object> uploadResult = this.cloudinary.uploader().upload(file.getBytes(), uploadOptions);
            Map data = this.cloudinary.uploader().upload(file.getBytes(), Map.of());
            return data;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

}