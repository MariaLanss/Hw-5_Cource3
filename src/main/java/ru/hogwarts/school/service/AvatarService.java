package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AvatarService {

    @Value("${avatars.path.dir}")
    String avatarsDir;

    private final AvatarRepository avatarRepository;
    private final StudentService studentService;
    private final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    public AvatarService(AvatarRepository avatarRepository, StudentService studentService) {
        this.avatarRepository = avatarRepository;
        this.studentService = studentService;
    }

    public Optional<Avatar> findByStudentId(Long id) {
        logger.info("Был вызван метод findByStudentId");
        return avatarRepository.findByStudentId(id);
    }

    public void uploadAvatar(Long id, MultipartFile file) throws IOException {
        logger.info("Был вызван метод uploadAvatar");
        if (file.getOriginalFilename() == null){
            logger.error("Файл для загрузки не найден");
        }
        Path filePath = Path.of(avatarsDir, id + "." + getExtension(file.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try(InputStream in = file.getInputStream();
            OutputStream out = Files.newOutputStream(filePath, StandardOpenOption.CREATE_NEW);
            BufferedInputStream bIn = new BufferedInputStream(in, 1024);
            BufferedOutputStream bOut = new BufferedOutputStream(out, 1024);
        ){
            in.transferTo(out);
        }

        Avatar avatar = findByStudentId(id).orElse(new Avatar());
        Student student = studentService.get(id);
        if (student == null){
            logger.error("Студент с id " + id + " не найден");
        }
        avatar.setStudent(student);
        avatar.setMediaType(file.getContentType());
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setData(createPreview(filePath));

        avatarRepository.save(avatar);
    }

    private byte[] createPreview(Path path) throws IOException{
        logger.info("Был вызван метод createPreview");
        try(InputStream in = Files.newInputStream(path);
            BufferedInputStream bIn = new BufferedInputStream(in, 1024);
            ByteArrayOutputStream bOut = new ByteArrayOutputStream()){
            BufferedImage image = ImageIO.read(bIn);

            int height = image.getHeight()/(image.getWidth()/100);
            BufferedImage preview = new BufferedImage(100, height, image.getType());
            Graphics2D graphics = preview.createGraphics();
            graphics.drawImage(image, 0, 0, 100, height, null);
            graphics.dispose();

            ImageIO.write(preview, getExtension(path.getFileName().toString()), bOut);
            return bOut.toByteArray();
        }
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private String getFileName(String filePath){
        return filePath.substring(filePath.lastIndexOf("\\")+1);
    }
    public List<Avatar> getListOfAvatars(int pageNumber, int pageSize) {
        logger.info("Был вызван метод getListOfAvatars");
        PageRequest pageRequest = PageRequest.of(pageNumber-1, pageSize);
        return avatarRepository.findAll(pageRequest).getContent();
    }
}