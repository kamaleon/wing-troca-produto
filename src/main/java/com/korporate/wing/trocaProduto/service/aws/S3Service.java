package com.korporate.wing.trocaProduto.service.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.korporate.spring.aws.s3.KorporateS3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URL;
import java.util.UUID;

@Service
public class S3Service
{
    @Value("${aws.s3.bucket:}")
    private String bucket;

    @Autowired
    private KorporateS3 korporateS3;

    public String upload(File file) {
        // envia pro S3
        korporateS3.upload(bucket, file.getName(), file);

        // url do arquivo
        return getS3FileUrl(file.getName());
    }

    public String getS3FileUrl (String s3Object)
    {
        long umaHora = 60*60;
        return korporateS3.shareURL(bucket, s3Object, umaHora);
    }
}
