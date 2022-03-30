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
import java.util.UUID;

import static java.lang.Math.min;

@Slf4j // 스프링 부트에서 로그를 남기는 방법 중 가장 편하게 사용되는 어노테이션
@RequiredArgsConstructor
@Component
public class S3Service {
    private final AmazonS3Client amazonS3Client;

    @Value("${aws.s3.image.bucket}")
    public String bucket;  // S3 버킷 이름

    // 전달 받은 데이터를 바로 S3에 업로드하는 메소드
    // 1. 사전 준비 - 메타데이터와 파일명 생성
    // 2. S3에 전달 받은 파일 업로드
    // 3. S3에 저장된 파일 이름과 주소 반환

    // 파라미터로 multipartFile(업로드하려는 파일)과 dirName(이 파일을 업로드하고 싶은 S3 버킷의 폴더 이름)을 받는다.

    // 사진 원본 파일 저장 메소드
    public ImageInfoDto uploadOriginalFile(MultipartFile multipartFile, String dirName) throws IOException {

        // 0. 이미지 파일인지 체크
        isImage(multipartFile);

        // 1. 사전 준비
        // 1-1 메타데이터 생성
        // InputStream을 통해 Byte만 전달되고 해당 파일에 대한 정보가 없기 때문에 파일의 정보를 담은 메타데이터가 필요하다.
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());

        // 1-2 S3에 저장할 파일명 생성
        // UUID 사용 이유 : 이름이 같은 파일들이 서로 덮어쓰지 않고 구분될 수 있도록
        String fileName = createFileName(multipartFile);
        String uploadImageName = dirName + "/" + UUID.randomUUID() + fileName;

        // 2. s3로 업로드
        amazonS3Client.putObject(bucket,uploadImageName, multipartFile.getInputStream(),metadata);
        // S3에 업로드한 이미지의 주소를 받아온다.
        String uploadImageUrl = amazonS3Client.getUrl(bucket, uploadImageName).toString();


        // 4. S3에 저장된 파일 이름과 주소 반환
        return ImageInfoDto.builder()
                .url(uploadImageUrl)
                .name(uploadImageName)
                .build();
    }

    // 리사이즈 파일 저장 메소드
    public ImageInfoDto uploadThumbnailFile(MultipartFile multipartFile, String dirName) throws IOException {

        // 0. 이미지 파일인지 체크
        isImage(multipartFile);

        // 1. 사전 준비
        // 1-1 메타데이터 생성
        // InputStream을 통해 Byte만 전달되고 해당 파일에 대한 정보가 없기 때문에 파일의 정보를 담은 메타데이터가 필요하다.
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());

        // 1-2 S3에 저장할 파일명 생성
        // UUID 사용 이유 : 이름이 같은 파일들이 서로 덮어쓰지 않고 구분될 수 있도록
        String fileName = createFileName(multipartFile);
        String uploadImageName = dirName + "/" + UUID.randomUUID() + fileName;

        BufferedImage originalImage = ImageIO.read(multipartFile.getInputStream());
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int cropSize = min(width,height);

        BufferedImage resizedImage = Thumbnails.of(multipartFile.getInputStream())
                .sourceRegion(Positions.CENTER, cropSize, cropSize)
                .size(200,200)
                .asBufferedImage();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", os);
        InputStream is = new ByteArrayInputStream(os.toByteArray());

        metadata.setContentLength(os.size());

        // 2. s3로 업로드
        amazonS3Client.putObject(bucket,uploadImageName, is, metadata);
        // S3에 업로드한 이미지의 주소를 받아온다.
        String uploadImageUrl = amazonS3Client.getUrl(bucket, uploadImageName).toString();

        return ImageInfoDto.builder()
                .url(uploadImageUrl)
                .name(uploadImageName)
                .build();
    }

    // 원본 + 리사이즈 파일 동시 저장 메소드
    public void uploadBothFile(MultipartFile multipartFile, String dirName) throws IOException {
        uploadOriginalFile(multipartFile, dirName);
        uploadThumbnailFile(multipartFile, dirName+"_RESIZE");
    }




    // 이미지 url로 파일 업로드하는 메소드
    public ImageInfoDto uploadThumbnailFileByUrl(String inputUrl, String dirName) throws IOException {

        URL url = new URL(inputUrl);
        BufferedInputStream orginalIs = new BufferedInputStream(url.openStream());

        BufferedImage originalImage = ImageIO.read(orginalIs);
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int cropSize = min(width,height);

        BufferedImage resizedImage = Thumbnails.of(url.openStream())
                .sourceRegion(Positions.CENTER, cropSize, cropSize)
                .size(200,200)
                .asBufferedImage();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", os);
        InputStream is = new ByteArrayInputStream(os.toByteArray());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("jpg");
        metadata.setContentLength(os.size());

        String uploadImageName = dirName + "/" + UUID.randomUUID();

        // 2. s3로 업로드
        amazonS3Client.putObject(bucket, uploadImageName, is, metadata);
        // S3에 업로드한 이미지의 주소를 받아온다.
        String uploadImageUrl = amazonS3Client.getUrl(bucket, uploadImageName).toString();

        return ImageInfoDto.builder()
                .url(uploadImageUrl)
                .name(uploadImageName)
                .build();
    }

    // 파일 삭제하기
    public void deleteFile(String fileName) {
        amazonS3Client.deleteObject(bucket, fileName);
    }

    // 파일 다운로드하기
    public ResponseEntity<byte[]> downloadFile(String storedFileName) throws IOException {

        // S3에서 해당 파일 가져와서 byte 배열로 변경
        S3Object o = amazonS3Client.getObject(bucket, storedFileName);
        S3ObjectInputStream objectInputStream = o.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        // response의 헤더 설정
        String fileName = "makecake-" + UUID.randomUUID();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_JPEG);
        httpHeaders.setContentLength(bytes.length);
        httpHeaders.setContentDispositionFormData("attachment", fileName);

        // 바디에 이미지 바이트파일 담고, 헤더에 위에서 설정한 헤더를 담고, 200코드로 response 전송
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

        // 파일 이름의 길이가 길면 100자로 자르기 (디비의 varchar(255) 제한에 걸리지 않으려고)
        if (name.length()>100){
            name = name.substring(0,100);
        }

        // S3에 저장할 파일 이름 생성
        String fileName = !ext.equals("")?name+"."+ext:name;

        return fileName;
    }

    // 이미지 파일인지 확인하는 메소드
    private void isImage(MultipartFile multipartFile) throws IOException {

        // tika를 이용해 파일 MIME 타입 체크
        // 파일명에 .jpg 식으로 붙는 확장자는 없앨 수도 있고 조작도 가능하므로 MIME 타입을 체크하는 것이 좋다.
        Tika tika = new Tika();
        String mimeType = tika.detect(multipartFile.getInputStream());

        // MIME타입이 이미지가 아니면 exception 발생
        if (!mimeType.startsWith("image/")) {
            throw new CustomException(ErrorCode.NOT_IMAGEFILE);
        }
    }
}
