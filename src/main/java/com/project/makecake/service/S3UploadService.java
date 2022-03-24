package com.project.makecake.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.project.makecake.dto.ImageInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j // 스프링 부트에서 로그를 남기는 방법 중 가장 편하게 사용되는 어노테이션
@RequiredArgsConstructor
@Component
public class S3UploadService {
    private final AmazonS3Client amazonS3Client;

    @Value("${aws.s3.image.bucket}")
    public String bucket;  // S3 버킷 이름

    // 전달 받은 데이터를 바로 S3에 업로드하는 메소드
    // 1. 사전 준비 - 메타데이터와 파일명 생성
    // 2. S3에 전달 받은 파일 업로드
    // 3. S3에 저장된 파일 이름과 주소 반환

    // 파라미터로 multipartFile(업로드하려는 파일)과 dirName(이 파일을 업로드하고 싶은 S3 버킷의 폴더 이름)을 받는다.
    public ImageInfoDto uploadFile(MultipartFile multipartFile, String dirName) throws IOException {

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

    // 파일 삭제하기
    public void deleteFile(String fileName) {
        amazonS3Client.deleteObject(bucket, fileName);
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
            throw new IllegalArgumentException("업로드하려는 파일이 이미지 파일이 아닙니다.");
        }
    }
}
