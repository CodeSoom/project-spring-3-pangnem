package io.devshare.application;

import io.devshare.domain.ImagePost;
import io.devshare.domain.ImagePostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ImagePost 관련 로직 처리를 담당.
 */
@Service
public class ImagePostService {
    private ImagePostRepository imagePostRepository;

    public ImagePostService(ImagePostRepository imagePostRepository) {
        this.imagePostRepository = imagePostRepository;
    }

    @Transactional(readOnly = true)
    public List<ImagePost> getAllImagePosts() {
        return imagePostRepository.findAll();
    }

    /**
     * 새 이미지 포스트를 만들어 추가한 후 리턴합니다.
     *
     * @param uploader 업로더
     * @param url      이미지 URL
     * @return 생성된 이미지 포스트
     */
    @Transactional
    public ImagePost add(String uploader, String url) {
        ImagePost imagePost = ImagePost.create(uploader);

        imagePost.upload(url);

        return imagePostRepository.save(imagePost);
    }
}
