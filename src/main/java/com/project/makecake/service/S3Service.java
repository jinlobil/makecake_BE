package com.project.makecake.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.project.makecake.dto.ImageInfoDto;
import com.project.makecake.exceptionhandler.CustomException;
import com.project.makecake.exceptionhandler.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

import static java.lang.Math.min;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Service {
    private final AmazonS3Client amazonS3Client;

    @Value("${aws.s3.image.bucket}")
    public String bucket;

    // 사진 원본 파일 저장 메소드
    public ImageInfoDto uploadImg(MultipartFile multipartFile, String dirName) throws IOException {

        isImage(multipartFile);

        // 메타데이터 생성
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());

        // 파일명 생성
        String fileName = createFileName(multipartFile);
        String uploadImageName = dirName + "/" + UUID.randomUUID() + fileName;

        // 업로드
        amazonS3Client.putObject(bucket,uploadImageName, multipartFile.getInputStream(),metadata);
        String uploadImageUrl = amazonS3Client.getUrl(bucket, uploadImageName).toString();

        return ImageInfoDto.builder()
                .url(uploadImageUrl)
                .name(uploadImageName)
                .build();
    }

    // 리사이즈 파일 저장 메소드
    public ImageInfoDto uploadThumbImg(MultipartFile multipartFile, int size, String dirName) throws IOException {

        String ext = isImage(multipartFile);

        // 파일명 생성
        String fileName = createFileName(multipartFile);
        String uploadImageName = dirName + "/" + UUID.randomUUID() + fileName;

        // 원본 이미지 사이즈
        BufferedImage originalImage = ImageIO.read(multipartFile.getInputStream());
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int cropSize = min(width,height);

        // 정사각형으로 크롭하고 사이즈 줄이기
        BufferedImage resizedImage = Thumbnails.of(multipartFile.getInputStream())
                .sourceRegion(Positions.CENTER, cropSize, cropSize)
                .size(size,size)
                .asBufferedImage();

        // 이미지 쓰기
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, ext, os);

        // 에러날 경우 무조건 png로 쓰기
        ByteArrayOutputStream uploadOs = new ByteArrayOutputStream();
        if (os.size() == 0) {
            ImageIO.write(resizedImage, "png", uploadOs);
        } else {
            uploadOs = os;
        }

        // input스트림과 메타데이터 생성
        InputStream is = new ByteArrayInputStream(uploadOs.toByteArray());
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(ext);
        metadata.setContentLength(uploadOs.size());

        // 업로드
        amazonS3Client.putObject(bucket,uploadImageName, is, metadata);
        String uploadImageUrl = amazonS3Client.getUrl(bucket, uploadImageName).toString();

        return ImageInfoDto.builder()
                .url(uploadImageUrl)
                .name(uploadImageName)
                .build();
    }

    // 원본 + 리사이즈 파일 동시 저장 메소드
    public HashMap<String,ImageInfoDto> uploadBothFile(
            MultipartFile multipartFile,
            int size,
            String dirName
    ) throws IOException {

        HashMap<String,ImageInfoDto> infoDtoList = new HashMap<>();
        infoDtoList.put("original", uploadImg(multipartFile, dirName));
        infoDtoList.put("thumbnail", uploadThumbImg(multipartFile, size, dirName+"_RESIZE"));
        return infoDtoList;
    }

    // 이미지 url로 썸네일 파일 업로드하는 메소드
    public ImageInfoDto uploadThumbByUrl(String inputUrl, int size, String dirName) throws IOException {

        URL url = new URL(inputUrl);
        BufferedInputStream orginalIs = new BufferedInputStream(url.openStream());

        Tika tika = new Tika();
        String mimeType = tika.detect(orginalIs);
        String ext = mimeType.substring(6);

        // 원본 이미지 사이즈
        BufferedImage originalImage = ImageIO.read(orginalIs);
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int cropSize = min(width,height);

        // 정사각형으로 크롭하고 사이즈 줄이기
        BufferedImage resizedImage = Thumbnails.of(url.openStream())
                .sourceRegion(Positions.CENTER, cropSize, cropSize)
                .size(size,size)
                .asBufferedImage();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, ext, os);

        // 에러날 경우 무조건 png로 쓰기
        ByteArrayOutputStream uploadOs = new ByteArrayOutputStream();
        if (os.size() == 0) {
            ImageIO.write(resizedImage, "png", uploadOs);
        } else {
            uploadOs = os;
        }

        // input스트림과 메타데이터 생성
        InputStream is = new ByteArrayInputStream(uploadOs.toByteArray());
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(ext);
        metadata.setContentLength(uploadOs.size());

        String uploadImageName = dirName + "/" + UUID.randomUUID();

        // 업로드
        amazonS3Client.putObject(bucket, uploadImageName, is, metadata);
        String uploadImageUrl = amazonS3Client.getUrl(bucket, uploadImageName).toString();

        return ImageInfoDto.builder()
                .url(uploadImageUrl)
                .name(uploadImageName)
                .build();

    }

    // 파일 삭제하기 메소드
    public void deleteFile(String fileName) {
        amazonS3Client.deleteObject(bucket, fileName);
    }

    // 파일 다운로드하기 메소드
    public ResponseEntity<byte[]> downloadFile(String storedFileName) throws IOException {

        // byte 배열로 변경
        S3Object o = amazonS3Client.getObject(bucket, storedFileName);
        S3ObjectInputStream objectInputStream = o.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        // response의 헤더 설정
        String fileName = "makecake-" + UUID.randomUUID();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_JPEG);
        httpHeaders.setContentLength(bytes.length);
        httpHeaders.setContentDispositionFormData("attachment", fileName);

        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }

    // 파일 이름 생성 메소드
    private String createFileName(MultipartFile multipartFile) {

        String name = multipartFile.getOriginalFilename();
        String ext = "";

        // 확장자와 파일명 분리
        if (name.contains(".")) {
            int position = name.lastIndexOf(".");
            ext = name.substring(position+1);
            name = name.substring(0,position);
        }

        if (name.length()>100){
            name = name.substring(0,100);
        }

        // 파일 이름 생성
        String fileName = !ext.equals("")?name+"."+ext:name;
        return fileName;
    }

    // 이미지 파일인지 확인하는 메소드
    private String isImage(MultipartFile multipartFile) throws IOException {

        Tika tika = new Tika();
        String mimeType = tika.detect(multipartFile.getInputStream());

        if (!mimeType.startsWith("image/")) {
            throw new CustomException(ErrorCode.NOT_IMAGEFILE);
        }

        return mimeType.substring(6);
    }
}
