package br.com.devalex.course.model;

import br.com.devalex.course.enums.CourseLevel;
import br.com.devalex.course.enums.CourseStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false)
    private String description;
    private String imgUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseStatus courseStatus;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseLevel courseLevel;

    @Column(nullable = false)
    private UUID userInstructor;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "created_at", nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy   HH:mm:ss")
    @Column(name = "updated_at", nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


}
