package io.devshare.controllers;

import io.devshare.application.ImagePostService;
import io.devshare.application.S3ImageUploader;
import io.devshare.domain.ImagePost;
import io.devshare.dto.ImagePostCreateRequest;
import io.devshare.dto.ImageUploadRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IndexController.class)
public class IndexControllerTest {

    private static final String FILENAME = "static/img/test.png";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private S3ImageUploader s3ImageUploader;

    @MockBean
    private ImagePostService imagePostService;

    @Test
    @DisplayName("GET / 요청은 200을 응답하고 모든 이미지 포스트를 리턴한다")
    void getIndex() throws Exception {
        given(imagePostService.getAllImagePosts())
                .willReturn(Arrays.asList());

        ResultActions actions = mvc.perform(
                get("/")
        );

        actions
                .andExpect(status().isOk())
                .andExpect(model().attribute("ImagePosts", Arrays.asList()));
    }

    @Test
    @DisplayName("POST /images 요청은 301을 응답한다")
    void postImages() throws Exception {
        given(s3ImageUploader.upload(any(ImageUploadRequest.class)))
                .willReturn("url");
        given(imagePostService.add(any(ImagePostCreateRequest.class)))
                .willReturn(ImagePost.create("uploader"));

        ClassPathResource fileResource = new ClassPathResource(FILENAME);
        MockMultipartFile image = new MockMultipartFile(
                "images",
                fileResource.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                fileResource.getInputStream()
        );

        ResultActions actions = mvc.perform(
                multipart("/images")
                        .file(image)
                        .param("uploader", "uploader")
        );

        actions
                .andExpect(status().isFound());
    }
}
