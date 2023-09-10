package ru.hogwarts.school.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@RestController

@RequestMapping("avatar")
public class AvatarController {
    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(value = "/{studentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity uploadAvatar(@PathVariable Long studentId, @RequestParam MultipartFile file) throws IOException {
        if (file.getSize() > 1024 * 300){
            return ResponseEntity.badRequest().body("File is too big");
        }
        avatarService.uploadAvatar(studentId, file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{studentId}")
    public ResponseEntity getAvatar(@PathVariable Long studentId, HttpServletResponse response) throws IOException{
        Optional<Avatar> op = avatarService.findByStudentId(studentId);
        if (op.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Avatar avatar = op.get();
        Path filePath = Path.of(avatar.getFilePath());
        try(InputStream in = Files.newInputStream(filePath);
            OutputStream out = response.getOutputStream();
            BufferedInputStream bIn = new BufferedInputStream(in, 1024);
            BufferedOutputStream bOut = new BufferedOutputStream(out, 1024)){
            response.setStatus(200);
            response.setContentType(avatar.getMediaType());
            response.setContentLength((int) avatar.getFileSize());
            in.transferTo(out);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{studentId}/preview")
    public ResponseEntity<byte[]> getAvatarPreview(@PathVariable Long studentId){
        Optional<Avatar> optionalAvatar = avatarService.findByStudentId(studentId);
        if (optionalAvatar.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Avatar avatar = optionalAvatar.get();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getData().length);

        System.out.println(studentId + " length = " + avatar.getData().length);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(avatar.getData());
    }
    @GetMapping("/list-of-avatars")
    public List<Avatar> getAllAvatarsPreview (@RequestParam("page") Integer pageNumber,
                                              @RequestParam("size") Integer size){
        return avatarService.getListOfAvatars(pageNumber, size);
    }

}
